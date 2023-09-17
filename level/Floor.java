import java.awt.Graphics;

public class Floor{

  final static int tileSize = Main.tileSize;

  final int LENGTH = 16;
  final int SIZE = LENGTH * LENGTH;

  final Room[][] rooms;

  int playerx, playery;

  public Floor(){
    System.out.println("Making new Floor...");
    rooms = new Room[LENGTH][LENGTH];

    playerx = (int)(Math.random()*LENGTH);
    playery = (int)(Math.random()*LENGTH);
    final int startRoom = playerx + playery*LENGTH;
    final int exitPos;
    
    //select which rooms are available
    rooms[playerx][playery] = FloorManager.createStartRoom();
    for(int j = 0; j < 5; j++){
      //begins a new walk
      System.out.println("Beginning new walk...");
      int roomPos = startRoom;
      for(int i = 0; i < Main.floorNum; i++){
        System.out.println("step " + i);
        switch((int)(Math.random()*4)){
          case 0:
            if(roomPos/LENGTH != 15)
              roomPos += LENGTH;
            break;
          case 1:
            if(roomPos/LENGTH != 0)
              roomPos -= LENGTH;
            break;
          case 2:
            if(roomPos%LENGTH != 15)
              roomPos += 1;
            break;
          case 3:
            if(roomPos%LENGTH != 0)
              roomPos -= 1;
            break;
        }
        if(rooms[roomPos%LENGTH][roomPos/LENGTH] != null) 
          continue;
        rooms[roomPos%LENGTH][roomPos/LENGTH] = new Room(16, 16);
      }
    }


    //add the exit to a random valid room
    System.out.println("Creating exit...");
    int attemptExitPos = (int)(Math.random()*SIZE);
    while(rooms[attemptExitPos%LENGTH][attemptExitPos/LENGTH] == null){
      System.out.println("failed exit");
      attemptExitPos = (int)(Math.random()*SIZE);  
    }
    System.out.println("success exit");
    exitPos = attemptExitPos;
    Room tempExit = rooms[attemptExitPos%LENGTH][attemptExitPos/LENGTH];
    tempExit.addActor(new Exit(8*tileSize, 8*tileSize, tempExit));

    
    //sets each room up
    System.out.println("Making doors...");
    for(int y = 0; y < LENGTH; y++){
      for(int x = 0; x < LENGTH; x++){
        if(rooms[x][y] == null) continue;

        boolean west = (x != 0 && rooms[x-1][y] != null);
        boolean north = (y != 0 && rooms[x][y-1] != null);
        boolean east = (x != LENGTH-1 && rooms[x+1][y] != null);
        boolean south = (y != LENGTH-1 && rooms[x][y+1] != null);
        int doorCount = 0;
        
        if(west){//if the room to the west exists
          rooms[x][y].addActor(new Door(0 * tileSize, 8 * tileSize, rooms[x][y], "west"));
          doorCount++;
        }

        //adds each room's door
        if(north){//if the room to the north exists
          rooms[x][y].addActor(new Door(8 * tileSize, 0 * tileSize, rooms[x][y], "north"));
          doorCount++;
        }
        
        if(east){//if the room to the east exists
          rooms[x][y].addActor(new Door(15 * tileSize, 8 * tileSize, rooms[x][y], "east"));
          doorCount++;
        }
        
        if(south){//if the room to the south exists
          rooms[x][y].addActor(new Door(8 * tileSize, 15 * tileSize, rooms[x][y], "south"));
          doorCount++;
        }

        //sets the base tiles of each valid room
        if(doorCount == 1 || (x == playerx && y == playery)){
          rooms[x][y].tile = FloorManager.roomBases[0].tile;
        }
        else if(west && east && !(south || north))
          rooms[x][y].tile = FloorManager.roomBases[3].tile;
        else if(south && north && !(west || east))
          rooms[x][y].tile = FloorManager.roomBases[5].tile;
        else if(doorCount == 3){
          //turns and t shapes hallways
          if(!south)
            rooms[x][y].tile = FloorManager.roomBases[7].tile;
          else if(!north)
            rooms[x][y].tile = FloorManager.roomBases[2].tile;
          else if(!east)
            rooms[x][y].tile = FloorManager.roomBases[8].tile;
          else
            rooms[x][y].tile = FloorManager.roomBases[9].tile;
        }
        else if(doorCount == 2){
          rooms[x][y].tile = FloorManager.roomBases[0].tile;
        }
        else{
          double randNum = Math.random();
          if(randNum > 0.5){
            rooms[x][y].tile = FloorManager.roomBases[4].tile;
          }
          else if(doorCount == 4){
            rooms[x][y].tile = FloorManager.roomBases[1].tile;
          }
          else{
            rooms[x][y].tile = FloorManager.roomBases[0].tile;
          }
        } 

        //sets up each room's actors and enemies
        double randActor = Math.random();
        if((x + y*LENGTH) != exitPos){
          
          if(randActor < 0.15){
            if(Main.floorNum < 5)
              rooms[x][y].addEntity(new Entity(8*tileSize, 8*tileSize, rooms[x][y], 0));
            else
              rooms[x][y].addEntity(new Entity(8*tileSize, 8*tileSize, rooms[x][y], 4));
            Main.enemiesDefeated++;
          }
          else if(doorCount == 1 && randActor < 0.3){
            rooms[x][y].addActor(new Entity(8*tileSize, 8*tileSize, rooms[x][y], 5));
          }
          else if(doorCount == 1 && randActor < 0.6){
            rooms[x][y].addActor(new ItemActor(8*tileSize, 8*tileSize, rooms[x][y], "key"));
          }
          
        }//end of not exitpos statement
      }//end of room setup inner loop
    }

    

    

    //prints room to console for debug
    for(int y = 0; y < LENGTH; y++){
      for(int x = 0; x < LENGTH; x++){
        if(y*LENGTH + x == exitPos){
          System.out.print("X");
        }
        else if(rooms[x][y] == null)
          System.out.print(" ");
        else
          System.out.print("0");
      }
      System.out.println();
    }

    System.out.println("Successfully created new floor");
  }
  public Floor(Room room){
    //allows for a single room to be converted and read as a floor
    rooms = new Room[1][1];
    rooms[0][0] = room;
    playerx = 0;
    playery = 0;
  }
  
