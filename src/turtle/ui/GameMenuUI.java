package turtle.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * MenuUI.java
 * 
 * This is displayed to the user when the user clicks the menu button, which
 * gives the user an array of in-game options.
 * 
 * @author Henry Wang
 * Date: 4/30/17
 * Period: 2
 */
public class GameMenuUI extends VBox 
{

	public static final int ID_PAUSED = 0;
	public static final int ID_RESUME = 1;
	public static final int ID_RESTART = 2;
	public static final int ID_LEVELSELECT = 3;
	public static final int ID_MAINMENU = 4;
	public static final int ID_EXIT = 5;
	
	private static final double BORDER = 2.0;
	private static final Insets MARGIN_INSET = new Insets(0.0, 5, 5, 5);
	
    private final Label lblTitle;
    private final Separator separator;
    
    private final GameUI parent;
    
	/**
	 * Creates a new MenuUI and initializes UI.
	 * @param parent the GameUI to supply handlers from.
	 */
    public GameMenuUI(GameUI parent) {
    	this.parent = parent;
    	
        lblTitle = createButton(ID_PAUSED, "Paused", false);
        
		separator = new Separator();
        separator.setStyle("-fx-background-color: WHITE;");
        VBox.setMargin(separator, MARGIN_INSET);

        getChildren().addAll(lblTitle, separator, 
    		createButton(ID_RESUME, "Resume", true),
    		createButton(ID_RESTART, "Restart Level", true),
    		createButton(ID_LEVELSELECT, "Level Select", true),
    		createButton(ID_MAINMENU, "Main Menu", true),
    		createButton(ID_EXIT, "Exit", true)
        );
		
        setBackground(new Background(new BackgroundFill(Color.BLACK, 
        		null, null)));
        
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPrefWidth(300.0);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setBorder(new Border(new BorderStroke(Color.WHITE, 
			BorderStrokeStyle.SOLID, null, new BorderWidths(BORDER))));
		
		/** Filters out any mouse clicks in this game menu UI.*/
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
	
	/**
	 * Creates a new light-weight button from the specified name.
	 * @param id the specified id of the action to pass to handler.
	 * @param name name of the button
	 * @param enabled whether if this button is enabled or disabled.
	 * @return a component (as a Label) that becomes the button.
	 */
	private Label createButton(final int id, String name, 
		boolean enabled)
	{
		Label button = new Label(name);
		button.getStyleClass().add("big");
		if (enabled)
			button.getStyleClass().add("lbutton");
		
		button.setAlignment(Pos.CENTER);
        button.setMaxHeight(Double.MAX_VALUE);
        button.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(button, MARGIN_INSET);
		
		if (enabled)
		{
			/** Handles mouse event when user clicks a menu button*/
			button.setOnMouseClicked(new EventHandler<MouseEvent>()
			{
				/** 
				 * Handles the event by delegating to the
				 * {@link turtle.ui.GameUI#handleGameMenu(int)} function.
				 * @param event the event associated with click.
				 */
				@Override
				public void handle(MouseEvent event)
				{
					parent.handleGameMenu(id);
				}
			});
		}
		
		return button;
	}
	
	
	
	
}
