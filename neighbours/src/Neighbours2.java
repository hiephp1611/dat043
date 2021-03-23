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
public class Neighbours2 extends Application {

    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;      // false by default

        Actor(Color color) {      // Constructor to initialize
            this.color = color;
        }
    }

    class Pair {
        int Row;
        int Col;

        public Pair(int row, int col) {
            this.Row = row;
            this.Col = col;
        }
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors
    final Random rand = new Random();

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;

        Pair[] empties;
        empties = checking(world, threshold);
        replace(world, empties);

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
        Actor[] pre = getActor(nLocations, dist);
        world = toMatrix(pre);
        // Should be last
        fixScreenSize(nLocations);
    }


    // ---------------  Methods ------------------------------

    Pair[] checking(Actor[][] world, double threshold) {
        Pair[] arr = new Pair[world.length * world.length];
        int n = 0;
        for (int i = 0; i < world.length; i++) {
            for (int k = 0; k < world.length; k++) {
                if (world[i][k] != null) {
                    world[i][k].isSatisfied = isSatisfied(world, i, k, threshold);

                } else {
                    arr[n] = new Pair(i, k);
                    n++;
                }
            }
        }

        Pair[] arr2 = Arrays.copyOf(arr, n);
        shuffle(arr2);

        return arr2;
    }

    <T> void shuffle(T[] arr) {
        for (int i = arr.length; i > 0; i--) {
            int j = rand.nextInt(i);
            T tmp = arr[j];
            arr[j] = arr[i - 1];
            arr[i - 1] = tmp;
        }
    }

    void replace(Actor[][] world, Pair[] arr) {
        Pair[] disastified = new Pair[world.length * world.length];
        int n = 0;
        for (int r = 0; r < world.length; r++) {
            for (int c = 0; c < world.length; c++) {
                if (world[r][c] != null) {
                    if (!world[r][c].isSatisfied) {
                        disastified[n] = new Pair(r, c);
                        n++;
                    }
                }
            }
        }
        if (n > 0) {
            Pair[] dis2 = Arrays.copyOf(disastified, n);
            shuffle(dis2);
            for (int i = 0; i < min(arr.length, dis2.length); i++) {
                world[arr[i].Row][arr[i].Col] = world[dis2[i].Row][dis2[i].Col];
                world[dis2[i].Row][dis2[i].Col] = null;
            }
        }
        /*int blue = 0;
        int red = 0;
        int j = 0;
        //Pair[] newnull = new Pair[world.length* world.length- arr.length];
        for (int i = 0; i < world.length; i++) {
            for (int k = 0; k < world.length; k++) {
                if (world[i][k] != null) {
                    if (!world[i][k].isSatisfied) {
                        if (world[i][k].color== Color.BLUE){blue++;}
                        else{red++;}
                        world[i][k] = null;
                        //newnull[j] = new Pair(i,k);
                    }
                }
            }
        }
        //Pair[] shortarr = Arrays.copyOf(newnull,j);
        int n = 0;
        int m = 0;
        while(blue>0) {
            world[arr[n].Row][arr[n].Col] = new Actor(Color.BLUE);
            blue--;
//            arr[n] = shortarr[m];
//            m++;
            n = (n+1)%arr.length;
        }
        while (red >0){
            world[arr[n].Row][arr[n].Col]= new Actor(Color.RED);
            red--;
//            arr[n]= shortarr[m];
//            m++;
            n = (n+1)%arr.length;

        }*/

    }

    Actor[] getActor(int nLocations, double[] dist) {
        Actor[] pre = new Actor[nLocations];
        int reds = (int) (nLocations * dist[0]);
        int blues = (int) (nLocations * dist[1]) + reds;
        for (int i = 0; i < pre.length; i++) {
            if (i < reds) {
                pre[i] = new Actor(Color.RED);
            } else if (i < blues) {
                pre[i] = new Actor(Color.BLUE);
            } else {
            }
        }
        for (int i = pre.length; i > 1; i--) {
            int j = rand.nextInt(i);
            Actor tmp = pre[j];
            pre[j] = pre[i - 1];
            pre[i - 1] = tmp;
        }
        return pre;
    }

    Actor[][] toMatrix(Actor[] arr) {
        int size = (int) round(sqrt(arr.length));
        Actor[][] matrix = new Actor[size][size];
        for (int i = 0; i < arr.length; i++) {
            matrix[i / size][i % size] = arr[i];
        }
        return matrix;
    }

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    // TODO (general method possible reusable elsewhere)
    boolean isSatisfied(Actor[][] matr, int r, int c, double tresh) {
        Actor a = matr[r][c];
        double sames = 0;
        double antal = 0;
        double perc;
        if (a != null) {
            for (int i = r - 1; i < (r + 2); i++) {
                for (int k = c - 1; k < (c + 2); k++) {
                    if (isValidLocation(matr.length, i, k)) {
                        if (matr[i][k] != null) {
                            if (matr[i][k].color == a.color) {
                                sames = sames + 1;
                            }
                            antal = antal + 1;
                        }
                    }
                }
            }
        }
        if (antal == 1) {
            return false;
        } else {
            perc = ((sames - 1) / (antal - 1));
            return (tresh < perc);
        }
    }
    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null},
                {null, null, null},
                {new Actor(Color.RED), null, new Actor(Color.BLUE)}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));
        out.println(isSatisfied(testWorld, 2, 0, 0.3));
        // TODO

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