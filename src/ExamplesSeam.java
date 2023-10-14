import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.awt.Color;

//Represents a Node in a graph
interface INode {

  // Returns the brightness of a node
  double brightness();

  // Constructs a seam from best to this and adds it to newSeams if it has a lower
  // weight than the seam that is already there
  void addTo(HashMap<Pixel, SeamInfo> newSeams, SeamInfo best);

  // Paints the pixels in img corresponding to this node and the nodes to its
  // right
  void paintRight(ComputedPixelImage img, int x, int y);

  // Applies INode.paintRight to this node and every node below it
  void paintDown(ComputedPixelImage img, int x, int y);

  // prevSeams maps each pixel in this row to the "best" seam from the top to
  // itself
  // Returns an equivalant mapping but for the row of Nodes below this Node
  HashMap<Pixel, SeamInfo> findSeamsRight(HashMap<Pixel, SeamInfo> prevSeams,
      HashMap<Pixel, SeamInfo> newSeams);

  // Calls INode.findSeamsRight to this Node and every node below it
  HashMap<Pixel, SeamInfo> findSeamsDown(HashMap<Pixel, SeamInfo> prevSeams);

  // Drags this node and every node to the right of it, one step to the left.
  // Correctly forms new links
  void dragLeft(ANode newUp, ANode newDown);

  // Returns true if the graph contains no content
  boolean empty();

  // Returns true if the graph contains no content
  boolean emptyHelper();

  // Sets up node
  void setUp(ANode node);

  // Sets topLeft node
  void setTopLeft(ANode node);

  // Sets topRight node
  void setTopRight(ANode node);

  // Sets right node
  void setRight(ANode node);

  // Sets downRight node
  void setDownRight(ANode node);

  // Sets down node
  void setDown(ANode node);

  // Sets downLeft node
  void setDownLeft(ANode node);

  // Sets left node
  void setLeft(ANode node);

}

//Represents a Node in a graph
abstract class ANode implements INode {
  ANode topLeft;
  ANode up;
  ANode topRight;
  ANode right;
  ANode downRight;
  ANode down;
  ANode downLeft;
  ANode left;

  ANode() {

    this.topLeft = this;
    this.up = this;
    this.topRight = this;
    this.right = this;
    this.downRight = this;
    this.down = this;
    this.downLeft = this;
    this.left = this;
  }

  // Effect: modifies this.up and node.down
  public void setUp(ANode node) {
    this.up = node;
    node.down = this;
  }

  // Effect: modifies this.topLeft and node.downRight
  public void setTopLeft(ANode node) {
    this.topLeft = node;
    node.downRight = this;
  }

  // Effect: modifies this.topRight and node.downLeft
  public void setTopRight(ANode node) {
    this.topRight = node;
    node.downLeft = this;
  }

  // Effect: modifies this.right and node.left
  public void setRight(ANode node) {
    this.right = node;
    node.left = this;
  }

  // Effect: modifies this.downRight and node.topLeft
  public void setDownRight(ANode node) {
    this.downRight = node;
    node.topLeft = this;
  }

  // Effect: modifies this.down and node.up
  public void setDown(ANode node) {
    this.down = node;
    node.up = this;
  }

  // Effect: modifies this.downLeft and node.topRight
  public void setDownLeft(ANode node) {
    this.downLeft = node;
    node.topRight = this;
  }

  // Effect: modifies this.left and node.right
  public void setLeft(ANode node) {
    this.left = node;
    node.right = this;
  }

  // Returns true if the graph is empty
  public boolean empty() {
    return this.downRight.emptyHelper();
  }

}

//Represents a Node at the edge of a graph (Analogous to a Sentinel in a Deque)
class Border extends ANode {

  // Default brightness for borders should be considered 0
  public double brightness() {
    return 0.0;
  }

  // Adds best to newSeams without changing it since Borders can not go in
  // SeamInfo
  // Effect: Adds to newSeams
  public void addTo(HashMap<Pixel, SeamInfo> newSeams, SeamInfo best) {
    newSeams.put(best.current, best);
  }

  // Does nothing as Borders are not draw
  // Effect: nothing
  public void paintRight(ComputedPixelImage img, int x, int y) {
    // We have got to the end of the row so theres nothing else to daw
  }

