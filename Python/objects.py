import numpy as np

from points import *

class Chunk(np.ndarray):
    metadata = {}

    def __new__(cls, array, **kwargs):
        obj = array.view(cls)
        obj.metadata = kwargs
        return obj

    def get_block(self, point):
        return self[point.x, point.z, point.y]

    def set_block(self, point, value):
        self[point.x, point.z, point.y] = value

class HeightMap(np.ndarray):
    metadata = {}

    def __new__(cls, array, **kwargs):
        obj = array.view(cls)
        obj.metadata = kwargs
        return obj

    def get_height(self, point):
        return self[point.x, point.z]

    def set_height(self, point, value):
        self[point.x, point.z] = value
