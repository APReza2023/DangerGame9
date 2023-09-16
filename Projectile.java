public class Projectile extends Entity{

  private final int lastDirection;
  
  public Projectile(int x, int y, Room currentRoom){
    super(x, y, currentRoom, 1);
    lastDirection = Main.player.lastDirection;
  }

  public void update(){
    int x = getX();
    int y = getY();
    switch(lastDirection){
      case 0:
        moveUp();
        break;
      case 1:
        moveLeft();
        break;
      case 2:
        moveDown();
        break;
      case 3:
        moveRight();
        break;
        
    }
    if(x == getX() && y == getY()){
      
      for(Entity e : currentRoom.getEntities()){
        if(distanceFrom(e) < 20 * Main.scale){
          e.addHp(-10);
        }
      }

      //despawn this entity
      Main.addMessage("despawn");
      Main.addMessage(toString());
      
    }
    
  }
}
