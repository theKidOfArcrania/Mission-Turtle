package turtle.ui;

/**
 * MenuUI.java
 * <p>
 * This is displayed to the user when the user clicks the menu button, which
 * gives the user an array of in-game options.
 *
 * @author Henry Wang
 *         Date: 4/30/17
 *         Period: 2
 */
public class GameMenuUI extends MenuUI
{
    public static final int ID_RESUME = 0;
    public static final int ID_RESTART = 1;
    public static final int ID_LEVEL_SELECT = 2;
    public static final int ID_MAIN_MENU = 3;
    public static final int ID_EXIT = 4;

    private static final String[] NAMES = {"Resume", "Restart Level",
            "Level Select", "Main Menu", "Exit"
    };

    private static final double MENU_WIDTH = 300.0;

    /**
     * Creates a new MenuUI and initializes UI.
     *
     * @param parent the GameUI to supply handlers from.
     */
    public GameMenuUI(GameUI parent)
    {
        super("Paused");

        for (int i = 0; i < NAMES.length; i++)
        {
            final int id = i;
            getChildren().add(MenuUI.createButton(NAMES[i], true, true,
                    event -> parent.handleGameMenu(id)));
        }

        setPrefWidth(MENU_WIDTH);
    }
}
