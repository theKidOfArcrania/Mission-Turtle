package turtle.ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import turtle.file.LevelPack;

import java.util.Collection;

/**
 * LevelSelectUI.java
 *
 * This displays a list of levels the user can play at the moment.
 *
 * @author Henry Wang
 * Date: 05/14/2017
 * Period: 2
 */
public class LevelSelectUI extends VBox
{

    private static final int MARGINS = 5;
    private static final Insets MARGIN_INSET = new Insets(MARGINS, MARGINS,
            MARGINS, 0);

    private static final int PACKS_WIDTH = 300;
    private static final int DIALOG_HEIGHT = 400;
    private static final int DIALOG_WIDTH = 800;

    private final VBox levels;
    private final MainApp app;

    private LevelPack selectedPack;
    private int selectedLevel;

    /**
     * Creates a new StartUI and initializes UI.
     * @param app the MainApp the app to execute run level
     */
    public LevelSelectUI(MainApp app)
    {
        this.app = app;

        selectedPack = null;
        selectedLevel = -1;

        MenuUI packs = initializePacksList();
        ScrollPane scrPacks = createScrollPane(packs);
        HBox.setMargin(scrPacks, new Insets(MARGINS));

        levels = new VBox();
        levels.getStyleClass().add("ldialog");

        ScrollPane scrLevels = createScrollPane(levels);
        HBox.setHgrow(scrLevels, Priority.ALWAYS);
        HBox.setMargin(scrLevels, MARGIN_INSET);

        HBox dlg = new HBox();
        dlg.setPrefWidth(DIALOG_WIDTH);
        dlg.setPrefHeight(DIALOG_HEIGHT);
        dlg.getChildren().addAll(scrPacks, scrLevels);

        Pane buttons = initButtonsUI();
        getChildren().addAll(dlg, buttons);
    }

    /**
     * Updates the list of levels of a level pack (if one is already selected).
     */
    public void updateStatus()
    {
        if (selectedPack != null)
            initLevelsUI(selectedPack);
    }

    /**
     * Initializes the interactable buttons to go back and play.
     * @return the buttons pane
     */
    private Pane initButtonsUI()
    {
        EqualGridPane buttons = new EqualGridPane(1, 2);
        buttons.setPadding(new Insets(MARGINS));
        buttons.setHgap(MARGINS);
        buttons.getChildren().addAll(
            /** Handles mouse event when user selects back button*/
            MenuUI.createButton("Back", true, true, new
                    EventHandler<MouseEvent>()
            {
                /**
                 * Handles the click event.
                 * @param event the event associated with click.
                 */
                @Override
                public void handle(MouseEvent event)
                {
                    app.showMainMenu();
                }
            }),

            /** Handles mouse event when user selects play button*/
            MenuUI.createButton("Play!", true, true, new
                    EventHandler<MouseEvent>()
            {
                /**
                 * Handles the click event.
                 * @param event the event associated with click.
                 */
                @Override
                public void handle(MouseEvent event)
                {
                    if (selectedPack != null && selectedLevel != -1)
                        app.startGame(selectedPack, selectedLevel);
                }
            }));
        return buttons;
    }

    /**
     * Creates a scroll pane adjusted to a node.
     * @param child the node to attach scroll pane.
     * @return an initialized scroll-pane.
     */
    private ScrollPane createScrollPane(Node child)
    {
        ScrollPane scrLevels = new ScrollPane(child);
        scrLevels.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrLevels.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrLevels.setPrefHeight(DIALOG_HEIGHT);
        scrLevels.setFitToHeight(true);
        scrLevels.setFitToWidth(true);
        return scrLevels;
    }

    /**
     * Initializes the list of level packs.
     * @return the UI list for level packs
     */
    private MenuUI initializePacksList()
    {
        MenuUI packs = new MenuUI("Level Packs");
        packs.setMaxHeight(Double.MAX_VALUE);
        packs.setPrefWidth(PACKS_WIDTH);

        for (LevelPack pck : app.getLevelPacks())
        {
            /** Handles mouse event when user selects a level pack*/
            Label button = MenuUI.createButton(pck.getName(), true,
                    true, new EventHandler<MouseEvent>()
                    {
                        /**
                         * Handles the event and selects the current level pack.
                         * @param event the event associated with click.
                         */
                        @Override
                        public void handle(MouseEvent event)
                        {
                            for (Node n : packs.getChildren())
                                setSelectedState(n, n == event.getSource());
                            initLevelsUI(pck);
                        }
                    }
            );
            button.getStyleClass().add("litem");
            packs.getChildren().add(button);
        }
        return packs;
    }

    /**
     * Updates the levels UI list for a particular level pack.
     * @param pack the level pack to populate level list.
     */
    private void initLevelsUI(LevelPack pack)
    {
        boolean reachable = true;
        levels.getChildren().clear();

        selectedPack = pack;
        selectedLevel = -1;

        for (int i = 0; i < pack.getLevelCount(); i++)
        {
            final int num = i;
            /** Handles mouse event when user clicks a menu button*/
            String name = (i + 1) + " - " + pack.getLevel(i).getName();
            if (!reachable)
                name = "\uD83D\uDD12 " + name;
            int status = app.checkLevelCompletion(pack, i);
            name += getStatus(status);

            Label button = MenuUI.createButton(name, false,
                    reachable, new EventHandler<MouseEvent>()
                    {
                        /**
                         * Handles the event and selects the current level.
                         * @param event the event associated with click.
                         */
                        @Override
                        public void handle(MouseEvent event)
                        {
                            for (Node n : levels.getChildren())
                                setSelectedState(n, n == event.getSource());
                            if (event.getClickCount() > 1)
                                app.startGame(pack, num);
                            selectedPack = pack;
                            selectedLevel = num;
                        }
                    });
            button.getStyleClass().add("litem");
            button.setAlignment(Pos.CENTER_LEFT);
            levels.getChildren().add(button);

            if (status == MainApp.RESULT_NOT_DONE)
                reachable = false;
        }
        levels.getChildren().add(new Pane());
    }

    /**
     * Sets the selected state of a list-item node.
     * @param n the list-item node.
     * @param selected true to be selected, false to deselect.
     */
    private void setSelectedState(Node n, boolean selected)
    {
        Collection<String> stylClss = n.getStyleClass();
        if (selected)
        {
            if (!stylClss.contains("selected"))
                stylClss.add("selected");
        }
        else
            stylClss.remove("selected");
    }

    /**
     * Turns a status code into its associated message.
     * @param status the finish status.
     * @return a status message.
     */
    private String getStatus(int status)
    {
        if (status == MainApp.RESULT_NOT_DONE)
            return "";
        else if (status == MainApp.RESULT_NO_TIME_LIMIT)
            return " (DONE)";

        final int SECONDS_TO_MIN = 60;
        int min = status / SECONDS_TO_MIN;
        int sec = status % SECONDS_TO_MIN;
        return String.format(" (+%d:%02d)", min, sec);
    }
}
