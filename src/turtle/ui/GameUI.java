/**
 * GameUI.java
 * 
 * This displays the main game UI that the user will interact with.
 * @author Henry Wang
 * Date: 4/28/17
 * Period: 2
 */

package turtle.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import turtle.comp.Player;
import turtle.core.Actor;
import turtle.core.GridView;
import turtle.file.Level;

public class GameUI extends VBox
{

	private static final Color DARKGRAY = Color.web("#505050");
	
	private static final double DEF_FONT_SIZE = 18.0;

	private static final double GAP_INSET = 5.0;
	private static final double LARGE_GAP_INSET = 20.0;
	private static final double FRAME_WIDTH = 10.0;

	private static final Duration FRAME_DURATION = Duration.millis(20);

	private static final int SECS_IN_MIN = 60;

	private static final Duration FADE_DURATION = Duration.seconds(.5);
	
	/**
	 * Runs the game timer, keep tracks of the game states each frame.
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
				Player p = view.getPlayer();
				view.updateFrame(frame);
				updateUI();
				if (p.isDead())
					stop(); //TODO: stop game and drop curtains.
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
	
	/* UI elements */
	private HBox pnlBar;
    private Label lblPackName;
    private Label lblLevelName;
    private Label lblMenu;
    private StackPane pnlFrame;
    private HBox pnlStatus;
    private StackPane pnlMessagePanel;
    private Label lblFood;
    private Label lblTime;
    private Label lblMsg;
	
    /* Game-related stuff */
	private final GridView view;
	private final GameTimer runner;
	private boolean started;
	
	private int foodLeft;
	private int timeLeft; 
	
