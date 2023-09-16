import java.util.ArrayList;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Menu{

  public static final MainMenu MAINMENU = new MainMenu();
  public static final Interact INTERACT = new Interact();
  public static void updatePrimary(){
    pause = false;
    
    MAINMENU.update();
    //INTERACT.update();
  }

  public static final Menu[] MenuList = {MAINMENU, INTERACT};

  public static boolean pause;

  InputHandler in;
  
  final int WIDTH;
  final int HEIGHT;
  int x, y;
  protected boolean visible = false;

  ArrayList<MenuItem> items = new ArrayList<MenuItem>();
  
  public Menu(int width, int height){
    this.WIDTH = width;
    this.HEIGHT = height;
    in = Main.in;
  }

  public void setX(int x){
    this.x = x;
  }
  public void setY(int y){
    this.y = y;
  }
  public void setActive(boolean ac){
    visible = ac;
  }
  public boolean getVisible(){
    return visible;
  }

  public void render(Graphics g){}

}

class MenuItem{
  int x, y;
  public void draw(Graphics g){
    g.drawString("temp", 1, 1);
  }
}

class MainMenu extends Menu{
  int pos;
  public MainMenu(){
    super(Main.screenWidth, Main.screenHeight);
    pos = 0;
  }

  public void update(){
    if(!visible) return;
    pause = true;
    if (in.key[KeyEvent.VK_O]) {
      visible = false;
    }
  }
  public void render(Graphics g){
    if(!visible) return;
    g.fillRect(0,0, WIDTH, HEIGHT);
    g.setColor(Color.white);
    g.setFont(new Font("Arial Black", Font.BOLD, 50));
    g.drawString("Main Menu", x, y+50);
    g.setFont(new Font("Arial Black", Font.BOLD, 20));
    g.drawString("Start", x, y+70);
    g.drawString("Nothing here", x, y+90);
  }
}

class Interact extends Menu{

  private ArrayList<Actor> interactables;
  public Interact(){
    super(50, 50);
  }

  public void run(ArrayList<Actor> interactables){
    visible = true;
    this.interactables = interactables;
  }
  private boolean lastPress = false;
  public void update(){
    if(!visible) return;
    if (in.key[KeyEvent.VK_O]) {
      visible = false;
    }

    //object selection
    if(in.mousePressing){
      lastPress = true;
    }
    else if(lastPress){
      lastPress = false;
      System.out.println("last press");
      //mouse location interpretation
      if(in.mouseX < 80){
        int num = (in.mouseY-65) / 20;

        visible = false;
        interactables.get(num).interact();
      }
    }
  }
  public void render(Graphics g){
    if(!visible) return;
    g.setColor(Color.white);
    g.setFont(new Font("Arial Black", Font.BOLD, 50));
    g.drawString("Interactables", x, y+50);

    //draw all interactable actors
    for(int i = 0; i < interactables.size(); i++){
      g.setFont(new Font("Arial Black", Font.BOLD, 20));
      g.drawString(interactables.get(i).getName(), x, y+65+(i*20));
    }
  }
}
