/*
 * Denne klassa har to enumer, 'CelleVerdi' og 'Retning'.
 * Andre ting som denne klassa inneholder er metoder for å :
 * starte nivået,
 * starte neste nytt spill,
 * laste inn neste nivå,
 * bevege spøkelsene,
 * bevege Pacman,
 * metode som gir ny posisjon om Pacman/spøkelsene går ut av skjermen,
 * endre retning om spøkelsene går i veggen,
 * og metode som sjekker om 'grid-tabellen' er lovlig.
 *
 * */

package sample;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class PacmanModel {
    /**
     * Dette dokumenterer det som skal bli omgjort til det som skal bli vist på skjermen fra tekstfilane.
     */
    public enum CelleVerdi {
        TOM, LITENPRIKK, STORPRIKK, VEGG, SPOEKELSEHJEM1, SPOEKELSEHJEM2, PACMANHJEM
    }

    /**
     * Dette dokumenterer retningane til Pacman og spoekelsene
     */
    public enum Retning {
        VENSTRE, HOEGRE, OPP, NED, INGEN
    }

    @FXML
    private int radTeller;
    @FXML
    private int kolonneTeller;

    private CelleVerdi[][] grid;
    private int score;
    private int nivaa;
    private int prikkTeller;
    private static boolean gameOver;
    private static boolean duVant;
    private static boolean spoekelseSpiseModus;
    private Point2D pacmanPosisjon;
    private Point2D pacmanFart;
    private Point2D spoekelseposisjon1;
    private Point2D spoekelseFart1;
    private Point2D spoekelseposisjon2;
    private Point2D spoekelseFart2;
    private static Retning sisteRetning;
    private static Retning naaverendeRetning;

    /**
     * Denne konstruktøren vil starte spillet.
     */
    public PacmanModel() {
        this.startNyttSpill();
    }

    /**
     * Denne metoden vil lese inn fil fra 'Levels' og starte nivået.
     * Den første scanneren vil telle radene og kolonnene.
     * Den andre scanneren vil plassere ut det som skal bli vist på skjermen. Fra enumet 'CelleVerdi'
     * Til slutt vil den plassere ut spøkelsene og Pacman, samtidig gi dei retning og fart.
     *
     * @param filnavn tar inn navnet på fila
     */

    public void initierNivaa(String filnavn) {
        //denne første scanneren teller opp rader og kolonner.
        File file = new File(filnavn);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            assert scanner != null;
            if (!scanner.hasNextLine()) break;
            String linje = scanner.nextLine();
            Scanner linjeScanner = new Scanner(linje);
            while (linjeScanner.hasNext()) {
                linjeScanner.next();
                kolonneTeller++;
            }
            radTeller++;
        }
        kolonneTeller = kolonneTeller / radTeller;

        //Scanner #2 gjør om bokstavene i tekstdokumenter til bilder i spillet.
        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid = new CelleVerdi[radTeller][kolonneTeller];
        int rad = 0;
        int pacmanRad = 0;
        int pacmanKolonne = 0;
        int spoekelseRad1 = 0;
        int spoekelseKolonne1 = 0;
        int spoekelseRad2 = 0;
        int spoekelseKolonne2 = 0;
        while (true) {
            assert scanner2 != null;
            if (!scanner2.hasNextLine()) break;
            int kolonne = 0;
            String linje = scanner2.nextLine();
            Scanner linjeScanner = new Scanner(linje);
            while (linjeScanner.hasNext()) {
                String verdi = linjeScanner.next();
                CelleVerdi denneVerdi;
                switch (verdi) {
                    case "W" -> denneVerdi = CelleVerdi.VEGG;
                    case "S" -> {
                        denneVerdi = CelleVerdi.LITENPRIKK;
                        prikkTeller++;
                    }
                    case "B" -> {
                        denneVerdi = CelleVerdi.STORPRIKK;
                        prikkTeller++;
                    }
                    case "1" -> {
                        denneVerdi = CelleVerdi.SPOEKELSEHJEM1;
                        spoekelseRad1 = rad;
                        spoekelseKolonne1 = kolonne;
                    }
                    case "2" -> {
                        denneVerdi = CelleVerdi.SPOEKELSEHJEM2;
                        spoekelseRad2 = rad;
                        spoekelseKolonne2 = kolonne;
                    }
                    case "P" -> {
                        denneVerdi = CelleVerdi.PACMANHJEM;
                        pacmanRad = rad;
                        pacmanKolonne = kolonne;
                    }
                    default -> denneVerdi = CelleVerdi.TOM;
                }
                grid[rad][kolonne] = denneVerdi;
                kolonne++;
            }
            rad++;
        }
        //Her gir eg start fart og posisjon til Pacman og spøkelsene.
        pacmanPosisjon = new Point2D(pacmanRad, pacmanKolonne);
        pacmanFart = new Point2D(0, 0);
        spoekelseposisjon1 = new Point2D(spoekelseRad1, spoekelseKolonne1);
        spoekelseFart1 = new Point2D(-1, 0);
        spoekelseposisjon2 = new Point2D(spoekelseRad2, spoekelseKolonne2);
        spoekelseFart2 = new Point2D(-1, 0);
        naaverendeRetning = Retning.INGEN;
        sisteRetning = Retning.INGEN;
    }


    /**
     * Denne metoden resetter alt og starter spillet på nytt.
     */
    public void startNyttSpill() {
        gameOver = false;
        duVant = false;
        spoekelseSpiseModus = false;
        prikkTeller = 0;
        kolonneTeller = 0;
        radTeller = 0;
        this.score = 0;
        this.nivaa = 1;
        this.initierNivaa(Controller.getNivaaFil(0));
    }

    /**
     * Denne metoden vil sjekke om nivået er ferdig. Resette posisjon til spøkelsene og Pacman,
     * samtidig vil den starte neste nivå.
     */
    public void startNesteNivaa() {
        if (this.erNivaaFerdig()) {
            this.nivaa++;
            radTeller = 0;
            kolonneTeller = 0;
            duVant = false;
            spoekelseSpiseModus = false;
            try {
                this.initierNivaa(Controller.getNivaaFil(nivaa - 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                duVant = true;
                gameOver = true;
                nivaa--;
            }
        }
    }

    /**
     * Denne metoden vil gi bevegelse til spøkelsene. Den tar inn metoden 'BevegEttSpoekelse(param1, param2).
     * Putter den i ein data tabell for begge to. Og gjer dei fart og posisjon.
     */
    public void bevegSpoekelser() {
        Point2D[] spoekelseData1 = bevegEttSpoekelse(spoekelseFart1, spoekelseposisjon1);
        Point2D[] spoekelseData2 = bevegEttSpoekelse(spoekelseFart2, spoekelseposisjon2);
        spoekelseFart1 = spoekelseData1[0];
        spoekelseposisjon1 = spoekelseData1[1];
        spoekelseFart2 = spoekelseData2[0];
        spoekelseposisjon2 = spoekelseData2[1];
    }

    /**
     * Denne metoden vil gi fart til et spøkelse og samtidig sjekke for posisjonen.
     * Om spøkelset treffer ein vegg vil den få ein ny tilfeldig posisjon.
     * 'potensiellPosisjon' er den tenkte posisjonen den vil få før den får informasjon om den har truffet ein vegg.
     * Denne sjekken vil heile tida pågå under kjøringa av programmet.
     * Om den ikkje har truffet ein vegg vil koordinatane bli gitt til 'posisjon'.
     *
     * @param fart - Dette er retninga som den vil gå mot.
     * @param posisjon - Dette er kor den er plassert på brettet.
     * @return Retur verdien er ein tabell som gir fart og posisjon etter at den har sjekket om den har truffet ein vegg.
     */
    public Point2D[] bevegEttSpoekelse(Point2D fart, Point2D posisjon) {

        boolean checkPacmanLocationY = posisjon.getY() == pacmanPosisjon.getY();
        boolean checkPacmanLocationX = posisjon.getX() > pacmanPosisjon.getX();
        boolean checkPacmanLocationX1 = posisjon.getX() == pacmanPosisjon.getX();
        boolean checkPacmanLocationY1 = posisjon.getY() > pacmanPosisjon.getY();
        Point2D potensiellPosisjon;
        if (!spoekelseSpiseModus) {
            if (checkPacmanLocationY) {
                if (checkPacmanLocationX) {
                    fart = endreFart(Retning.OPP);
                } else {
                    fart = endreFart(Retning.NED);
                }
                potensiellPosisjon = posisjon.add(fart);
                potensiellPosisjon = setGaarUtAvSkjermNyposisjon(potensiellPosisjon);
                while (grid[(int) potensiellPosisjon.getX()][(int) potensiellPosisjon.getY()] == CelleVerdi.VEGG) {
                    fart = endreFart(nyRetning());
                    potensiellPosisjon = posisjon.add(fart);
                }
                posisjon = potensiellPosisjon;
            } else if (checkPacmanLocationX1) {
                if (checkPacmanLocationY1) {
                    fart = endreFart(Retning.VENSTRE);
                } else {
                    fart = endreFart(Retning.HOEGRE);
                }
                potensiellPosisjon = posisjon.add(fart);
                potensiellPosisjon = setGaarUtAvSkjermNyposisjon(potensiellPosisjon);
                while (grid[(int) potensiellPosisjon.getX()][(int) potensiellPosisjon.getY()] == CelleVerdi.VEGG) {
                    fart = endreFart(nyRetning());
                    potensiellPosisjon = posisjon.add(fart);
                }
                posisjon = potensiellPosisjon;
            } else {
                potensiellPosisjon = posisjon.add(fart);
                potensiellPosisjon = setGaarUtAvSkjermNyposisjon(potensiellPosisjon);
                while (grid[(int) potensiellPosisjon.getX()][(int) potensiellPosisjon.getY()] == CelleVerdi.VEGG) {
                    fart = endreFart(nyRetning());
                    potensiellPosisjon = posisjon.add(fart);
                }
                posisjon = potensiellPosisjon;
            }
        }
        if (spoekelseSpiseModus) {
            if (checkPacmanLocationY) {
                if (checkPacmanLocationX) {
                    fart = endreFart(Retning.NED);
                } else {
                    fart = endreFart(Retning.OPP);
                }
                potensiellPosisjon = posisjon.add(fart);
                potensiellPosisjon = setGaarUtAvSkjermNyposisjon(potensiellPosisjon);
                while (grid[(int) potensiellPosisjon.getX()][(int) potensiellPosisjon.getY()] == CelleVerdi.VEGG) {
                    fart = endreFart(nyRetning());
                    potensiellPosisjon = posisjon.add(fart);
                }
                posisjon = potensiellPosisjon;

            } else if (checkPacmanLocationX1) {
                if (checkPacmanLocationY1) {
                    fart = endreFart(Retning.HOEGRE);
                } else {
                    fart = endreFart(Retning.VENSTRE);
                }
                potensiellPosisjon = posisjon.add(fart);
                potensiellPosisjon = setGaarUtAvSkjermNyposisjon(potensiellPosisjon);
                while (grid[(int) potensiellPosisjon.getX()][(int) potensiellPosisjon.getY()] == CelleVerdi.VEGG) {
                    fart = endreFart(nyRetning());
                    potensiellPosisjon = posisjon.add(fart);
                }

                posisjon = potensiellPosisjon;
            } else {
                potensiellPosisjon = posisjon.add(fart);
                potensiellPosisjon = setGaarUtAvSkjermNyposisjon(potensiellPosisjon);
                while (grid[(int) potensiellPosisjon.getX()][(int) potensiellPosisjon.getY()] == CelleVerdi.VEGG) {
                    fart = endreFart(nyRetning());
                    potensiellPosisjon = posisjon.add(fart);
                }
                posisjon = potensiellPosisjon;
            }
        }
        return new Point2D[]{fart, posisjon};
    }


    /**
     * sjekker om objektet går utafor brettet. Om sant vil objektet bli hentet inn igjen.
     *
     * @param posisjon - Dette er den gamle posisjonen
     * @return Returverdien er den nye posisjonen. Den vil sende objektet til den andre sida av brettet.
     */
    public Point2D setGaarUtAvSkjermNyposisjon(Point2D posisjon) {
        Point2D nyposisjon = posisjon;
        //om objektet går utafor brettet på høgre side
        if (nyposisjon.getY() >= kolonneTeller) {
            nyposisjon = new Point2D(nyposisjon.getX(), 0);
        }
        //om objektet går utafor brettet på venstre side
        if (nyposisjon.getY() < 0) {
            nyposisjon = new Point2D(nyposisjon.getX(), kolonneTeller - 1);
        }
        return nyposisjon;
    }


    /**
     * Denne metoden bruker ein 'enhanced switch' metode som bruker lambdautrykk. Denne gir ny retning til Point2D objektet.
     * Samtidig gjør den om enumet 'Retning' og verdiane inni der til koordinater.
     *
     * @param retning - Dette er fra Enum Retning.
     * @return bytter retning på enten spøkelse eller pacman.
     */
    public Point2D endreFart(Retning retning) {
        return switch (retning) {
            case VENSTRE -> new Point2D(0, -1);
            case HOEGRE -> new Point2D(0, 1);
            case OPP -> new Point2D(-1, 0);
            case NED -> new Point2D(1, 0);
            case INGEN -> new Point2D(0, 0);
        };
    }

    /**
     * Denne metoden henter inn enumet 'Retning' og gir ny retning basert på tall. Om tallet skulle være utenfor rekkevidda
     * vil den kaste 'IllegalStateException'.
     *
     * @param tall - Tallet som kjem inn er tilfeldig. Det skal alltid vere mellom 0-3.
     * @return returnerer retning basert på kva slags tall som kom inn.
     */
    public Retning intTilRetning(int tall) {
        return switch (tall) {
            case 0 -> Retning.VENSTRE;
            case 1 -> Retning.HOEGRE;
            case 2 -> Retning.OPP;
            case 3 -> Retning.NED;
            default -> throw new IllegalStateException("Unexpected value: " + tall);
        };
    }


    /**
     * Gir hjem posisjon til spøkelse 1
     */
    public void sendSpoekelseHjem1() {
        for (int rad = 0; rad < this.radTeller; rad++) {
            for (int kolonne = 0; kolonne < this.kolonneTeller; kolonne++) {
                if (grid[rad][kolonne] == CelleVerdi.SPOEKELSEHJEM1) {
                    spoekelseposisjon1 = new Point2D(rad, kolonne);
                }
            }
        }
        spoekelseFart1 = new Point2D(-1, 0);
    }

    /**
     * Gir hjem posisjon til spøkelse 2
     */
    public void sendSpoekelseHjem2() {
        for (int rad = 0; rad < this.radTeller; rad++) {
            for (int kolonne = 0; kolonne < this.kolonneTeller; kolonne++) {
                if (grid[rad][kolonne] == CelleVerdi.SPOEKELSEHJEM2) {
                    spoekelseposisjon2 = new Point2D(rad, kolonne);
                }
            }
        }
        spoekelseFart2 = new Point2D(-1, 0);
    }

    /**
     * pcvp = PacmanCelleverdiPosisjon
     * Denne metoden gir retning til Pacman.
     * Den sjekker om Pacman har gått over dei ulike tinga som gir Pacman poeng eller spøkelsene.
     * Den sjekker også om Pacman har gått over ei stor kule som gjør spøkelsene redde.
     * Om den har det henter den inn den inn metoden 'sendSpoekelseHjem()' og får ekstra poeng.
     * Den sjekker også om nivået er ferdig. Om den er det henter den inn metoden 'startNesteNivaa()' .
     *
     * @param retning - Dette er retning som kommer fra spilleren
     */
    public void steg(Retning retning) {
        this.bevegPacman(retning);
        CelleVerdi pcvp = grid[(int) pacmanPosisjon.getX()][(int) pacmanPosisjon.getY()];
        if (pcvp == CelleVerdi.LITENPRIKK) {
            grid[(int) pacmanPosisjon.getX()][(int) pacmanPosisjon.getY()] = CelleVerdi.TOM;
            prikkTeller--;
            score += 10;
        }
        if (pcvp == CelleVerdi.STORPRIKK) {
            grid[(int) pacmanPosisjon.getX()][(int) pacmanPosisjon.getY()] = CelleVerdi.TOM;
            prikkTeller--;
            score += 50;
            spoekelseSpiseModus = true;
            Controller.setSpoekelseSpiserTeller();
        }
        if (spoekelseSpiseModus) {
            if (pacmanPosisjon.equals(spoekelseposisjon1)) {
                sendSpoekelseHjem1();
                score += 100;
            }
            if (pacmanPosisjon.equals(spoekelseposisjon2)) {
                sendSpoekelseHjem2();
                score += 100;
            }
        } else {
            if (pacmanPosisjon.equals(spoekelseposisjon1)) {
                gameOver = true;
                pacmanFart = new Point2D(0, 0);
            }
            if (pacmanPosisjon.equals(spoekelseposisjon2)) {
                gameOver = true;
                pacmanFart = new Point2D(0, 0);
            }
        }
        this.bevegSpoekelser();
        if (spoekelseSpiseModus) {
            if (pacmanPosisjon.equals(spoekelseposisjon1)) {
                sendSpoekelseHjem1();
                score += 100;
            }
            if (pacmanPosisjon.equals(spoekelseposisjon2)) {
                sendSpoekelseHjem2();
                score += 100;
            }
        } else {
            if (pacmanPosisjon.equals(spoekelseposisjon1)) {
                gameOver = true;
                pacmanPosisjon = new Point2D(0, 0);
            }
            if (pacmanPosisjon.equals(spoekelseposisjon2)) {
                gameOver = true;
                pacmanPosisjon = new Point2D(0, 0);
            }
        }
        if (this.erNivaaFerdig()) {
            pacmanFart = new Point2D(0, 0);
            startNesteNivaa();
        }
    }

    /**
     * Denne metoden vil få inn parameter 'retning' og gjør om retninga til koordinater.
     * Den vil sjekke om Pacman går utafor skjermen og om den gjør det vil metoden 'setGaarUtAvSkjerm(Point2D param)
     * ta seg av det.
     * Deretter vil den heile tida sjekke om 'retning' treffer ein vegg. Om den gjør det skal Pacman sin fart vere 'INGEN'.
     * Eller skal den tenkte fart og posisjon bli den endelige posisjonen.
     *
     * @param retning - Dette er retning som seier kor Pacman skal gå.
     */
    public void bevegPacman(Retning retning) {
        Point2D potensiellPacmanFart = endreFart(retning);
        Point2D potensiellpacmanPosisjon = pacmanPosisjon.add(potensiellPacmanFart);

        potensiellpacmanPosisjon = setGaarUtAvSkjermNyposisjon(potensiellpacmanPosisjon);

        if (retning.equals(sisteRetning)) {

            if (grid[(int) potensiellpacmanPosisjon.getX()][(int) potensiellpacmanPosisjon.getY()] == CelleVerdi.VEGG) {
                pacmanFart = endreFart(Retning.INGEN);
                setSisteRetning(Retning.INGEN);
            } else {
                pacmanFart = potensiellPacmanFart;
                pacmanPosisjon = potensiellpacmanPosisjon;
            }
        } else {

            if (grid[(int) potensiellpacmanPosisjon.getX()][(int) potensiellpacmanPosisjon.getY()] == CelleVerdi.VEGG) {
                potensiellPacmanFart = endreFart(sisteRetning);
                potensiellpacmanPosisjon = pacmanPosisjon.add(potensiellPacmanFart);

                if (grid[(int) potensiellpacmanPosisjon.getX()][(int) potensiellpacmanPosisjon.getY()] == CelleVerdi.VEGG) {
                    pacmanFart = endreFart(Retning.INGEN);
                    setSisteRetning(Retning.INGEN);
                } else {
                    pacmanFart = endreFart(sisteRetning);
                    pacmanPosisjon = pacmanPosisjon.add(pacmanFart);
                }
            } else {
                pacmanFart = potensiellPacmanFart;
                pacmanPosisjon = potensiellpacmanPosisjon;
                setSisteRetning(retning);
            }
        }
    }


    /**
     * @return Returen sjekker om priktelleren er 0. Om den er 0 er den 'true' og alle prikkene har blitt spist opp.
     * Om 'false' vil spillet fortsette.
     */
    public boolean erNivaaFerdig() {
        return this.prikkTeller == 0;
    }

    public int getRadTeller() {
        return radTeller;
    }

    public void setRadTeller(int radTeller) {
        this.radTeller = radTeller;
    }

    public int getKolonneTeller() {
        return kolonneTeller;
    }

    public void setKolonneTeller(int kolonneTeller) {
        this.kolonneTeller = kolonneTeller;
    }

    public CelleVerdi[][] getGrid() {
        return grid;
    }

    public void setGrid(CelleVerdi[][] grid) {
        this.grid = grid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public static Retning getNaaverendeRetning() {
        return naaverendeRetning;
    }

    public int getNivaa() {
        return nivaa;
    }

    public void setNivaa(int nivaa) {
        this.nivaa = nivaa;
    }


    /**
     * Denne henter kor mange prikker som er igjen på brettet
     *
     * @return returnerer antall prikker som er igjen.
     */
    public int getPrikkTeller() {
        return prikkTeller;
    }

    public void setPrikkTeller(int prikkTeller) {
        this.prikkTeller = prikkTeller;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean gameOver) {
        PacmanModel.gameOver = gameOver;
    }

    public static boolean erDuVant() {
        return duVant;
    }

    public static void setDuVant(boolean duVant) {
        PacmanModel.duVant = duVant;
    }

    public static boolean erSpoekelseSpiser() {
        return spoekelseSpiseModus;
    }

    public static void setSpoekelseSpiser(boolean spoekelseSpiser) {
        PacmanModel.spoekelseSpiseModus = spoekelseSpiser;
    }

    public Point2D getPacmanPosisjon() {
        return pacmanPosisjon;
    }

    public void setpacmanPosisjon(Point2D pacmanPosisjon) {
        this.pacmanPosisjon = pacmanPosisjon;
    }

    public Point2D getPacmanFart() {
        return pacmanFart;
    }

    public void setPacmanFart(Point2D pacmanFart) {
        this.pacmanFart = pacmanFart;
    }

    public Point2D getSpoekelsePosisjon1() {
        return spoekelseposisjon1;
    }

    public void setSpoekelsePosisjon1(Point2D spoekelseposisjon1) {
        this.spoekelseposisjon1 = spoekelseposisjon1;
    }

    public Point2D getSpoekelseFart1() {
        return spoekelseFart1;
    }

    public void setSpoekelseFart1(Point2D spoekelseFart1) {
        this.spoekelseFart1 = spoekelseFart1;
    }

    public Point2D getSpoekelsePosisjon2() {
        return spoekelseposisjon2;
    }

    public void setSpoekelsePosisjon2(Point2D spoekelseposisjon2) {
        this.spoekelseposisjon2 = spoekelseposisjon2;
    }

    public Point2D getSpoekelseFart2() {
        return spoekelseFart2;
    }

    public void setSpoekelseFart2(Point2D spoekelseFart2) {
        this.spoekelseFart2 = spoekelseFart2;
    }

    public static Retning getSisteRetning() {
        return sisteRetning;
    }


    public static void setSisteRetning(Retning sisteRetning) {
        PacmanModel.sisteRetning = sisteRetning;
    }


    public static void setNaaverendeRetning(Retning naaverendeRetning) {
        PacmanModel.naaverendeRetning = naaverendeRetning;
    }

    /**
     * Denne sjekker om 'grid' har lovlige verdier. Om sjekken er 'true' vil den returnere grid-tabellen.
     * Om den returnerer 'false' vil den returnere feilmeldinga.
     * throw new IllegalArgumentException("Feil med celleverdien").
     *
     * @param rad - Tallet som kjem inn bør vere større enn 0.
     * @param kolonne - Tallet som kjem inn bør vere større enn 0.
     * @return Retur verdien er tabellen 'grid[rad][kolonne]'.
     */
    public CelleVerdi getCelleVerdi(int rad, int kolonne) {
        boolean check;
        check = (rad >= 0 && rad < this.grid.length && kolonne >= 0 && kolonne < this.grid[0].length);
        if(check) {
            return this.grid[rad][kolonne];
        }
        else{
            throw new IllegalArgumentException("Feil med celleverdien");
        }
    }

    /**
     * @return Retur verdien er eit tilfeldig tall mellom ein til 4. Den vil bli brukt til å gi ny retning
     *  til spøkelsene. Grunnen til 4 er fordi det er 4 retninger spøkelsene kan gå.
     */
    private Retning nyRetning() {
        Random generator = new Random();
        int random = generator.nextInt(4);
        return intTilRetning(random);
    }

}
