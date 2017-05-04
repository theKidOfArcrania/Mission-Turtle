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
import turtle.core.Component;
import turtle.core.Location;
import turtle.file.CompSpec;
import turtle.file.Level;
import turtle.file.LevelPack;

public class GameUITester extends Application
{

	private static final short COMP_IND_WATER = (short)3;
	private static final short COMP_IND_PLAYER = (short)9;

	public static void main(String[] args)
	{
		Application.launch(args);
	}

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
				test.getCellCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
						new Location(r, c), COMP_IND_WATER, new HashMap<>()));				
		
		test.getActorCompSpecs().add(new CompSpec(Component.DEFAULT_SET, 
				new Location(0, 1), COMP_IND_PLAYER, new HashMap<>()));
		testPack.addLevel(test);
		
		return testPack;
	}
}
