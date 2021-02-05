def isWithinBounds(block, point, dim=2):
    width, depth, height = block.shape
    return point.x >= 0 and point.x < width and point.z >= 0 and point.z < depth and (dim == 2 or point.y >= 0 and point.y < height)
