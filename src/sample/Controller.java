/*
 * Dette er Controller-klassa til Pacman-programmet.
 * Denne klassa inneholder er metoden 'initialize()' som initierer hoveddelen av 'viewet'. Som veggene, Pacman, spøkelsene
 * prikken osv.
 * Metoden 'startTimer()' som henter retningen til Pacman og oppdaterer det spilleren ser. Den bruker ein 'background thread'
 * med å bruke klassa 'TimerTask'.
 * Og den siste metoden er 'handle(KeyEvent param1)' som håndterer tastebindinger for å styre Pacman,
 * eller starte ett nytt spill.
 * */
package sample;


import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

public class Controller implements EventHandler<KeyEvent> {
    final private static double FPS = 5.0;
    private PacmanModel pacmanModell;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private PacmanView pacmanView;
    private static final String[] nivaaFiler = {"src/levels/level1.txt", "src/levels/level2.txt", "src/levels/level3.txt"};
    private Timer timer;
    private static int spoekelseSpiseModus;
    private boolean pause;

    public Controller() {
        this.pause = false;
    }

    /**
     * Denne metoden starter spillet. Den lager ein ny Pacman modell, den gir startrettning 'INGEN',
     * setter spoekelseSpiseModus til 25,
     * og henter metoden 'startTimer()' som starter bakgrunns tråden som flytter på bildene.
     */
    public void initialize() {
        this.pacmanModell = new PacmanModel();
        this.update(PacmanModel.Retning.INGEN);
        spoekelseSpiseModus = 25;
        this.startTimer();
    }

    /**
     * Denne metoden bruker ein tråd som den henter med å initiere klassa 'TimerTask'.
     * Tråden kjører metoden 'oppdater(PacmanModel param1)'.
     * Deretter gjør den om int til long og henter metoden 'schedule(TimerTask param1, long param2, long param2)'.
     * Det denne metoden gjør er å repetere den oppgava som skal bli gjort i form av.
     * Param1, navnet på oppgava.
     * Param2, den skal sette ein forsinkelse i oppgava.
     * param3, den skal starte oppgava etter forsinkelsen.
     */
    private void startTimer() {
        this.timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> update(PacmanModel.getNaaverendeRetning()));
            }
        };
        long frameTimeInMill = (long) (1000.0 / FPS);
        this.timer.schedule(timerTask, 0, frameTimeInMill);

    }

    /*
     * Denne metoden oppdaterer viewet til programmet.
     * I form av oppdatere poengscoren, vise nivået, game over tekst, du vant, osv.
     *
     * */

    private void update(PacmanModel.Retning retning) {
        this.pacmanModell.steg(retning);
        this.pacmanView.oppdater(pacmanModell);
        this.scoreLabel.setText(String.format("Score: %d", this.pacmanModell.getScore()));
        this.levelLabel.setText(String.format("Level: %d", this.pacmanModell.getNivaa()));
        if (PacmanModel.isGameOver()) {
            this.gameOverLabel.setText("GAME OVER");
            pause();
        }
        if (PacmanModel.erDuVant()) {
            this.gameOverLabel.setText("DU VANT!");
        }
        if (PacmanModel.erSpoekelseSpiser()) {
            spoekelseSpiseModus--;
        }
        if (spoekelseSpiseModus == 0 && PacmanModel.erSpoekelseSpiser()) {
            PacmanModel.setSpoekelseSpiser(false);
        }
    }

    /*
     * Denne metoden tar seg av tastetrykkene som blir registrert av programmet.
     * Den sjekker mot tastetrykk om den er lovlig til å styre Pacman og viser deretter retning til Pacman.
     * Den sjekker også mot nytt spill.
     *
     * */
    @Override
    public void handle(KeyEvent keyEvent) {
        boolean kjennIgjenTasteTrykk = true;
        KeyCode keyCode = keyEvent.getCode();
        PacmanModel.Retning retning = PacmanModel.Retning.INGEN;
        if (keyCode == KeyCode.LEFT) {
            retning = PacmanModel.Retning.VENSTRE;
        } else if (keyCode == KeyCode.RIGHT) {
            retning = PacmanModel.Retning.HOEGRE;
        } else if (keyCode == KeyCode.UP) {
            retning = PacmanModel.Retning.OPP;
        } else if (keyCode == KeyCode.DOWN) {
            retning = PacmanModel.Retning.NED;
        } else if (keyCode == KeyCode.G) {
            pause();
            this.pacmanModell.startNyttSpill();
            this.gameOverLabel.setText("");
            pause = false;
            this.startTimer();
        } else {
            kjennIgjenTasteTrykk = false;
        }
        if (kjennIgjenTasteTrykk) {
            keyEvent.consume();
            PacmanModel.setNaaverendeRetning(retning);
        }
    }

    public static int getSpoekelseSpiseModus() {
        return spoekelseSpiseModus;
    }

    public static String getNivaaFil(int nivaa) {
        return nivaaFiler[nivaa];
    }

    public void pause() {
        this.timer.cancel();
        this.pause = true;
    }

    public double getBoardWidth() {
        return PacmanView.CELLE_BREDDE * pacmanView.getKolonneTeller();
    }

    public double getBoardHeight() {
        return PacmanView.CELLE_BREDDE * pacmanView.getRadTeller();
    }

    public static void setSpoekelseSpiseModus() {
        spoekelseSpiseModus = 25;
    }

    public static int getSpoekelseSpiseModusTeller() {
        return spoekelseSpiseModus;
    }

    public static void setSpoekelseSpiserTeller() {
        spoekelseSpiseModus = 25;
    }

    public boolean getPaused() {
        return pause;
    }
}
