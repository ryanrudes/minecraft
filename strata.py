from objects import *
from tqdm import tqdm
import numpy as np
import blocks

def createStrata(heightMap,
                 height=64,
                 noise=lambda x, y: pnoise2(x, y, octaves = 8)):

    width, depth = heightMap.shape
  
    cube = np.full((width, depth, height), blocks.AIR)
    block = Chunk(cube, waterLevel = heightMap.metadata["waterLevel"])

    for x in tqdm(range(width), "Soiling..."):
        for z in range(depth):
            dirtThickness = noise(x / width, z / depth) / 24 - 4
            dirtTransition = heightMap[x, z]
            stoneTransition = dirtTransition + dirtThickness

            for y in range(height):
                if y == 0:
                    blockType = blocks.LAVA
                elif y <= stoneTransition:
                    blockType = blocks.STONE
                elif y <= dirtTransition:
                    blockType = blocks.DIRT
                else:
                    blockType = blocks.AIR

                block[x, z, y] = blockType

    return block
