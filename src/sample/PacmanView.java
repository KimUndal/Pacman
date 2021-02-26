package sample;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PacmanView extends Group {

    public final static double CELL_WIDTH = 20.0;
    @FXML
    private int rowCount;
    @FXML
    private int columnCount;
    private ImageView[][] cellViews;
    private final Image pacmanRightImage;
    private final Image pacmanLeftImage;
    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image ghostImage1;
    private final Image ghostImage2;
    private final Image blueGhostImage;
    private final Image wallImage;
    private final Image bigDotImage;
    private final Image smallDotImage;

    public PacmanView() {
        this.pacmanRightImage = new Image(getClass().getResourceAsStream("/gifs/pacmanRight.gif"));
        this.pacmanUpImage = new Image(getClass().getResourceAsStream("/gifs/pacmanUp.gif"));
        this.pacmanDownImage = new Image(getClass().getResourceAsStream("/gifs/pacmanDown.gif"));
        this.pacmanLeftImage = new Image(getClass().getResourceAsStream("/gifs/pacmanLeft.gif"));
        this.ghostImage1 = new Image(getClass().getResourceAsStream("/gifs/redghost.gif"));
        this.ghostImage2 = new Image(getClass().getResourceAsStream("/gifs/ghost2.gif"));
        this.blueGhostImage = new Image(getClass().getResourceAsStream("/gifs/blueghost.gif"));
        this.wallImage = new Image(getClass().getResourceAsStream("/gifs/wall.png"));
        this.bigDotImage = new Image(getClass().getResourceAsStream("/gifs/whitedot.png"));
        this.smallDotImage = new Image(getClass().getResourceAsStream("/gifs/smalldot.png"));
    }

    /*   private void initializeGrid() {
           if (this.rowCount > 0 && this.columnCount > 0) {
               this.cellViews = new ImageView[this.rowCount][this.columnCount];
               for (int row = 0; row < this.rowCount; row++) {
                   for (int column = 0; column < this.columnCount; column++) {
                       ImageView imageView = new ImageView();
                       imageView.setX((double) row * CELL_WIDTH);
                       imageView.setY((double) column * CELL_WIDTH);
                       imageView.setFitWidth(CELL_WIDTH);
                       imageView.setFitWidth(CELL_WIDTH);
                       this.cellViews[row][column] = imageView;
                       this.getChildren().add(imageView);
                   }
               }
           }
       }
   */
    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new ImageView[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setX((double) column * CELL_WIDTH);
                    imageView.setY((double) row * CELL_WIDTH);
                    imageView.setFitWidth(CELL_WIDTH);
                    imageView.setFitHeight(CELL_WIDTH);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    public void update(PacmanModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        //for each ImageView, set the image to correspond with the CellValue of that cell
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                PacmanModel.CellValue value = model.getCellValue(row, column);
                if (value == PacmanModel.CellValue.WALL) {
                    this.cellViews[row][column].setImage(this.wallImage);
                } else if (value == PacmanModel.CellValue.BIGDOT) {
                    this.cellViews[row][column].setImage(this.bigDotImage);
                } else if (value == PacmanModel.CellValue.SMALLDOT) {
                    this.cellViews[row][column].setImage(this.smallDotImage);
                } else {
                    this.cellViews[row][column].setImage(null);
                }
                //check which direction PacMan is going in and display the corresponding image
                if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && (PacmanModel.getLastDirection() == PacmanModel.Direction.RIGHT || PacmanModel.getLastDirection() == PacmanModel.Direction.NONE)) {
                    this.cellViews[row][column].setImage(this.pacmanRightImage);
                } else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == PacmanModel.Direction.LEFT) {
                    this.cellViews[row][column].setImage(this.pacmanLeftImage);
                } else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == PacmanModel.Direction.UP) {
                    this.cellViews[row][column].setImage(this.pacmanUpImage);
                } else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == PacmanModel.Direction.DOWN) {
                    this.cellViews[row][column].setImage(this.pacmanDownImage);
                }
                //make ghosts "blink" towards the end of ghostEatingMode (display regular ghost images on alternating updates of the counter)
                if (PacmanModel.isGhostEating() && (Controller.getGhostEatingModeCounter() == 6 || Controller.getGhostEatingModeCounter() == 4 || Controller.getGhostEatingModeCounter() == 2)) {
                    getGhostLocation1(model, row, column);
                }
                //display blue ghosts in ghostEatingMode
                else if (PacmanModel.isGhostEating()) {
                    if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
                        this.cellViews[row][column].setImage(this.blueGhostImage);
                    }
                    if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
                        this.cellViews[row][column].setImage(this.blueGhostImage);
                    }
                }
                //dispaly regular ghost images otherwise
                else {
                    getGhostLocation1(model, row, column);
                }
            }
        }
    }

    private void getGhostLocation1(PacmanModel model, int row, int column) {
        if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
            this.cellViews[row][column].setImage(this.ghostImage1);
        }
        if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
            this.cellViews[row][column].setImage(this.ghostImage2);
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }

}


