package turtle.ui;

/**
 * This shows the user's main menu options, which will be the first thing
 * that pops up in the game.
 *
 * @author Henry Wang
 */
public class StartUI extends MenuUI {

    public static final int ID_PLAY = 0;
    public static final int ID_LEVEL_SELECT = 1;
    public static final int ID_RESET = 2;
    public static final int ID_EXIT = 3;

    private static final String[] NAMES = {"Play!", "Level Select",
            "Reset Scores", "Exit"
    };

    private final MainApp app;

    /**
     * Creates a new StartUI and initializes UI.
     *
     * @param app the MainApp the app to execute commands.
     */
    public StartUI(MainApp app) {
        super("Mission Turtle: The Rescue");

        this.app = app;

        for (int i = 0; i < NAMES.length; i++) {
            final int id = i;
            getChildren().add(MenuUI.createButton(NAMES[i], true, true,
                    event -> handleCommand(id)));
        }

        final int MENU_WIDTH = 400;
        setPrefWidth(MENU_WIDTH);
    }

    /**
     * Handles a specific command when the user clicks on a menu button.
     *
     * @param index the index of the button clicked.
     */
    private void handleCommand(int index) {
        switch (index) {
            case ID_PLAY:
                app.startPreviousGame();
                break;
            case ID_LEVEL_SELECT:
                app.showLevelSelect();
                break;
            case ID_RESET:
                prompt("Are you sure you want to reset all scores?", index);
                break;
            case ID_EXIT:
                prompt("Are you sure you want to exit game?", index);
                break;
        }
    }

    /**
     * Utility method to prompt user to confirm a command.
     *
     * @param prompt  the prompt to ask user.
     * @param command id of command to carry out if confirmed.
     */
    private void prompt(String prompt, int command) {
        DialogBoxUI dlg = new DialogBoxUI(prompt, "Yes", "No");

        dlg.onResponse(value ->
        {
            app.hideDialog(dlg);
            if (value == 0) {
                if (command == ID_EXIT) {
                    System.exit(0);
                } else if (command == ID_RESET) {
                    app.resetScores();
                }
            }

        });
        app.showDialog(dlg);
    }
}
