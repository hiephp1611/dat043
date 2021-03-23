import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.*;
import static java.lang.System.*;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    final Random rand = new Random();

    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;      // false by default

        Actor(Color color) {      // Constructor to initialize
            this.color = color;
        }
    }


    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.75;

        // TODO update world
       nextState(world, threshold);

    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 90000;   // Should also try 90 000

        // TODO initialize the world
        Actor[] array = creation(nLocations, dist);
        shuffle(array);
        world = toMatrix(array);


        // Should be last
        fixScreenSize(nLocations);
    }

    // ---------------  Methods ------------------------------

    // TODO Many ...

    Actor[] creation(int population, double[] percent) {

        Actor[] flatWorld = new Actor[population];
        int choose = 0;
        int sum = (int) round(percent[choose] * population);
        for (int i = 0; i < flatWorld.length ; i++) {
            if (i == sum) {
                choose++;
                sum += (int) round(percent[choose] * population);

            }
            if (choose==percent.length-1){
                break;
            }
            flatWorld[i] = place(choose);
        }
        return flatWorld;
    }

    void nextState(Actor[][] world, double threshold) {
        Integer[] nEmpties;
        nEmpties = nullArr(world,threshold );
        reorganize(world, nEmpties);
    }

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    // TODO (general method possible reusable elsewhere)
    Actor place(int alt) {
        if (alt == 0) {
            return new Actor(Color.RED);
        } else {
            return new Actor(Color.BLUE);
        }
    }

    Actor[][] toMatrix(Actor[] arr) {
        int size = (int) round(sqrt(arr.length));
        Actor[][] matrix = new Actor[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                matrix[row][col] = arr[row * size + col];
            }
        }
        return matrix;
    }

    boolean satisfied(Actor[][] world, int row, int col, double threshold)  {
        double numberNeighbour = 0;
        double numberAlike = 0;
        for (int i = row - 1; i < row + 2; i++) {
            for (int j = col - 1; j < col + 2; j++) {
                if (isValidLocation(world.length, i, j) && !(i == row && j == col)) {
                    if (world[i][j]!=null){
                        numberNeighbour++;
                        if (world[row][col].color== world[i][j].color) {
                            numberAlike++;
                        }
                    }

                }
            }
        }
        if (numberNeighbour == 0){ return false;}
        return  numberAlike >= numberNeighbour*threshold;
    }

    <T> void shuffle(T[] arr) {
        for (int i = arr.length; i > 0; i--) {
            int j = rand.nextInt(i);
            T tmp = arr[j];
            arr[j] = arr[i-1];
            arr[i-1] = tmp;
        }
    }

    Integer[] trim(Integer[] longArr, int size) {
        Integer[] newArr = new Integer[size];
        for (int i = 0; i < size; i++) {
            newArr[i] = longArr[i];
        }
        return newArr;
    }

    Integer[] nullArr(Actor[][] world, double threshold) {
        Integer[] array = new Integer[world.length * world.length];
        Integer arrLen = 0;
        for (int r = 0; r < world.length; r++) {
            for (int c = 0; c < world.length; c++) {
                if (world[r][c] == null) {
                    array[arrLen] = world.length * r + c;
                    arrLen++;
                }else{
                    world[r][c].isSatisfied =satisfied(world,r,c,threshold);
                }
            }
        }
        return trim(array, arrLen);

    }

    void reorganize(Actor[][] world, Integer[] nullArr){
        shuffle(nullArr);
        int k=0;
        for (int r = 0; r < world.length; r++) {
            for (int c = 0; c < world.length ; c++) {
                if(world[r][c] != null && k< nullArr.length){
                    if (!(world[r][c].isSatisfied)) {
                        //int n = rand.nextInt(nullArr.length);
                        Actor tmp = world[r][c];
                        int index = nullArr[k];
                        world[r][c] = null;
                        world[(index/ world.length)][index % world.length] = tmp;
                        //nullArr[k]=r*world.length+c;
                        //k= (k+1)%nullArr.length;
                        k++;
                    }
                }
            }
        }
    }

    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null},
                {null, new Actor(Color.RED), null},
                {null, new Actor(Color.BLUE), new Actor(Color.BLUE)}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(satisfied(testWorld, 1, 1, 0.7));
        //out.println(swap(testWorld,0, 0));
        //out.println(!isValidLocation(size, 0, 3));

        // TODO
        nextState(testWorld,0.75);
        /*testWorld=swap(testWorld, 0, 4);
        out.println(testWorld[1][1].color==Color.RED);*/

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
