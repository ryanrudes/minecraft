import numpy as np
from tqdm import tqdm
import cv2
import time
import plotly.graph_objects as go
from noise import *
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import sys

from colors import *
from ore import *
from points import *
from flood import *
from objects import *
from blocks import *
from tree import *
from render import *

sys.setrecursionlimit(10 ** 6)

# https://github.com/UnknownShadow200/ClassiCube/wiki/Minecraft-Classic-map-generation-algorithm

def createHeightMap(x0=0,
                    z0=0,
                    width=128,
                    depth=128,
                    waterLevel=32,
                    noise1=lambda x, y: pnoise2(pnoise2(x, y, octaves = 8), pnoise2(x, y, octaves = 8)),
                    noise2=lambda x, y: pnoise2(x, y, octaves = 6)):

    heightMap = HeightMap(np.zeros((width, depth), dtype = np.uint8), waterLevel = waterLevel)

    for i, x in enumerate(tqdm(np.linspace(x0, x0 + 1, num = width), "Raising...")):
        for j, z in enumerate(np.linspace(z0, z0 + 1, num = depth)):
            heightLow = noise1(x * 1.3, z * 1.3) / 6 - 4
            heightHigh = noise1(x * 1.3, z * 1.3) / 5 + 6

            if noise2(x, z) / 8 > 0:
                heightResult = heightLow
            else:
                heightResult = max(heightLow, heightHigh)

            heightResult /= 2

            if heightResult < 0:
                heightResult *= 8 / 10

            heightMap[i, j] = int(heightResult + waterLevel)

    return heightMap

def createStrata(heightMap,
                 height=64,
                 noise=lambda x, y: pnoise2(x, y, octaves = 8)):

    width, depth = heightMap.shape
    block = Chunk(np.full((width, depth, height), AIR), waterLevel = heightMap.metadata["waterLevel"])

    for i, x in enumerate(tqdm(np.linspace(0, 1, num = width), "Soiling...")):
        for j, z in enumerate(np.linspace(0, 1, num = depth)):
            dirtThickness = noise(x, z) / 24 - 4
            dirtTransition = heightMap[i, j]
            stoneTransition = dirtTransition + dirtThickness

            for y in range(height):
                if y == 0:
                    blockType = LAVA
                elif y <= stoneTransition:
                    blockType = STONE
                elif y <= dirtTransition:
                    blockType = DIRT
                else:
                    blockType = AIR

                block[i, j, y] = blockType

    return block

def fillOblateSpheroid(block, centerPos, radius, fillBlock):
    minx = max(0, int(centerPos.x - radius))
    maxx = min(block.shape[0], int(centerPos.x + radius))
    minz = max(0, int(centerPos.z - radius))
    maxz = min(block.shape[1], int(centerPos.z + radius))
    miny = max(0, int(centerPos.y - radius))
    maxy = min(block.shape[2], int(centerPos.y + radius))

    for x in range(minx, maxx):
        for z in range(minz, maxz):
            for y in range(miny, maxy):
                dx = x - centerPos.x
                dy = y - centerPos.y
                dz = z - centerPos.z

                if dx ** 2 + 2 * dy ** 2 + dz ** 2 < radius ** 2:
                    if block[x, z, y] == STONE:
                        block[x, z, y] = fillBlock

    return block

def carveOutCaves(block):
    width, depth, height = block.shape
    numCaves = int(width * height * depth / 8192)

    for i in tqdm(range(numCaves), "Carving..."):
        cavePos = Point3D(np.random.randint(0, width),
                          np.random.randint(0, height),
                          np.random.randint(0, depth))

        caveLength = int(np.random.random() * np.random.random() * 200)

        theta = np.random.random() * np.pi * 2
        deltaTheta = 0
        phi = np.random.random() * np.pi * 2
        deltaPhi = 0

        caveRadius = np.random.random() * np.random.random()

        for j in range(caveLength):
            cavePos.x = cavePos.x + np.sin(theta) * np.cos(phi)
            cavePos.y = cavePos.y + np.cos(theta) * np.cos(phi)
            cavePos.z = cavePos.z + np.sin(phi)

            theta = theta + deltaTheta * 0.2
            deltaTheta = (deltaTheta * 0.9) + np.random.random() - np.random.random()
            phi = phi / 2 + deltaPhi / 4
            deltaPhi = (deltaPhi * 0.75) + np.random.random() - np.random.random()

            if np.random.random() >= 0.25:
                centerPos = Point3D(cavePos.x + (np.random.randint(0, 4) - 2) * 0.2,
                                    cavePos.y + (np.random.randint(0, 4) - 2) * 0.2,
                                    cavePos.z + (np.random.randint(0, 4) - 2) * 0.2)

                radius = (height - centerPos.y) / height
                radius = 1.2 + (radius * 3.5 + 1) * caveRadius
                radius = radius * np.sin(j * np.pi / caveLength)

                block = fillOblateSpheroid(block, centerPos, radius, AIR)

    return block

