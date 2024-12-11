import java.awt.Color;
import java.awt.event.*;
import java.util.*;

//Abstract class which handles the fundamental properties and actions
//for a Scrolling game.


//************************************************************************************
//*                                                                                  *
//*                                                                                  *
//*                    YOU ARE NOT ALLOWED TO MODIFY THIS CLASS                      *
//*         (though you'll need to read, trace, and use the methods in it)           *
//*                                                                                  *
//*                                                                                  *
//************************************************************************************



public abstract class GameEngine2D {
    
    
    //********        KEYBOARD KEY VALES      ******** 
    
    //The Java API stores the values for various keys on the keyboard
    //as integer values.  These values are used by the keyPress methods
    //in order to determine which key(s) the player has pressed.  The KeyEvent
    //class stores a large collection of finals for all the different keys.
    
    //See the following API page for more info:
    //https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
    
    public static final int KEY_QUIT_GAME = KeyEvent.VK_ESCAPE;//quit the game
    public static final int KEY_PAUSE_GAME = KeyEvent.VK_P;//pause the game
    public static final int KEY_TOGGLE_DEBUG = KeyEvent.VK_D;//toggle debug mode on/off  
    
    public static final int SPEED_DOWN_KEY = KeyEvent.VK_MINUS;//make game run slower
    public static final int SPEED_UP_KEY = KeyEvent.VK_EQUALS;//make game run faster
    
    public static final int UP_KEY = KeyEvent.VK_UP;//move the player up
    public static final int DOWN_KEY = KeyEvent.VK_DOWN;//.... down
    public static final int LEFT_KEY = KeyEvent.VK_LEFT;//.... left
    public static final int RIGHT_KEY = KeyEvent.VK_RIGHT;//.... right
    
    //A collection of all the movement keys 
    //These keys are handled differently by the game engine in that they can be held down
    //to be considered repeatedly "pressed"
    public static final int[] MOVEMENT_KEYS = {UP_KEY, DOWN_KEY, LEFT_KEY, RIGHT_KEY};    
    
    //************************************************
    
    
    //Dimensions of game window
    protected static final int DEFAULT_WIDTH = 900;
    protected static final int DEFAULT_HEIGHT = 600;     
    
    //tracks if the game is currently paused or not (i.e. the player pressed the 'P' key).
    protected boolean isPaused;
    
    //Number of ticks (ie, iterations of the game loop) since the game started running
    private int ticksElapsed;
    
    //the graphical window where the game is played and the entities are drawn
    private Window window;
        
    //A collection of entities to be drawn on the screen
    //Whatever Entities are in this DisplayList are drawn to the game window
    //(per their individual attributes) on each call to window.refresh();
    //
    //Entities are drawn in order of appearance in the DisplayList.
    //First element is drawn underneath all entities, last element ontop of all entities.
    protected DisplayList entities = new DisplayList();
    
    
    
