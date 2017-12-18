package turtle.ui;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import turtle.comp.ColorType;
import turtle.core.Component;
import turtle.core.Direction;
import turtle.core.Location;
import turtle.file.CompSpec;
import turtle.file.Level;
import turtle.file.LevelPack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Ensures that the GameUI and its associate parts are working properly.
 *
 * @author Henry Wang
 */
@SuppressWarnings("unused")
public class GameUITester extends Application {
    public static final int CYCLE_PATTERN = 6;
    public static final double PLASTIC_CHANCE = .1;
    public static final double FIFTY_CHANCE = .5;

    private static final Location EXIT_LOC = new Location(1, 1);
    private static final Location BIRD_LOC = new Location(1, 13);
    private static final Location PLAYER_LOC = new Location(0, 0);

    private static final String TEST_PREFIX = "test";
    private static final int TEST_SIZE = 20;

    private static final short COMP_IND_DOOR = (short) 0;
    private static final short COMP_IND_GRASS = (short) 1;
    private static final short COMP_IND_PLASTIC = (short) 2;
    private static final short COMP_IND_WATER = (short) 3;
    private static final short COMP_IND_EXIT = (short) 4;
    private static final short COMP_IND_FIRE = (short) 5;
    private static final short COMP_IND_SAND = (short) CYCLE_PATTERN;
    private static final short COMP_IND_BUCKET = (short) 7;
    private static final short COMP_IND_CANNON = (short) 8;
    private static final short COMP_IND_PROJECTILE = (short) 9;
    private static final short COMP_IND_PLAYER = (short) 10;
    private static final short COMP_IND_KEY = (short) 11;
    private static final short COMP_IND_WALL = (short) 12;
    private static final short COMP_IND_BIRD = (short) 13;
    private static final short COMP_IND_FOOD = (short) 14;
    private static final short COMP_IND_HINT = (short) 15;
    private static final short COMP_IND_TRAP = (short) 16;
    private static final short COMP_IND_LAWNMOWER = (short) 17;
    private static final short COMP_IND_CHILD = (short) 18;
    private static final short COMP_IND_BUTTON = (short) 19;
    private static final short COMP_IND_FACTORY = (short) 20;
    private static final short COMP_IND_TEST = (short) 21;
    private Scanner in;
    private MainApp app;

