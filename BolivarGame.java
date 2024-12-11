import java.awt.event.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
public class BolivarGame extends StarterGame{
    private static int DEFAULT_HEIGHT = GameEngine2D.DEFAULT_HEIGHT;
    private static int DEFAULT_WIDTH = GameEngine2D.DEFAULT_WIDTH;

    protected static final int STARTING_PLAYER_X = 0;
    protected static final int STARTING_PLAYER_Y = 100;
    protected static final int POWERUP_TIME = 200;


    private boolean powerUp = false;
    Player player;


    public BolivarGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public BolivarGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
    }


    protected void pregame(){
        setBgImage("/assets/bg.gif");
        super.pregame();
        this.player = super.player;

    }
    protected void updateGame(){
        super.updateGame();
        if (getTicksElapsed() % SPAWN_INTERVAL == 0){
            evilEntityShoot();
        }
        if (powerUp && (getTicksElapsed() % POWERUP_TIME == 0)){
            player.setImage("/assets/player.gif");
        }

    }
    protected void updateTitleText(){
        int percentage = (int)((this.score / (float)super.SCORE_TO_WIN) * 100);
        percentage = Math.min(100, Math.max(0, percentage));
        setTitleText("HP: " + player.getHP() + ", FTL Charge: " + percentage + "%");
        if (isGameOver()){
            setTitleText("GAME OVER - You Lose!");
        }
    }
    protected void keyReact(int key){
        super.keyReact(key);
        if(key == KeyEvent.VK_E || key == KeyEvent.VK_SPACE){
            handlePlayerAttack(key);
        }
    }
    protected void scrollEntities(){
        super.scrollEntities();

        for (int i = 0; i < entities.size(); i++){
            Entity temp = entities.get(i);
            if (temp instanceof Projectile){
                Projectile projTemp = (Projectile)temp;
                projTemp.scroll();
                collidedWithEntity(projTemp);
                handleEnemyProjectileCollision(projTemp);
            }
        }

    }



    private void handlePlayerAttack(int key){
        Projectile temp = new Projectile();
        temp.setX(player.getX() + 100);
        temp.setY(player.getY()+15);
        entities.add(temp);
        playSound("/assets/laser.wav");

    }

    protected void collidedWithPlayer(Consumable collidedWith) {
        if (!powerUp){
            super.collidedWithPlayer(collidedWith);
        }
        else if (powerUp && collidedWith instanceof Collect || collidedWith instanceof RareCollect){
            super.collidedWithPlayer(collidedWith);
        }
        if (collidedWith instanceof RareCollect) {
            RareCollect rareCollect = (RareCollect) collidedWith;
            if (rareCollect.isColliding(player)) {
                player.setImage("/assets/playerrainbow.gif");
                powerUp = true;
            }
        }
    }

    private void collidedWithEntity(Projectile projectile){
        for(int i = 0; i < entities.size(); i++){
            if ((projectile != entities.get(i)) && (!(entities.get(i) instanceof Player)) && projectile.isColliding(entities.get(i))){
                entities.get(i).setGCFlag(true);
                projectile.setGCFlag(true);
            }
        }
    }
    private void evilEntityShoot(){
        int random = super.rand.nextInt(entities.size());
        Projectile temp = new Projectile();
        temp.setStatus(true);

        if (entities.get(random) instanceof Avoid || entities.get(random) instanceof RareAvoid){
            temp.setX(entities.get(random).getX() - 100);
            temp.setY(entities.get(random).getY()  + 15);
            entities.add(temp);
            playSound("/assets/enemylaser.wav");
        }

    }
    private void handleEnemyProjectileCollision(Projectile projectile){
        if (projectile.isColliding(player)){
            projectile.setGCFlag(true);
            player.modifyHP(-1);
        }
    }
    private void playSound(String file){
        try{
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(BolivarGame.class.getResource(file));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        }
        catch(Exception e){
            System.out.println("Cannot locate sound effect.");
        }
    }
    protected void postgame(){
        super.postgame();
        if (player.getHP() <= 0){
            setSplashImage("/assets/youlose.gif");
        }
        else{
            setSplashImage("/assets/youwin.gif");
        }
    }
    protected void gcOffscreenEntities(){
        for(int i = 0;i < entities.size(); i++){
            if (entities.get(i).getX() < -100){
                if (entities.get(i) instanceof Avoid || entities.get(i) instanceof RareAvoid){
                    super.player.modifyHP(-1);
                }
                entities.get(i).setGCFlag(true);
            }
        }
    }
}



