package turtle.ui;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.function.IntConsumer;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import turtle.comp.Player;
import turtle.core.Actor;
import turtle.core.Grid;
import turtle.core.GridView;
import turtle.file.Level;
import turtle.file.LevelPack;

import static turtle.ui.GameMenuUI.*;

/**
 * GameUI.java
 * 
 * This displays the main game UI that the user will interact with.
 * @author Henry Wang
 * Date: 4/28/17
 * Period: 2
 */
public class GameUI extends VBox
{
	private static final String SECT_BREAK = "   ";
	
	/**
	 * Runs the game timer, keep tracks of the game states each frame.
	 */
	private class GameTimer extends AnimationTimer
	{
		private static final int SUBFRAMES = 1;
		private static final int FRAME_SAMPLE = 10;
		private long prevTime;
		private long frame;
		private ArrayDeque<Long> frameTimes;
		private double fps;
		private int subframe;
		
		/**
		 * Constructs a new GameTimer.
		 */
		public GameTimer(){
			prevTime = -1;
			subframe = 0;
			frame = 0;
			fps = 0;
			frameTimes = new ArrayDeque<>(FRAME_SAMPLE);
		}
		
		/**
		 * @return current frame-per-second value
		 */
		public double getFps()
		{
			return fps;
		}
		
		/**
		 * Handles each frame tick of the game (at 50fps). Delegate 
		 * method to {@link turtle.ui.GameUI#updateFrame(long)}.
		 * @param now the current time in nano seconds.
		 */
		@Override
		public void handle(long now)
		{
			subframe++;
			if (subframe >= SUBFRAMES)
			{
				subframe = 0;
				
				long time = System.nanoTime();
				if (prevTime != -1)
				{
					while (frameTimes.size() > FRAME_SAMPLE - 1)
						frameTimes.remove();
					frameTimes.add(time - prevTime);
					double fps = 0;
					for (long ftime : frameTimes)
						fps += ftime * 1e-9;
					fps = frameTimes.size() / fps;
					this.fps = fps;
					//System.out.printf("%.9f\n", fps);
				}
				prevTime = time;
				updateFrame(frame);
				frame++;
			}
		}
		
		/**
		 * Pauses the game timer, but doesn't reset game frame counter.
		 */
		public void pause()
		{
			super.stop();
		}
		
		/**
		 * Stops the game timer, and resets game frame counter. 
		 */
		@Override
		public void stop()
		{
			super.stop();
			frame = 0;
			subframe = 0;
		}
	}
	
	private static final int FPS_UPDATE_RATE = 10;
	
	private static final int ACTION_START = -1;
	private static final int ACTION_PAUSE = -2;
	private static final int ACTION_RESTART = -3;
	private static final int ACTION_NEXT = -4;
	private static final int ACTION_PREVIOUS = -5;

	private static final double SEMI_TRANS_ALPHA = .5;
	private static final Color DARKGRAY = Color.web("#505050");

	private static final double GAP_INSET = 5.0;
	private static final double LARGE_GAP_INSET = 20.0;
	
	private static final int LABEL_MIN_WIDTH = 50;
	
	private static final double FRAME_WIDTH = 10.0;
	private static final int FRAMES_PER_SEC = 50;
	private static final Duration FRAME_DURATION = Duration.seconds(1.0 / 
			FRAMES_PER_SEC);
	private static final Duration FADE_DURATION = Duration.seconds(.5);

	private static final double FIT_WIDTH = 799;
	private static final Duration CAROUSEL_DELAY = Duration.seconds(1.0); 
	private static final double CAROUSEL_SPEED = 100;

	private static final double SPACE_SIZE = 19.79296875;
	
	/**
	 * Creates a spacer pane used for layouts.
	 * @return a pane with a specfic spacing
	 */
	private static Pane createSpacer()
	{
		Pane spacing = new Pane();
        HBox.setHgrow(spacing, Priority.NEVER);
        spacing.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        spacing.setPrefSize(LARGE_GAP_INSET, 0);
        spacing.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        return spacing;
	}
	
	/* UI elements */
	private HBox pnlBar;
    private Label lblFps;
    private Label lblPackName;
    private Label lblLevelName;
    private Label lblLevelStatus;
    private Label lblMenu;
    private StackPane pnlFrame;
    private StackPane pnlMenuBack;
    private HBox pnlStatus;
    private StackPane pnlMessagePanel;
    private Label lblFood;
    private Label lblTime;
    private Label lblMsg;
    private GameMenuUI pnlMenuDialog;
    
