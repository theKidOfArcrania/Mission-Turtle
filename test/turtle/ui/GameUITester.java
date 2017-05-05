/**
 * GameUITester.java
 * 
 * Ensures that the GameUI and its associate parts are working properly.
 * 
 * @author Henry Wang
 * Date: 4/29/17
 * Period: 2
 */
package turtle.ui;

import java.util.HashMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import turtle.comp.ColorType;
import turtle.comp.Fire;
import turtle.core.Component;
import turtle.core.Grid;
import turtle.core.Location;
import turtle.file.CompSpec;
import turtle.file.Level;
import turtle.file.LevelPack;

public class GameUITester extends Application
{

	private static final short COMP_IND_WATER = (short)3;
	private static final short COMP_IND_FIRE = (short)4;
	private static final short COMP_IND_PLAYER = (short)9;
	private static final short COMP_IND_KEY = (short)10;
	
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
		GameUI gui = new GameUI();
		Scene s = new Scene(gui);
		s.getStylesheets().add("/turtle/ui/styles.css");
		
		primaryStage.setScene(s);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		gui.requestFocus();
		
		LevelPack pack = generateTestPack();
		gui.initLevelPack(pack);
		
		Grid lvl = gui.getGridView().getGrid();
		//((Fire)lvl.getCellAt(0, 0)).transformToSand();
		((Fire)lvl.getCellAt(6, 0)).transformToSand();
	}

	/**
	 * Generates a test level pack to test against.
	 * @return a created level-pack.
	 */
	private LevelPack generateTestPack()
	{
		LevelPack testPack = new LevelPack("Test Pack");
		Level test = new Level("Test Level", 20, 20);
		
		for (int r = 0; r < 20; r++)
			for (int c = 0; c < 20; c++)
			{
				if (r % 6 == 0 && r > 0)
					test.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
							new Location(r, c), COMP_IND_FIRE, new HashMap<>()));
				else
					test.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
							new Location(r, c), COMP_IND_WATER, new HashMap<>()));
				
				HashMap<String, Object> params = new HashMap<>();
				if (Math.random() < .2)
				{
					params.put("color", (int)(Math.random() * ColorType.values()
							.length));
					test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET,
							new Location(r, c), COMP_IND_KEY, params));
				}
				
			}
		
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 1), COMP_IND_PLAYER, new HashMap<>()));
		testPack.addLevel(test);
		
		return testPack;
	}
}
