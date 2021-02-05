public class Block {
  Color color;
  Ore ore;
  double abundance;
  
  public Block(Color color) {
    this.color = color;
  }

  public Block(Ore ore) {
    this.ore = ore;
    this.color = ore.color;
    this.abundance = ore.abundance;
  }
}