	/**
	 * Creates a new GameUI and initializes UI.
	 */
	public GameUI()
	{
		view = new GridView(null);
		runner = new GameTimer();
		started = false;
		
		foodLeft = 0;
		timeLeft = -1;
		
		initUI();
		
		/**
		 * Event handler that listens to all key presses that occurs.
		 */
		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
		{
			/**
			 * Called when a key event occurs.
			 * @param event an event object describing the key event that 
			 *   occurred.
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
				
				//TODO: start game when player makes first move.
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
	 * Initializes this Game UI with the level.
	 * @param lvl the level to initialize with.
	 * @throws NullPointerException if lvl is null.
	 */
	private void initLevel(Level lvl)
	{
		foodLeft = lvl.getFoodRequirement();
		timeLeft = lvl.getTimeLimit();
		
		if (lvl.getPack() == null)
			lblPackName.setText("");
		else
			lblPackName.setText(lvl.getPack().getName() + ":");
		lblLevelName.setText(lvl.getName());
		//TODO: update grid.
		
		updateUI();
	}
	
	/**
	 * Initializes UI for GameUI
	 */
	private void initUI()
	{
		initBarUI();
        initGameView();
        initStatusUI();
        
        setBackground(new Background(new BackgroundFill(Color.BLACK, 
        		null, null)));
        getChildren().addAll(pnlBar, pnlFrame, pnlStatus);
	}
	
	/**
	 * Initializes the game view UI area. (Has grid view).
	 */
	private void initGameView()
	{
        StackPane.setMargin(view, new Insets(FRAME_WIDTH));
        view.setEffect(new DropShadow());
        
        pnlFrame = new StackPane();
        VBox.setVgrow(pnlFrame, javafx.scene.layout.Priority.ALWAYS);
        pnlFrame.setBackground(new Background(new BackgroundFill(DARKGRAY, 
        		null, null)));
        pnlFrame.setEffect(new InnerShadow());
        pnlFrame.getChildren().add(view);
	}

	/**
	 * Initializes the status bar UI, containing user status information.
	 */
	private void initStatusUI()
	{
		pnlStatus = new HBox();
		
		pnlMessagePanel = new StackPane();
        HBox.setHgrow(pnlMessagePanel, javafx.scene.layout.Priority.ALWAYS);
        
        Label lblLabelFood = new Label("Food Left:");
        lblLabelFood.setTextFill(javafx.scene.paint.Color.WHITE);
        lblLabelFood.setFont(new Font("System Bold", DEF_FONT_SIZE));

        lblFood = new Label("0000000000");
        lblFood.setTextFill(javafx.scene.paint.Color.WHITE);
        lblFood.setFont(new Font(DEF_FONT_SIZE));
        HBox.setMargin(lblFood, new Insets(0, GAP_INSET, 0, GAP_INSET));

        Pane spacing = new Pane();
        HBox.setHgrow(spacing, javafx.scene.layout.Priority.NEVER);
        spacing.setPrefHeight(0);
        spacing.setPrefWidth(LARGE_GAP_INSET);

        Label lblLabelTime = new Label("Time Left:");
        lblLabelTime.setTextFill(javafx.scene.paint.Color.WHITE);
        lblLabelTime.setFont(new Font("System Bold", DEF_FONT_SIZE));

        lblTime = new Label("--:--");
        lblTime.setTextFill(javafx.scene.paint.Color.WHITE);
        lblTime.setFont(new Font(DEF_FONT_SIZE));
        lblTime.setPadding(new Insets(0, GAP_INSET, 0, GAP_INSET));
        pnlStatus.getChildren().addAll(pnlMessagePanel, lblLabelFood, lblFood,
        		spacing, lblLabelTime, lblTime);
	}

	/**
	 * Initializes the top bar UI, contains the level name, and also menu
	 * button.
	 */
	private void initBarUI()
	{
		pnlBar = new HBox();
		
		lblPackName = new Label("[Level Pack]:");
        lblPackName.setTextFill(javafx.scene.paint.Color.WHITE);
        lblPackName.setFont(new Font("System Bold", DEF_FONT_SIZE));
        HBox.setMargin(lblPackName, new Insets(0, GAP_INSET, 0, GAP_INSET));

        lblLevelName = new Label("[Level name]");
        lblLevelName.setTextFill(javafx.scene.paint.Color.WHITE);
        lblLevelName.setFont(new Font(DEF_FONT_SIZE));

        Pane spacing = new Pane();
        HBox.setHgrow(spacing, javafx.scene.layout.Priority.ALWAYS);

        //TODO: menu button.
        lblMenu = new Label("Menu");
        lblMenu.setTextFill(javafx.scene.paint.Color.WHITE);
        lblMenu.setFont(new Font(DEF_FONT_SIZE));
        HBox.setMargin(lblMenu, new Insets(0, GAP_INSET, 0, GAP_INSET));
        
        pnlBar.getChildren().addAll(lblPackName, lblLevelName, spacing, 
        		lblMenu);
	}
	
	/**
	 * Update the dynamic parts of this UI to reflect the game status.
	 */
	private void updateUI()
	{
		lblFood.setText(String.format("%010d", foodLeft));
		
		if (timeLeft == -1)
			lblTime.setText("--:--");
		else
		{
			int min = timeLeft / SECS_IN_MIN;
			int sec = timeLeft % SECS_IN_MIN;
			lblTime.setText(String.format("%02d:%02d", min, sec));
		}
		
		Player p = view.getPlayer();
		String msg = "";
		
		if (p != null)
			msg = p.getMessage();
		
		if (!lblMsg.getText().equals(msg))
		{
			if (lblMsg != null)
			{
				messageEdit(false, lblMsg);
				lblMsg = null;
			}
			if (!msg.isEmpty())
			{
				lblMsg = new Label(msg);
				lblMsg.setFont(new Font("System Italic", DEF_FONT_SIZE));
				messageEdit(true, lblMsg);
			}
		}
	}
	
	/**
	 * Edits the message panel so that the message will fade in or fade out.
	 * This either adds or removes a message label.
	 * 
	 * @param adding true to add the message, false to remove the message.
	 * @param msg the label message that is being edited.
	 */
	private void messageEdit(boolean adding, Label msg)
	{
		FadeTransition fade = new FadeTransition(FADE_DURATION, msg);
		if (adding)
		{
			fade.setFromValue(0);
			fade.setToValue(1);
			pnlMessagePanel.getChildren().add(msg);
		}
		else
		{
			fade.setFromValue(1);
			fade.setToValue(0);
			
			/** Removes the label when the animation is finished. */
			fade.setOnFinished(new EventHandler<ActionEvent>()
			{
				/**
				 * Removes the component when the finish action occurs.
				 * @param event the associated event object with the action.
				 */
				@Override
				public void handle(ActionEvent event)
				{
					pnlMessagePanel.getChildren().remove(msg);
					fade.setOnFinished(null); //Prevent memory leakage.
				}
			});
		}
	}
}
