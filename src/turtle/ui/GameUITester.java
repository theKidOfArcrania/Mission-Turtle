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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameUITester extends Application
{

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		GameUI gui = new GameUI();
		Scene s = new Scene(gui);
		
		primaryStage.setScene(s);
		primaryStage.show();
	}

}