  // Does nothing as Borders are not draw
  // Effect: nothing
  public void paintDown(ComputedPixelImage img, int x, int y) {
    // We have gotten to the end of the column so there is nothing else to paint
  }

  // Returns mapping from all Pixels in this row to the best seam which can get to
  // it
  public HashMap<Pixel, SeamInfo> findSeamsRight(HashMap<Pixel, SeamInfo> prevSeams,
      HashMap<Pixel, SeamInfo> newSeams) {
    return newSeams;
  }

  // Returns all the seams from the previous row
  public HashMap<Pixel, SeamInfo> findSeamsDown(HashMap<Pixel, SeamInfo> prevSeams) {
    return prevSeams;
  }

  // Drags this node and all nodes to its right one spot to the left
  // Effect: nothing
  public void dragLeft(ANode newUp, ANode newDown) {
    // We have gotten to the end of the row so there is nothing else to drag
  }

  // Returns true as this means the Graph is empty
  public boolean emptyHelper() {
    return true;
  }

}

//Represents a Pixel as a node in a graph
class Pixel extends ANode {
  Color color;
  int x;
  int y;
  static int z;

  Pixel() {
    this.topLeft = new Border();
    this.up = new Border();
    this.topRight = new Border();
    this.right = new Border();
    this.downRight = new Border();
    this.down = new Border();
    this.downLeft = new Border();
    this.left = new Border();
  }

  Pixel(Color color, int x, int y) {
    this.color = color;
    this.x = x;
    this.y = y;
  }

  Pixel(ANode topLeft, ANode up, ANode topRight, ANode right, ANode downRight, ANode down,
      ANode downLeft, ANode left, Color color, int x, int y) {
    this.x = x;
    this.y = y;
    this.color = color;

    topLeft.downRight = this;
    this.topLeft = topLeft;

    up.down = this;
    this.up = up;

    topRight.downLeft = this;
    this.topRight = topRight;

    right.left = this;
    this.right = right;

    downRight.topLeft = this;
    this.downRight = downRight;

    down.up = this;
    this.down = down;

    downLeft.topRight = this;
    this.downLeft = downLeft;

    left.right = this;
    this.left = left;
  }

  // Returns Pixels energy
  double energy() {
    double vertical = (this.topLeft.brightness() + 2 * this.up.brightness()
        + this.topRight.brightness())
        - (this.downLeft.brightness() + 2 * this.down.brightness() + this.downRight.brightness());
    double horizontal = (this.topLeft.brightness() + 2 * this.left.brightness()
        + this.downLeft.brightness())
        - (this.topRight.brightness() + 2 * this.right.brightness() + this.downRight.brightness());

    return Math.sqrt(Math.pow(vertical, 2) + Math.pow(horizontal, 2));
  }

  // Returns pixels brightness
  public double brightness() {
    return (this.color.getRed() + this.color.getBlue() + this.color.getGreen()) / (255.0 * 3);
  }

  // Drags this Node and every node to its right one step to the left
  // Effect: Sets all six neighbor nodes of this node other than this.left and
  // this.right. Fixes all links
  public void dragLeft(ANode newUp, ANode newDown) {
    this.x = this.x - 1;

    newUp.setDown(this);
    newDown.setUp(this);

    this.setTopLeft(newUp.left);

    this.setTopRight(newUp.right);

    this.setDownLeft(newDown.left);
    this.setDownRight(newDown.right);

    this.right.dragLeft(this.topRight, this.downRight);
  }

  // Constructs a seam from best to this and adds it to newSeams if it has a lower
  // weight than the seam that is already there
  // Effect: Adds seam to newSeams
  public void addTo(HashMap<Pixel, SeamInfo> newSeams, SeamInfo bestSeam) {

    if (!newSeams.containsKey(this)
        || bestSeam.totalWeight + this.energy() < newSeams.get(this).totalWeight) {
      newSeams.put(this, new SeamInfo(this, bestSeam.totalWeight + this.energy(), bestSeam));
    }
  }

  // Paints the pixels in img corresponding to this node and the nodes to its
  // right
  // Effect: Sets pixels in img
  public void paintRight(ComputedPixelImage img, int x, int y) {
    img.setPixel(x, y, this.color);
    this.right.paintRight(img, x + 1, y);
  }

  // Applies paintRight to this Node and every Node below it
  // Effect: sets pixels in img
  public void paintDown(ComputedPixelImage img, int x, int y) {
    this.paintRight(img, x, y);
    this.down.paintDown(img, x, y + 1);
  }

