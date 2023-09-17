import java.io.Serializable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tile implements Serializable{

  //List of all usable tiles
  final static TileBase[] tileList = new TileBase[]{
    new TileBase("void", false),
    new TileBase("grass", true),
    new TileBase("wall", false),
    new TileBase("dirt", true),
    new TileBase("sand", true)
  };

  //instance variables
  public final TileBase base;
  private final double height;
  private Actor actor;
  
  public Tile(int tileListIndex, double height){
    base = tileList[tileListIndex];
    this.height = height;
  }
  public void draw(int x, int y, Graphics g){
    g.drawImage(base.image, x, y, Main.tileSize, Main.tileSize, null);
  }
  public boolean hasActor(){
    return (actor != null);
  }
}

class TileBase implements Serializable{
  
  //instance variables
  public final String name;
  public final boolean passable;
  public BufferedImage image;
  
  public TileBase(String name, boolean passable){
    this.name = name;
    this.passable = passable;

    //sets tile image
    try{
      image = ImageIO.read(getClass().getResourceAsStream("sprites/" + name + ".png"));
    } catch(IOException e){
      try{
        image = ImageIO.read(getClass().getResourceAsStream("sprites/void.png"));
      } catch(IOException e2){
        e2.printStackTrace();
      }
      e.printStackTrace();
    }
  }
}
