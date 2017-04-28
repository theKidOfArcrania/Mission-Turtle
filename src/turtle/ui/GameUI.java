/**
 * GameUI.java
 * 
 * This displays the main game UI that the user will interact with.
 * @author Henry Wang
 * Date: 4/28/17
 * Period: 2
 */

package turtle.ui;

import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import turtle.comp.Player;
import turtle.core.Actor;
import turtle.core.GridView;

public class GameUI extends StackPane
{
	private static final Duration FRAME_DURATION = Duration.millis(20);
	
	/**
	 * Runs the game timer, keep tracks of the game states each frame.
	 * 
	 */
	private class GameTimer extends Transition
	{
		private long frame;
		
		/**
		 * Constructs a new GameTimer.
		 */
		public GameTimer(){
			frame = 0;
			setCycleCount(INDEFINITE);
			setCycleDuration(FRAME_DURATION);
		}
		
		/**
		 * Handles each frame tick of the animation timer.
		 * @param frac double from 0 to 1 to determine relative position. 
		 * 		When it is 1, we update frame counter.
		 */
		@Override
		public void interpolate(double frac)
		{
			if (frac == 1)
			{
				//Next frame
				view.updateFrame(frame);
				//TODO: other stuff here.
				frame++;
			}
		}
		
		/**
		 * Stops the game timer, and resets game frame counter. 
		 */
		@Override
		public void stop()
		{
			super.stop();
			frame = 0;
		}
	}
	
	private final GridPane root;
	private final GridView view;
	private final GameTimer runner;
	private boolean started;
	
	public GameUI()
	{
		root = new GridPane();
		view = new GridView(null);
		runner = new GameTimer();
		started = false;
		
		initUI();
		
		/**
		 * Event handler that listens to all key presses that occurs.
		 */
		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			/**
			 * Called when a key event occurs.
			 * @param event an event object describing the key event that occurred.
			 */
			@Override
			public void handle(KeyEvent event)
			{
				int dir = -1;
				
				Player p = view.getPlayer();
				switch (event.getCode())
				{
				case LEFT:
				case A:
					dir = Actor.WEST;
					break;
				case UP:
				case W:
					dir = Actor.NORTH;
					break;
				case RIGHT:
				case D:
					dir = Actor.EAST;
					break;
				case DOWN:
				case S:
					dir = Actor.SOUTH;
				}
				
				if (dir != -1)
					p.traverseDirection(dir);
			}
		});
	}
	
	/**
	 * Starts the game play, unpausing the game timer (if it hasn't
	 * already started).
	 */
	public void startGame()
	{
		if (!started)
			runner.play();
		started = true;
	}
	
	/**
	 * Initializes UI for GameUI
	 */
	private void initUI()
	{
		//TODO: ui
	}
}