  // Calls INode.findSeamsRight to this Node and every node below it
  public HashMap<Pixel, SeamInfo> findSeamsRight(HashMap<Pixel, SeamInfo> prevSeams,
      HashMap<Pixel, SeamInfo> newSeams) {

    if (!prevSeams.containsKey(this)) {
      prevSeams.put(this, new SeamInfo(this, this.energy(), null));
    }

    this.downLeft.addTo(newSeams, prevSeams.get(this));
    this.down.addTo(newSeams, prevSeams.get(this));
    this.downRight.addTo(newSeams, prevSeams.get(this));

    return this.right.findSeamsRight(prevSeams, newSeams);
  }

  // Calls INode.findSeamsRight to this Node and every node below it
  public HashMap<Pixel, SeamInfo> findSeamsDown(HashMap<Pixel, SeamInfo> prevSeams) {
    return this.down.findSeamsDown(this.findSeamsRight(prevSeams, new HashMap<Pixel, SeamInfo>()));
  }

  // Returns false as the graph is not empty
  public boolean emptyHelper() {
    return false;
  }

  // Removes this Pixel from the graph and "squishes" the hole vertically
  // Effect: Changes links in the graph which this Pixel is in so that it is
  // removed
  void popVertically() {
    this.left.setRight(this.right);
    this.right.dragLeft(this.up, this.down);
  }

}

//Represents a Seam in a graph
class SeamInfo {
  Pixel current;
  double totalWeight;
  SeamInfo cameFrom;

  SeamInfo(Pixel current, double totalWeight, SeamInfo cameFrom) {
    this.current = current;
    this.totalWeight = totalWeight;
    this.cameFrom = cameFrom;
  }

  // Removes all Pixels in this seam from the graph
  // Effect: Removes Pixels from their graph and fixes links
  void removePixels() {
    this.current.popVertically();

    if (this.cameFrom != null) {
      this.cameFrom.removePixels();
    }
  }

  // Draws this seam in red over the given ComputedPixelImage
  // Effect: sets pixels in the given ComputedPixelImage img
  void drawSeam(ComputedPixelImage img) {
    img.setPixel(this.current.x, this.current.y, Color.RED);
    if (this.cameFrom != null) {
      this.cameFrom.drawSeam(img);
    }
  }
}

//Represents an Image as a graph
class Graph {
  ANode topLeft;

  Graph(ANode node) {
    this.topLeft = node;
  }

  Graph(ArrayList<ArrayList<Pixel>> arr) {
    for (int i = 0; i < arr.size(); i++) {
      for (int j = 0; j < arr.get(0).size(); j++) {
        if (i != 0) {
          arr.get(i).get(j).setUp(arr.get(i - 1).get(j));
          if (j != 0) {
            arr.get(i).get(j).setTopLeft(arr.get(i - 1).get(j - 1));
          }
        }
        if (j != 0) {
          arr.get(i).get(j).setLeft(arr.get(i).get(j - 1));
          if (i != arr.size() - 1) {
            arr.get(i).get(j).setDownLeft(arr.get(i + 1).get(j - 1));
          }
        }
        if (i != arr.size() - 1) {
          arr.get(i).get(j).setDown(arr.get(i + 1).get(j));
          if (j != arr.get(0).size() - 1) {
            arr.get(i).get(j).setDownRight(arr.get(i + 1).get(j + 1));
          }
        }

        if (j != arr.get(0).size() - 1) {
          arr.get(i).get(j).setRight(arr.get(i).get(j + 1));
          if (i != 0) {
            arr.get(i).get(j).setTopRight(arr.get(i - 1).get(j + 1));
          }
        }

      }
    }

    this.topLeft = arr.get(0).get(0).topLeft;
  }

  // Returns true if the Graph is empty
  boolean empty() {
    return this.topLeft.empty();
  }

  // Returns the vertical seam with lowest weight
  SeamInfo getVerticalSeam() {
    HashMap<Pixel, SeamInfo> seams = this.topLeft.downRight
        .findSeamsDown(new HashMap<Pixel, SeamInfo>());

    Iterator<SeamInfo> iter = seams.values().iterator();

    // Sets a local variable to store the best SeamInfo found so far
    SeamInfo best = new SeamInfo(null, Integer.MAX_VALUE, null);
    while (iter.hasNext()) {
      // Stores the next SeamInfo from the iterator
      SeamInfo next = iter.next();
      if (next.totalWeight < best.totalWeight) {
        best = next;
      }
    }

    return best;
  }

