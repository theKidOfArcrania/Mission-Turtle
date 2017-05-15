package turtle.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * MenuUI.java
 *
 * This is an UI base for all UI's with a number of menu buttons to press.
 *
 * @author Henry Wang
 * Date: 5/14/17
 * Period: 2
 */
public class MenuUI extends VBox {

    private static final Insets MARGIN_INSET = new Insets(0.0, 5, 5, 5);



    /**
     * Creates a new light-weight button from the specified name.
     * @param name name of the button
     * @param big whether if this button is big or small.
     * @param enabled whether if this button is enabled or disabled.
     * @param handler the event handler when button is clicked.
     * @return a component (as a Label) that becomes the button.
     */
    public static Label createButton(String name, boolean big, boolean enabled,
                                     EventHandler<MouseEvent> handler)
    {
        Label button = new Label(name);
        if (big)
            button.getStyleClass().add("big");
        if (enabled)
            button.getStyleClass().add("lbutton");

        button.setAlignment(Pos.CENTER);
        button.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(button, MARGIN_INSET);

        if (enabled)
            button.setOnMouseClicked(handler);

        return button;
    }

    /**
     * Creates a new menu UI.
     * @param title the title of menu.
     */
    public MenuUI(String title)
    {
        Label lblTitle = createButton(title, true,false, null);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: WHITE;");
        VBox.setMargin(separator, MARGIN_INSET);

        getChildren().addAll(lblTitle, separator);

        getStyleClass().add("ldialog");

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        /** Filters out any mouse clicks in this menu UI.*/
        setOnMouseClicked(new EventHandler<Event>()
        {

            /**
             * Consumes all mouse click events.
             * @param event associated event object with click.
             */
            @Override
            public void handle(Event event)
            {
                event.consume();
            }

        });
    }


}
