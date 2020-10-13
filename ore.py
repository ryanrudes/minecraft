import color

class Ore:
    def __init__(self, abundance, color):
        self.abundance = abundance
        self.color = color

COAL = Ore(0.9, color.STONE)
IRON = Ore(0.7, color.STONE)
GOLD = Ore(0.5, color.STONE)
DIAMOND = Ore(0.1, color.STONE)
