from bounds import *
from colors import *
from points import *
from blocks import *

def floodFillBlockFromPoint(block, point, floodBlock):
    if block[point.x, point.z, point.y] != AIR:
        return block
    else:
        block[point.x, point.z, point.y] = floodBlock

        spread = [Point3D(point.x, point.y - 1, point.z),
                  Point3D(point.x - 1, point.y, point.z - 1),
                  Point3D(point.x - 1, point.y, point.z),
                  Point3D(point.x - 1, point.y, point.z + 1),
                  Point3D(point.x, point.y, point.z - 1),
                  Point3D(point.x, point.y, point.z + 1),
                  Point3D(point.x + 1, point.y, point.z - 1),
                  Point3D(point.x + 1, point.y, point.z),
                  Point3D(point.x + 1, point.y, point.z + 1)]

        for spreadPoint in spread:
            if isWithinBounds(block, spreadPoint):
                block = floodFillBlockFromPoint(block, spreadPoint, floodBlock)

    return block