    //determines if the game should pause while a splash screen is displayed.
    //if set to false: the game will continue running underneath the splash screen
    //   when a splash screen is displayed.
    //if set to true: the game automatically pauses when a splash screen is displayed
    protected boolean pauseOnSplash;     
    
    
    public GameEngine2D(){
      this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    
    //Constructor, taking the width and height of the game window (in pixels)
    public GameEngine2D(int gameWidth, int gameHeight){
        init(gameWidth, gameHeight);
    }
    
    //Initializes the game and window, performed before the game launches    
    private void init(int gameWidth, int gameHeight){
        //We need to pass DisplayList a reference to our Window at instantation
        this.window = new Window(gameWidth, gameHeight, entities); 
        this.pauseOnSplash = true;
        this.isPaused = false;
        this.ticksElapsed = 0;
    }
    
    
    //Runs the game once, start to finish
    public void runGame(){
        pregame(); //perform any pre-game tasks
        gameLoop(); //play the game
        postgame(); //perform any post-game tasks 
        window.endGame();//ends the game  
    }
    
    
    //Plays the game.
    //This is the "heart" of your Scrolling Game code.  
    private void gameLoop(){
        //This is our game loop.  It keeps iterating until the game is over (player 
        //wins or loses).  One iteration of this loop is referred to as a "tick".
       
        //Each tick the game updates the state of the game by reacing to user input and
        //advancing the game state/redrawing the graphics accordingly.         
        while(!isGameOver()){  
            //react to keyboard/mouse input
            captureInput();
            
            //determine if the game state is updated
            //note: the game typically pauses while a splash screen is displayed.
            //      however, if pauseOnSplash == false, the game keeps running underneath
            //      the splash screen
            if (!isPaused && (getSplashImage() == null || !pauseOnSplash)){
                //update the state of the game (scroll, deal with collisions, etc)
                updateGame();
                ticksElapsed++;
            }
            
            //Garbage Collects any Entities that are flagged for removal from the game window
            entities.performGC();
            //redraws the game window (including everything in entities)
            window.refresh(); 
        }                
    }    
    
   
    
    
    //Handles reacting to any keyboard keys the player has pressed on the keyboard 
    //and/or mouse clicks.
    private void captureInput(){
        //Returns a Collection of all the keys currently being pressed.
        //The player could be pressing multiple keys simultaneously, ex: moving diagonally.
        Collection<Integer> keysPressed = window.getKeysPressed();       
        for(Integer key : keysPressed)
            this.keyReact(key); //handle each key individually
        
        //...also handle any mouse clicks 
        MouseEvent click = window.getLastMousePress();
        if (click != null)
            reactToMouseClick(click);
    }
    
    
    
    
    //Returns the width of the game window, in pixels
    public int getWindowWidth(){
        return window.getWidth();
    }
    
    
    //Returns the height of the game window, in pixels    
    public int getWindowHeight(){
        return window.getHeight();
    }    
    
    
    //retrieves the background image currently being drawn in the window.
    //returns the filename of the background image, or null if there is 
    //no background image currently being drawn.
    public String getBgImage(){
        return window.getBgImage();
    }
    
    
    //Sets the background image of the window to the argument image.
    //Background is draw UNDERNEATH all the entities
    //Argument passed is the filename of the image file.
    public void setBgImage(String imageFilename){
        window.setBgImage(imageFilename);
    }
    
    //Sets the splashscreen image drawn in the window to the argument image.
    //Splash is drawn ON TOP of all the entities.
    //Argument passed is the filename of the image file.
    public void setSplashImage(String splashFilename){
        window.setSplashImage(splashFilename);
    }    
    
    
    //Collects the splash screen image of the window.
    //returns the filename of the splash screen image, or null if there is no background.
    public String getSplashImage(){
        return window.getSplashImage();
    }    
    
    
    //Collects the current background color of the game window.
    //This background color will only be visible if there is no background
    //image, or if the background image has transparency.
    public Color getBgColor(Color bgColor){
        return window.getBgColor();
    }    
    
    //Sets the background color of the game window to the argument Color.
    //This background color will only be visible if there is no background
    //image, or if the background image has transparency.
    public void setBgColor(Color bgColor){
        window.setBgColor(bgColor);
    }
    
    
    //Checks for a collision in the game window at the argument coordinate.
    //This method returns a Collection containing any Entities that are being
    //drawn at the argument x,y coordinate
    protected Collection<Entity> getAllCollisions(int x, int y){
        Collection<Entity> collisions = new HashSet<Entity>();
        for (int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            if (e.containsCoord(x, y))
                collisions.add(e);
        }
        return collisions;            
    }
    
    
    //Checks to see if the argument entity is colliding with ANYTHING in the game window.
    //This method returns a Collection containing any Entities that are colliding with
    //the argument Entity
    protected Collection<Entity> getAllCollisions(Entity toCheck){
        Collection<Entity> collisions = new HashSet<Entity>();
        for (int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            //the argument Entity won't be detected as colliding with
            //itself if it is currently in the entities
            if (e != toCheck && e.isColliding(toCheck))
                collisions.add(e);
        }
        return collisions;            
    }    
    
    
    //Sets the title text to the argument String.
    //This text is drawn in the top bar of the game window.
    protected void setTitleText(String text){
        window.setTitle(text);    
    }    
        
    
    //Returns the current game speed.
    //The game speed is represented as an integer indicating a percentage.
    //a value of 100 indicates the game is running at 100% speed, ie the default, normal game speed.
    //150 and 50 would indicate game running 50% faster and 50% slower, respectively.
    public int getGameSpeed(){
        return window.getGameSpeed();
    }
    
    
    //Returns the number of iterations of the game loop since the game has been runninng.
    //Used by child classes to govern timing of certain game events
    protected int getTicksElapsed(){
      return this.ticksElapsed;      
    }
    
    
    //Sets the current game speed to the argument percentage.
    //The game speed is represented as integer indicating a percentage.
    //a value of 100 indicates the game is running at 100% speed, ie the default game speed.
    //150 and 50 would indicate game running 50% faster and 50% slower, respectively.    
    public void setGameSpeed(int newGameSpeed){
        if (newGameSpeed <= 0)
            throw new IllegalStateException("ERROR! Game speed set to invalid value: " + newGameSpeed);
        window.setGameSpeed(newGameSpeed);
    }
    
    
    
    
    //******** Abstract methods to be implemented by child classes ******** 
    
    //called on each "tick" (if not paused) to update the state of the game
    protected abstract void updateGame(); 
    
    //called once before the game starts
    protected abstract void pregame();
    
    //called once after the game ends
    protected abstract void postgame();
    
    //Returns a boolean indicating whether the game is over (true) or not (false).
    //Does not indicate if the player won or lost the game.
    protected abstract boolean isGameOver();
    
    //Handles reacting to a single key the player has pressed on the keyboard.
    protected abstract void keyReact(int keyCode);   
    
    //Handles reacting to a single mouse click in the game window
    protected abstract MouseEvent reactToMouseClick(MouseEvent click);    
    
    //********************************************************************* 
    
}
