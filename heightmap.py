import nupmy as np
from objects import HeightMap
from tqdm import tqdm

def createHeightMap(width=128,
                    depth=128,
                    waterLevel=32,
                    noise1=lambda x, y: pnoise2(pnoise2(x, y, octaves = 8), pnoise2(x, y, octaves = 8)),
                    noise2=lambda x, y: pnoise2(x, y, octaves = 6)):

    plane = np.zeros((width, depth), dtype = np.uint8)
    heightMap = HeightMap(plane, waterLevel = waterLevel)

    for x in tqdm(range(width), "Raising..."):
        for z in range(depth):
            heightLow = noise1(x / width * 1.3, z / depth * 1.3) / 6 - 4
            heightHigh = noise1(x / width * 1.3, z / depth * 1.3) / 5 + 6

            if noise2(x / width, z / depth) / 8 > 0:
                heightResult = heightLow
            else:
                heightResult = max(heightLow, heightHigh)

            heightResult /= 2

            if heightResult < 0:
                heightResult *= 8 / 10

            heightMap[x, z] = int(heightResult + waterLevel)

    return heightMap
