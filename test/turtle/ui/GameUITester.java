package turtle.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import turtle.comp.ColorType;
import turtle.core.Component;
import turtle.core.Location;
import turtle.file.CompSpec;
import turtle.file.Level;
import turtle.file.LevelPack;

/**
 * GameUITester.java
 * 
 * Ensures that the GameUI and its associate parts are working properly.
 * 
 * @author Henry Wang
 * Date: 4/29/17
 * Period: 2
 */
public class GameUITester extends Application
{
	private static final int TEST_SIZE = 20;
	private static final short COMP_IND_DOOR = (short)0;
	private static final short COMP_IND_WATER = (short)3;
	private static final short COMP_IND_EXIT = (short)4;
	private static final short COMP_IND_FIRE = (short)5;
	private static final short COMP_IND_SAND = (short)6;
	private static final short COMP_IND_PLAYER = (short)10;
	private static final short COMP_IND_KEY = (short)11;
	private static final short COMP_IND_BIRD = (short)13;
	private static final short COMP_IND_FOOD = (short)14;
	private static final short COMP_IND_HINT = (short)15;
	private static final short COMP_IND_TRAP = (short)16;
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	/**
	 * Starts the main application
	 * @param primaryStage the primary window that will first start up.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Map<String, Method> tests = initializeTests();
		System.out.println("Available tests: ");
		for (String test : tests.keySet())
			System.out.println("  -" + test);
		
		Method selected = null;
		Scanner in = new Scanner(System.in);
		while (selected == null)
		{
			System.out.printf("Enter a test to do: ");
			String test = in.nextLine();
			if (tests.containsKey(test))
				selected = tests.get(test);
			else
				System.out.println("Invalid test: " + test + ". Try again.");
		}
		
		GameUI gui = new GameUI();
		Scene s = new Scene(gui);
		s.getStylesheets().add("/turtle/ui/styles.css");
		
		primaryStage.setScene(s);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		gui.requestFocus();
		
		LevelPack pack = (LevelPack) selected.invoke(this);
		gui.initLevelPack(pack);
	}

	/**
	 * Initializes all the test methods found within this class.
	 * @return a mapping of test names and methods.
	 */
	private Map<String, Method> initializeTests()
	{
		final String TEST_PREFIX = "test";
		Map<String, Method> tests = new TreeMap<>();
		for (Method mth : GameUITester.class.getDeclaredMethods())
		{
			if (mth.getName().startsWith(TEST_PREFIX) && 
					mth.getParameterTypes().length == 0 &&
					LevelPack.class.isAssignableFrom(mth.getReturnType()))
			{
				mth.setAccessible(true);
				tests.put(mth.getName().substring(TEST_PREFIX.length()), mth);
			}
		}
		return tests;
	}

	/**
	 * Generates a test level pack that tests the items (keys and food items)
	 * the time limit, the food requirement, and also whether if the keys unlock
	 * doors.
	 * 
	 * @return a created level-pack.
	 */
	private LevelPack testItemTimes()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		test.setFoodRequirement(50);
		test.setTimeLimit(50);
		test.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
				new Location(1, 1), COMP_IND_EXIT, new HashMap<>()));
		
		fillCells(test);
		for (int r = 0; r < TEST_SIZE; r++)
			for (int c = 0; c < TEST_SIZE; c++)
			{
				if (r % 6 != 1)
				{
					HashMap<String, Object> params = new HashMap<>();
					if (Math.random() < .2)
					{
						params.put("color", (int)(Math.random() * ColorType.values()
								.length));
						test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
								new Location(r, c), COMP_IND_KEY, params));
					}
					else if (Math.random() < .5)
						test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
								new Location(r, c), COMP_IND_FOOD, params));
					else if (Math.random() < .5)
					{
						params.put("color", (int)(Math.random() * ColorType.values()
								.length));
						test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
								new Location(r, c), COMP_IND_DOOR, params));
					}
				}
			}
		
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(1, 13), COMP_IND_BIRD, new HashMap<>()));
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 1), COMP_IND_PLAYER, new HashMap<>()));
		testPack.addLevel(test);
		
		return testPack;
	}
	
	/**
	 * Tests the enemies' movement patterns.
	 * @return a level pack to test enemy patterns.
	 */
	private LevelPack testEnemies()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		test.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
				new Location(1, 1), COMP_IND_EXIT, new HashMap<>()));
		
		fillCells(test);
		
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(1, 13), COMP_IND_BIRD, new HashMap<>()));
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 1), COMP_IND_PLAYER, new HashMap<>()));
		testPack.addLevel(test);
		
		return testPack;
	}
	
	/**
	 * Generates test packs to test hint tiles.
	 * @return
	 */
	private LevelPack testHints()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		fillCells(test);
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("message", "This is a test hint.");
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 2), COMP_IND_HINT, params));
		params.put("message", "No, really! This is a test!");
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 3), COMP_IND_HINT, params));
		params.put("message", "Testing how long a string could be before " +
				"these ellipse will show up!! Hmmm, maybe a little longer " +
				"than I anticipated! Wait? Where are the ellipses? Pretty " + 
				"sure it's long enough for those '...'!!! Where is it? Okay " +
				"now i'm worried! Where are they? Oh I miss you :(");
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 4), COMP_IND_HINT, params));
		
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 1), COMP_IND_PLAYER, new HashMap<>()));
		testPack.addLevel(test);
		
		return testPack;
	}
	
	private LevelPack testTraps()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		fillCells(test);
		
		HashMap<String, Object> params = new HashMap<>();
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(1, 10), COMP_IND_BIRD, params));
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(1, 2), COMP_IND_TRAP, params));
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 1), COMP_IND_PLAYER, new HashMap<>()));
		testPack.addLevel(test);
		
		return testPack;
	}
	
	private LevelPack testLoading() throws IOException
	{
		return new LevelPack(new File("test.mtp"));
	}
	
	/**
	 * Fills the level with the base cells (water, fire, and sand)
	 * @param lvl the level to fill
	 */
	private static void fillCells(Level lvl)
	{
		for (int r = 0; r < TEST_SIZE; r++)
			for (int c = 0; c < TEST_SIZE; c++)
			{
				if (r % 6 == 1 || c == 0)
					lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
							new Location(r, c), COMP_IND_SAND, new HashMap<>()));
				else if (r % 6 == 0 && r > 0)
					lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
							new Location(r, c), COMP_IND_FIRE, new HashMap<>()));
				else
					lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
							new Location(r, c), COMP_IND_WATER, new HashMap<>()));
			}
		lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
				new Location(1, 1), COMP_IND_EXIT, new HashMap<>()));
	}
}