  public void update(){
    rooms[playerx][playery].update();
  }
  public void render(Graphics g){
    rooms[playerx][playery].render(g);
  }
  public void loadCurrentRoom(){
    Main.player.setRoom(rooms[playerx][playery]);
    rooms[playerx][playery].onLoad();
  }
  
  public void moveSouth(){
    playery++;
    loadCurrentRoom();
  }
  public void moveNorth(){
    playery--;
    loadCurrentRoom();
  }
  public void moveWest(){
    playerx--;
    loadCurrentRoom();
  }
  public void moveEast(){
    playerx++;
    loadCurrentRoom();
  }

  public Room getCurrentRoom(){
    return rooms[playerx][playery];
  }
  public void setPlayerRoomPos(int x, int y){
    Player player = rooms[playerx][playery].getPlayer();
    player.setX(x);
    player.setY(y);
  }
}

class FloorManager {

  final static int tileSize = Main.tileSize;

  // random room assingment
  public static Floor HubBase(){
    return new Floor(createHubBase());
  }

  // level bases. create tiles for levels, not populated with entities
  public static Room createHubBase() {
    // initial level tile condiditons
    int width = 16;
    int height = 16;
    int enterx = 15 * 16;
    int entery = 15 * 16;
    Room newRoom = new Room(width, height);

    // sets floor to grass
    for (int i = 0; i < width * height; i++) {
      newRoom.setTile(i % width, i / width, new Tile(1, 0));
    }
    for (int i = 0; i < 5; i++) {
      newRoom.setTile(i + 6, 0, new Tile(0, 0));
      newRoom.setTile(i + 6, 1, new Tile(2, 0));
    }
    newRoom.setTile(5, 0, new Tile(2, 0));
    newRoom.setTile(10, 0, new Tile(2, 0));

    newRoom.addActor(new Exit(8 * tileSize, 1 * tileSize, newRoom));

    return newRoom;
  }
  private static Room createBossRoom() {
    // initial level tile condiditons
    int width = 16;
    int height = 16;
    int enterx = 15 * 16;
    int entery = 15 * 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 0, new Tile(2, 0));
      newRoom.setTile(i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, i, new Tile(2, 0));
      newRoom.setTile(15, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < height - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j, new Tile(1, 0));
      }
    }

      //spawns boss
    newRoom.addActor(new ItemActor(8 * tileSize, 4 * tileSize, newRoom, "beautifulBoy"));

    return newRoom;
  }
  public static Floor createBossFloor(){
    return new Floor(createBossRoom());
  }

  public static Room createStartRoom() {
    // initial level tile condiditons
    int width = 16;
    int height = 16;
    int enterx = 9 * 16;
    int entery = 8 * 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 0, new Tile(2, 0));
      newRoom.setTile(i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, i, new Tile(2, 0));
      newRoom.setTile(15, i, new Tile(2, 0));
    }

    // entities
    Player player = newRoom.getPlayer();
    player.setX(8 * 16);
    player.setY(8 * 16);
    

    return newRoom;
  }



  //Floor bases: square, cross, T-shape, hallway, small square, end
  static Room[] roomBases = {
    squareBase(),
    crossBase(),
    TBaseNorth(),
    horzHallBase(),
    squareSandBase(),
    vertHallBase(),
    smallSquareBase(),
    TBaseSouth(),
    TBaseEast(),
    TBaseWest(),
    //NECorner(),
    //SECorner(),
    //NWCorner(),
    //SWCorner()
  };

  private static Room squareBase(){
    // initial level tile condiditons
    int width = 16;
    int height = 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 0, new Tile(2, 0));
      newRoom.setTile(i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, i, new Tile(2, 0));
      newRoom.setTile(15, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < height - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room crossBase(){
    // initial level tile condiditons
    int width = 16;
    int height = 16;
    int enterx = 9 * tileSize;
    int entery = 8 * tileSize;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < 6; i++) {
      newRoom.setTile(5 + i, 0, new Tile(2, 0));
      newRoom.setTile(5 + i, 15, new Tile(2, 0));

      newRoom.setTile(0, 5 + i, new Tile(2, 0));
      newRoom.setTile(15, 5 + i, new Tile(2, 0));
    }

    // inside walls
    for (int i = 1; i < 6; i++) {
      // north
      newRoom.setTile(5, i, new Tile(2, 0));
      newRoom.setTile(10, i, new Tile(2, 0));
      // south
      newRoom.setTile(5, 10 + i, new Tile(2, 0));
      newRoom.setTile(10, 10 + i, new Tile(2, 0));
      // west
      newRoom.setTile(i, 5, new Tile(2, 0));
      newRoom.setTile(i, 10, new Tile(2, 0));
      // east
      newRoom.setTile(9 + i, 5, new Tile(2, 0));
      newRoom.setTile(9 + i, 10, new Tile(2, 0));
    }
    // floor (east/west)
    for (int i = 1; i < width - 1; i++) {
      for (int j = 6; j < 10; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }
    // floor (north/south)
    for (int i = 6; i < 10; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room TBaseNorth(){
    // initial level tile condiditons
    int width = 16;
    int height = 6;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 5, new Tile(2, 0));
      newRoom.setTile(i, 10, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, 5 + i, new Tile(2, 0));
      newRoom.setTile(15, 5 + i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j+5, new Tile(3, 0));
      }
    }
    
    // Offshoot
    // south walls
    for (int i = 5; i < 11; i++) {
      newRoom.setTile(i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < 6; i++) {
      newRoom.setTile(5, 10 + i, new Tile(2, 0));
      newRoom.setTile(10, 10 + i, new Tile(2, 0));
    }
    // floor
    for (int i = 6; i < 10; i++) {
      for (int j = 10; j < 15; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room horzHallBase(){
    // initial level tile condiditons
    int width = 16;
    int height = 6;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 5, new Tile(2, 0));
      newRoom.setTile(i, 10, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, 5 + i, new Tile(2, 0));
      newRoom.setTile(15, 5 + i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j+5, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room vertHallBase(){
    // initial level tile condiditons
    int width = 6;
    int height = 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(5 + i, 0, new Tile(2, 0));
      newRoom.setTile(5 + i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(5, i, new Tile(2, 0));
      newRoom.setTile(10, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i+5, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room smallSquareBase(){
    // initial level tile condiditons
    int width = 8;
    int height = 8;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(4 + i, 4, new Tile(2, 0));
      newRoom.setTile(4 + i, 11, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 0; i < height - 2; i++) {
      newRoom.setTile(4, 5 + i, new Tile(2, 0));
      newRoom.setTile(11, 5 + i, new Tile(2, 0));
    }

    return newRoom;
  }
  private static Room endBase(){
    // initial level tile condiditons
    int width = 6;
    int height = 6;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(5 + i, 5, new Tile(2, 0));
      newRoom.setTile(5 + i, 10, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 0; i < height - 2; i++) {
      newRoom.setTile(5, 6 + i, new Tile(2, 0));
      newRoom.setTile(10, 6 + i, new Tile(2, 0));
    }

    return newRoom;
  }
  private static Room squareSandBase(){
    // initial level tile condiditons
    int width = 16;
    int height = 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 0, new Tile(2, 0));
      newRoom.setTile(i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, i, new Tile(2, 0));
      newRoom.setTile(15, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < height - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j, new Tile(4, 0));
      }
    }

    return newRoom;
  }
  private static Room TBaseSouth(){
    // initial level tile condiditons
    int width = 16;
    int height = 6;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(i, 5, new Tile(2, 0));
      newRoom.setTile(i, 10, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(0, 5 + i, new Tile(2, 0));
      newRoom.setTile(15, 5 + i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i, j+5, new Tile(3, 0));
      }
    }
    
    // Offshoot
    // north walls
    for (int i = 5; i < 11; i++) {
      newRoom.setTile(i, 0, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 0; i < 5; i++) {
      newRoom.setTile(5, i, new Tile(2, 0));
      newRoom.setTile(10, i, new Tile(2, 0));
    }
    // floor
    for (int i = 6; i < 10; i++) {
      for (int j = 1; j < 9; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room TBaseEast(){
    // initial level tile condiditons
    int width = 6;
    int height = 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(5 + i, 0, new Tile(2, 0));
      newRoom.setTile(5 + i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(5, i, new Tile(2, 0));
      newRoom.setTile(10, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i+5, j, new Tile(3, 0));
      }
    }
    
    // Offshoot
    // north/south walls
    for (int i = 0; i < 5; i++) {
      newRoom.setTile(i, 5, new Tile(2, 0));
      newRoom.setTile(i, 10, new Tile(2, 0));
    }
    // west wall
    for (int i = 5; i < 10; i++) {
      newRoom.setTile(0, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < 9; i++) {
      for (int j = 6; j < 10; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
  private static Room TBaseWest(){
    // initial level tile condiditons
    int width = 6;
    int height = 16;
    Room newRoom = new Room(width, height);

    // north/south walls
    for (int i = 0; i < width; i++) {
      newRoom.setTile(5 + i, 0, new Tile(2, 0));
      newRoom.setTile(5 + i, 15, new Tile(2, 0));
    }
    // east/west walls
    for (int i = 1; i < height - 1; i++) {
      newRoom.setTile(5, i, new Tile(2, 0));
      newRoom.setTile(10, i, new Tile(2, 0));
    }
    // floor
    for (int i = 1; i < width - 1; i++) {
      for (int j = 1; j < height - 1; j++){
        newRoom.setTile(i+5, j, new Tile(3, 0));
      }
    }
    
    // Offshoot
    // north/south walls
    for (int i = 11; i < 16; i++) {
      newRoom.setTile(i, 5, new Tile(2, 0));
      newRoom.setTile(i, 10, new Tile(2, 0));
    }
    // west wall
    for (int i = 5; i < 10; i++) {
      newRoom.setTile(15, i, new Tile(2, 0));
    }
    // floor
    for (int i = 10; i < 15; i++) {
      for (int j = 6; j < 10; j++){
        newRoom.setTile(i, j, new Tile(3, 0));
      }
    }

    return newRoom;
  }
}
