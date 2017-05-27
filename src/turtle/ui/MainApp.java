package turtle.ui;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import turtle.file.LevelPack;
import turtle.core.Recording;
import turtle.file.LevelPackSaveData;
import turtle.file.LevelSaveData;

import static turtle.file.LevelSaveData.*;

/**
 * MainApp.java
 * This initializes the entire game, and also manages user progress
 * among the levels.
 *
 * @author Henry
 *         Date: 5/8/17
 *         Period: 2
 */
@SuppressWarnings("resource")
public class MainApp extends Application
{
    public static final int RESULT_NO_TIME_LIMIT = -1;
    public static final int RESULT_NOT_DONE = -2;


    private static final Preferences prefs = Preferences.userNodeForPackage(
            MainApp.class);
    
    private final HashMap<UUID, LevelPack> loadedPacks;
    private final HashMap<UUID, LevelPackSaveData> saveStatus;
    private final StackPane root;
    private final StartUI startUI;
    private final GameUI gameUI;
    
    private LevelSelectUI selectUI;

    /**
     * Constructs a new MainApp.
     */
    public MainApp()
    {
        loadedPacks = new HashMap<>();
        saveStatus = new HashMap<>();
        root = new StackPane(new Pane());

        startUI = new StartUI(this);
        gameUI = new GameUI(this);
    }

