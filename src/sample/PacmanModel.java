package sample;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class PacmanModel {
    public enum CellValue {
        EMPTY, SMALLDOT, BIGDOT, WALL, GHOSTHOME1, GHOSTHOME2, PACKHOME
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    @FXML
    private int rowCount;
    @FXML
    private int columnCount;
    private CellValue[][] grid;
    private int score;
    private int level;
    private int dotCount;
    private static boolean gameOver;
    private static boolean youWon;
    private static boolean ghostEating;
    private Point2D pacmanLocation;
    private Point2D pacmanVelocity;
    private Point2D ghost1Location;
    private Point2D ghost1Velocity;
    private Point2D ghost2Location;
    private Point2D ghost2Velocity;
    private static Direction lastDirection;
    private static Direction currentDirection;

    public PacmanModel() {
        this.startNewGame();
    }

    public void initializeLevel(String filename) {
        File file = new File(filename);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (true) {
            assert scanner != null;
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                lineScanner.next();
                columnCount++;
            }
            rowCount++;
        }
        columnCount = columnCount / rowCount;
        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid = new CellValue[rowCount][columnCount];
        int row = 0;
        int pacmanRow = 0;
        int pacmanColumn = 0;
        int ghost1row = 0;
        int ghost1column = 0;
        int ghost2row = 0;
        int ghost2column = 0;
        while (true) {
            assert scanner2 != null;
            if (!scanner2.hasNextLine()) break;
            int column = 0;
            String line = scanner2.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                String value = lineScanner.next();
                CellValue thisValue;
                switch (value) {
                    case "W" -> thisValue = CellValue.WALL;
                    case "S" -> {
                        thisValue = CellValue.SMALLDOT;
                        dotCount++;
                    }
                    case "B" -> {
                        thisValue = CellValue.BIGDOT;
                        dotCount++;
                    }
                    case "1" -> {
                        thisValue = CellValue.GHOSTHOME1;
                        ghost1row = row;
                        ghost1column = column;
                    }
                    case "2" -> {
                        thisValue = CellValue.GHOSTHOME2;
                        ghost2row = row;
                        ghost2column = column;
                    }
                    case "P" -> {
                        thisValue = CellValue.PACKHOME;
                        pacmanRow = row;
                        pacmanColumn = column;
                    }
                    default -> thisValue = CellValue.EMPTY;
                }
                grid[row][column] = thisValue;
                column++;
            }
            row++;
        }
        pacmanLocation = new Point2D(pacmanRow, pacmanColumn);
        pacmanVelocity = new Point2D(0, 0);
        ghost1Location = new Point2D(ghost1row, ghost1column);
        ghost1Velocity = new Point2D(-1, 0);
        ghost2Location = new Point2D(ghost2row, ghost2column);
        ghost2Velocity = new Point2D(-1, 0);
        currentDirection = Direction.NONE;
        lastDirection = Direction.NONE;
    }


    public void startNewGame() {
        gameOver = false;
        youWon = false;
        ghostEating = false;
        dotCount = 0;
        columnCount = 0;
        rowCount = 0;
        this.score = 0;
        this.level = 1;
        this.initializeLevel(Controller.getLevelFile(0));
    }
    public void startNextLevel() {
        if (this.isLevelComplete()) {
            this.level++;
            rowCount = 0;
            columnCount = 0;
            youWon = false;
            ghostEating = false;
            try {
                this.initializeLevel(Controller.getLevelFile(level - 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                youWon = true;
                gameOver = true;
                level--;
            }
        }
    }

    public void moveGhosts() {
        Point2D[] ghostData1 = moveAGhost(ghost1Velocity, ghost1Location);
        Point2D[] ghostData2 = moveAGhost(ghost2Velocity, ghost2Location);
        ghost1Velocity = ghostData1[0];
        ghost1Location = ghostData1[1];
        ghost2Velocity = ghostData2[0];
        ghost2Location = ghostData2[1];
    }

    public Point2D[] moveAGhost(Point2D velocity, Point2D location) {
        Random generator = new Random();
        if (!ghostEating) {
            if (location.getY() == pacmanLocation.getY()) {
                if (location.getX() > pacmanLocation.getX()) {
                    velocity = changeVelocity(Direction.UP);
                } else {
                    velocity = changeVelocity(Direction.DOWN);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else if (location.getX() == pacmanLocation.getX()) {
                if (location.getY() > pacmanLocation.getY()) {
                    velocity = changeVelocity(Direction.LEFT);
                } else {
                    velocity = changeVelocity(Direction.RIGHT);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            }
        }
        if (ghostEating) {
            if (location.getY() == pacmanLocation.getY()) {
                if (location.getX() > pacmanLocation.getX()) {
                    velocity = changeVelocity(Direction.DOWN);
                } else {
                    velocity = changeVelocity(Direction.UP);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else if (location.getX() == pacmanLocation.getX()) {
                if (location.getY() > pacmanLocation.getY()) {
                    velocity = changeVelocity(Direction.RIGHT);
                } else {
                    velocity = changeVelocity(Direction.LEFT);
                }
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNumb = generator.nextInt(4);
                    Direction direction = intToDirection(randomNumb);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            } else {
                Point2D potentialLocation = location.add(velocity);
                potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
                while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
                    int randomNum = generator.nextInt(4);
                    Direction direction = intToDirection(randomNum);
                    velocity = changeVelocity(direction);
                    potentialLocation = location.add(velocity);
                }
                location = potentialLocation;
            }
        }
        return new Point2D[]{velocity, location};
    }

    //sjekker om objektet går utafor brettet. Om sant vil objektet bli hentet inn igjen.
    public Point2D setGoingOffscreenNewLocation(Point2D newLocation) {
        //om objektet går utafor brettet på høgre side
        if (newLocation.getY() > columnCount) {
            newLocation = new Point2D(newLocation.getX(), 0);
        }

        //om objektet går utafor brettet på venstre side
        if (newLocation.getY() < 0) {
            newLocation = new Point2D(newLocation.getX(), columnCount - 1);
        }
        return newLocation;
    }

    public Point2D changeVelocity(Direction direction) {
        if (direction == Direction.LEFT) {
            return new Point2D(0, -1);
        } else if (direction == Direction.RIGHT) {
            return new Point2D(0, 1);
        } else if (direction == Direction.UP) {
            return new Point2D(-1, 0);
        } else if (direction == Direction.DOWN) {
            return new Point2D(1, 0);
        } else {
            return new Point2D(0, 0);
        }
    }

    public Direction intToDirection(int numb) {
        if (numb == 0) {
            return Direction.LEFT;
        } else if (numb == 1) {
            return Direction.RIGHT;
        } else if (numb == 2) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }



    public void sendGhostHome1() {
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (grid[row][column] == CellValue.GHOSTHOME1) {
                    ghost1Location = new Point2D(row, column);
                }
            }
        }
        ghost1Velocity = new Point2D(-1, 0);
    }

    public void sendGhostHome2() {
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (grid[row][column] == CellValue.GHOSTHOME2) {
                    ghost2Location = new Point2D(row, column);
                }
            }
        }
        ghost2Velocity = new Point2D(-1, 0);
    }

    public void step(Direction direction) {
        this.movePacman(direction);
        CellValue pacmanLocationCellValue = grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()];
        if (pacmanLocationCellValue == CellValue.SMALLDOT) {
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            dotCount--;
            score += 10;
        }
        if (pacmanLocationCellValue == CellValue.BIGDOT) {
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            dotCount--;
            score += 50;
            ghostEating = true;
            Controller.setGhostEatingCounter();
        }
        if (ghostEating) {
            if (pacmanLocation.equals(ghost1Location)) {
                sendGhostHome1();
                score += 100;
            }
            if (pacmanLocation.equals(ghost2Location)) {
                sendGhostHome2();
                score += 100;
            }
        }
        this.moveGhosts();
        if (ghostEating) {
            if (pacmanLocation.equals(ghost1Location)) {
                sendGhostHome1();
                score += 100;
            }
            if (pacmanLocation.equals(ghost2Location)) {
                sendGhostHome2();
                score += 100;
            }
        } else {
            if (pacmanLocation.equals(ghost1Location)) {
                gameOver = true;
                pacmanLocation = new Point2D(0, 0);
            }
            if (pacmanLocation.equals(ghost2Location)) {
                gameOver = true;
                pacmanLocation = new Point2D(0, 0);
            }
        }
        if (this.isLevelComplete()) {
            pacmanVelocity = new Point2D(0, 0);
            startNextLevel();
        }
    }

    public void movePacman(Direction direction) {
        Point2D potentialPacmanVelocity = changeVelocity(direction);
        Point2D potentialPacmanLocation = pacmanLocation.add(potentialPacmanVelocity);

        potentialPacmanLocation = setGoingOffscreenNewLocation(potentialPacmanLocation);

        if (direction.equals(lastDirection)) {
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                pacmanVelocity = changeVelocity(Direction.NONE);
                setLastDirection(Direction.NONE);
            } else {
                pacmanVelocity = potentialPacmanVelocity;
                pacmanLocation = potentialPacmanLocation;
            }
        } else {
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                potentialPacmanVelocity = changeVelocity(lastDirection);
                potentialPacmanLocation = pacmanLocation.add(potentialPacmanVelocity);
                if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                    pacmanVelocity = changeVelocity(Direction.NONE);
                    setLastDirection(Direction.NONE);
                } else {
                    pacmanVelocity = changeVelocity(lastDirection);
                    pacmanLocation = pacmanLocation.add(pacmanVelocity);
                }
            } else {
                pacmanVelocity = potentialPacmanVelocity;
                pacmanLocation = potentialPacmanLocation;
                setLastDirection(direction);
            }
        }
    }

    public boolean isLevelComplete() {
        return this.dotCount == 0;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public CellValue[][] getGrid() {
        return grid;
    }

    public void setGrid(CellValue[][] grid) {
        this.grid = grid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static Direction getCurrentDirection() {
        return currentDirection;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean gameOver) {
        PacmanModel.gameOver = gameOver;
    }

    public static boolean isYouWon() {
        return youWon;
    }

    public static void setYouWon(boolean youWon) {
        PacmanModel.youWon = youWon;
    }

    public static boolean isGhostEating() {
        return ghostEating;
    }

    public static void setGhostEating(boolean ghostEating) {
        PacmanModel.ghostEating = ghostEating;
    }

    public Point2D getPacmanLocation() {
        return pacmanLocation;
    }

    public void setPacmanLocation(Point2D pacmanLocation) {
        this.pacmanLocation = pacmanLocation;
    }

    public Point2D getPacmanVelocity() {
        return pacmanVelocity;
    }

    public void setPacmanVelocity(Point2D pacmanVelocity) {
        this.pacmanVelocity = pacmanVelocity;
    }

    public Point2D getGhost1Location() {
        return ghost1Location;
    }

    public void setGhost1Location(Point2D ghost1Location) {
        this.ghost1Location = ghost1Location;
    }

    public Point2D getGhost1Velocity() {
        return ghost1Velocity;
    }

    public void setGhost1Velocity(Point2D ghost1Velocity) {
        this.ghost1Velocity = ghost1Velocity;
    }

    public Point2D getGhost2Location() {
        return ghost2Location;
    }

    public void setGhost2Location(Point2D ghost2Location) {
        this.ghost2Location = ghost2Location;
    }

    public Point2D getGhost2Velocity() {
        return ghost2Velocity;
    }

    public void setGhost2Velocity(Point2D ghost2Velocity) {
        this.ghost2Velocity = ghost2Velocity;
    }

    public static Direction getLastDirection() {
        return lastDirection;
    }

    public static void setLastDirection(Direction lastDirection) {
        PacmanModel.lastDirection = lastDirection;
    }


    public static void setCurrentDirection(Direction currentDirection) {
        PacmanModel.currentDirection = currentDirection;
    }

    public CellValue getCellValue(int row, int column) {
        assert row >= 0 && row < this.grid.length && column >= 0 && column < this.grid[0].length;
        return this.grid[row][column];
    }
}
