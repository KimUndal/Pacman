package sample;
/*
 * Denne klassa henter inn bilder for å vise ka som skal bli vist på skjermen.
 * Den viser også retningen til Pacman basert på ka spilleren trykker på.
 *
 *
 * */

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PacmanView extends Group {

    public final static double CELLE_BREDDE = 20.0;

    @FXML
    private int radTeller;
    @FXML
    private int kolonneTeller;

    private ImageView[][] celleViews;
    private final Image pacmanHoegreBilde;
    private final Image pacmanVenstreBilde;
    private final Image pacmanOppBilde;
    private final Image pacmanNedBilde;
    private final Image spoekelseBilde1;
    private final Image spoekelseBilde2;
    private final Image blaatSpoekelseBilde;
    private final Image veggBilde;
    private final Image storPrikkBilde;
    private final Image litenPrikkBilde;


    public PacmanView() {
        this.pacmanHoegreBilde = new Image(getClass().getResourceAsStream("/gifs/pacmanRight.gif"));
        this.pacmanOppBilde = new Image(getClass().getResourceAsStream("/gifs/pacmanUp.gif"));
        this.pacmanNedBilde = new Image(getClass().getResourceAsStream("/gifs/pacmanDown.gif"));
        this.pacmanVenstreBilde = new Image(getClass().getResourceAsStream("/gifs/pacmanLeft.gif"));
        this.spoekelseBilde1 = new Image(getClass().getResourceAsStream("/gifs/redghost.gif"));
        this.spoekelseBilde2 = new Image(getClass().getResourceAsStream("/gifs/ghost2.gif"));
        this.blaatSpoekelseBilde = new Image(getClass().getResourceAsStream("/gifs/blueghost.gif"));
        this.veggBilde = new Image(getClass().getResourceAsStream("/gifs/wall.png"));
        this.storPrikkBilde = new Image(getClass().getResourceAsStream("/gifs/whitedot.png"));
        this.litenPrikkBilde = new Image(getClass().getResourceAsStream("/gifs/smalldot.png"));
    }

    /*
     * Denne metoden starter med å sjekke antall rader og kolonner om det er større enn 0.
     * Om det er skal dei bli brukt til å skape brettet.
     * Om den er 'false' skal den telle og sette bygge opp brettet.
     * For kver gang kolonne har truffet 19 ganger vil det bli laget 1 rad.
     * Dette vil skje til rad har nådd 21 ganger.
     * */
    private void initierGrid() {
        if (this.radTeller > 0 && this.kolonneTeller > 0) {
            this.celleViews = new ImageView[this.radTeller][this.kolonneTeller];
            for (int rad = 0; rad < this.radTeller; rad++) {
                for (int kolonne = 0; kolonne < this.kolonneTeller; kolonne++) {
                    ImageView imageView = new ImageView();
                    imageView.setX((double) kolonne * CELLE_BREDDE);
                    imageView.setY((double) rad * CELLE_BREDDE);
                    imageView.setFitWidth(CELLE_BREDDE);
                    imageView.setFitHeight(CELLE_BREDDE);
                    this.celleViews[rad][kolonne] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }


    /**
     * Denne metoden oppdaterer bildet av Pacman.
     * Dette skal bli vist etter at spilleren har byttet retning på Pacman.
     *
     * @param modell denne blir brukt til å hente to metoder, 'getRadTeller()' og 'getKolonneTeller()'
     *              og hente X, Y koordinatene til å finne ut ka slags bild eosm skal bli vist basert på koordinatane
     */
    public void oppdater(PacmanModel modell) {
        assert modell.getRadTeller() == this.radTeller && modell.getKolonneTeller() == this.kolonneTeller;

        //Denne for-loopen skal sette riktig bilde som korresponderer med celleverdien av den cella.
        for (int rad = 0; rad < this.radTeller; rad++) {
            for (int kolonne = 0; kolonne < this.kolonneTeller; kolonne++) {
                PacmanModel.CelleVerdi verdi = modell.getCelleVerdi(rad, kolonne);
                if (verdi == PacmanModel.CelleVerdi.VEGG) {
                    this.celleViews[rad][kolonne].setImage(this.veggBilde);
                } else if (verdi == PacmanModel.CelleVerdi.STORPRIKK) {
                    this.celleViews[rad][kolonne].setImage(this.storPrikkBilde);
                } else if (verdi == PacmanModel.CelleVerdi.LITENPRIKK) {
                    this.celleViews[rad][kolonne].setImage(this.litenPrikkBilde);
                } else {
                    this.celleViews[rad][kolonne].setImage(null);
                }
                //Denne 'if-checken' ser etter kas posisjon Pacman viser og setter bildet som korresponderer etter retningen
                if (rad == modell.getPacmanPosisjon().getX()
                        && kolonne == modell.getPacmanPosisjon().getY()
                        && (PacmanModel.getSisteRetning() == PacmanModel.Retning.HOEGRE
                        || PacmanModel.getSisteRetning() == PacmanModel.Retning.INGEN)) {
                    this.celleViews[rad][kolonne].setImage(this.pacmanHoegreBilde);
                } else if (rad == modell.getPacmanPosisjon().getX()
                        && kolonne == modell.getPacmanPosisjon().getY()
                        && PacmanModel.getSisteRetning() == PacmanModel.Retning.VENSTRE) {
                    this.celleViews[rad][kolonne].setImage(this.pacmanVenstreBilde);
                } else if (rad == modell.getPacmanPosisjon().getX()
                        && kolonne == modell.getPacmanPosisjon().getY()
                        && PacmanModel.getSisteRetning() == PacmanModel.Retning.OPP) {
                    this.celleViews[rad][kolonne].setImage(this.pacmanOppBilde);
                } else if (rad == modell.getPacmanPosisjon().getX()
                        && kolonne == modell.getPacmanPosisjon().getY()
                        && PacmanModel.getSisteRetning() == PacmanModel.Retning.NED) {
                    this.celleViews[rad][kolonne].setImage(this.pacmanNedBilde);
                }

                // Denne skal få spøkelsene til å blinke mot slutten av spøkelseSpiseModus
                // Den skal også få bildene til å blinke mellom vanlig og blå modus
                if (PacmanModel.erSpoekelseSpiser()
                        && (Controller.getSpoekelseSpiseModusTeller() == 6
                        || Controller.getSpoekelseSpiseModusTeller() == 4
                        || Controller.getSpoekelseSpiseModusTeller() == 2)) {
                    getSpoekelsePlassering(modell, rad, kolonne);
                }
                // Om Pacman er i spøkelseSpiseModus skal spøkelsene bli vist i blått bilde
                else if (PacmanModel.erSpoekelseSpiser()) {
                    if (rad == modell.getSpoekelsePosisjon1().getX() && kolonne == modell.getSpoekelsePosisjon1().getY()) {
                        this.celleViews[rad][kolonne].setImage(this.blaatSpoekelseBilde);
                    }
                    if (rad == modell.getSpoekelsePosisjon2().getX() && kolonne == modell.getSpoekelsePosisjon2().getY()) {
                        this.celleViews[rad][kolonne].setImage(this.blaatSpoekelseBilde);
                    }
                }
                // Om ikkje skal det bli vist normale bilder
                else {
                    getSpoekelsePlassering(modell, rad, kolonne);
                }
            }
        }
    }


    /**
     * Denne metoden oppdaterer kor spøkelsene skal vere på skjermen.
     * Den sjekker mot spøkelset X, Y koordinater mot rad og kolonne, for å vite kor spøkelse skal vere.
     *
     * @param modell - Denne gir X, Y koordinatene til spøkelsene
     * @param rad
     * @param kolonne
     */
    private void getSpoekelsePlassering(PacmanModel modell, int rad, int kolonne) {
        if (rad == modell.getSpoekelsePosisjon1().getX()
                && kolonne == modell.getSpoekelsePosisjon1().getY()) {
            this.celleViews[rad][kolonne].setImage(this.spoekelseBilde1);
        }
        if (rad == modell.getSpoekelsePosisjon2().getX()
                && kolonne == modell.getSpoekelsePosisjon2().getY()) {
            this.celleViews[rad][kolonne].setImage(this.spoekelseBilde2);
        }
    }


    /**
     * @return Denne returnerer antall rader
     */
    public int getRadTeller() {
        return this.radTeller;
    }

    /**
     * Denne setter inn rader og henter 'initiergrid()' metoden.
     *
     * @param radTeller
     */
    public void setRadTeller(int radTeller) {
        this.radTeller = radTeller;
        this.initierGrid();
    }

    /**
     * @return Denne returnerer antall kolonner
     */
    public int getKolonneTeller() {
        return this.kolonneTeller;
    }


    /**
     * Denne setter inn kolonner og henter 'initiergrid()' metoden.
     *
     * @param kolonneTeller
     */
    public void setKolonneTeller(int kolonneTeller) {
        this.kolonneTeller = kolonneTeller;
        this.initierGrid();
    }

}