    @SuppressWarnings("javadoc")
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    /**
     * Checks whether if a level has been unlocked or not
     *
     * @param pack  the level pack
     * @param level the index of level to check
     * @return true if unlocked, false if still locked.
     * @throws IllegalArgumentException if illegal argument is supplied.
     */
    public boolean checkLevelUnlock(LevelPack pack, int level)
    {
        try
        {
            LevelSaveData levelData = obtainLevelPackSaveData(pack)
                    .getLevel(level);
            return levelData.isUnlocked();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether if a level has been completed or not, and in how much
     * time it is completed.
     *
     * @param pack  the level pack
     * @param level the index of level to check
     * @return the time completed, or the constants RESULT_NO_TIME_LIMIT
     * or RESULT_NOT_DONE.
     * @throws IllegalArgumentException if illegal argument is supplied.
     */
    public int checkLevelCompletion(LevelPack pack, int level)
    {
        if (level < 0 || level >= pack.getLevelCount())
            throw new IllegalArgumentException("Level index is out of bounds.");

        try
        {
            LevelSaveData levelData = obtainLevelPackSaveData(pack)
                    .getLevel(level);
            if (!levelData.isCompleted())
                return RESULT_NOT_DONE;
            return levelData.getScore();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return RESULT_NOT_DONE;
        }
    }

    /**
     * @return a list of all loaded level packs.
     */
    public List<LevelPack> getLevelPacks()
    {
        return new ArrayList<>(loadedPacks.values());
    }

    /**
     * Runs the game UI at a particular level pack and level.
     *
     * @param pack  the level pack to run.
     * @param level the level index to run.
     */
    public void startGame(LevelPack pack, int level)
    {
        if (!gameUI.initLevelPack(pack, level))
            return;
        gameUI.requestFocus();
        showUI(gameUI);
    }

    /**
     * Runs the game UI at the progress where the player last stopped off.
     * If there is no progress, this will run the first level of the first
     * level pack.
     */
    public void startPreviousGame()
    {
        LevelPack active = getLastActivePack();
        if (active == null)
        {
            if (loadedPacks.isEmpty())
                return;
            active = loadedPacks.values().iterator().next();
        }
        if (active.getLevelCount() == 0)
            return;

        int lvl = 0;
        for (; lvl < active.getLevelCount() - 1; lvl++)
        {
            if (checkLevelCompletion(active, lvl) == RESULT_NOT_DONE)
                break;
        }
        startGame(active, lvl);
    }

    /**
     * Starts the game application!
     *
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

        showMainMenu();
        loadLevelPacks();
        selectUI = new LevelSelectUI(this);
    }

    /**
     * Shows the level selection UI. This will automatically remove any
     * showing dialogs.
     */
    public void showLevelSelect()
    {
        selectUI.updateStatus();
        showUI(selectUI);
    }

    /**
     * Shows the main menu selection. This will automatically remove any
     * showing dialogs.
     */
    public void showMainMenu()
    {
        showUI(startUI);
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
     * Hides all dialogs from displaying.
     */
    public void hideAllDialogs()
    {
        ArrayList<DialogBoxUI> dlgs = new ArrayList<>();
        for (Node n : root.getChildren())
            if (n instanceof DialogBoxUI)
                dlgs.add((DialogBoxUI) n);
        for (DialogBoxUI dlg : dlgs)
            hideDialog(dlg);
    }

    /**
     * @return the last level pack the user has accessed, or null if none
     * existed.
     */
    public LevelPack getLastActivePack()
    {
        String lastPack = prefs.get("status.pack", "");
        if (lastPack.isEmpty())
            return null;

        try
        {
            return loadedPacks.get(UUID.fromString(lastPack));
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the last level pack the user has accessed
     *
     * @param pack the level pack accessed
     */
    public void setLastActivePack(LevelPack pack)
    {
        prefs.put("status.pack", pack.getLevelPackID().toString());
    }

    /**
     * Resets ALL scores.
     */
    public void resetScores()
    {
        try
        {
            for (LevelPackSaveData packData : saveStatus.values())
                packData.forceClose();
            saveStatus.clear();

            boolean success = true;
            File[] dir = new File(System.getProperty("user.home"), ".turtle")
                    .listFiles();
            if (dir != null)
            {
                for (File f : dir)
                {
                    if (f.isFile() && !f.delete())
                        success = false;
                }
            }

            if (!success)
                showDialog(new DialogBoxUI("Unable to reset scores.", "OK"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showDialog(new DialogBoxUI("Unable to reset scores.", "OK"));
        }
    }

    /**
     * Unlocks a particular level from a pack so that it can be accessible in
     * level select UI. This should only be called by GameUITester,
     * otherwise, this will automatically be called when the previous level
     * is completed.
     *
     * @param pack the level pack to select.
     * @param level the level number to unlock.
     * @throws IOException if an error occurs while trying to write to file
     *
     */
    void unlockLevel(LevelPack pack, int level) throws IOException
    {
        obtainLevelPackSaveData(pack).getLevel(level).setStatus
                (MASK_UNLOCKED, MASK_UNLOCKED);
    }

    /**
     * Saves the information that the user has completed a particular level
     * from a particular level pack. This is internally called by GameUI.
     *
     * @param pack  the level pack of the level
     * @param level the level index to save
     * @param rec   the recording of the game play
     * @throws IOException if an error occurs while saving level status
     */
    void completeLevel(LevelPack pack, int level, Recording rec)
            throws IOException
    {
        LevelPackSaveData packData = obtainLevelPackSaveData(pack);
        LevelSaveData saveData = packData.getLevel(level);

        int time = pack.getLevel(level).getTimeLimit();
        if (time != RESULT_NO_TIME_LIMIT)
            time -= rec.getRecordingFrames() / GameUI.FRAMES_PER_SEC;

        saveData.setScore(time);
        saveData.setStatus(MASK_COMPLETE | MASK_UNLOCKED, MASK_COMPLETE |
                MASK_UNLOCKED);
        saveData.setRecording(rec);

        if (level != pack.getLevelCount() - 1 && !checkLevelUnlock(pack, level))
            packData.getLevel(level + 1).setStatus(MASK_UNLOCKED, MASK_UNLOCKED);
    }

    /**
     * Obtains the opened level-pack save data for the level pack.
     * This will open a new file if one did not exist yet.
     *
     * @param pack the associated level pack.
     * @return the loaded level-pack save data.
     * @throws IOException if an error occurs in opening file.
     */
    private LevelPackSaveData obtainLevelPackSaveData(LevelPack pack) throws IOException
    {
        UUID id = pack.getLevelPackID();
        if (!saveStatus.containsKey(id))
            saveStatus.put(id, new LevelPackSaveData(pack));
        return saveStatus.get(id);
    }

    /**
     * Loads all the level packs bundled with this game.
     */
    private void loadLevelPacks()
    {
        Pattern fileReg = Pattern.compile(".+\\.mtp$");
        File dir = new File(".");
        File[] packs = dir.listFiles();
        if (packs == null)
            return;

        boolean success = true;
        for (File f : packs)
        {
            try
            {
                if (f.isFile() && fileReg.matcher(f.getName()).matches())
                {
                    LevelPack pack = new LevelPack(f);
                    loadedPacks.put(pack.getLevelPackID(), pack);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                success = false;
            }
        }
        if (!success)
            showDialog(new DialogBoxUI("Unable to load some level packs.",
                    "OK"));
    }

    /**
     * Displays a UI within this app and readies its size.
     *
     * @param ui the UI to display
     */
    private void showUI(Pane ui)
    {
        hideAllDialogs();
        root.getChildren().set(0, ui);
        Stage s = (Stage) root.getScene().getWindow();
        s.sizeToScene();
        s.centerOnScreen();
    }

}
