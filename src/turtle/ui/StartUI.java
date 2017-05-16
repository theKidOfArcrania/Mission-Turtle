package turtle.ui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.function.IntConsumer;

/**
 * StartUI.java
 * <p>
 * This shows the user's main menu options, which will be the first thing
 * that pops up in the game.
 *
 * @author Henry Wang
 *         Date: 5/14/17
 *         Period: 2
 */
public class StartUI extends MenuUI
{

    public static final int ID_PLAY = 0;
    public static final int ID_LEVELSELECT = 1;
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
    public StartUI(MainApp app)
    {
        super("Mission Turtle: The Rescue");

        this.app = app;

        for (int i = 0; i < NAMES.length; i++)
        {
            final int id = i;
            /** Handles mouse event when user clicks a menu button*/
            getChildren().add(MenuUI.createButton(NAMES[i], true, true,
                    new EventHandler<MouseEvent>()
                    {
                        /**
                         * Handles the event by delegating to the
                         * {@link StartUI#handleCommand(int)}  function.
                         * @param event the event associated with click.
                         */
                        @Override
                        public void handle(MouseEvent event)
                        {
                            handleCommand(id);
                        }
                    }));
        }

        final int MENU_WIDTH = 400;
        setPrefWidth(MENU_WIDTH);
    }

    /**
     * Handles a specific command when the user clicks on a menu button.
     *
     * @param index the index of the button clicked.
     */
    private void handleCommand(int index)
    {
        switch (index)
        {
            case ID_PLAY:
                app.startPreviousGame();
                break;
            case ID_LEVELSELECT:
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
    private void prompt(String prompt, int command)
    {
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
                if (value == 0)
                {
                    if (command == ID_EXIT)
                        System.exit(0);
                    else if (command == ID_RESET)
                        app.resetScores();
                }

            }
        });
        app.showDialog(dlg);
    }
}
