import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Script{

  public void execute(Entity entity){}

  //generic script events
  public void despawn(Entity e){
    Main.addMessage("despawn");
    Main.addMessage(e.toString());
  }
  public void despawn(Actor e){
    Main.addMessage("despawnAct");
    Main.addMessage(e.toString());
  }
}

class GenericAi extends Script{
  public void execute(Entity entity){
    switch((int)(Math.random() * 8)){
      case 0:
        entity.moveRight();
        break;
      case 1:
        entity.moveLeft();
        break;
      case 2:
        entity.moveUp();
        break;
      case 3:
        entity.moveDown();
        break;
    }
  }
  public void despawn(Entity e){
    super.despawn(e);
    Main.enemiesDefeated--;
  }
}
class DirectedAi extends Script{
  int tox = -1;
  int toy = -1;
  final int tileSize = Main.tileSize;
  public void execute(Entity e){
    if(tox == -1) 
      tox = tileSize+1 + (int)(Math.random()*tileSize*13-2);
    if(toy == -1) 
      toy = tileSize+1 + (int)(Math.random()*tileSize*13-2);
    //x coor
    int x = e.getX();
    if(x < tox-4)
      e.moveRight();
    else if(x > tox+4)
      e.moveLeft();
    else
      tox = -1;
    //y coord
    int y = e.getY();
    if(y < toy-4)
      e.moveDown();
    else if(y > toy+4)
      e.moveUp();
    else
      toy = -1;
  }
  public void despawn(Entity e){
    super.despawn(e);
    Main.enemiesDefeated--;
  }
}

class Attack extends Script{

  int lifeFrames = 15;

  public void execute(Entity e){
    lifeFrames--;

    //deletes any entities hit
    for(Entity target : e.currentRoom.getEntities()){
      if(target != e && target.isColliding(e)){
        despawn(target);
      }
    }

    if(lifeFrames <= 0){
      despawn(e);
    }
  }
}

class Follower extends Script{
  
  private Entity following;
  
  public Follower(Entity following){
    this.following = following;
  }

  public void execute(Entity entity){
    //checks if entity is close enough to whatever its following
    if(entity.distanceFrom(following) < 24)
      return;

    //move towards entity
    if(following.getX() > entity.getX())
      entity.moveRight();
    else
      entity.moveLeft();
    if(following.getY() < entity.getY())
      entity.moveUp();
    else
      entity.moveDown();
  }
}

class Spawner extends Script{
  
  int timer = 0;
  
  public Spawner(){}

  public void execute(Entity entity){
    timer++;
    if(timer > 60){
      timer -= 60;
      Main.addMessage("spawn");
    }
  }
}
class NPC extends Script{

  BufferedImage left;
  BufferedImage right;
  
  public NPC(){
    //sprite setting
    try{
      left = ImageIO.read(getClass().getResourceAsStream("sprites/player/player.png"));
      right = ImageIO.read(getClass().getResourceAsStream("sprites/player/playerRight.png"));
    } catch(IOException e){
      System.out.println("Sprite unable to load: player");
      e.printStackTrace();
    }
  }
  public void execute(Entity e){
    if(Main.player.getX() > e.getX())
      e.setSprite(right);
    else
      e.setSprite(left);
  }
}
class ItemScript extends Script{
  
  public void pickup(Actor e){
    despawn(e);
  }
}
