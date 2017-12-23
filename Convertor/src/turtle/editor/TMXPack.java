package turtle.editor;

import turtle.file.LevelPack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;
import static java.util.regex.Pattern.compile;
import static turtle.editor.TMXToMTP.loadLevel;

/**
 * Packs all the TMX level files into a single level pack.
 * @author Henry Wang
 */
public class TMXPack {
    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java turtle.editor.TMXPack <Level pack directory>");
            System.exit(1);
        }

        //Verify argument is a valid directory
        File lvlDir = new File(args[0]);
        if (!lvlDir.isDirectory()) {
            System.out.println("Not a directory: " + args[0]);
            System.exit(1);
        }

        //Change current directory
        lvlDir = lvlDir.getCanonicalFile();
        System.setProperty("user.dir", lvlDir.toString());

        //Make sure level pack descriptor exists
        File lpdFile = new File(lvlDir, "pack.lpd");
        if (!lpdFile.isFile()) {
            System.out.println("Cannot find pack.lpd file");
            System.exit(1);
        }

        //Load level descriptor and initialize level pack
        LevelPackDescriptor lpd = new LevelPackDescriptor();
        lpd.load(lpdFile);

        LevelPack pack = new LevelPack(lpd.getName());
        pack.setLevelPackID(lpd.getLevelPackUUID());

        //Load all the Level files which have the format Level***.tmx
        for (File f : getLevelFiles(lvlDir)) {
            pack.addLevel(loadLevel(f));
            System.out.println("Loaded " + f.toString());
        }
        pack.savePack(new File(lvlDir, pack.getName() + ".mtp"));
    }

    /**
     * Obtains all the level files in the specified directory
     * @param lvlDir the directory with the level files.
     * @return a list of files.
     */
    private static List<File> getLevelFiles(File lvlDir) {
        File[] files = lvlDir.listFiles();
        ArrayList<File> list = new ArrayList<>();
        if (files == null)
            return list;

        Pattern levels = compile("Level\\d{3}.tmx");
        for (File f : files) {
            if (levels.matcher(f.getName()).matches()) {
                list.add(f);
            }
        }

        list.sort(comparing(File::getName));
        return list;
    }
}
