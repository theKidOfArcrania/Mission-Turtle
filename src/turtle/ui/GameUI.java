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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import turtle.comp.Player;
import turtle.core.Actor;
import turtle.core.Grid;
import turtle.core.GridView;
import turtle.file.Level;
import turtle.file.LevelPack;

import static turtle.ui.GameMenuUI.*;

public class GameUI extends VBox
{

	private static final double SEMI_TRANS_ALPHA = .5;

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
		 * Handles each frame tick of the game (at 50fps). Delegate 
		 * method to {@link turtle.ui.GameUI#updateFrame(long)}.
		 * @param frac double from 0 to 1 to determine relative position. 
		 * 		When it is 1, we update frame counter.
		 */
		@Override
		public void interpolate(double frac)
		{

			if (frac == 1) // Next Frame
			{
				updateFrame(frame);
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
	
	private static final Color DARKGRAY = Color.web("#505050");

	private static final double GAP_INSET = 5.0;
	private static final double LARGE_GAP_INSET = 20.0;
	private static final double FRAME_WIDTH = 10.0;

	private static final int FRAMES_PER_SEC = 50;
	private static final Duration FRAME_DURATION = Duration.seconds(1.0 / 
			FRAMES_PER_SEC);
	private static final int SECS_IN_MIN = 60;
	private static final Duration FADE_DURATION = Duration.seconds(.5);
	
	/* UI elements */
	private HBox pnlBar;
    private Label lblPackName;
    private Label lblLevelName;
    private Label lblMenu;
    private StackPane pnlFrame;
    private StackPane pnlMenuBack;
    private HBox pnlStatus;
    private StackPane pnlMessagePanel;
    private Label lblFood;
    private Label lblTime;
    private Label lblMsg;
    
    private GameMenuUI pnlMenuDialog;
	
    /* Game-related stuff */
    private int moveDir;
    
    private LevelPack currentPack;
	private final GridView view;
	private final GameTimer runner;
	
	private int timeLeft;
	
	private boolean started;
	private boolean paused;
	
	/**
	 * Creates a new GameUI and initializes UI.
	 */
	public GameUI()
	{
		pnlMenuDialog = new GameMenuUI(this);
		view = new GridView(null);
		runner = new GameTimer();
		started = false;
		paused = false;
		
		timeLeft = -1;
		moveDir = -1;
		
		initUI();
		
		setFocusTraversable(true);
		requestFocus();
		
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
			@SuppressWarnings("incomplete-switch")
			@Override
			public void handle(KeyEvent event)
			{
				
				switch (event.getCode())
				{
				case LEFT:
				case A:
					moveDir = Actor.WEST;
					break;
				case UP:
				case W:
					moveDir = Actor.NORTH;
					break;
				case RIGHT:
				case D:
					moveDir = Actor.EAST;
					break;
				case DOWN:
				case S:
					moveDir = Actor.SOUTH;
					break;
				case ESCAPE:
				case PAUSE:
					if (pnlMenuBack.isVisible())
						handleGameMenu(ID_RESUME);
					else
					{
						pauseGame();
						pnlMenuBack.setVisible(true);
					}
					break;
				case SPACE:
					startGame();
				}
				
				Player p = view.getPlayer();
				if (moveDir != -1 && p != null)
					startGame();
			}
		});
	}
	
	/**
	 * @return the grid view displayed by this game UI.
	 */
	public GridView getGridView()
	{
		return view;
	}
	
	/**
	 * Initializes this GameUI with a level pack.
	 * @param pck the pack to use.
	 */
	public void initLevelPack(LevelPack pck)
	{
		//TODO: load scores. start from chosen level.
		currentPack = pck;
		initLevel(currentPack.getLevel(0));
	}
	
	/**
	 * Handles the different actions a user clicks on the menu. Should 
	 * only be internally called by GameMenuUI.
	 * 
	 * @param id the id of action
	 */
	void handleGameMenu(int id)
	{
		//TODO: actions here.
		switch (id)
		{
			case ID_RESUME:
				break;
			case ID_RESTART:
				break;
			case ID_LEVELSELECT:
				break;
			case ID_MAINMENU:
				break;
			case ID_EXIT:
				break;
			default:
				return;
		}
		
		resumeGame();
		pnlMenuBack.setVisible(false);
	}
	
	/**
	 * Initializes the top bar UI, contains the level name, and also menu
	 * button.
	 */
	private void initBarUI()
	{
		pnlBar = new HBox();
		
		lblPackName = new Label("[Level Pack]:");
		lblPackName.getStyleClass().add("bold");
        HBox.setMargin(lblPackName, new Insets(0, GAP_INSET, 0, GAP_INSET));

        lblLevelName = new Label("[Level name]");

        Pane spacing = new Pane();
        HBox.setHgrow(spacing, Priority.ALWAYS);

        lblMenu = new Label("Menu");
        lblMenu.getStyleClass().add("lbutton");
        lblMenu.setPadding(new Insets(0, GAP_INSET, 0, GAP_INSET));
        HBox.setMargin(lblMenu, new Insets(0, GAP_INSET, 0, GAP_INSET));
        
        /** Handler called when user clicks menu button. */
        lblMenu.setOnMouseClicked(new EventHandler<MouseEvent>()
		{

        	/**
        	 * Shows the game menu dialog when the user clicks button.
        	 * @param event the associated event with click.
        	 */
			@Override
			public void handle(MouseEvent event)
			{
				pauseGame();
				pnlMenuBack.setVisible(true);
			}
		});
        
        pnlBar.getChildren().addAll(lblPackName, lblLevelName, spacing, 
        		lblMenu);
	}
	
	/**
	 * Initializes the game view UI area. (Has grid view).
	 */
	private void initGameView()
	{
        StackPane.setMargin(view, new Insets(FRAME_WIDTH));
        view.setEffect(new DropShadow());
        
        pnlMenuBack = new StackPane();
        pnlMenuBack.setBackground(new Background(new BackgroundFill(
        		Color.grayRgb(0, SEMI_TRANS_ALPHA), null, null)));
        pnlMenuBack.setVisible(false);
        pnlMenuBack.getChildren().add(pnlMenuDialog);
        
        /** Exits menu dialog when clicked */
        pnlMenuBack.setOnMouseClicked(new EventHandler<MouseEvent>()
		{

        	/** 
        	 * Exits menu dialog as if user clicked "Resume"
        	 * @param event associated mouse click event object
        	 */
			@Override
			public void handle(MouseEvent event)
			{
				handleGameMenu(ID_RESUME);
			}
        	
		});
        
        pnlFrame = new StackPane();
        setVgrow(pnlFrame, javafx.scene.layout.Priority.ALWAYS);
        pnlFrame.setBackground(new Background(new BackgroundFill(DARKGRAY, 
        		null, null)));
        pnlFrame.setEffect(new InnerShadow());
        pnlFrame.getChildren().addAll(view, pnlMenuBack);
        
        
	}

	/**
	 * Initializes this Game UI with the level.
	 * @param lvl the level to initialize with.
	 * @throws NullPointerException if <code>lvl</code> is null.
	 */
	private void initLevel(Level lvl)
	{
		timeLeft = lvl.getTimeLimit();
		
		if (lvl.getPack() == null)
			lblPackName.setText("");
		else
			lblPackName.setText(lvl.getPack().getName() + ":");
		lblLevelName.setText(lvl.getName());
		
		Grid g = lvl.createLevel();
		view.initGrid(g);
		
		updateUI();
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
        lblLabelFood.getStyleClass().add("bold");

        lblFood = new Label("0000000000");
        HBox.setMargin(lblFood, new Insets(0, GAP_INSET, 0, GAP_INSET));

        Pane spacing = new Pane();
        HBox.setHgrow(spacing, Priority.NEVER);
        spacing.setPrefHeight(0);
        spacing.setPrefWidth(LARGE_GAP_INSET);

        Label lblLabelTime = new Label("Time Left:");
        lblLabelTime.getStyleClass().add("bold");

        lblTime = new Label("--:--");
        lblTime.setPadding(new Insets(0, GAP_INSET, 0, GAP_INSET));
        pnlStatus.getChildren().addAll(pnlMessagePanel, lblLabelFood, lblFood,
        		spacing, lblLabelTime, lblTime);
	}
	
	/**
	 * Initializes UI for GameUI
	 */
	private void initUI()
	{
		initBarUI();
        initGameView();
        initStatusUI();
        
        getChildren().addAll(pnlBar, pnlFrame, pnlStatus);
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
	
	/**
	 * Pauses the game. Does nothing if it already is paused
	 * or if game hasn't started.
	 */
	private void pauseGame()
	{
		if (paused || !started)
			return;
		runner.pause();
		paused = true;
	}
	
	/**
	 * Resumes the game after a pause. Does nothing if it 
	 * already is unpaused, or if game hasn't started.
	 */
	private void resumeGame()
	{
		if (!paused || !started)
			return;
		runner.play();
		paused = false;
	}
	
	/**
	 * Starts the game play and the game timer (if it hasn't
	 * already started).
	 */
	private void startGame()
	{
		if (!started)
			runner.play();
		started = true;
	}
	
	/**
	 * Updates next frame of game. 
	 * @param frame current frame count.
	 */
	private void updateFrame(long frame)
	{
		//Move player.
		Player p = view.getPlayer();
		if (moveDir != -1 && !p.isMoving())
		{
			p.setHeading(moveDir);
			p.traverseDirection(moveDir);
			moveDir = -1;
		}
		
		//Update grid stuff.
		view.updateFrame(frame);
		if (timeLeft != -1 && frame % FRAMES_PER_SEC == 0)
			timeLeft--;
		updateUI();
		
		//Check player game-status.
		String status = null;
		boolean success = false;
		
		if (p.isWinner())
		{
			status = "You Win!";
			success = true;
		}
		else if (p.isDead())
			status = "You Died!";
		else if (timeLeft == 0)
			status = "Time's Up!";
		
		if (status != null)
		{
			//TODO: show level-end dialog and maybe advance to next level.
			System.out.println(status);
			System.exit(1);
		}
	}
	
	/**
	 * Update the dynamic parts of this UI to reflect the game status.
	 */
	private void updateUI()
	{
		lblFood.setText(String.format("%010d", view.getGrid().getFoodRequirement()));
		
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
		String oldMsg = "";
		
		if (lblMsg != null)
			oldMsg = lblMsg.getText();
		if (oldMsg == null)
			oldMsg = "";
		if (p != null)
			msg = p.getMessage();
		
		if (!oldMsg.equals(msg))
		{
			if (lblMsg != null)
			{
				messageEdit(false, lblMsg);
				lblMsg = null;
			}
			if (!msg.isEmpty())
			{
				lblMsg = new Label(msg);
				lblMsg.getStyleClass().add("italic");
				messageEdit(true, lblMsg);
			}
		}
	}
}
