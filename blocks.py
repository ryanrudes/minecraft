import color

class Block:
    def __init__(self, color):
        self.color = color


DANDELION = Block(color.PLANT)
ROSE = Block(color.PLANT)
MUSHROOM_RED = Block(color.RED)
MUSHROOM_BROWN = Block(color.BROWN)
AIR = Block(color.NONE)
LEAVES = Block(color.PLANT)
TRUNK = Block(color.WOOD)
LAVA = Block(color.FIRE)
STONE = Block(color.STONE)
DIRT = Block(color.DIRT)
WATER = Block(color.WATER)
GRAVEL = Block(color.STONE)
SAND = Block(color.SAND)
GRASS = Block(color.GRASS)
