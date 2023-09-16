import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Scanner;

class InputHandler implements KeyListener, MouseListener, MouseMotionListener, FocusListener{
  
  public boolean[] key = new boolean[400];

  public int mouseX;
  public int mouseY;
  public boolean mousePressing;

  private Scanner scan;

  @Override
//KeyListener
  public void keyTyped(KeyEvent e){}
  public void keyPressed(KeyEvent e){
    int code = e.getKeyCode();
    key[code] = true;
  }
  public void keyReleased(KeyEvent e){
    int code = e.getKeyCode();
    key[code] = false;
  }

//MouseListener
  public void mouseExited(MouseEvent e){}
  public void mouseClicked(MouseEvent e){}
  public void mousePressed(MouseEvent e){
    mousePressing = true;
    System.out.println(mouseX + ", " + mouseY);
  }
  public void mouseReleased(MouseEvent e){
    mousePressing = false;
  }
  public void mouseEntered(MouseEvent e){}

//MouseMotionListener
  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){
    mouseX = e.getX();
    mouseY = e.getY();
  }
  
//FocusListener
  public void focusGained(FocusEvent e){}
  public void focusLost(FocusEvent e){}

//Scanner
  public String nextLine(){
    return scan.nextLine();
  }
  public int nextInt(){
    return scan.nextInt();
  }
}
