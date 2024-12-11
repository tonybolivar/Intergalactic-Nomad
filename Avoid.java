//Avoids are entities the player needs to avoid colliding with.
//If a player collides with an avoid, it reduces the players Hit Points (HP).
public class Avoid extends Entity implements Consumable, Scrollable {
    
    //Location of image file to be drawn for an Avoid
    public static final String AVOID_IMAGE_FILE = "assets/avoid.gif";
    //Dimensions of the Avoid    
    public static final int AVOID_WIDTH = 75;
    public static final int AVOID_HEIGHT = 75;
    //Number of pixels that the Avoid moves left each time the game scrolls
    public static final int AVOID_DEFAULT_SCROLL_SPEED = 5;
    //Amount of HP deducted when Player collides with an Avoid
    public static final int AVOID_DMG_VALUE = -1;    

    private int scrollSpeed = AVOID_DEFAULT_SCROLL_SPEED;
        
    public Avoid(){
        this(0, 0);        
    }
    
    public Avoid(int x, int y){
        this(x, y, AVOID_IMAGE_FILE);  
    }

    public Avoid(int x, int y, String imageName){
        this(x, y, AVOID_WIDTH, AVOID_HEIGHT, imageName); 
    }
    
    public Avoid(int x, int y, int width, int height, String imageName){
        super(x, y, width, height, imageName);
    }

    
    public int getScrollSpeed(){
        return this.scrollSpeed;
    }
    
    //Sets the scroll speed to the argument amount
    public void setScrollSpeed(int newSpeed){
       this.scrollSpeed = newSpeed;
    }
    
    //Move the avoid left by the scroll speed
    public void scroll(){
        setX(getX() - this.scrollSpeed);
    }
    
    //Colliding with an Avoid does not affect the player's score
    public int getScoreModifier(){
       //implement me!
       return 0;
    }
    
    //Colliding with an Avoid reduces players HP by 1
    public int getHPModifier(){
        return AVOID_DMG_VALUE;
    }
    
}
