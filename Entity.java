import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Entity extends Actor{

  protected int hp;
  protected int speed;
  private Script script;

  private EntityBase base;
  
  public Entity(int x, int y, Room currentRoom, int baseIndex){
    super("Entity", x, y, false, currentRoom);
    base = EntityBase.entityList[baseIndex];
    name = base.name;
    hp = base.hp;
    speed = base.speed * tileSize/16;
    script = base.script;
    image = base.sprite;
  }

  public void addHp(int hp){
    this.hp += hp;
  }
  public void setScript(Script script){
    this.script = script;
  }
  public void moveUp(){
    //tile collision
    if(!currentRoom.tile[(x)/tileSize][(y - speed)/tileSize].base.passable || !currentRoom.tile[(x + tileSize - 1)/tileSize][(y - speed)/tileSize].base.passable){
      return;
    }
    y -= speed;//move
    if(willCollide()) y += speed;

    //check for out of bounds
    if(y < 0){
      y = 0;
    }
  }
  public void moveDown(){
    //tile collision
    if(!currentRoom.tile[(x)/Main.tileSize][(y + Main.tileSize + speed - 1)/Main.tileSize].base.passable || !currentRoom.tile[(x + Main.tileSize - 1)/Main.tileSize][(y + Main.tileSize + speed - 1)/Main.tileSize].base.passable){
      return;
    }
    y += speed;
    if(willCollide()) y -= speed;

    //check for out of bounds
    if(y >= currentRoom.HEIGHT * tileSize-height-1){
      y = currentRoom.HEIGHT * tileSize-1-height;
    }
  }
  public void moveLeft(){
    if(!currentRoom.tile[(x - speed)/Main.tileSize][(y)/Main.tileSize].base.passable || !currentRoom.tile[(x - speed)/Main.tileSize][(y + Main.tileSize - 1)/Main.tileSize].base.passable){
      return;
    }
    x -= speed; //move
    if(willCollide()) x += speed;

    if(x < 0){
      x = 0;
    }
  }
  public void moveRight(){
    if(!currentRoom.tile[(x + Main.tileSize + speed - 1)/Main.tileSize][(y)/Main.tileSize].base.passable || !currentRoom.tile[(x + Main.tileSize + speed - 1)/Main.tileSize][(y + Main.tileSize - 1)/Main.tileSize].base.passable){
      return;
    }
    x += speed;
    if(willCollide()) x -= speed;
    
    if(x >= currentRoom.WIDTH * Main.tileSize-width-1){
      x = currentRoom.WIDTH * Main.tileSize-1-width;
    }
  }
  private boolean willCollide(){
    //returns if this entity collides with any actor or entity in its level
    if(passable) return false;
    
    if(currentRoom.getPlayer() != this && isColliding(currentRoom.getPlayer()))
      return true;
    
    for(Actor actor : currentRoom.getActors()){
      if(actor == this) continue;
      if(isColliding(actor)){
        return true;
      }
    }
    for(Entity entity : currentRoom.getEntities()){
      if(entity == this) continue;
      if(isColliding(entity)){
        return true;
      }
    }
    return false;
  }

  
  public void update(){
    script.execute(this);
    if(hp <= 0){
      script.despawn(this);
    }
  }
  public void render(Graphics g){
    if((image != null))
      g.drawImage(image, x, y, Main.tileSize, Main.tileSize, null);
    else{
      g.setColor(Color.green);
      g.fillRect(x, y, Main.tileSize, Main.tileSize);
    }
  }
}