    private TranslateTransition msgScroller;
    private boolean doubled;
    
    /* Game-related stuff */
    private int dirPrevPressed;
    private boolean[] moving;
    
	private LevelPack currentPack;
	private final GridView view;
	private final GameTimer runner;
	private int timeLeft;
	
	private boolean halted;
	private boolean started;
	private boolean paused;
	
	private EnumMap<KeyCode, Integer> mappedKeys;
	
	private int currentLevelNum;
	
	private final MainApp app;
	
	/**
	 * Creates a new GameUI and initializes UI.
	 * @param app the main application of this game
	 */
	public GameUI(MainApp app)
	{
		this.app = app;
		
		pnlMenuDialog = new GameMenuUI(this);
		view = new GridView(null);
		runner = new GameTimer();
		
		started = false;
		paused = false;
		halted = false;
		
		msgScroller = null;
		doubled = false;
		
		timeLeft = -1;
		moving = new boolean[Actor.WEST + 1];
		
		currentLevelNum = 0;
		
		mappedKeys = new EnumMap<>(KeyCode.class);
		mapKeys();
		
		initUI();
		
		setFocusTraversable(true);
		requestFocus();
		
		/**
		 * Event handler that listens to all key events that occurs.
		 */
		addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>()
		{
			/**
			 * Called when a key event occurs.
			 * @param event an event object describing the key event that 
			 *   occurred.
			 */
			@Override
			public void handle(KeyEvent event)
			{
				handleKey(event);
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
	 * @param level the level index to start from. 
	 */
	public void initLevelPack(LevelPack pck, int level)
	{
		currentPack = pck;
		initLevel(level);
	}
	
	/**
	 * Handles the different actions a user clicks on the menu. Should 
	 * only be internally called by GameMenuUI.
	 * 
	 * @param id the id of action
	 */
	void handleGameMenu(int id)
	{
		if (id < ID_RESUME || id > ID_EXIT)
			throw new IllegalArgumentException("Invalid action ID");
		
		if (id == ID_RESUME)
		{
			pnlMenuBack.setVisible(false);
			resumeGame();
			return;
		}
		
		String prompt = "Are you sure you want to exit?";
		if (id == ID_RESTART)
			prompt = "Are you sure you want to restart?";
		
		DialogBoxUI dlg = new DialogBoxUI(prompt, "Yes", "No");
		
		/**
		 * Listens to the user's response to confirm this exiting.
		 */
		dlg.onResponse(new IntConsumer()
		{
			
			/**
			 * Called when user presses a response button.
			 * @param value the button id pressed
			 */
			@Override
			public void accept(int value)
			{
				app.hideDialog(dlg);
				pnlMenuBack.setVisible(false);
				if (value == 0)
				{
					stopGame();
					executeMenu(id);
				}
			}
		});
		app.showDialog(dlg);
	}
	
	/**
	 * Executes game menu command based on id. This is called after
	 * user confirms to do that command.
	 * 
	 * @param id the menu id
	 */
	private void executeMenu(int id)
	{
		switch (id)
		{
			case ID_RESTART:
				initLevel(currentLevelNum);
				break;
			case ID_LEVELSELECT:
				pnlMenuBack.setVisible(false);
				app.showLevelSelect();
				break;
			case ID_MAINMENU:
				pnlMenuBack.setVisible(false);
				app.showMainMenu();
				break;
			case ID_EXIT:
				pnlMenuBack.setVisible(false);
				System.exit(0);
				break;
		}
	}
	
	/**
	 * Checks player's current game status (whether if player won or lost).
	 * @param p the player to check status against.
	 */
	private void checkPlayerStatus(Player p)
	{
		String status = null;
		boolean success = false;
		
		if (p.isWinner())
		{
			status = "Success! Level Completed!";
			if (timeLeft != MainApp.RESULT_NO_TIME_LIMIT)
				status += "\nYour time bonus: " + timeLeft;
			status += "\n" + saveProgress();
			success = true;
		}
		else if (p.isDead())
			status = "You Died!";
		else if (timeLeft == 0)
			status = "Time's Up!";
		
		if (status != null)
		{
			stopGame();
			
			boolean allowNext = success && currentLevelNum < 
					currentPack.getLevelCount() - 1;
			String[] options;
			if (allowNext)
				options = new String[] {"Menu", "Restart", "Onward!"};
			else
				options = new String[] {"Menu", "Restart"};
			
			DialogBoxUI prompt = new DialogBoxUI(status, options);
			
			/**
			 * Listens for a user response (index of button pressed). 
			 */
			prompt.onResponse(new IntConsumer()
			{
				/**
				 * Called when user responds. Delegates to handleLevelDialog.
				 * @param value the index of response.
				 */
				@Override
				public void accept(int value)
				{
					handleLevelDialog(value);
					app.hideDialog(prompt);
				}
			});
			
			app.showDialog(prompt);
			halted = true;
		}
	}
	
	/**
	 * Saves the completion status of the current game.
	 * @return a string describing a message about the score to player.
	 */
	private String saveProgress()
	{
		try
		{
			int prevScore = app.checkLevelCompletion(currentPack, 
					currentLevelNum);
			
			if (timeLeft > prevScore)
			{
				app.completeLevel(currentPack, currentLevelNum, timeLeft);
				return "Wowzers! New High Score!";
			}
			else if (prevScore != MainApp.RESULT_NO_TIME_LIMIT)
				return "Impressive... but not as good as your previous score.";
			else
				return "Good Job!";
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return "Unable to save scores!";
		}
	}
	
	/**
	 * Handles a end-of-level dialog response.
	 * @param response the response index of user.
	 */
	private void handleLevelDialog(int response)
	{
		final int BUTTON_MENU = 0;
		final int BUTTON_AGAIN = 1;
		final int BUTTON_NEXT = 2;
		
		switch (response)
		{
		case BUTTON_AGAIN:
			initLevel(currentLevelNum);
			break;
		case BUTTON_NEXT:
			initLevel(currentLevelNum + 1);
			break;
		case BUTTON_MENU:
		default:
			app.showLevelSelect();
			break;
		}
	}
	
	/**
	 * Called whenever a key event occurs.
	 * @param event an event object describing the key event that 
	 *   occurred.
	 */
	private void handleKey(KeyEvent event)
	{
		if (halted)
			return;
		if (event.getEventType() == KeyEvent.KEY_TYPED)
			return;
		if (!mappedKeys.containsKey(event.getCode()))
			return;
		
		boolean keyDown = event.getEventType() == KeyEvent.KEY_PRESSED;
		int action = mappedKeys.get(event.getCode());
		if (action >= 0)
		{
			moving[action] = keyDown;
			if (keyDown)
				dirPrevPressed = action;
			startGame();
		}
		else
		{
			boolean controlDown = event.isControlDown();
			if (!keyDown)
				return;
			handleAction(action, controlDown);
		}
		
		
	}

	/**
	 * Handles an action that a user might trigger.
	 * @param action the action index triggered.
	 * @param controlDown whether if user is holding control button down.
	 */
	private void handleAction(int action, boolean controlDown)
	{
		switch (action)
		{
		case ACTION_PAUSE:
			
			if (pnlMenuBack.isVisible())
				handleGameMenu(ID_RESUME);
			else
			{
				pauseGame();
				pnlMenuBack.setVisible(true);
			}
			break;
		case ACTION_START:
			startGame();
			break;
		case ACTION_RESTART:
			if (controlDown)
				initLevel(currentLevelNum);
			break;
		case ACTION_NEXT:
			try
			{
				if (controlDown && currentLevelNum < currentPack.
						getLevelCount() - 1 && app.checkLevelCompletion
						(currentPack, currentLevelNum) != MainApp.RESULT_NOT_DONE)
					initLevel(currentLevelNum + 1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return;
		case ACTION_PREVIOUS:
			if (controlDown && currentLevelNum > 0)
				initLevel(currentLevelNum - 1);
		}
	}

	/**
	 * Initializes the top bar UI, contains the level name, and also menu
	 * button.
	 */
	private void initBarUI()
	{
		pnlBar = new HBox();
		
		lblPackName = new Label("");
		lblPackName.getStyleClass().add("bold");
        HBox.setMargin(lblPackName, new Insets(0, GAP_INSET, 0, GAP_INSET));

        lblLevelName = new Label("");
        HBox.setMargin(lblLevelName, new Insets(0, GAP_INSET, 0, 0));
        
        lblLevelStatus = new Label("");
        lblLevelStatus.getStyleClass().add("italic");
        HBox.setMargin(lblLevelStatus, new Insets(0, GAP_INSET, 0, GAP_INSET));

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
        
        pnlBar.getChildren().addAll(lblPackName, lblLevelName, 
        		lblLevelStatus, spacing, lblMenu);
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
	 * @param index the level index to initialize with.
	 * @throws NullPointerException if <code>lvl</code> is null.
	 */
	private void initLevel(int index)
	{
		stopGame();
		halted = false;
		
		currentLevelNum = index;
		Level lvl = currentPack.getLevel(index);
		try
		{
			if (!lvl.isLoaded())
				currentPack.loadLevel(index);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			//TODO: tell user that level is corrupted and exit
			System.out.println("Level data corrupted!");
			System.exit(1);
		}
		
		timeLeft = lvl.getTimeLimit();
		
		if (lvl.getPack() == null || lvl.getPack().getName().isEmpty())
			lblPackName.setText("");
		else
			lblPackName.setText(lvl.getPack().getName() + ":");
		lblLevelName.setText(lvl.getName());
		
		int score = MainApp.RESULT_NOT_DONE;
		try
		{
			score = app.checkLevelCompletion(currentPack, index);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if (score == MainApp.RESULT_NOT_DONE)
			lblLevelStatus.setText("");
		else if (score == MainApp.RESULT_NO_TIME_LIMIT)
			lblLevelStatus.setText("Already completed!");
		else 
			lblLevelStatus.setText("Already completed (" +
				score + " time bonus)");
		
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
		
		lblFps = new Label();
		lblFps.getStyleClass().add("small");
		lblFps.setMaxHeight(Double.MAX_VALUE);
		lblFps.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		HBox.setMargin(lblFps, new Insets(0, GAP_INSET, 0, GAP_INSET));
		
		pnlMessagePanel = new StackPane();
		pnlMessagePanel.setMinSize(0, 0);
        HBox.setHgrow(pnlMessagePanel, javafx.scene.layout.Priority.ALWAYS);
        
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(pnlMessagePanel.widthProperty());
        clip.heightProperty().bind(pnlMessagePanel.heightProperty());
        pnlMessagePanel.setClip(clip);
        
        Label lblLabelFood = new Label("Food Left:");
        lblLabelFood.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        lblLabelFood.getStyleClass().add("bold");

        lblFood = new Label("");
        lblFood.setMinSize(LABEL_MIN_WIDTH, USE_PREF_SIZE);
        lblFood.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(lblFood, new Insets(0, GAP_INSET, 0, GAP_INSET));

        Label lblLabelTime = new Label("Time Left:");
        lblLabelTime.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        lblLabelTime.getStyleClass().add("bold");

        lblTime = new Label("---");
        lblTime.setPadding(new Insets(0, GAP_INSET, 0, GAP_INSET));
        lblTime.setMinSize(LABEL_MIN_WIDTH, USE_PREF_SIZE);
        lblTime.setAlignment(Pos.CENTER_RIGHT);
        pnlStatus.getChildren().addAll(lblFps, createSpacer(), pnlMessagePanel, 
        		createSpacer(), lblLabelFood, lblFood, lblLabelTime, lblTime);
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
	 * Makes all the key mappings to function id.
	 */
	private void mapKeys()
	{
		mappedKeys.put(KeyCode.LEFT, Actor.WEST);
		mappedKeys.put(KeyCode.A, Actor.WEST);
		mappedKeys.put(KeyCode.UP, Actor.NORTH);
		mappedKeys.put(KeyCode.W, Actor.NORTH);
		mappedKeys.put(KeyCode.RIGHT, Actor.EAST);
		mappedKeys.put(KeyCode.D, Actor.EAST);
		mappedKeys.put(KeyCode.DOWN, Actor.SOUTH);
		mappedKeys.put(KeyCode.S, Actor.SOUTH);
		mappedKeys.put(KeyCode.ESCAPE, ACTION_PAUSE);
		mappedKeys.put(KeyCode.PAUSE, ACTION_PAUSE);
		mappedKeys.put(KeyCode.SPACE, ACTION_START);
		mappedKeys.put(KeyCode.R, ACTION_RESTART);
		mappedKeys.put(KeyCode.N, ACTION_NEXT);
		mappedKeys.put(KeyCode.P, ACTION_PREVIOUS);
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
			fade.play();
			
			msg.setOpacity(0);
			pnlMessagePanel.getChildren().add(msg);
			checkMessageOverflow(msg);
		}
		else
		{
			fade.setFromValue(1);
			fade.setToValue(0);
			fade.play();
			
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
					if (msgScroller != null)
						msgScroller = null;
				}
			});
		}
	}

	/**
	 * Checks whether if this message overflows out of the bounds.
	 * If so, it will scroll through 
	 * @param msg
	 */
	private void checkMessageOverflow(Label msg)
	{
		msg.applyCss();
		
		double msgWidth = msg.prefWidth(-1);
		double overflow = msgWidth - FIT_WIDTH;
		if (overflow > 0)
		{
			double firstSaw = (FIT_WIDTH - SPACE_SIZE) / 2;
			double from = msgWidth - firstSaw;
			double to = -SPACE_SIZE - firstSaw;
			
			msg.setText(msg.getText() + SECT_BREAK + msg.getText());
			msg.setTranslateX(from);
			doubled = true;
			
			msgScroller = new TranslateTransition(Duration.seconds(
					(from - to) / CAROUSEL_SPEED), msg);
			msgScroller.setDelay(CAROUSEL_DELAY);
			msgScroller.setFromX(from);
			msgScroller.setToX(to);
			msgScroller.setInterpolator(Interpolator.LINEAR);
			msgScroller.setCycleCount(Animation.INDEFINITE);
			msgScroller.play();
		}
		else
			doubled = false;
	}
	
	/**
	 * Stops the game.
	 */
	private void stopGame()
	{
		runner.stop();
		started = false;
		paused = false;
		
		for (int i = 0; i < moving.length; i++)
			moving[i] = false;
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
		
		for (int i = 0; i < moving.length; i++)
			moving[i] = false;
	}
	
	/**
	 * Resumes the game after a pause. Does nothing if it 
	 * already is unpaused, or if game hasn't started.
	 */
	private void resumeGame()
	{
		if (!paused || !started)
			return;
		runner.start();
		paused = false;
	}
	
	/**
	 * Starts the game play and the game timer (if it hasn't
	 * already started).
	 */
	private void startGame()
	{
		if (halted || !started)
			runner.start();
		started = true;
	}

	/**
	 * Updates next frame of game. 
	 * @param frame current frame count.
	 */
	private void updateFrame(long frame)
	{
		//Move player.
		int moveDir = -1;
		if (moving[dirPrevPressed])
			moveDir = dirPrevPressed;
		else
		{
			for (int dir = 0; dir < moving.length; dir++)
			{
				if (moving[dir])
				{
					moveDir = dir;
					break;
				}
			}
		}
		
		Player p = view.getPlayer();
		if (p == null)
			return;
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
		if (frame % FPS_UPDATE_RATE == 0)
			lblFps.setText(String.format("Fps: %.3f", runner.getFps()));
		
		checkPlayerStatus(p);
	}
	
	/**
	 * Update the dynamic parts of this UI to reflect the game status.
	 */
	private void updateUI()
	{
		String newStr = "" + view.getGrid().getFoodRequirement();
		if (!newStr.equals(lblFood.getText()))
			lblFood.setText(newStr);
		
		if (timeLeft == -1)
			newStr = "---";
		else
			newStr = "" + timeLeft;
		if (!newStr.equals(lblTime.getText()))
			lblTime.setText(newStr);
		
		Player p = view.getPlayer();
		String msg = "";
		String oldMsg = "";
		
		if (lblMsg != null)
			oldMsg = lblMsg.getText();
		if (oldMsg == null)
			oldMsg = "";
		if (p != null)
			msg = p.getMessage();
		
		String check = msg;
		if (doubled)
			check = msg + SECT_BREAK + msg;
		if (!oldMsg.equals(check))
		{
			if (lblMsg != null)
			{
				messageEdit(false, lblMsg);
				lblMsg = null;
			}
			if (!msg.isEmpty())
			{
				lblMsg = new Label(msg);
				lblMsg.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
				lblMsg.getStyleClass().add("italic");
				messageEdit(true, lblMsg);
			}
		}
	}
}
