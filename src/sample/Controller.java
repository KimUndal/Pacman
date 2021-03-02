package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.security.KeyException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements EventHandler<KeyEvent> {
    final private static double FPS = 5.0;
    private PacmanModel pacmanModel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private PacmanView pacmanView;
    private static final String[] levels = {"src/levels/level1.txt","src/levels/level2.txt","src/levels/level3.txt"};
    private Timer timer;
    private static int ghostEatingMode;
    private boolean pause;

    public Controller(){
        this.pause = false;
    }

    public static void setGhostEatingCounter() {
        ghostEatingMode = 25;
    }

    public void initialize(){
        this.pacmanModel = new PacmanModel();
        this.update(PacmanModel.Direction.NONE);
        ghostEatingMode = 25;
        this.startTimer();
    }

    private void startTimer(){
        this.timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> update(PacmanModel.getCurrentDirection()));
            }
        };
        long frameTimeInMill = (long) (1000.0/FPS);
        this.timer.schedule(timerTask, 0, frameTimeInMill);
    }

    private void update(PacmanModel.Direction direction){
        this.pacmanModel.step(direction);
        this.pacmanView.update(pacmanModel);
        this.scoreLabel.setText(String.format("Score: %d", this.pacmanModel.getScore()));
        this.levelLabel.setText(String.format("Level: %d", this.pacmanModel.getLevel()));
        if(PacmanModel.isGameOver()){
            this.gameOverLabel.setText("GAME OVER");
            pause();
        }
        if(PacmanModel.isYouWon()){
            this.gameOverLabel.setText("YOU WON");
        }
        if(PacmanModel.isGhostEating()){
            ghostEatingMode--;
        }
        if(ghostEatingMode == 0 && PacmanModel.isGhostEating()){
            PacmanModel.setGhostEating(false);
        }
    }

    @Override
    public void handle(KeyEvent keyEvent){
        boolean keyRecognised = true;
        KeyCode keyCode = keyEvent.getCode();
        PacmanModel.Direction direction = PacmanModel.Direction.NONE;
        if(keyCode == KeyCode.LEFT){
            direction = PacmanModel.Direction.LEFT;
        }
        else if(keyCode == KeyCode.RIGHT){
            direction = PacmanModel.Direction.RIGHT;
        }
        else if(keyCode == KeyCode.UP){
            direction = PacmanModel.Direction.UP;
        }
        else if(keyCode == KeyCode.DOWN){
            direction = PacmanModel.Direction.DOWN;
        }
        else if(keyCode == KeyCode.G){
            pause();
            this.pacmanModel.startNewGame();
            this.gameOverLabel.setText("");
            pause = false;
            this.startTimer();
        }else{
            keyRecognised = false;
        }
        if(keyRecognised){
            keyEvent.consume();
            PacmanModel.setCurrentDirection(direction);
        }
    }

    public static int getGhostEatingMode() {
        return ghostEatingMode;
    }

    public static String getLevelFile(int level){
        return levels[level];
    }
    public void pause(){
        this.timer.cancel();
        this.pause = true;
    }

    public double getBoardWidth(){
        return PacmanView.CELL_WIDTH * pacmanView.getColumnCount();
    }

    public double getBoardHeight(){
        return PacmanView.CELL_WIDTH * pacmanView.getRowCount();
    }

    public static void setGhostEatingMode(){
        ghostEatingMode = 25;
    }

    public static int getGhostEatingModeCounter(){
        return ghostEatingMode;
    }

    public boolean getPaused(){
        return pause;
    }
}