    @SuppressWarnings("javadoc")
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Utility method to adding cell specs
     *
     * @param lvl    level object
     * @param loc    location of actor
     * @param id     component id
     * @param params extra parameters to pass to component
     */
    private static void addCellSpecs(Level lvl, Location loc, short id,
                                     HashMap<String, Object> params) {
        lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
                loc, id, params));
    }

    /**
     * Utility method to adding actor specs
     *
     * @param lvl    level object
     * @param loc    location of actor
     * @param id     component id
     * @param params extra parameters to pass to component
     */
    private static void addActorSpecs(Level lvl, Location loc, short id,
                                      HashMap<String, Object> params) {
        lvl.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
                loc, id, params));
    }

    /**
     * Fills the level with the base cells (water, fire, and sand)
     *
     * @param lvl the level to fill
     */
    private static void fillCells(Level lvl) {
        HashMap<String, Object> params = new HashMap<>();
        for (int r = 0; r < TEST_SIZE; r++)
            for (int c = 0; c < TEST_SIZE; c++) {
                Location loc = new Location(r, c);
                if (r % CYCLE_PATTERN == 1 || c == 0) {
                    addCellSpecs(lvl, loc, COMP_IND_SAND, params);
                } else if (r % CYCLE_PATTERN == 0 && r > 0) {
                    addCellSpecs(lvl, loc, COMP_IND_FIRE, params);
                } else {
                    addCellSpecs(lvl, loc, COMP_IND_WATER, params);
                }
            }
        lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
                EXIT_LOC, COMP_IND_EXIT, new HashMap<>()));
    }

    /**
     * Fills the level with sand and walls
     *
     * @param lvl   the level to fill
     * @param walls true to add a sprinkle of walls
     */
    private static void fillSandCells(Level lvl, boolean walls) {
        final double WALL_CHANCE = .05;
        HashMap<String, Object> params = new HashMap<>();
        for (int r = 0; r < TEST_SIZE; r++)
            for (int c = 0; c < TEST_SIZE; c++) {
                Location loc = new Location(r, c);
                if (Math.random() < WALL_CHANCE && walls) {
                    addCellSpecs(lvl, loc, COMP_IND_WALL, params);
                } else {
                    addCellSpecs(lvl, loc, COMP_IND_SAND, params);
                }
            }
        lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
                EXIT_LOC, COMP_IND_EXIT, new HashMap<>()));
    }

    /**
     * Starts the main application
     *
     * @param primaryStage the primary window that will first start up.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Map<String, Method> tests = initializeTests();
        ArrayList<Method> tests = initializeTests();
        System.out.println("Available tests: ");
        for (int i = 0; i < tests.size(); i++)
            System.out.println("  - (" + i + ") " + tests.get(i).getName().
                    substring(TEST_PREFIX.length()));

        Method selected = null;
        in = new Scanner(System.in);
        while (selected == null) {
            System.out.printf("Enter the index of test to do: ");
            Scanner resp = new Scanner(in.nextLine());
            if (resp.hasNextInt()) {
                int index = resp.nextInt();
                if (index < 0 || index >= tests.size()) {
                    System.out.println("Invalid index. Try again");
                } else {
                    selected = tests.get(index);
                }
            } else {
                System.out.println("Invalid number. Try again.");
            }
        }

        app = new MainApp();
        app.start(primaryStage);

        if (LevelPack.class.isAssignableFrom(selected.getReturnType())) {
            LevelPack pack = (LevelPack) selected.invoke(this);
            app.startGame(pack, 0);
        } else {
            selected.invoke(this);
        }
    }

    /**
     * Initializes all the test methods found within this class.
     *
     * @return a sorted list of test methods.
     */
    private ArrayList<Method> initializeTests() {

        ArrayList<Method> tests = new ArrayList<>();
        for (Method mth : GameUITester.class.getDeclaredMethods()) {
            if (mth.getName().startsWith(TEST_PREFIX) &&
                    mth.getParameterTypes().length == 0) {
                mth.setAccessible(true);
                tests.add(mth);
            }
        }

        tests.sort(Comparator.comparing(Method::getName));
        return tests;
    }

    /**
     * Generates a test level pack that tests the plastic wraps and grass.
     *
     * @return a created level-pack.
     */
    private LevelPack testPlasticWrapGrass() {
        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        HashMap<String, Object> params = new HashMap<>();
        fillCells(test);
        for (int r = 0; r < TEST_SIZE; r++)
            for (int c = 0; c < TEST_SIZE; c++) {
                if (r % CYCLE_PATTERN != 1 && (r != 0 || c != 0)) {
                    if (Math.random() < PLASTIC_CHANCE) {
                        addActorSpecs(test, new Location(r, c),
                                COMP_IND_PLASTIC, params);
                    }
                    addActorSpecs(test, new Location(r, c),
                            COMP_IND_GRASS, params);
                }
            }

        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Generates a test level pack that tests the items (keys and food items)
     * the time limit, the food requirement, and also whether if the keys unlock
     * doors.
     *
     * @return a created level-pack.
     */
    private LevelPack testItemTimes() {
        final int FOOD_REQ = 50;
        final int TIME_LIMIT = 50;
        final double KEY_CHANCE = .2;

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        HashMap<String, Object> params = new HashMap<>();

        test.setFoodRequirement(FOOD_REQ);
        test.setTimeLimit(TIME_LIMIT);

        ColorType[] values = ColorType.values();
        addCellSpecs(test, EXIT_LOC, COMP_IND_EXIT, params);
        fillCells(test);
        for (int r = 0; r < TEST_SIZE; r++)
            for (int c = 0; c < TEST_SIZE; c++) {
                if (r % CYCLE_PATTERN != 1) {
                    ColorType rand = values[(int) (Math.random() * values.length)];
                    Location loc = new Location(r, c);
                    if (Math.random() < KEY_CHANCE) {
                        params.put("color", rand);
                        addActorSpecs(test, loc, COMP_IND_KEY, params);
                    } else if (Math.random() < FIFTY_CHANCE) {
                        addActorSpecs(test, loc, COMP_IND_FOOD, params);
                    } else if (Math.random() < FIFTY_CHANCE) {
                        params.put("color", rand);
                        addActorSpecs(test, loc, COMP_IND_DOOR, params);
                    }
                    params.clear();
                }
            }

        addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, params);
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    //TODO: test with no player.

    /**
     * Tests the bucket functionality.
     *
     * @return test level pack
     */
    private LevelPack testBuckets() {
        final Location BUCKET_A = new Location(1, 1);
        final Location BUCKET_B = new Location(1, 3);
        final Location BUCKET_C = new Location(1, 5);

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        HashMap<String, Object> params = new HashMap<>();
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        addActorSpecs(test, BUCKET_A, COMP_IND_BUCKET, params);
        addActorSpecs(test, BUCKET_B, COMP_IND_BUCKET, params);

        params.put("filled", true);
        addActorSpecs(test, BUCKET_C, COMP_IND_BUCKET, params);

        fillCells(test);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the buttons and factory functionality.
     *
     * @return a test level pack.
     */
    private LevelPack testButtons() {
        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        final Location FACT_A = new Location(1, 1);
        final Location FACT_B = new Location(1, 3);
        final Location BUT_LNK_A = new Location(2, 1);
        final Location BUT_LNK_B = new Location(2, 3);
        final Location BUT_NO_LNK = new Location(3, 1);
        final Location BUT_BAD_LNK = new Location(4, 1);

        HashMap<String, Object> params = new HashMap<>();
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);

        params.put("heading", Direction.EAST);
        addCellSpecs(test, FACT_A, COMP_IND_FACTORY, params);

        System.out.print("Input a component ID to clone (9 is default): ");
        if (in.hasNextShort()) {
            params.put("cloned", in.nextShort());
        } else {
            params.put("cloned", COMP_IND_PROJECTILE);
        }
        addCellSpecs(test, FACT_B, COMP_IND_FACTORY, params);
        params.clear();

        params.put("linked", FACT_A);
        addActorSpecs(test, BUT_LNK_A, COMP_IND_BUTTON, params);
        params.put("linked", FACT_B);
        addActorSpecs(test, BUT_LNK_B, COMP_IND_BUTTON, params);
        params.put("linked", PLAYER_LOC);
        addActorSpecs(test, BUT_BAD_LNK, COMP_IND_BUTTON, params);
        params.clear();
        addActorSpecs(test, BUT_NO_LNK, COMP_IND_BUTTON, params);

        fillCells(test);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the functionality of cannons.
     *
     * @return a level pack to test cannons.
     */
    private LevelPack testCannon() {
        Location CANNON_LOC = new Location(1, 0);

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        fillCells(test);

        HashMap<String, Object> params = new HashMap<>();
        addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, params);

        params.put("heading", Direction.EAST);
        addActorSpecs(test, CANNON_LOC, COMP_IND_CANNON, params);

        params.clear();
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the functionality of the child
     *
     * @return a test level pack
     */
    private LevelPack testChild() {
        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        fillSandCells(test, true);

        HashMap<String, Object> params = new HashMap<>();
        addActorSpecs(test, BIRD_LOC, COMP_IND_CHILD, params);
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the functionality of the lawnmower
     *
     * @return a test level pack
     */
    private LevelPack testLawnMower() {
        final double MOWER_CHANCE = .05;

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        fillSandCells(test, true);

        HashMap<String, Object> params = new HashMap<>();
        for (int r = 0; r < TEST_SIZE; r++)
            for (int c = 0; c < TEST_SIZE; c++) {
                if (Math.random() < MOWER_CHANCE) {
                    params.put("heading", Direction.randomDirection());
                    addActorSpecs(test, new Location(r, c), COMP_IND_LAWNMOWER,
                            params);
                }
            }


        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the enemies' movement patterns.
     *
     * @return a level pack to test enemy patterns.
     */
    private LevelPack testEnemies() {
        final Location MOWER_LOC = new Location(1, 2);

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        addCellSpecs(test, EXIT_LOC, COMP_IND_EXIT, new HashMap<>());
        fillCells(test);
        HashMap<String, Object> params = new HashMap<>();
        params.put("heading", Direction.WEST);
        addActorSpecs(test, MOWER_LOC, COMP_IND_LAWNMOWER, params);
        addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, new HashMap<>());
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, new HashMap<>());
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Generates test packs to test dominance levels
     *
     * @return a level pack to test dominance
     */
    private LevelPack testDominance() {
        final Location LOC_A = new Location(0, 1);
        final Location LOC_B = new Location(0, 2);
        final Location LOC_C = new Location(0, 3);
        final Location LOC_D = new Location(0, 4);
        final Location LOC_E = new Location(0, 5);
        final Location LOC_F = new Location(0, CYCLE_PATTERN);

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        fillSandCells(test, false);

        //Test adding. We should see squares with colors RED RED BLUE
        HashMap<String, Object> params = new HashMap<>();
        params.put("level", 1);
        params.put("back", Color.RED);
        addActorSpecs(test, LOC_A, COMP_IND_TEST, params);
        params.put("level", 2);
        params.put("back", Color.BLUE);
        addActorSpecs(test, LOC_A, COMP_IND_TEST, params);
        addActorSpecs(test, LOC_B, COMP_IND_TEST, params);
        params.put("level", 1);
        params.put("back", Color.RED);
        addActorSpecs(test, LOC_B, COMP_IND_TEST, params);
        addActorSpecs(test, LOC_C, COMP_IND_TEST, params);
        params.put("back", Color.BLUE);
        addActorSpecs(test, LOC_C, COMP_IND_TEST, params);
        params.clear();

        //Test interact. Only first will print out stuff.
        params.put("back", Color.GREEN);
        params.put("level", 1);
        addActorSpecs(test, LOC_D, COMP_IND_TEST, params);
        params.put("level", 0);
        addActorSpecs(test, LOC_E, COMP_IND_TEST, params);
        params.put("level", -1);
        addActorSpecs(test, LOC_F, COMP_IND_TEST, params);
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Generates test packs to test hint tiles.
     *
     * @return a level pack to test hints
     */
    private LevelPack testHints() {
        final Location LOC_A = new Location(0, 1);
        final Location LOC_B = new Location(0, 2);
        final Location LOC_C = new Location(0, 3);
        final Location LOC_D = new Location(0, 4);

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        fillCells(test);

        HashMap<String, Object> params = new HashMap<>();
        params.put("message", "This is a test hint under us.");
        addActorSpecs(test, LOC_A, COMP_IND_HINT, params);
        params.put("message", "This is a test hint.");
        addActorSpecs(test, LOC_B, COMP_IND_HINT, params);
        params.put("message", "No, really! This is a test!");
        addActorSpecs(test, LOC_C, COMP_IND_HINT, params);
        params.put("message", "Testing how long a string could be before " +
                "these ellipse will show up!! Hmmm, maybe a little longer " +
                "than I anticipated! Wait? Where are the ellipses? Pretty " +
                "sure it's long enough for those '...'!!! Where is it? Okay " +
                "now i'm worried! Where are they? Oh I miss you :(");
        addActorSpecs(test, LOC_D, COMP_IND_HINT, params);
        params.clear();

        addActorSpecs(test, LOC_A, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the trap functionality.
     *
     * @return a level pack testing traps
     */
    private LevelPack testTraps() {
        final Location TRAP_LOC = new Location(1, 2);

        LevelPack testPack = new LevelPack("Test Pack");
        Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);

        fillCells(test);

        HashMap<String, Object> params = new HashMap<>();
        addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, params);
        addActorSpecs(test, TRAP_LOC, COMP_IND_TRAP, params);
        addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
        testPack.addLevel(test);

        return testPack;
    }

    /**
     * Tests the loading functionality.
     *
     * @return the loaded test level-pack.
     * @throws IOException if an error occurs while reading level pack.
     */
    private LevelPack testLoading() throws IOException {
        return new LevelPack(new File("test.mtp"));
    }

    /**
     * Unlocks all levels by marking them as done.
     *
     * @throws IOException if an error occurs while saving scores.
     */
    private void testUnlock() throws IOException {
        for (LevelPack pack : app.getLevelPacks())
            for (int i = 0; i < pack.getLevelCount(); i++)
                app.unlockLevel(pack, i);
    }

}
