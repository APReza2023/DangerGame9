import java.io.Serializable;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Actor{

  final static int tileSize = Main.tileSize;
  
  Room currentRoom;
  String name;
  int x,y;
  protected int width,height;
  public boolean passable;
  BufferedImage image;
  
  public Actor(String name, int x, int y, boolean passable, Room currentRoom){
    this.name = name;
    this.x = x;
    this.y = y;
    width = tileSize;
    height = tileSize;
    this.passable = passable;
    this.currentRoom = currentRoom;
  }

  public void update(){}
  public void render(Graphics g){
    if((image != null))
      g.drawImage(image, x, y, Main.tileSize, Main.tileSize, null);
  }

  public void interact(){}
  public void onLoad(){}

  public String getName(){
    return name;
  }
  public int getX(){
    return x;
  }
  public int getY(){
    return y;
  }
  public int getWidth(){
    return width;
  }
  public int getHeight(){
    return height;
  }
  public void setRoom(Room room){
    currentRoom = room;
  }
  public void setX(int x){
    this.x = x;
  }
  public void addX(int x){
    this.x += x;
  }
  public void setY(int y){
    this.y = y;
  }
  public void addY(int y){
    this.y += y;
  }
  public void setWidth(int w){
    this.width = w;
  }
  public void setHeight(int h){
    this.height = h;
  }
  public void setPassable(boolean passable){
    this.passable = passable;
  }
  public void setSprite(String s){
    try{
      image = ImageIO.read(getClass().getResourceAsStream("sprites/" + s + ".png"));
    } catch(IOException e){
      System.out.println("Sprite unable to load: " + s);
      e.printStackTrace();
    }
  }
  public void setSprite(BufferedImage s){
    image = s;
  }
  public boolean isColliding(int x, int y){
    return (
      x > this.x && x < (this.x + width-1) ||
      y > this.y && y < (this.y + width-1)
    );
  }
  public boolean isColliding(Actor a){
    if(a.passable) return false;
    return (
      x >= a.x && x < (a.x + a.width-1) &&
      y <= a.y && y > (a.y - a.height+1) ||

      x + width-1 > a.x && x < (a.x + a.width-1) &&
      y < a.y && y > (a.y - a.height+1) ||
      
      x > a.x && x < (a.x + a.width-1) &&
      y - height+1 < a.y && y > (a.y - a.height+1) ||
      
      x + width-1 > a.x && x < (a.x + a.width-1) &&
      y - height+1 < a.y && y > (a.y - a.height+1)
    );
  }
  public boolean isColliding(Entity e){
    if(e.passable) return false;
    return distanceFrom(e) <= e.width;
  }
  public double distanceFrom(Actor a){
    int dis = (a.x - x)*(a.x - x) + (a.y - y)*(a.y - y);
    return Math.pow(dis, 0.5);
  }
}

class Door extends Actor{
  
  private String dir;
  
  public Door(int x, int y, Room currentRoom, String dir){
    super("Door", x, y, false, currentRoom);
    this.dir = dir;

    try{
      image = ImageIO.read(getClass().getResourceAsStream("sprites/door.png"));
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  public void interact(){
    Floor floor = Main.floor;
    switch(dir){
      case "north":
        floor.moveNorth();
        floor.setPlayerRoomPos(8 * tileSize, 14 * tileSize);
        break;
      case "south":
        floor.moveSouth();
        floor.setPlayerRoomPos(8 * tileSize, 1 * tileSize);
        break;
      case "west":
        floor.moveWest();
        floor.setPlayerRoomPos(14 * tileSize, 8 * tileSize);
        break;
      case "east":
        floor.moveEast();
        floor.setPlayerRoomPos(1 * tileSize, 8 * tileSize);
        break;
    }
  }
}

class Exit extends Actor{

    BufferedImage open;
  public Exit(int x, int y, Room currentRoom){
    super("exit", x, y, true, currentRoom);
    
    try{
      image = ImageIO.read(getClass().getResourceAsStream("sprites/door.png"));
      open = ImageIO.read(getClass().getResourceAsStream("sprites/void.png"));
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  public void interact(){
    if(Main.enemiesDefeated == 0){
      newFloor();
    }
    else if(Main.player.items.size() > 0){
      newFloor();
      Main.player.items.remove(0);
    }
  }
  private void newFloor(){
      Main.floorNum++;
    if(Main.floorNum == 10)
      Main.floor = FloorManager.createBossFloor();
    else
      Main.floor = new Floor();
    Main.floor.loadCurrentRoom();
  }
  public void onLoad(){
    if(Main.enemiesDefeated == 0){
      image = open;
    }
  }
}
class ItemActor extends Actor{

  ItemScript script;
  
  public ItemActor(int x, int y, Room currentRoom, String name){
    super(name, x, y, true, currentRoom);
    setSprite(name);
    script = new ItemScript();
  }
  public void interact(){
    script.pickup(this);
    Main.player.addItem(this);
  }
}
/*
class DogEater extends Actor{

  Player player = Main.player;
  
  public DogEater(int x, int y, Room currentRoom){
    super("dogEater", x, y, true, currentRoom);
  }
  public void interact(){
    if(player.captured != null){
      Main.enemiesDefeated--;
      player.captured = null;
      
    }
  }
}
*/
