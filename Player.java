import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Player extends Entity{

  InputHandler in;

  //sprites
  BufferedImage left;
  BufferedImage right;

  static int lastDirection = 0;
  static int interactCooldown = 0;
  static int projectileCooldown = 0;
  static int attackCooldown = 0;
  
  private static int roomX, roomY;

  static Entity captured;
  static ArrayList<ItemActor> items = new ArrayList<ItemActor>();
  
  public Player(){
    super(0, 0, null, 3);
    in = Main.in;
    passable = false;

    //sprite setting
    try{
      left = ImageIO.read(getClass().getResourceAsStream("sprites/player/player.png"));
      right = ImageIO.read(getClass().getResourceAsStream("sprites/player/playerRight.png"));
    } catch(IOException e){
      System.out.println("Sprite unable to load: player");
      e.printStackTrace();
    }
  }

  public void update(){
    //movement and collision. checks if the tile being moved to is passable
    if(currentRoom == null) 
      currentRoom = Main.floor.getCurrentRoom();
    if(in.key[KeyEvent.VK_W]){
      lastDirection = 0;
      moveUp();
    }
    if(in.key[KeyEvent.VK_A]){
      lastDirection = 1;
      moveLeft();
    }
    if(in.key[KeyEvent.VK_S]){
      lastDirection = 2;
      moveDown();
    }
    if(in.key[KeyEvent.VK_D]){
      lastDirection = 3;
      moveRight();
    }

    //interact
    if(in.key[KeyEvent.VK_E] && interactCooldown <= 0){
      
      interactCooldown = 15;

      //get the closest actor, within 24 pixels
      Actor interWith = new Actor("interWith", -100, -100, false, null);
      int closestDis = 24 * tileSize/16;
      for(Actor actor : currentRoom.getActors()){
        if(distanceFrom(actor) < closestDis){
          interWith = actor;
          closestDis = (int)distanceFrom(actor);
        }
      }
      if(closestDis != 24)
        interWith.interact();
    }

    //fire projectile
    if(in.key[KeyEvent.VK_R] && projectileCooldown <= 0){
      spawnProjectile();
      projectileCooldown = 30;
    }
    
    //attack
    if(in.key[KeyEvent.VK_C] && attackCooldown <= 0){
      spawnFollower();
      attackCooldown = 30;
    }

    //capture
    if(in.key[KeyEvent.VK_X] && attackCooldown <= 0){
      if(captured == null)
        attemptCapture();
      else
        releaseCapture();
      attackCooldown = 30;
    }

    //menu activation
    if(in.key[KeyEvent.VK_I]){  
      Menu.MAINMENU.setActive(true);
    }

    //cooldown countdown
    interactCooldown--;
    projectileCooldown--;
    attackCooldown--;
  }
  public void render(Graphics g){
    if(lastDirection < 2)
      g.drawImage(left, x, y, Main.tileSize, Main.tileSize, null);
    else
      g.drawImage(right, x, y, Main.tileSize, Main.tileSize, null);
  }

  //unique player methods
  private void spawnProjectile(){
    Projectile projectile = new Projectile(x, y, currentRoom);

    //offset
    switch(lastDirection){
      case 0:
        projectile.addY(-Main.tileSize);
        break;
      case 1:
        projectile.addX(-Main.tileSize);
        break;
      case 2:
        projectile.addY(Main.tileSize);
        break;
      case 3:
        projectile.addX(Main.tileSize);
        break;
    }
    
    currentRoom.addEntity(projectile);
  }
  private void spawnAttack(){
    Entity attackHitbox = new Entity(x, y, currentRoom, 2);
    //move attack to last direction moved
    if(lastDirection == 0)
      attackHitbox.addY(-16);
    else if(lastDirection == 1)
      attackHitbox.addX(-16);
    else if(lastDirection == 2)
      attackHitbox.addY(16);
    else
      attackHitbox.addX(16);
    
    currentRoom.addEntity(attackHitbox);
  }
  private void spawnFollower(){
    //spawn a follower for the player
    Entity follower = new Entity(x, y, currentRoom, 0);
    follower.setScript(new Follower(this));
    follower.addX(16);
    currentRoom.addEntity(follower);
  }
  private void attemptCapture(){
    //spawn a follower for the player
    for(Entity e: currentRoom.getEntities()){
      if(distanceFrom(e) < 2*tileSize){
        captured = e;
        currentRoom.getEntities().remove(e);
        return;
      }
    }
  }
  private void releaseCapture(){
    //spawn a follower for the player
    captured.setX(x+tileSize);
    captured.setY(y);
    currentRoom.addEntity(captured);
    captured = null;
  }
  public void addItem(ItemActor a){
    items.add(a);
  }

}