def createOreVeins(block):
    width, depth, height = block.shape

    for oreBlock in [COAL_ORE, IRON_ORE, GOLD_ORE, DIAMOND_ORE]:
        numVeins = int(width * height * depth * oreBlock.abundance / 16384)
        for i in range(numVeins):
            veinPos = Point3D(np.random.randint(0, width),
                              np.random.randint(0, height),
                              np.random.randint(0, depth))

            veinLength = int(np.random.random() * np.random.random() * 75 * oreBlock.abundance)

            theta = np.random.random() * np.pi * 2
            deltaTheta = 0
            phi = np.random.random() * np.pi * 2
            deltaPhi = 0

            for j in range(veinLength):
                veinPos.x = veinPos.x + np.sin(theta) * np.cos(phi)
                veinPos.y = veinPos.y + np.cos(theta) * np.cos(phi)
                veinPos.z = veinPos.z + np.sin(phi)

                theta = deltaTheta * 0.2
                deltaTheta = (deltaTheta * 0.9) + np.random.random() - np.random.random()
                phi = phi / 2 + deltaPhi / 4
                deltaPhi = (deltaPhi * 0.9) + np.random.random() - np.random.random()

                radius = oreBlock.abundance * np.sin(j * np.pi / veinLength) + 1

                block = fillOblateSpheroid(block, veinPos, radius, oreBlock)

    return block

def floodFillWater(block):
    width, depth, height = block.shape
    waterLevel = block.metadata["waterLevel"]

    xleft = [Point3D(0, waterLevel - 1, z) for z in range(depth)]
    xright = [Point3D(width - 1, waterLevel - 1, z) for z in range(depth)]
    zleft = [Point3D(x, waterLevel - 1, 0) for x in range(width)]
    zright = [Point3D(x, waterLevel - 1, depth - 1) for x in range(width)]

    undergroundSources = []
    X, Z, Y = np.where(block == AIR)
    for x, z, y in zip(X, Z, Y):
        if y < waterLevel - 1:
            undergroundSources.append(Point3D(x, y, z))

    numUndergroundSources = int(width * depth / 2000)
    undergroundSources = np.random.choice(undergroundSources, size = numUndergroundSources, replace = False)

    sources = xleft + xright + zleft + zright + list(undergroundSources)

    for source in tqdm(sources, "Watering..."):
        block = floodFillBlockFromPoint(block, source, WATER)

    return block

def floodFillLava(block):
    width, depth, height = block.shape
    waterLevel = block.metadata["waterLevel"]

    numLavaSources = int(width * depth / 20000)

    for i in tqdm(range(numLavaSources), "Melting..."):
        x = np.random.randint(0, width)
        z = np.random.randint(0, depth)
        y = int((waterLevel - 3) * np.random.random() * np.random.random())
        source = Point3D(x, y, z)

        if block[x, z, y] == AIR:
            block = floodFillBlockFromPoint(block, source, LAVA)

    return block

