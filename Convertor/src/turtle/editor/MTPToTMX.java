package turtle.editor;

import org.mapeditor.io.TMXMapWriter;
import turtle.file.Level;
import turtle.file.LevelPack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.lang.String.format;

/**
 * @author Henry Wang
 */
public class MTPToTMX {

    public static void main(String[] args) throws Exception {
        //Load level pack.
        LevelPack pack = chooseLevelPack();
        if (pack == null)
            return;

        //TODO: load other tilesets as well.
        Files.copy(getSystemResourceAsStream("tileset.png"), Paths.get("tileset.png"),
                StandardCopyOption.REPLACE_EXISTING);

        LevelPackDescriptor lpd = new LevelPackDescriptor(pack);
        lpd.save(new File("pack.lpd"));

        //Load each level data
        TMXMapWriter writer = new TMXMapWriter();
        for (int i = 0; i < pack.getLevelCount(); i++) {
            Level lvl = pack.getLevel(i);
            pack.loadLevel(i);
            writer.writeMap(new MazeMap(lvl), format("Level%03d.tmx", i + 1));
        }
    }



    private static LevelPack chooseLevelPack() throws IOException {
        Scanner in = new Scanner(System.in);
        File[] dir = new File(".").listFiles();
        ArrayList<File> selections = new ArrayList<>();
        if (dir == null) {
            System.err.println("Unable to open current directory");
            return null;
        }

        for (File f : dir) {
            if (f.getName().toLowerCase().endsWith(".mtp")) {
                selections.add(f);
            }
        }

        System.out.println("Choose a level pack to extract: ");
        for (int i = 0; i < selections.size(); i++) {
            System.out.printf(" %d - %s\n", i, selections.get(i).getName());
        }

        int res = -1;
        while (in.hasNext()) {
            if (in.hasNextInt()) {
                res = in.nextInt();
                if (res >= 0 && res < selections.size())
                    break;

                res = -1;
            }

            System.out.printf("Please enter a valid integer from %d to %d\n",
                    0, selections.size() - 1);

            in.nextLine();
        }

        if (res == -1)
            return null;

        return new LevelPack(selections.get(res));
    }
}
