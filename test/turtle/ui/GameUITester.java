package turtle.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.stage.Stage;
import turtle.comp.ColorType;
import turtle.core.Actor;
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
	private static final Location BIRD_LOC = new Location(1, 13);
	private static final Location PLAYER_LOC = new Location(0, 0);

	private static final String TEST_PREFIX = "test";
	
	private static final int TEST_SIZE = 20;
	
	private static final short COMP_IND_DOOR = (short)0;
	private static final short COMP_IND_GRASS = (short)1;
	private static final short COMP_IND_PLASTIC = (short)2;
	private static final short COMP_IND_WATER = (short)3;
	private static final short COMP_IND_EXIT = (short)4;
	private static final short COMP_IND_FIRE = (short)5;
	private static final short COMP_IND_SAND = (short)6;
	private static final short COMP_IND_BUCKET = (short)7;
	private static final short COMP_IND_CANNON = (short)8;
	private static final short COMP_IND_PROJECTILE = (short)9;
	private static final short COMP_IND_PLAYER = (short)10;
	private static final short COMP_IND_KEY = (short)11;
	private static final short COMP_IND_BIRD = (short)13;
	private static final short COMP_IND_FOOD = (short)14;
	private static final short COMP_IND_HINT = (short)15;
	private static final short COMP_IND_TRAP = (short)16;
	private static final short COMP_IND_BUTTON = (short)17;
	private static final short COMP_IND_FACTORY = (short)18;
	private static final short COMP_IND_LAWNMOWER = (short)19;
	private static final short COMP_IND_CHILD = (short)20;
	
	
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	/**
	 * Utility method to adding cell specs
	 * @param lvl level object
	 * @param loc location of actor
	 * @param id component id
	 * @param params extra parameters to pass to component
	 */
	private static void addCellSpecs(Level lvl, Location loc, short id, 
			HashMap<String, Object> params)
	{
		lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				loc, id, params));
	}
	
	/**
	 * Utility method to adding actor specs
	 * @param lvl level object
	 * @param loc location of actor
	 * @param id component id
	 * @param params extra parameters to pass to component
	 */
	private static void addActorSpecs(Level lvl, Location loc, short id, 
			HashMap<String, Object> params)
	{
		lvl.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				loc, id, params));
	}
	
	/**
	 * Fills the level with the base cells (water, fire, and sand)
	 * @param lvl the level to fill
	 */
	private static void fillCells(Level lvl)
	{
		HashMap<String, Object> params = new HashMap<>();
		for (int r = 0; r < TEST_SIZE; r++)
			for (int c = 0; c < TEST_SIZE; c++)
			{
				Location loc = new Location(r, c);
				if (r % 6 == 1 || c == 0)
					addCellSpecs(lvl, loc, COMP_IND_SAND, params);
				else if (r % 6 == 0 && r > 0)
					addCellSpecs(lvl, loc, COMP_IND_FIRE, params);
				else
					addCellSpecs(lvl, loc, COMP_IND_WATER, params);
			}
		lvl.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
				new Location(1, 1), COMP_IND_EXIT, new HashMap<>()));
	}
	
	private Scanner in;
	
	/**
	 * Starts the main application
	 * @param primaryStage the primary window that will first start up.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		//Map<String, Method> tests = initializeTests();
		ArrayList<Method> tests = initializeTests();
		System.out.println("Available tests: ");
		for (int i = 0; i < tests.size(); i++)
			System.out.println("  - (" + i + ") " + tests.get(i).getName().
					substring(TEST_PREFIX.length()));
		
		Method selected = null;
		in = new Scanner(System.in);
		while (selected == null)
		{
			System.out.printf("Enter the index of test to do: ");
			Scanner resp = new Scanner(in.nextLine());
			if (resp.hasNextInt())
			{
				int index = resp.nextInt();
				if (index < 0 || index >= tests.size())
					System.out.println("Invalid index. Try again");
				else
					selected = tests.get(index);
			}
			else
				System.out.println("Invalid number. Try again.");
		}
		MainApp app = new MainApp();
		LevelPack pack = (LevelPack) selected.invoke(this);
		
		app.start(primaryStage);
		app.startGame(pack, 7);
		primaryStage.requestFocus();
	}

	/**
	 * Initializes all the test methods found within this class.
	 * @return a sorted list of test methods.
	 */
	private ArrayList<Method> initializeTests()
	{
		
		ArrayList<Method> tests = new ArrayList<>();
		for (Method mth : GameUITester.class.getDeclaredMethods())
		{
			if (mth.getName().startsWith(TEST_PREFIX) && 
					mth.getParameterTypes().length == 0 &&
					LevelPack.class.isAssignableFrom(mth.getReturnType()))
			{
				mth.setAccessible(true);
				tests.add(mth);
			}
		}
		
		/**
		 * Compares methods by their name, sorting by A-Z
		 */
		tests.sort(new Comparator<Method>()
		{

			/**
			 * Compares two methods, effectively by alphabetical order via name
			 * @param o1 the first method 
			 * @param o2 the second method
			 * @return negative value if first method comes first in alpha order, 
			 * 		zero if both are equal,
			 * 		positive value if second method cones first in alpha order.
			 */
			@Override
			public int compare(Method o1, Method o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		return tests;
	}

	/**
	 * Generates a test level pack that tests the plastic wraps and grass.
	 * 
	 * @return a created level-pack.
	 */
	private LevelPack testPlasticWrapGrass()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		HashMap<String, Object> params = new HashMap<>();
		fillCells(test);
		for (int r = 0; r < TEST_SIZE; r++)
			for (int c = 0; c < TEST_SIZE; c++)
			{
				if (r % 6 != 1 && (r != 0 || c != 0))
				{
					if (Math.random() < .1)
						addActorSpecs(test, new Location(r, c), 
								COMP_IND_PLASTIC, params);
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
	private LevelPack testItemTimes()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		HashMap<String, Object> params = new HashMap<>();
		
		test.setFoodRequirement(50);
		test.setTimeLimit(50);
		
		addCellSpecs(test, new Location(1, 1), COMP_IND_EXIT, params);
		fillCells(test);
		for (int r = 0; r < TEST_SIZE; r++)
			for (int c = 0; c < TEST_SIZE; c++)
			{
				if (r % 6 != 1)
				{
					int rand = (int)(Math.random() * ColorType.values().length);
					Location loc = new Location(r, c);
					if (Math.random() < .2)
					{
						params.put("color", rand);
						addActorSpecs(test, loc, COMP_IND_KEY, params);
					}
					else if (Math.random() < .5)
						addActorSpecs(test, loc, COMP_IND_FOOD, params);
					else if (Math.random() < .5)
					{
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
	 * @return test level pack 
	 */
	private LevelPack testBuckets()
	{
		final Location BUCKT_A = new Location(1, 1);
		final Location BUCKT_B = new Location(1, 3);
		final Location BUCKT_C = new Location(1, 5);
		
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		HashMap<String, Object> params = new HashMap<>();
		addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
		addActorSpecs(test, BUCKT_A, COMP_IND_BUCKET, params);
		addActorSpecs(test, BUCKT_B, COMP_IND_BUCKET, params);
		
		params.put("filled", true);	
		addActorSpecs(test, BUCKT_C, COMP_IND_BUCKET, params);
		
		fillCells(test);
		testPack.addLevel(test);
		
		return testPack;
	}
	
	/**
	 * Tests the buttons and factory functionality.
	 * @return a test level pack.
	 */
	private LevelPack testButtons()
	{
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
		
		params.put("heading", Actor.EAST);
		addCellSpecs(test, FACT_A, COMP_IND_FACTORY, params);
		
		System.out.print("Input a comonent ID to clone (9 is default): ");
		if (in.hasNextShort())
			params.put("cloned", in.nextShort());
		else
			params.put("cloned", COMP_IND_PROJECTILE);
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
	 * @return a level pack to test cannons.
	 */
	private LevelPack testCannon()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		fillCells(test);
		
		HashMap<String, Object> params = new HashMap<>();
		addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, params);
		
		params.put("heading", Actor.EAST);
		addActorSpecs(test, new Location(1, 0), COMP_IND_CANNON, params);
		
		params.clear();
		addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
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
		
		addCellSpecs(test, new Location(1, 1), COMP_IND_EXIT, new HashMap<>());
		fillCells(test);
		HashMap<String, Object> params = new HashMap<>();
		params.put("heading", Actor.WEST);
		addActorSpecs(test, new Location(1, 2), COMP_IND_LAWNMOWER, 
				params);
		addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, new HashMap<>());
		addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, new HashMap<>());
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
		params.put("message", "This is a test hint under us.");
		addActorSpecs(test, new Location(0,1), COMP_IND_HINT, params);
		params.put("message", "This is a test hint.");
		addActorSpecs(test, new Location(0, 2), COMP_IND_HINT, params);
		params.put("message", "No, really! This is a test!");
		addActorSpecs(test, new Location(0, 3), COMP_IND_HINT, params);
		params.put("message", "Testing how long a string could be before " +
				"these ellipse will show up!! Hmmm, maybe a little longer " +
				"than I anticipated! Wait? Where are the ellipses? Pretty " + 
				"sure it's long enough for those '...'!!! Where is it? Okay " +
				"now i'm worried! Where are they? Oh I miss you :(");
		addActorSpecs(test, new Location(0, 4), COMP_IND_HINT, params);
		params.clear();
		
		addActorSpecs(test, new Location(0, 1), COMP_IND_PLAYER, params);
		testPack.addLevel(test);
		
		return testPack;
	}
	
	private LevelPack testTraps()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", TEST_SIZE, TEST_SIZE);
		
		fillCells(test);
		
		HashMap<String, Object> params = new HashMap<>();
		addActorSpecs(test, BIRD_LOC, COMP_IND_BIRD, params);
		addActorSpecs(test, new Location(1, 2), COMP_IND_TRAP, params);
		addActorSpecs(test, PLAYER_LOC, COMP_IND_PLAYER, params);
		testPack.addLevel(test);
		
		return testPack;
	}
	
	private LevelPack testLoading() throws IOException
	{
		return new LevelPack(new File("test.mtp"));
	}
	
}
