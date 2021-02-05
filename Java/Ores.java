import java.util.Hashtable;

public class Ores {
  Hashtable<String, Ore> ores;

  public Ores() {
    Colors colorTable = new Colors();
    Hashtable<String, Color> colors = colorTable.colors;
    Hashtable<String, Ore> ores = new Hashtable<String, Ore>();

    ores.put("COAL", new Ore(0.9, colors.get("STONE")));
    ores.put("IRON", new Ore(0.7, colors.get("STONE")));
    ores.put("GOLD", new Ore(0.5, colors.get("GOLD")));
    ores.put("DIAMOND", new Ore(0.1, colors.get("DIAMOND")));

    this.ores = ores;
  }
}
