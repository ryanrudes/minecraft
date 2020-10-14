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
