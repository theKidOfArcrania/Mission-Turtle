package turtle.ui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.UUID;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import turtle.file.LevelPack;

/**
 * MainApp.java
 * This initializes the entire game, and also manages user progress 
 * among the levels.
 * 
 * @author Henry
 * Date: 5/8/17
 * Period: 2
 */
@SuppressWarnings("resource")
public class MainApp extends Application
{
	/** Result when level is completed with no time-limit */
	public static final int RESULT_NO_TIME_LIMIT = -1;
	
	/** Result when level is not completed. */
	public static final int RESULT_NOT_DONE = -2;
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	private final HashMap<UUID, RandomAccessFile> openedFiles;
	private final StackPane root;
	private final GameUI game;
	
	/**
	 * Constructs a new MainApp.
	 */
	public MainApp()
	{
		openedFiles = new HashMap<>();
		root = new StackPane(new Pane());
		game = new GameUI(this);
		
	}
	
	/**
	 * Checks whether if a level has been completed or not, and in how much 
	 * time it is completed.
	 * 
	 * @param pack the level pack 
	 * @param level the index of level to check
	 * @return the time completed, or the constants RESULT_NO_TIME_LIMIT
	 * 	or RESULT_NOT_DONE.
	 * @throws IOException if an I/O error occurs while reading file.
	 * @throws IllegalArgumentException if illegal argument is supplied.
	 */
	public int checkLevelCompletion(LevelPack pack, int level) throws IOException
	{
		if (level < 0 || level >= pack.getLevelCount())
			throw new IllegalArgumentException("Level index is out of bounds.");
		
		RandomAccessFile raf = openLevelSaveFile(pack);
		long offset = Long.BYTES * level;
		if (raf.length() < offset + Long.BYTES)
			raf.setLength(offset + Long.BYTES);
		raf.seek(offset);
		if (raf.readInt() == 0)
			return RESULT_NOT_DONE;
		else
			return raf.readInt();
	}
	
	/**
	 * Runs the game UI at a particular level pack and level.
	 * @param pack the level pack to run.
	 * @param level the level index to run.
	 */
	public void startGame(LevelPack pack, int level)
	{
		root.getChildren().set(0, game);
		game.initLevelPack(pack, level);;
		game.requestFocus();
		
		Stage s = (Stage)root.getScene().getWindow();
		s.sizeToScene();
		s.centerOnScreen();
	}
	
	/**
	 * Starts the game application!
	 * @param primaryStage the initial main window of application.
	 */
	@Override
	public void start(Stage primaryStage) 
	{
		Scene s = new Scene(root);
		s.getStylesheets().add("/turtle/ui/styles.css");
		
		primaryStage.setScene(s);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	/**
	 * Shows the level selection UI. This will automatically remove any
	 * showing dialogs.
	 */
	public void showLevelSelect()
	{
		for (Node n : root.getChildren())
			if (n instanceof DialogBoxUI)
				hideDialog((DialogBoxUI)n);
		//TODO: here
		System.exit(1);
	}
	
	/**
	 * Shows the main menu selection. This will automatically remove any
	 * showing dialogs.
	 */
	public void showMainMenu()
	{
		for (Node n : root.getChildren())
			if (n instanceof DialogBoxUI)
				hideDialog((DialogBoxUI)n);
		//TODO: here
		System.exit(1);
	}
	
	/**
	 * Adds/Shows a dialog box onto this stack of elements and shows it
	 * to the user.
	 * 
	 * @param dlg the dialog to show.
	 */
	public void showDialog(DialogBoxUI dlg)
	{
		root.getChildren().add(dlg);
	}
	
	/**
	 * Removes/Hides the dialog from this stack of elements.
	 * 
	 * @param dlg the dialog to hide.
	 */
	public void hideDialog(DialogBoxUI dlg)
	{
		root.getChildren().remove(dlg);
	}
	
	/**
	 * Saves the information that the user has completed a particular level 
	 * from a particular level pack. This is internally called by GameUI.
	 * 
	 * @param pack the level pack of the level
	 * @param level the level index to save
	 * @param time the time completed in.
	 * @throws IOException if an error occurs while saving level status
	 * @throws IllegalArgumentException if illegal argument is supplied.
	 */
	void completeLevel(LevelPack pack, int level, int time) throws IOException
	{
		if (level < 0 || level >= pack.getLevelCount())
			throw new IllegalArgumentException("Level index is out of bounds.");
		if (time <= 0 && time != RESULT_NO_TIME_LIMIT)
			throw new IllegalArgumentException("Invalid time completion"); 
		
		RandomAccessFile raf = openLevelSaveFile(pack);
		long offset = Long.BYTES * level;
		if (raf.length() < offset + Long.BYTES)
			raf.setLength(offset + Long.BYTES);
		raf.seek(offset);
		raf.writeInt(1);
		raf.writeInt(time);
	}
	
	/**
	 * Obtains the opened level-pack save data for the level pack.
	 * This will open a new file if one did not exist yet.
	 * @param pack the associated level pack.
	 * @return an opened
	 * @throws IOException if an error occurs in opening file.
	 */
	private RandomAccessFile openLevelSaveFile(LevelPack pack) throws IOException
	{
		UUID id = pack.getLevelPackID();
		if (!openedFiles.containsKey(id))
		{
			File dir = new File(System.getProperty("user.home"), ".turtle");
			if (!dir.exists())
				dir.mkdir();
			openedFiles.put(id, new RandomAccessFile(new File(dir, 
					pack.getLevelPackID() + ".sav"), "rw"));
		}
		return openedFiles.get(id);
	}
}
