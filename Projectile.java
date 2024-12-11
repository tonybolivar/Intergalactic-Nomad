//Protectiles are entities the player uses to attack enemies.
//If a protectile collides with an entity, both the protectile and entity are garbage collected.
public class Projectile extends Entity implements Consumable, Scrollable {

    //Location of image file to be drawn for an Projectile
    public static String PROJECTILE_IMAGE_FILE = "assets/projectile.gif";
    //Dimensions of the Projectile
    public static final int PROJECTILE_WIDTH = 30;
    public static final int PROJECTILE_HEIGHT = 15;
    //Number of pixels that the Projectile moves right each time the game scrolls
    public static final int PROJECTILE_DEFAULT_SCROLL_SPEED = 10;
    public static final int ENEMY_SCROLL_SPEED = 5;
    //Amount of HP deducted when Player collides with an Projectile
    public static final int PROJECTILE_DMG_VALUE = -1;

    private boolean isEnemyProjectile;

    private int scrollSpeed = PROJECTILE_DEFAULT_SCROLL_SPEED;

    public Projectile(){
        this(0, 0);
    }

    public Projectile(int x, int y){
        this(x, y, PROJECTILE_IMAGE_FILE);
    }

    public Projectile(int x, int y, String imageName){
        this(x, y, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, imageName);
    }

    public Projectile(int x, int y, int width, int height, String imageName){
        this(x, y, width, height, imageName, false);
    }
    public Projectile(int x, int y, int width, int height, String imageName, boolean isEnemyProjectile){
        super(x, y, width, height, imageName);
        this.isEnemyProjectile = isEnemyProjectile;
    }


    public int getScrollSpeed(){
        return this.scrollSpeed;
    }

    //Sets the scroll speed to the argument amount
    public void setScrollSpeed(int newSpeed){
       this.scrollSpeed = newSpeed;
    }




    public void scroll(){
        //Move the projectile left because it was fired by enemy
        if (isEnemyProjectile){
            setX(getX() - this.ENEMY_SCROLL_SPEED);
        }
        //Move the projectile right because it was fired by player
        else{
            setX(getX() + this.scrollSpeed);
        }
    }

    //Colliding with an Projectile does not affect the player's score
    public int getScoreModifier(){
       //implement me!
       return 0;
    }
    //Returns boolean that determines if projectile was fired by Enemy or not.
    public boolean getStatus(){
        return isEnemyProjectile;
    }
    public void setStatus(boolean value){
        isEnemyProjectile = value;
        setImage("/assets/evilprojectile.gif");
    }
    //Colliding with an Projectile reduces players HP by 1
    public int getHPModifier(){
        return PROJECTILE_DMG_VALUE;
    }

}
