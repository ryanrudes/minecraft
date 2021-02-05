import java.lang.Math;
import java.util.Random;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.util.*;

public class Chunk {
  int width, depth, height, waterLevel;
  PerlinNoise p;
  int[][] heightMap;
  Block[][][] block;

  Colors colorTable;
  Hashtable<String, Color> colors;

  Blocks blockTable;
  Hashtable<String, Block> blocks;

  Ores oreTable;
  Hashtable<String, Ore> ores;

  Random rand;

  public Chunk(int seed, int width, int depth, int height, int waterLevel) {
    this.p = new PerlinNoise(seed);
    this.width = width;
    this.depth = depth;
    this.height = height;
    this.waterLevel = waterLevel;

    this.colorTable = new Colors();
    this.colors = this.colorTable.colors;

    this.blockTable = new Blocks();
    this.blocks = this.blockTable.blocks;

    this.oreTable = new Ores();
    this.ores = this.oreTable.ores;

    this.rand = new Random();
  }

  public int[][] createHeightMap() {
    int[][] heightMap = new int[width][depth];
    double x, z, heightLow, heightHigh, heightResult;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < depth; j++) {
        x = (double)i / (double)width;
        z = (double)j / (double)depth;

        heightLow = p.noise(x * 1.3, z * 1.3) / 6 - 4;
        heightHigh = p.noise(x * 1.3, z * 1.3) / 5 + 6;

        if (p.noise(x, z) / 8 > 0) {
          heightResult = heightLow;
        } else {
          heightResult = Math.max(heightLow, heightHigh);
        }

        heightResult /= 2;

        if (heightResult < 0) {
          heightResult *= 0.8;
        }

        heightMap[i][j] = (int)heightResult + waterLevel;
      }
    }

    this.heightMap = heightMap;
    return heightMap;
  }

  public Block[][][] createStrata() {
    Block[][][] block = new Block[width][depth][height];
    double x, z, dirtThickness, dirtTransition, stoneTransition;
    Block blockType;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < depth; j++) {
        x = (double)i / (double)width;
        z = (double)j / (double)depth;

        dirtThickness = p.noise(x, z) / 24 - 4;
        dirtTransition = heightMap[i][j];
        stoneTransition = dirtTransition + dirtThickness;

        for (int k = 0; k < height; k++) {
          if (k == 0) {
            blockType = blocks.get("LAVA");
          } else if (k <= stoneTransition) {
            blockType = blocks.get("STONE");
          } else if (k <= dirtTransition) {
            blockType = blocks.get("DIRT");
          } else {
            blockType = blocks.get("AIR");
          }

          block[i][j][k] = blockType;
        }
      }
    }

    this.block = block;
    return block;
  }

  public Block[][][] fillOblateSpheroid(Block[][][] block, Point3D centerPos, int radius, Block fillBlock) {
    int minx = Math.max(0, (int)(centerPos.x - radius));
    int maxx = Math.min(width, (int)(centerPos.x + radius));
    int minz = Math.max(0, (int)(centerPos.z - radius));
    int maxz = Math.min(depth, (int)(centerPos.z + radius));
    int miny = Math.max(0, (int)(centerPos.y - radius));
    int maxy = Math.min(height, (int)(centerPos.y + radius));

    double dx, dy, dz;

    for (int x = minx; x < maxx; x++) {
      for (int z = minz; z < maxz; z++) {
        for (int y = miny; y < maxy; y++) {
          dx = x - centerPos.x;
          dy = y - centerPos.y;
          dz = z - centerPos.z;

          if (Math.pow(dx, 2) + 2 * Math.pow(dy, 2) + Math.pow(dz, 2) < Math.pow(radius, 2)) {
            if (block[x][z][y] == blocks.get("STONE")) {
              block[x][z][y] = fillBlock;
            }
          }
        }
      }
    }

    return block;
  }

  public Block[][][] carveOutCaves(Block[][][] block) {
    int numCaves = width * height * depth / 8192;

    Point3D cavePos, centerPos;
    int caveLength;
    double theta, phi, deltaTheta, deltaPhi, caveRadius, radius;

    for (int i = 0; i < numCaves; i++) {
      cavePos = new Point3D(rand.nextInt(width), rand.nextInt(height), rand.nextInt(depth));

      caveLength = (int)(Math.random() * Math.random() * 200);

      theta = Math.random() * Math.PI * 2;
      deltaTheta = 0;
      phi = Math.random() * Math.PI * 2;
      deltaPhi = 0;

      caveRadius = Math.random() * Math.random();

      for (int j = 0; j < caveLength; j++) {
        cavePos.x = cavePos.x + Math.sin(theta) * Math.cos(phi);
        cavePos.y = cavePos.y + Math.cos(theta) * Math.cos(phi);
        cavePos.z = cavePos.z + Math.sin(phi);

        theta = theta + deltaTheta * 0.2;
        deltaTheta = (deltaTheta * 0.9) + Math.random() - Math.random();
        phi = phi / 2 + deltaPhi / 4;
        deltaPhi = (deltaPhi * 0.75) + Math.random() - Math.random();

        if (Math.random() >= 0.25) {
          centerPos = new Point3D(cavePos.x + (rand.nextInt(4) - 2) * 0.2, cavePos.y + (rand.nextInt(4) - 2) * 0.2, cavePos.z + (rand.nextInt(4) - 2) * 0.2);

          radius = (height - centerPos.y) / height;
          radius = 1.2 + (radius * 3.5 + 1) * caveRadius;
          radius = radius * Math.sin(j * Math.PI / caveLength);

          block = fillOblateSpheroid(block, centerPos, (int)radius, blocks.get("AIR"));
        }
      }
    }

    return block;
  }

  public Block[][][] createOreVeins(Block[][][] block) {
    int numVeins, veinLength;
    Point3D veinPos;
    double theta, phi, deltaTheta, deltaPhi, radius;
    Ore oreBlock;

    Block[] oreBlocks = {blocks.get("COAL"), blocks.get("IRON"), blocks.get("GOLD"), blocks.get("DIAMOND")};

    for (Block ore: oreBlocks) {
        numVeins = (int)(width * height * depth * ore.abundance / 16384);

        for (int i = 0; i < numVeins; i++) {
          veinPos = new Point3D(rand.nextInt(width), rand.nextInt(height), rand.nextInt(depth));

          veinLength = (int)(Math.random() * Math.random() * 75 * ore.abundance);

          theta = Math.random() * Math.PI * 2;
          deltaTheta = 0;
          phi = Math.random() * Math.PI * 2;
          deltaPhi = 0;

          for (int j = 0; j < veinLength; j++) {
            veinPos.x = veinPos.x + Math.sin(theta) * Math.cos(phi);
            veinPos.y = veinPos.y + Math.cos(theta) * Math.cos(phi);
            veinPos.z = veinPos.z + Math.sin(phi);

            theta = deltaTheta * 0.2;
            deltaTheta = (deltaTheta * 0.9) + Math.random() - Math.random();
            phi = phi / 2 + deltaPhi / 4;
            deltaPhi = (deltaPhi * 0.9) + Math.random() - Math.random();

            radius = ore.abundance * Math.sin(j * Math.PI / veinLength) + 1;

            block = fillOblateSpheroid(block, veinPos, (int)radius, ore);
          }
        }
    }

    return block;
  }

  public boolean isWithinBounds(Point3D point, int dim) {
    return point.x >= 0 && point.x < width && point.z >= 0 && point.z < depth && (dim == 2 || point.y >= 0 && point.y < height);
  }

  public Block[][][] floodFillBlockFromPoint(Block[][][] block, Point3D point, Block floodBlock) {
    if (block[(int)point.x][(int)point.z][(int)point.y] != blocks.get("AIR")) {
      return block;
    } else {
      block[(int)point.x][(int)point.z][(int)point.y] = floodBlock;

      Point3D[] spread = {new Point3D(point.x, point.y - 1, point.z), new Point3D(point.x - 1, point.y, point.z - 1), new Point3D(point.x - 1, point.y, point.z), new Point3D(point.x - 1, point.y, point.z + 1), new Point3D(point.x, point.y, point.z - 1), new Point3D(point.x, point.y, point.z + 1), new Point3D(point.x + 1, point.y, point.z - 1), new Point3D(point.x + 1, point.y, point.z), new Point3D(point.x + 1, point.y, point.z + 1)};

      for (Point3D spreadPoint: spread) {
        if (isWithinBounds(spreadPoint, 2)) {
          block = floodFillBlockFromPoint(block, spreadPoint, floodBlock);
        }
      }
    }

    return block;
  }

  public Block[][][] floodFillWater(Block[][][] block) {
    Point3D[] xleft = new Point3D[depth];
    Point3D[] xright = new Point3D[depth];
    Point3D[] zleft = new Point3D[width];
    Point3D[] zright = new Point3D[width];

    for (int z = 0; z < depth; z++) { xleft[z] = new Point3D(0, waterLevel - 1, z); }
    for (int z = 0; z < depth; z++) { xright[z] = new Point3D(width - 1, waterLevel - 1, z); }
    for (int x = 0; x < width; x++) { zleft[x] = new Point3D(x, waterLevel - 1, 0); }
    for (int x = 0; x < width; x++) { zright[x] = new Point3D(x, waterLevel - 1, depth - 1); }

    List<Integer> X = new ArrayList<Integer>();
    List<Integer> Z = new ArrayList<Integer>();
    List<Integer> Y = new ArrayList<Integer>();

    for (int x = 0; x < width; x++) {
      for (int z = 0; z < depth; z++) {
        for (int y = 0; y < height; y++) {
          if (block[x][z][y] == blocks.get("AIR")) {
            X.add(x);
            Z.add(z);
            Y.add(y);
          }
        }
      }
    }

    List<Point3D> undergroundSources = new ArrayList<Point3D>();

    int x, y, z;

    for (int i = 0; i < X.size(); i++) {
      x = X.get(i);
      z = Z.get(i);
      y = Y.get(i);

      if (y < waterLevel - 1) {
        undergroundSources.add(new Point3D(x, y, z));
      }
    }

    int numUndergroundSources = width * depth / 2000;
    Collections.shuffle(undergroundSources);

    for (int i = 0; i < numUndergroundSources; i++) {
      undergroundSources.remove(0);
    }

    for (Point3D source: xleft) {
      block = floodFillBlockFromPoint(block, source, blocks.get("WATER"));
    }
    for (Point3D source: xright) {
      block = floodFillBlockFromPoint(block, source, blocks.get("WATER"));
    }
    for (Point3D source: zleft) {
      block = floodFillBlockFromPoint(block, source, blocks.get("WATER"));
    }
    for (Point3D source: zright) {
      block = floodFillBlockFromPoint(block, source, blocks.get("WATER"));
    }
    for (Point3D source: undergroundSources) {
      block = floodFillBlockFromPoint(block, source, blocks.get("WATER"));
    }

    return block;
  }

  public Block[][][] floodFillLava(Block[][][] block) {
    int numLavaSources = width * depth / 20000;

    Point3D source;
    int x, y, z;

    for (int i = 0; i < numLavaSources; i++) {
      x = rand.nextInt(width);
      z = rand.nextInt(depth);
      y = (int)((waterLevel - 3) * Math.random() * Math.random());
      source = new Point3D(x, y, z);

      if (block[x][z][y] == blocks.get("AIR")) {
        block = floodFillBlockFromPoint(block, source, blocks.get("LAVA"));
      }
    }

    return block;
  }

  public Block[][][] createSurfaceLayer(Block[][][] block, int[][] heightMap) {
    double sandChance, gravelChance, x, z;
    Block blockAbove;
    int y;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < depth; j++) {
        x = (double)i / (double)width;
        z = (double)j / (double)depth;

        sandChance = p.noise(x, z);
        gravelChance = p.noise(x, z);

        y = heightMap[i][j];
        blockAbove = block[i][j][y + 1];

        if (blockAbove == blocks.get("WATER") && gravelChance > 8) {
          block[i][j][y] = blocks.get("GRAVEL");
        } else if (blockAbove == blocks.get("AIR")) {
          if (y <= waterLevel && sandChance > 8) {
            block[i][j][y] = blocks.get("SAND");
          } else {
            block[i][j][y] = blocks.get("GRASS");
          }
        }
      }
    }

    return block;
  }

  public int getMin(int[][] array) {
    int minValue = 100000;
    for (int x = 0; x < array.length; x++) {
      for (int y = 0; y < array[0].length; y++) {
        if (array[x][y] < minValue) {
          minValue = array[x][y];
        }
      }
    }
    return minValue;
  }

  public int getMax(int[][] array) {
    int maxValue = -100000;
    for (int x = 0; x < array.length; x++) {
      for (int y = 0; y < array[0].length; y++) {
        if (array[x][y] > maxValue) {
          maxValue = array[x][y];
        }
      }
    }
    return maxValue;
  }

  public void writeHeightMapAsImage(int[][] heightMap, String name) {
    String path = "heightmaps/" + name + ".png";
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    int maximum = getMax(heightMap);
    int minimum = getMin(heightMap);
    double range = maximum - minimum;

    int value;

    for (int x = 0; x < width; x++) {
      for (int z = 0; z < height; z++) {
        value = 255 - (int)((heightMap[x][z] - minimum) / range * 255);
        image.setRGB(x, z, value);
      }
    }

    try {
      File imageFile = new File(path);
      ImageIO.write(image, "png", imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeBlockLayerAsImage(Block[][][] block, int y, String name) {
    String path = "blockcrosssections/" + name + ".png";
    BufferedImage image = new BufferedImage(width, depth, BufferedImage.TYPE_INT_RGB);

    Color color;
    int col;

    for (int x = 0; x < width; x++) {
      for (int z = 0; z < depth; z++) {
        color = block[x][z][y].color;
        col = (color.alpha << 24) | (color.red << 16) | (color.green << 8) | color.blue;
        image.setRGB(x, z, col);
      }
    }

    try {
      File imageFile = new File(path);
      ImageIO.write(image, "png", imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    double start_time, end_time, duration;
    start_time = System.currentTimeMillis();
    Chunk chunk = new Chunk(254, 256, 256, 64, 32);
    int[][] heightMap = chunk.createHeightMap();
    // chunk.writeHeightMapAsImage(chunk.heightMap, "heightMap");
    Block[][][] block = chunk.createStrata();
    // chunk.writeBlockLayerAsImage(block, 35, "block");
    block = chunk.carveOutCaves(block);
    // chunk.writeBlockLayerAsImage(block, 14, "block");
    block = chunk.createOreVeins(block);
    // chunk.writeBlockLayerAsImage(block, 24, "block");
    block = chunk.floodFillWater(block);
    // chunk.writeBlockLayerAsImage(block, 24, "block");
    block = chunk.floodFillLava(block);
    // chunk.writeBlockLayerAsImage(block, 24, "block");
    block = chunk.createSurfaceLayer(block, heightMap);
    end_time = System.currentTimeMillis();
    duration = (end_time - start_time) / 1000;
    System.out.println(String.format("That took %s seconds", duration));
    chunk.writeBlockLayerAsImage(block, 35, "block");
  }
}
