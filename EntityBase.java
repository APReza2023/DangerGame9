import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EntityBase{

  public static EntityBase[] entityList = {
    new EntityBase("dog", 20, 4, true, new GenericAi()),
    new EntityBase("projectile", 1, 4, false, null),
    new EntityBase("projectile", 1, 0, false, new Attack()),
    new EntityBase("player/player", 20, 4, false, null),
    new EntityBase("cerberDog", 40, 2, true, new DirectedAi()),
    new EntityBase("player/player", 20, 4, false, new NPC())
  };

  final String name;
  final int hp;
  final int speed;
  final boolean isEnemy;

  BufferedImage sprite;
  Script script;
  
  public EntityBase(String name, int hp, int speed, boolean isEnemy, Script script){
    this.name = name;
    this.hp = hp;
    this.speed = speed;
    this.isEnemy = isEnemy;
    this.script = script;

    try{
      sprite = ImageIO.read( getClass().getResourceAsStream("sprites/" + name + ".png"));
    } catch(IOException e){
      e.printStackTrace();
    }
  }
}