  // Modifies the given image so that it resembles the graph
  // EFFECT: mutates given img so that it resembles the new graph
  void drawOnImage(ComputedPixelImage img) {
    this.topLeft.downRight.paintDown(img, 0, 0);
  }

}

//Represents a World used to animate Seam Carving
class SeamCarveWorld extends World {
  int width;
  int height;
  Graph graph;

  SeamCarveWorld(String path) {
    FromFileImage src = new FromFileImage(path);
    this.width = (int) src.getWidth();
    this.height = (int) src.getHeight();

    ArrayList<ArrayList<Pixel>> arr = new ArrayList<ArrayList<Pixel>>(this.height);
    for (int i = 0; i < this.height; i++) {
      arr.add(new ArrayList<Pixel>(this.width));
      for (int j = 0; j < this.width; j++) {

        arr.get(i).add(new Pixel(new Border(), new Border(), new Border(), new Border(),
            new Border(), new Border(), new Border(), new Border(), src.getColorAt(j, i), j, i));
      }
    }

    this.graph = new Graph(arr);
  }

  public WorldScene makeScene() {
    WorldScene worldScene = new WorldScene(this.width, this.height);
    ComputedPixelImage img = new ComputedPixelImage(this.width, this.height);
    if (!this.graph.empty()) {
      SeamInfo toBeRemoved = this.graph.getVerticalSeam();

      this.graph.drawOnImage(img);
      toBeRemoved.drawSeam(img);

      worldScene.placeImageXY(img, width / 2, height / 2);

      toBeRemoved.removePixels();

    }
    return worldScene;

  }

}

//Represents a class for examples and testing
class ExamplesSeam {
  Graph empty;
  SeamCarveWorld world = new SeamCarveWorld("balloons.jpeg");
  SeamCarveWorld world2;
  SeamCarveWorld pixelWorld;
  SeamInfo seam1;
  SeamInfo seam2;

  ANode b = new Border();

  Pixel p1 = new Pixel(Color.red, 0, 0);
  Pixel p2 = new Pixel(Color.yellow, 0, 1);
  Pixel p3 = new Pixel(Color.blue, 0, 2);
  Pixel p4 = new Pixel(Color.green, 1, 0);
  Pixel p5 = new Pixel(Color.blue, 1, 1);
  Pixel p6 = new Pixel(Color.green, 1, 2);
  Pixel p7 = new Pixel(Color.pink, 2, 0);
  Pixel p8 = new Pixel(Color.red, 2, 1);
  Pixel p9 = new Pixel(Color.green, 2, 2);

  Pixel middle;
  Pixel topLeft;
  Pixel up;
  Pixel topRight;
  Pixel right;
  Pixel downRight;
  Pixel down;
  Pixel downLeft;
  Pixel left;

  // Resets all fields to what they should be
  // Effect: Changes fields of this class
  void initData() {
    this.p5.topLeft = this.p1;
    this.p5.up = this.p2;
    this.p5.topRight = this.p3;

    this.p5.left = this.p4;
    this.p5.right = this.p6;

    this.p5.downLeft = this.p7;
    this.p5.down = this.p8;
    this.p5.downRight = this.p9;

    ComputedPixelImage img = new ComputedPixelImage(3, 3);
    img.setPixels(0, 0, 3, 3, Color.blue);
    img.setPixel(1, 1, Color.red);

    img.saveImage("pixelImg.jpeg");

    this.pixelWorld = new SeamCarveWorld("pixelImg.jpeg");
    this.world2 = new SeamCarveWorld("test5");

    this.topLeft = new Pixel(Color.red, 0, 0);
    this.up = new Pixel(Color.red, 0, 0);
    this.topRight = new Pixel(Color.red, 0, 0);
    this.right = new Pixel(Color.red, 0, 0);
    this.downRight = new Pixel(Color.red, 0, 0);
    this.down = new Pixel(Color.red, 0, 0);
    this.downLeft = new Pixel(Color.red, 0, 0);
    this.left = new Pixel(Color.red, 0, 0);
    this.middle = new Pixel(Color.red, 0, 0);

    this.seam1 = new SeamInfo(p1, 0, null);
    this.seam2 = new SeamInfo(p2, 2, seam1);

    this.empty = new Graph(new Border());

  }

