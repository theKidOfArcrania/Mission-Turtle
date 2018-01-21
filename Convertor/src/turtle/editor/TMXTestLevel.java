package turtle.editor;

import javafx.application.Application;
import javafx.stage.Stage;
import turtle.file.Level;
import turtle.file.LevelPack;
import turtle.ui.MainApp;

import java.io.File;
import java.util.UUID;

/**
 * @author Henry Wang
 */
public class TMXTestLevel {
    public static class Launcher extends Application {
        private static LevelPack testing;

        @Override
        public void start(Stage stage) throws Exception {
            MainApp app = new MainApp();
            app.start(stage);
            app.startGame(testing, 0);
        }
    }
    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java turtle.editor.TMXTestLevel <TMX levelfile>");
            System.exit(1);
        }

        File lvlFile = new File(args[0]);
        if (!lvlFile.isFile()) {
            System.out.println("Not a file: " + args[0]);
            System.exit(1);
        }

        System.setProperty("user.dir", lvlFile.getParent());

        LevelPack pack = Launcher.testing = new LevelPack("Testing");
        pack.setLevelPackID(new UUID(0, 0));

        Level lvl = TMXToMTP.loadLevel(lvlFile);
        pack.addLevel(lvl);

        Application.launch(Launcher.class);
    }
}
