import numpy as np

from blocks import *
from bounds import *
from points import *

def randomLeaves():
    if np.random.random() < 0.5:
        return AIR
    else:
        return LEAVES

def generateCanopy():
    canopy = np.array([[[AIR, AIR,    AIR,    AIR,    AIR],
                        [AIR, AIR,    LEAVES, AIR,    AIR],
                        [AIR, LEAVES, LEAVES, LEAVES, AIR],
                        [AIR, AIR,    LEAVES, AIR,    AIR],
                        [AIR, AIR,    AIR,    AIR,    AIR]],

                       [[AIR, AIR,            AIR,    AIR,            AIR],
                        [AIR, randomLeaves(), LEAVES, randomLeaves(), AIR],
                        [AIR, LEAVES,         LEAVES, LEAVES,         AIR],
                        [AIR, randomLeaves(), LEAVES, randomLeaves(), AIR],
                        [AIR, AIR,            AIR,    AIR,            AIR]],

                       [[randomLeaves(), LEAVES, LEAVES, LEAVES, randomLeaves()],
                        [LEAVES,         LEAVES, LEAVES, LEAVES, LEAVES],
                        [LEAVES,         LEAVES, TRUNK,  LEAVES, LEAVES],
                        [LEAVES,         LEAVES, LEAVES, LEAVES, LEAVES],
                        [randomLeaves(), LEAVES, LEAVES, LEAVES, randomLeaves()]],

                       [[randomLeaves(), LEAVES, LEAVES, LEAVES, randomLeaves()],
                        [LEAVES,         LEAVES, LEAVES, LEAVES, LEAVES],
                        [LEAVES,         LEAVES, TRUNK,  LEAVES, LEAVES],
                        [LEAVES,         LEAVES, LEAVES, LEAVES, LEAVES],
                        [randomLeaves(), LEAVES, LEAVES, LEAVES, randomLeaves()]]])

    return canopy

def generateTrunk(trunkHeight):
    trunk = np.array([[[AIR if x <= 1 or x >= 3 or z <= 1 or z >= 3 else TRUNK for z in range(5)] for x in range(5)] for y in range(trunkHeight)])
    return trunk

def isSpaceForTree(block, treePos, treeHeight):
    trunkBlocks = [[x, z, y] for x in range(treePos.x - 1, treePos.x + 2) for z in range(treePos.z - 1, treePos.z + 2) for y in range(treePos.y, treePos.y + treeHeight - 3)]
    canopyBlocks = [[x, z, y] for x in range(treePos.x - 2, treePos.x + 3) for z in range(treePos.z - 2, treePos.z + 3) for y in range(treePos.y + treeHeight - 3, treePos.y + treeHeight)]

    treeBlocks = trunkBlocks + canopyBlocks

    for x, z, y in treeBlocks:
        if not (isWithinBounds(block, Point3D(x, y, z)) and block[x, z, y] == AIR):
            return False

    return True

def growTree(block, treePos, treeHeight):
    canopy = generateCanopy()
    trunk = generateTrunk(treeHeight - 3)
    tree = np.vstack((trunk, canopy[::-1]))

    for i, x in enumerate(range(treePos.x - 2, treePos.x + 3)):
        for j, z in enumerate(range(treePos.z - 2, treePos.z + 3)):
            for k, y in enumerate(range(treePos.y, treePos.y + treeHeight)):
                block[x, z, y] = tree[k, i, j]

    return block
