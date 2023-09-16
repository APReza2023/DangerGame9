/*
Author: Andrew Reza
Name: Dog Catcher 2
Version: 9.1.0.0
*/

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.awt.image.BufferStrategy;

public class Main extends Canvas implements Runnable{
    
  //Screen settings
  public static final int originalTileSize = 16; //16x16 tiles
  public static final int scale = 3;
  public static final int tileSize = originalTileSize * scale; //48x48 tile
  public static final int maxScreenCol = 24;
  public static final int maxScreenRow = 16;
  public static final int screenWidth = tileSize * maxScreenCol; //768 pixels
  public static final int screenHeight = tileSize * maxScreenRow; // 576 pixels  

  public static final ArrayList<String> messages = new ArrayList<String>();

  public static InputHandler in = new InputHandler();
  Thread thread;
  int fps = 0;
  ArrayList<String> commandQueue = new ArrayList<String>();
  Scanner scan = new Scanner(System.in);
  
  boolean isRunning = false;
  static Room currentRoom = FloorManager.createHubBase();
  static Room nextRoom;

  static Floor floor;
  static Player player;
  

  //player data
  static int floorNum = 0;
  static int enemiesDefeated = 0;

  public Main(){
    setPreferredSize(new Dimension(screenWidth, screenHeight));
    setBackground(Color.black);
    addKeyListener(in);
    addMouseListener(in);
    addMouseMotionListener(in);
    setFocusable(true);

    Menu.MAINMENU.setActive(false);
  }
  public void start(){
    if(isRunning) return;
    isRunning = true;
    
    player = new Player();
    floor = FloorManager.HubBase();
    
    thread = new Thread(this);
    thread.start();

    System.out.println("Working!");
    while(isRunning){
      handleCommand();
    }
  }
  @Override
  public void run(){
    System.out.println("running");
    int timer = 0;
    long lastTime = System.nanoTime();
    double amountOfTicks = 30.0;
    double ns = 1000000000 / amountOfTicks;
    double delta = 0;
    int tic = 0;
    
    //game loop
    Menu.MAINMENU.setActive(true);
    while(isRunning) {
      long now = System.nanoTime();

      delta += (now - lastTime) / ns;
      timer += (now - lastTime);

      lastTime = now;

      if(delta >= 1) {
        //execute frame
        update();
        render();
        tic++;
        delta--;
      } 
      if(timer >= 1000000000){
        fps = tic;
        tic = 0;
        timer = 0;
      }
    }
  }

  private void update(){

    //run any recieved messages from last frame
    handleMessages();
    
    //menu update
    Menu.updatePrimary();
    if(Menu.pause) return;

    //updates the current level
    floor.update();
  }
  
  private void render(){
    BufferStrategy bs = this.getBufferStrategy();
    if(bs == null){
      createBufferStrategy(3);
      return;
    }
    
    Graphics g = bs.getDrawGraphics();

    floor.render(g);

    //draw menus
    Menu.MAINMENU.render(g);
    Menu.INTERACT.render(g);

    //draw info
    g.fillRect(screenWidth - 64, 0, 64, 60);
    g.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
    g.setColor(Color.YELLOW);
    g.drawString("FPS: " + fps, screenWidth - 64, 10);
    g.drawString("pos: " + player.getX() + ", " + player.getY(), screenWidth - 64, 20);
    g.drawString("floor: " + floorNum, screenWidth - 64, 30);
    g.drawString("enemies: " + enemiesDefeated, screenWidth - 64, 40);
    g.drawString("room: " + floor.playerx + " " + floor.playery, screenWidth - 64, 50);
    g.drawString("keys: " + player.items.size(), screenWidth - 64, 60);
        
    g.dispose();
    bs.show();

    
  }

  private static void handleMessages(){
    if(messages.size() == 0) return;
    
    switch(messages.get(0)){

      //spawns generic entity
      case "spawn":
        currentRoom.addEntity(new Entity(80,80, currentRoom, Integer.valueOf(messages.get(1))));
        messages.remove(1);
        break;

      //despawn given entity
      case "despawn":
        for(Entity e : floor.getCurrentRoom().getEntities()){
          if(e.toString().equals(messages.get(1))){
            floor.getCurrentRoom().getEntities().remove(e);
            break;
          }
        }
        messages.remove(1);
        break; //end of despawn
      //despawn given actor
      case "despawnAct":
        for(Actor e : floor.getCurrentRoom().getActors()){
          if(e.toString().equals(messages.get(1))){
            floor.getCurrentRoom().getActors().remove(e);
            break;
          }
        }
        messages.remove(1);
        break; //end of despawn
        /*
      case "spawnKey":
        currentRoom.addActor(new ItemActor(8*tileSize, 8*tileSize, rooms[x][y], "key"));
        break; //end of item
        */
    }//end of switch

    //remove used message
    messages.remove(0);
  }
  public static void addMessage(String s){
    messages.add(s);
  }
  private void handleCommand(){
    switch(nextCommand()){
      case "setpos":
        System.out.print("x:");
player.setX( Integer.valueOf(nextCommand()));
        System.out.print("y:");
player.setY( Integer.valueOf(nextCommand()));
        break;
      case "spawn":
        System.out.print("spawn: ");
        currentRoom.addEntity(new Entity(player.getX(), player.getX(), currentRoom, 0));
        break;
      case "end":
        isRunning = false;
        break;
      case "setfloor":
        floorNum = Integer.valueOf(nextCommand());
      case "setenem":
        enemiesDefeated = Integer.valueOf(nextCommand());
    }
  }
  private String nextCommand(){
    //waits for first command
    if(commandQueue.size() == 0)
      commandQueue.add(scan.nextLine());
    //removes and returns next command
    String c = commandQueue.get(0);
    commandQueue.remove(0);
    return c;
  }


  public static void main(String[] args) {
    JFrame jFrame = new JFrame("Dog Catcher 2");
    Main game = new Main();
    jFrame.setSize(screenWidth, screenHeight);
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jFrame.setResizable(false);
    jFrame.add(game);
    jFrame.pack();
    
    jFrame.setLocationRelativeTo(null);
    jFrame.setVisible(true);
    
    game.start();
  }
}
