from colors import *

class Ore:
    def __init__(self, abundance, color):
        self.abundance = abundance
        self.color = color

COAL_ORE = Ore(0.9, COLOR_STONE)
IRON_ORE = Ore(0.7, COLOR_STONE)
GOLD_ORE = Ore(0.5, COLOR_GOLD)
DIAMOND_ORE = Ore(0.1, COLOR_DIAMOND)