  // Invke big bang
  void testBigBang(Tester t) {

    this.world.bigBang(1000, 1000, 0.001);
  }

  void testPixel(Tester t) {
    initData();

    // tests brigtness
    t.checkInexact(
        new Pixel(this.b, this.b, this.b, this.b, this.b, this.b, this.b, this.b, Color.black, 0, 0)
            .brightness(),
        0.0, 0.001);
    t.checkInexact(
        new Pixel(this.b, this.b, this.b, this.b, this.b, this.b, this.b, this.b, Color.red, 0, 0)
            .brightness(),
        0.3333333333333333, 0.001);
    t.checkInexact(
        new Pixel(this.b, this.b, this.b, this.b, this.b, this.b, this.b, this.b, Color.blue, 0, 0)
            .brightness(),
        0.3333333333333333, 0.001);
    t.checkInexact(new Pixel(this.b, this.b, this.b, this.b, this.b, this.b, this.b, this.b,
        new Color(10, 20, 30), 0, 0).brightness(), 0.0784, 0.001);

    // tests energy
    t.checkInexact(this.p5.energy(), 0.503, 0.001);

    // tests dragLeft
    ANode leftMiddle = this.pixelWorld.graph.topLeft.downRight.down;
    ANode middle = this.pixelWorld.graph.topLeft.downRight.downRight;

    middle.left = leftMiddle.left;
    leftMiddle.left.right = middle;
    middle.dragLeft(middle.up, middle.down);

    ANode leftMiddleTop = this.pixelWorld.graph.topLeft.downRight;
    ANode middleTop = this.pixelWorld.graph.topLeft.downRight.right;

    middleTop.left = leftMiddleTop.left;
    leftMiddleTop.left.right = middleTop;
    middleTop.dragLeft(middleTop.up, middleTop.down);

    ANode leftMiddleDown = this.pixelWorld.graph.topLeft.downRight.down.down;
    ANode middleDown = this.pixelWorld.graph.topLeft.downRight.down.down.right;

    middleDown.left = leftMiddleDown.left;
    leftMiddleDown.left.right = middleDown;
    middleDown.dragLeft(middleDown.up, middleDown.down);
  }

  // Test Node setters
  void testNodeSetters(Tester t) {
    this.initData();

    this.middle.setTopLeft(this.topLeft);
    this.middle.setUp(this.up);
    this.middle.setTopRight(this.topRight);
    this.middle.setRight(this.right);
    this.middle.setDownRight(this.downRight);
    this.middle.setDown(this.down);
    this.middle.setDownLeft(this.downLeft);
    this.middle.setLeft(this.left);

    t.checkExpect(this.middle.topLeft, this.topLeft);
    t.checkExpect(this.topLeft.downRight, this.middle);

    t.checkExpect(this.middle.up, this.up);
    t.checkExpect(this.up.down, this.middle);

    t.checkExpect(this.middle.topRight, this.topRight);
    t.checkExpect(this.topRight.downLeft, this.middle);

    t.checkExpect(this.middle.right, this.right);
    t.checkExpect(this.right.left, this.middle);

    t.checkExpect(this.middle.downRight, this.downRight);
    t.checkExpect(this.downRight.topLeft, this.middle);

    t.checkExpect(this.middle.downLeft, this.downLeft);
    t.checkExpect(this.downLeft.topRight, this.middle);

    t.checkExpect(this.middle.left, this.left);
    t.checkExpect(this.left.right, this.middle);

  }

  // Test empty
  void testEmpty(Tester t) {
    this.initData();
    t.checkExpect(this.empty.empty(), true);
    t.checkExpect(this.world.graph.empty(), false);
  }

  // Test paint
  void testPaint(Tester t) {
    this.initData();
    // Test paintRight
    ComputedPixelImage img = new ComputedPixelImage(5, 5);
    this.world2.graph.drawOnImage(img);
    ComputedPixelImage img2 = new ComputedPixelImage(5, 5);
    FromFileImage src = new FromFileImage("test5");
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        img2.setPixel(i, j, src.getColorAt(j, i));
      }
    }

    t.checkExpect(img, img2);

  }

}
