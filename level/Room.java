import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

public class Room implements Serializable{

  //instance variables
  public static final int WIDTH = 16;
  public static final int HEIGHT = 16;
  public Tile[][] tile;

  private ArrayList<Actor> actors;
  private ArrayList<Entity> entities;
  private Player player;
  
  public Room(int width, int height){
    tile = new Tile[WIDTH][HEIGHT];
    actors = new ArrayList<Actor>();
    entities = new ArrayList<Entity>();
    player = Main.player;

    //intitial tile conditions
    for(int y = 0; y < HEIGHT; y++){
      for(int x = 0; x < WIDTH; x++){
        setTile(x, y, new Tile(0,0));
      }
    }
  }

  public void setTile(int x, int y, Tile tile){
    this.tile[x][y] = tile;
  }
  public void addActor(Actor a){
    actors.add(a);
  }
  public ArrayList<Actor> getActors(){
    return actors;
  }
  public void addEntity(Entity e){
    entities.add(e);
  }
  public ArrayList<Entity> getEntities(){
    return entities;
  }
  public Player getPlayer(){
    return player;
  }
  
  //util methods
  public void update(){
    //updates all actors and entities
    player.update();
    for(Actor actor : actors){
      actor.update();
    }
    for(Entity entity : entities){
      entity.update();
    }
  }
  public void render(Graphics g){
    //draw level
    for(int y = 0; y < HEIGHT; y++){
      for(int x = 0; x < WIDTH; x++){
        tile[x][y].draw(x*Main.tileSize, y*Main.tileSize, g);
      }
    }

    //draw actors
    for(Actor actor : actors){
      actor.render(g);
    }
    for(Entity entity : entities){
      entity.render(g);
    }
    //draw player
    player.render(g);
  }
  public void onLoad(){
    //load actors
    for(Actor actor : actors){
      actor.onLoad();
    }
    for(Entity entity : entities){
      entity.onLoad();
    }
  }
}
