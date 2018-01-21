package turtle.comp;

import javafx.scene.media.AudioClip;


import static java.lang.ClassLoader.getSystemResource;

/**
 * Contains all the default sounds that can be used for various interactions
 * @author Henry Wang
 */
public interface Sounds {
    AudioClip BLOWING = loadClip("turtle/comp/blowing.wav");
    AudioClip CLICK = loadClip("turtle/comp/click.wav");
    AudioClip EXPLOSION = loadClip("turtle/comp/explosion.wav");
    AudioClip GRASS = loadClip("turtle/comp/grass.wav");
    AudioClip SPLASH = loadClip("turtle/comp/splash.wav");
    AudioClip STEAM = loadClip("turtle/comp/steam.wav");
    AudioClip TAP = loadClip("turtle/comp/tap.wav");
    AudioClip UNLOCK = loadClip("turtle/comp/unlock.wav");
    AudioClip WHIP = loadClip("turtle/comp/whip.wav");

    /**
     * Loads an audio clip from the classpath
     * @param res the classpath of the audio clip
     * @return the loaded audio clip
     */
    static AudioClip loadClip(String res) {

        return new AudioClip(getSystemResource(res).toExternalForm());
    }
}
