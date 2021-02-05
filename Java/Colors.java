import java.util.Hashtable;

public class Colors {
  Hashtable<String, Color> colors;

  public Colors() {
    Hashtable<String, Color> colors = new Hashtable<String, Color>();

    colors.put("NONE", new Color(0, "NONE", 0, 0, 0, 0));
    colors.put("GRASS", new Color(1, "GRASS", 127, 178, 56, 255));
    colors.put("SAND", new Color(2, "SAND", 247, 233, 163, 255));
    colors.put("WOOL", new Color(3, "WOOL", 199, 199, 199, 255));
    colors.put("FIRE", new Color(4, "FIRE", 255, 0, 0, 255));
    colors.put("ICE", new Color(5, "ICE", 160, 160, 255, 255));
    colors.put("METAL", new Color(6, "METAL", 167, 167, 167, 255));
    colors.put("PLANT", new Color(7, "PLANT", 0, 124, 0, 255));
    colors.put("SNOW", new Color(8, "SNOW", 255, 255, 255, 255));
    colors.put("CLAY", new Color(9, "CLAY", 164, 168, 184, 255));
    colors.put("DIRT", new Color(10, "DIRT", 151, 109, 77, 255));
    colors.put("STONE", new Color(11, "STONE", 112, 112, 112, 255));
    colors.put("WATER", new Color(12, "WATER", 64, 64, 255, 255));
    colors.put("WOOD", new Color(13, "WOOD", 143, 119, 72, 255));
    colors.put("QUARTZ", new Color(14, "QUARTZ", 255, 252, 245, 255));
    colors.put("ORANGE", new Color(15, "ORANGE", 216, 127, 51, 255));
    colors.put("MAGENTA", new Color(16, "MAGENTA", 178, 76, 216, 255));
    colors.put("LIGHT_BLUE", new Color(17, "LIGHT_BLUE", 102, 153, 216, 255));
    colors.put("YELLOW", new Color(18, "YELLOW", 229, 229, 51, 255));
    colors.put("LIGHT_GREEN", new Color(19, "LIGHT_GREEN", 127, 204, 25, 255));
    colors.put("PINK", new Color(20, "PINK", 242, 127, 165, 255));
    colors.put("GRAY", new Color(21, "GRAY", 76, 76, 76, 255));
    colors.put("LIGHT_GRAY", new Color(22, "LIGHT_GRAY", 153, 153, 153, 255));
    colors.put("CYAN", new Color(23, "CYAN", 76, 127, 153, 255));
    colors.put("PURPLE", new Color(24, "PURPLE", 127, 63, 178, 255));
    colors.put("BLUE", new Color(25, "BLUE", 51, 76, 178, 255));
    colors.put("BROWN", new Color(26, "BROWN", 102, 76, 51, 255));
    colors.put("GREEN", new Color(27, "GREEN", 102, 127, 51, 255));
    colors.put("RED", new Color(28, "RED", 153, 51, 51, 255));
    colors.put("BLACK", new Color(29, "BLACK", 25, 25, 25, 255));
    colors.put("GOLD", new Color(30, "GOLD", 250, 238, 77, 255));
    colors.put("DIAMOND", new Color(31, "DIAMOND", 92, 219, 213, 255));
    colors.put("LAPIS", new Color(32, "LAPIS", 74, 128, 255, 255));
    colors.put("EMERALD", new Color(33, "EMERALD", 0, 217, 58, 255));
    colors.put("PODZOL", new Color(34, "PODZOL", 129, 86, 49, 255));
    colors.put("NETHER", new Color(35, "NETHER", 112, 2, 0, 255));
    colors.put("TERRACOTTA_WHITE", new Color(36, "TERRACOTTA_WHITE", 209, 177, 161, 255));
    colors.put("TERRACOTTA_ORANGE", new Color(37, "TERRACOTTA_ORANGE", 159, 82, 36, 255));
    colors.put("TERRACOTTA_MAGENTA", new Color(38, "TERRACOTTA_MAGENTA", 149, 87, 108, 255));
    colors.put("TERRACOTTA_LIGHT_BLUE", new Color(39, "TERRACOTTA_LIGHT_BLUE", 112, 108, 138, 255));
    colors.put("TERRACOTTA_YELLOW", new Color(40, "TERRACOTTA_YELLOW", 186, 133, 36, 255));
    colors.put("TERRACOTTA_LIGHT_GREEN", new Color(41, "TERRACOTTA_LIGHT_GREEN", 103, 117, 53, 255));
    colors.put("TERRACOTTA_PINK", new Color(42, "TERRACOTTA_PINK", 160, 77, 78, 255));
    colors.put("TERRACOTTA_GRAY", new Color(43, "TERRACOTTA_GRAY", 57, 41, 35, 255));
    colors.put("TERRACOTTA_LIGHT_GRAY", new Color(44, "TERRACOTTA_LIGHT_GRAY", 135, 107, 98, 255));
    colors.put("TERRACOTTA_CYAN", new Color(45, "TERRACOTTA_CYAN", 87, 92, 92, 255));
    colors.put("TERRACOTTA_PURPLE", new Color(46, "TERRACOTTA_PURPLE", 122, 73, 88, 255));
    colors.put("TERRACOTTA_BLUE", new Color(47, "TERRACOTTA_BLUE", 76, 62, 92, 255));
    colors.put("TERRACOTTA_BROWN", new Color(48, "TERRACOTTA_BROWN", 76, 50, 35, 255));
    colors.put("TERRACOTTA_GREEN", new Color(49, "TERRACOTTA_GREEN", 76, 82, 42, 255));
    colors.put("TERRACOTTA_RED", new Color(50, "TERRACOTTA_RED", 142, 60, 46, 255));
    colors.put("TERRACOTTA_BLACK", new Color(51, "TERRACOTTA_BLACK", 37, 22, 16, 255));
    colors.put("CRIMSON_NYLIUM", new Color(52, "CRIMSON_NYLIUM", 189, 48, 49, 255));
    colors.put("CRIMSON_STEM", new Color(53, "CRIMSON_STEM", 148, 63, 97, 255));
    colors.put("CRIMSON_HYPHAE", new Color(54, "CRIMSON_HYPHAE", 92, 25, 29, 255));
    colors.put("WARPED_NYLIUM", new Color(55, "WARPED_NYLIUM", 22, 126, 134, 255));
    colors.put("WARPED_STEM", new Color(56, "WARPED_STEM", 58, 142, 140, 255));
    colors.put("WARPED_HYPHAE", new Color(57, "WARPED_HYPHAE", 86, 44, 62, 255));
    colors.put("WARPED_WART_BLOCK", new Color(58, "WARPED_WART_BLOCK", 20, 180, 133, 255));

    this.colors = colors;
  }
}
