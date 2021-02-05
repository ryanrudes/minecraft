import java.util.Hashtable;

public class Blocks {
  Colors colorTable;
  Hashtable<String, Color> colors;

  Ores oreTable;
  Hashtable<String, Ore> ores;

  Hashtable<String, Block> blocks;

  public Blocks() {
    this.colorTable = new Colors();
    this.colors = this.colorTable.colors;

    this.oreTable = new Ores();
    this.ores = this.oreTable.ores;

    Hashtable<String, Block> blocks = new Hashtable<String, Block>();

    // Flowers
    blocks.put("DANDELION", new Block(colors.get("PLANT")));
    blocks.put("ROSE", new Block(colors.get("PLANT")));

    // Mushrooms
    blocks.put("MUSHROOM_RED", new Block(colors.get("RED")));
    blocks.put("MUSHROOM_BROWN", new Block(colors.get("BROWN")));

    // Air
    blocks.put("AIR", new Block(colors.get("NONE")));

    // Tree
    blocks.put("LEAVES", new Block(colors.get("PLANT")));
    blocks.put("TRUNK", new Block(colors.get("WOOD")));

    // Flowing blocks
    blocks.put("WATER", new Block(colors.get("WATER")));
    blocks.put("LAVA", new Block(colors.get("FIRE")));

    // Ground
    blocks.put("STONE", new Block(colors.get("STONE")));
    blocks.put("DIRT", new Block(colors.get("DIRT")));
    blocks.put("GRAVEL", new Block(colors.get("STONE")));
    blocks.put("SAND", new Block(colors.get("SAND")));
    blocks.put("Grass", new Block(colors.get("GRASS")));

    // Ores
    blocks.put("COAL", new Block(ores.get("COAL")));
    blocks.put("IRON", new Block(ores.get("IRON")));
    blocks.put("GOLD", new Block(ores.get("GOLD")));
    blocks.put("DIAMOND", new Block(ores.get("DIAMOND")));

    this.blocks = blocks;
  }
}
