package turtle.editor;

import turtle.file.LevelPack;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;

/**
 * A parseable descriptor representing a {@link turtle.file.LevelPack} file. This is used by
 * other classes in order to generate the packed MTP file.
 * @author Henry Wang
 */
public class LevelPackDescriptor {
    public static final int VERSION_1_0 = 0x10000;
    public static final int VERSION_INVALID = -1;

    private static final int VERSION_MASK = 0xFFFF;

    /**
     * Parses a version string. Must be in the format <tt>MAJ.MIN</tt>. This will be parsed as a
     * version number here the higher significant half represent the major version number, and the
     * lower significant half represent the minor version number.
     * @param ver the version string to parse
     * @return a version number
     */
    private static int parseVersion(String ver) {
        String[] parts = ver.split("\\.");
        if (parts.length != 2)
            return VERSION_INVALID;
        else {
            try {
                int major = parseInt(parts[0]) & VERSION_MASK;
                int minor = parseInt(parts[1]) & VERSION_MASK;
                return major << Short.SIZE | minor;
            } catch (NumberFormatException e) {
                return VERSION_INVALID;
            }
        }

    }

    private String name;
    private UUID levelPackUUID;
    private int version;

    /**
     * Creates a new descriptor setting to default values.
     */
    public LevelPackDescriptor() {
        name = "";
        levelPackUUID = randomUUID();
        version = VERSION_1_0;
    }

    /**
     * Creates a new descriptor from the level pack
     * @param pack the level pack to model
     */
    public LevelPackDescriptor(LevelPack pack) {
        name = pack.getName();
        levelPackUUID = pack.getLevelPackID();
        version = VERSION_1_0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getLevelPackUUID() {
        return levelPackUUID;
    }

    public void setLevelPackUUID(UUID levelPackUUID) {
        this.levelPackUUID = levelPackUUID;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Loads a level pack descriptor file from a specified file
     * @param file the file to read from.
     * @throws IOException if an error occurs while reading.
     */
    public void load(File file) throws IOException{
        load(new FileInputStream(file));
    }

    /**
     * Loads a level pack descriptor file from a specified stream
     * @param is the input stream to read from.
     * @throws IOException if an error occurs while reading.
     */
    public void load(InputStream is) throws IOException {
        Properties props = new Properties();
        props.load(is);

        int ver = parseVersion(props.getProperty("version", ""));
        if (ver == VERSION_INVALID) {
            throw new IOException("Invalid version number");
        } else if (ver > VERSION_1_0) {
            throw new IOException("Unsupported version");
        }

        version = ver;
        name = props.getProperty("name", "");
        try {
            levelPackUUID = fromString(props.getProperty("levelPackUUID", randomUUID().toString()));
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid level-pack UUID");
        }
    }

    /**
     * Saves a level pack descriptor to the file.
     * @param file the file to save to
     * @throws IOException if a error occurs while writing to file
     */
    public void save(File file) throws IOException{
        save(new FileOutputStream(file));
    }

    /**
     * Saves a level pack descriptor to the file.
     * @param os the stream to write to
     * @throws IOException if a error occurs while writing to file
     */
    public void save(OutputStream os) throws IOException{
        Properties props = new Properties();

        props.setProperty("version", format("%d.%d", (version >> Short.SIZE) & VERSION_MASK,
                version & VERSION_MASK));
        props.setProperty("name", name);
        props.setProperty("levelPackUUID", levelPackUUID.toString());

        props.store(os, "Level pack descriptor for Mission Turtle.");
    }
}