def createSurfaceLayer(block,
                       heightMap,
                       noise=lambda x, y: pnoise2(x, y, octaves = 8)):

    width, depth, height = block.shape
    waterLevel = block.metadata["waterLevel"]

    for i, x in enumerate(tqdm(np.linspace(0, 1, num = width), "Growing...")):
        for j, z in enuemrate(np.linspace(0, 1, num = depth)):
            sandChance = noise(x, z) > 8
            gravelChance = noise(x, z) > 12

            y = heightMap[i, j]
            blockAbove = block[i, j, y + 1]

            if blockAbove == WATER and gravelChance:
                block[i, j, y] = GRAVEL
            elif blockAbove == AIR:
                if y <= waterLevel and sandChance:
                    block[i, j, y] = SAND
                else:
                    block[i, j, y] = GRASS

    return block

def addFlowers(block, heightMap):
    width, depth, height = block.shape
    waterLevel = block.metadata["waterLevel"]

    numPatches = int(width * depth / 3000)

    for i in range(numPatches):
        flowerType = np.random.choice([DANDELION, ROSE])
        patchPos = Point3D(np.random.randint(0, width),
                           0,
                           np.random.randint(0, depth))

        for j in range(10):
            flowerPos = patchPos
            for h in range(5):
                flowerPos.x = flowerPos.x + np.random.randint(0, 6) - np.random.randint(0, 6)
                flowerPos.z = flowerPos.z + np.random.randint(0, 6) - np.random.randint(0, 6)

                if isWithinBounds(block, flowerPos):
                    flowerPos.y = heightMap[flowerPos.x, flowerPos.z] + 1
                    blockBelow = block[flowerPos.x, flowerPos.z, flowerPos.y - 1]

                    if block.get_block(flowerPos) == AIR and blockBelow == GRASS:
                        block.set_block(flowerPos, flowerType)

    return block

def addMushrooms(block, heightMap):
    width, depth, height = block.shape

    numPatches = int(width * height * depth / 2000)

    for i in range(numPatches):
        mushroomType = np.random.choice([MUSHROOM_RED, MUSHROOM_BROWN])
        patchPos = Point3D(np.random.randint(0, width),
                           np.random.randint(0, height),
                           np.random.randint(0, depth))

        for j in range(20):
            mushPos = patchPos
            for h in range(5):
                mushPos.x = mushPos.x + np.random.randint(0, 6) - np.random.randint(0, 6)
                mushPos.z = mushPos.z + np.random.randint(0, 6) - np.random.randint(0, 6)

                if isWithinBounds(block, mushPos) and mushPos.y < heightMap.get_height(mushPos) - 1:
                    blockBelow = block[mushPos.x, mushPos.z, mushPos.y - 1]
                    if block.get_block(mushPos) == AIR and blockBelow == STONE:
                        block.set_block(mushPos, mushroomType)

    return block


def addTrees(block, heightMap):
    width, depth, height = block.shape

    numPatches = int(width * height * depth / 4000)

    for i in range(numPatches):
        patchPos = Point3D(np.random.randint(0, width),
                           0,
                           np.random.randint(0, depth))

        for j in range(20):
            treePos = patchPos

            for h in range(20):
                treePos.x = treePos.x + np.random.randint(0, 6) - np.random.randint(0, 6)
                treePos.z = treePos.z + np.random.randint(0, 6) - np.random.randint(0, 6)

                if isWithinBounds(block, treePos) and np.random.random() <= 0.25:
                    treePos.y = heightMap.get_height(treePos) + 1
                    treeHeight = np.random.randint(0, 3) + 4

                    if isSpaceForTree(block, treePos, treeHeight):
                        block = growTree(block, treePos, treeHeight)

    return block

def createPlants(block, heightMap):
    for plantFunction in tqdm([addFlowers, addMushrooms, addTrees], "Planting..."):
        block = plantFunction(block, heightMap)

    return block

def generateBlock(seed, x0=0, z0=0, width=128, depth=128, height=64, waterLevel=32):
    heightMap = createHeightMap(x0 = seed + x0, z0 = seed + x0, width = width, depth = depth, waterLevel = waterLevel)
    block = createStrata(heightMap, height = height)
    block = carveOutCaves(block)
    block = createOreVeins(block)
    block = floodFillWater(block)
    block = floodFillLava(block)
    block = createSurfaceLayer(block, heightMap)
    block = createPlants(block, heightMap)
    return block

block = generateBlock(seed = 12, width = 64, depth = 64, height = 64)
render3D(block)
