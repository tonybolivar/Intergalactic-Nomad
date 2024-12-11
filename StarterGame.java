import java.awt.*;
import java.awt.event.*;
import java.util.*;
//Partner: Reed Gilbert

//A Starter version of the scrolling game, featuring Avoids, Collects, RareAvoids, and RareCollects
//Players must reach a score threshold to win.
//If player runs out of HP (via too many Avoid/RareAvoid collisions) they lose.
public class StarterGame extends GameEngine2D {
    
    
    //Starting Player coordinates
    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;
    
    //Score needed to win the game
    protected static final int SCORE_TO_WIN = 300;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    protected static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    protected static final int SPEED_CHANGE_INTERVAL = 20;    
    
    public static final String INTRO_SPLASH_FILE = "assets/splash.gif";        
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    protected static final int SPAWN_INTERVAL = 45;

    
    //A Random object for all your random number generation needs!
    public static final Random rand = new Random();
    
    //player's current score
    protected int score;
    
    
    //Stores a reference to game's Player object for quick reference (Though this Player presumably
    //is also in the DisplayList, but it will need to be referenced often)
    protected Player player;
    
    
    public StarterGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public StarterGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }
    
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void pregame(){
        this.setBgColor(Color.BLACK);
        this.player = new Player(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        this.entities.add(player); 
        this.score = 0;
        super.setSplashImage(INTRO_SPLASH_FILE);
    }
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();   
        //Spawn new entities only at a certain interval
        if (super.getTicksElapsed() % SPAWN_INTERVAL == 0){
            spawnEntities();
        }
        updateTitleText();
        gcOffscreenEntities();
    }
    

    //Update the text at the top of the game window
    protected void updateTitleText(){
        setTitleText("HP: " + player.getHP() + ", Score: " + this.score);
        if (isGameOver()){
            setTitleText("GAME OVER - You Lose!");
        }
    }
    

    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        for (int i = 0; i < entities.size(); i++) {
        Entity entity = entities.get(i);

        if (entity instanceof Scrollable) {
            Scrollable scrollableEntity = (Scrollable) entity;
            scrollableEntity.scroll();
        }

        if (entity instanceof Consumable) {
            Consumable consumableEntity = (Consumable) entity;
            collidedWithPlayer(consumableEntity);
            }
        }

    }

    
    //Handles "garbage collection" of the entities
    //Flags entities in the displaylist that are no longer relevant
    //(i.e. will no longer need to be drawn in the game window).
    protected void gcOffscreenEntities(){
        for(int i = 0;i < entities.size(); i++){
            if (entities.get(i).getX() < -100){
                entities.get(i).setGCFlag(true);
            }
        }
    }
    
    //Called whenever it has been determined that the Player collided with a consumable
    protected void collidedWithPlayer(Consumable collidedWith){
        if (((Entity) collidedWith).isColliding(player)){
            System.out.println("Collided with Player!");
            player.modifyHP(collidedWith.getHPModifier());
            this.score += collidedWith.getScoreModifier();
            System.out.println("HP: " + player.getHP() + " Score: " + score);
            ((Entity)collidedWith).setGCFlag(true);
        }
    }


    
    
    //Spawn new Entities on the right edge of the game board
    private void spawnEntities(){
        int x = super.DEFAULT_WIDTH;
        int numEntities = rand.nextInt(4);

        for(int i = 0; i < numEntities; i++){
            int y = 0;
            Entity temp;
            int randNum = rand.nextInt(10);
            if (randNum == 0){
                temp = new RareAvoid();
            }
            else if (randNum == 1){
                temp = new RareCollect();
            }
            else if (randNum <= 5){
                temp = new Avoid();
            }
            else{
                temp = new Collect();
            }

            y += rand.nextInt(super.getWindowHeight() - temp.getHeight());
            if (y + temp.getHeight() > super.getWindowHeight()){
                y = super.getWindowHeight() - temp.getHeight() + rand.nextInt(20);
            }

            temp.setY(y);
            temp.setX(x);
            if (super.getAllCollisions(temp).size() > 0){
                numEntities++;
                temp.setGCFlag(true);
            }
            else{
                entities.add(temp);
            }
        }
    }
    
    protected void handlePlayerMovement(int key){
        if (key == super.UP_KEY){
            if (player.getY() > 0){
                player.setY(Math.max(player.getY() - player.getMoveSpeed(), 0));
            }
        }

        if (key == super.DOWN_KEY){
            if (player.getY()+player.getHeight() < super.getWindowHeight()){
                player.setY(Math.min(player.getY() + player.getMoveSpeed(), super.getWindowHeight()));
            }
        }

        if (key == super.RIGHT_KEY){
            if (player.getX()+player.getWidth() < super.getWindowWidth()){
                player.setX(Math.min(player.getX() + player.getMoveSpeed(), super.getWindowWidth()));

            }
        }

        if (key == super.LEFT_KEY){
            if (player.getX() > 0){
                player.setX(Math.max(player.getX() - player.getMoveSpeed(), 0));
            }
        }

    }
    //Called once the game is over, performs any end-of-game operations
    protected void postgame(){
        if (player.getHP() <= 0){
            super.setTitleText("GAME OVER - You Lose!");
        }
        else{
            super.setTitleText("GAME OVER - You Won!");
        }
    }
    
    //Returns a boolean indicating if the game is over (true) or not (false)
    //Game can be over due to either a win or lose state
    protected boolean isGameOver(){
        if (player.getHP() <= 0){
            return true;
        }

        if (this.score >= SCORE_TO_WIN){
            return true;
        }

        return false;
    }

    //Reacts to a single key press on the keyboard
    protected void keyReact(int key){
        //if a splash screen is up, only react to the advance splash key
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            return;
        }

        if (key == super.KEY_PAUSE_GAME && super.isPaused){
            super.isPaused = false;
        }
        else if (key == super.KEY_PAUSE_GAME){
            super.isPaused = true;
        }

        if (key == super.SPEED_DOWN_KEY && super.getGameSpeed()-SPEED_CHANGE_INTERVAL >= 1){
            super.setGameSpeed(super.getGameSpeed()-SPEED_CHANGE_INTERVAL);
        }

        if (key == super.SPEED_UP_KEY && super.getGameSpeed()+SPEED_CHANGE_INTERVAL <= MAX_GAME_SPEED){
            super.setGameSpeed(super.getGameSpeed()+SPEED_CHANGE_INTERVAL);
        }

        if (super.isPaused == false){
            handlePlayerMovement(key);
        }
    }
    
    
    
    //Handles reacting to a single mouse click in the game window
    protected MouseEvent reactToMouseClick(MouseEvent click){
       
        //Mouse functionality is not used at all in the Starter game...
        //you may want to override this function for a CreativeGame feature though!

        return click;//returns the mouse event for any child classes overriding this method
    }
    
    

}
