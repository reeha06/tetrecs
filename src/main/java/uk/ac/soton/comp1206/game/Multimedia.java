package uk.ac.soton.comp1206.game;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static boolean audioEnabled = true;
    private static boolean musicEnabled = true;
    /**
     * audioPlayer that plays audio
     */
    private static MediaPlayer audioPlayer;
    /**
     * musicPlayer that plays music
     */
    private static MediaPlayer musicPlayer;

    /**
     * plays audio
     * @param file location of audio file
     */
    public static void playAudio(String file) {
        String music = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing audio: " + music);

        try {
            Media play = new Media(music);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * plays music on a loop
     * @param file location of music file
     */
    public static void playMusic(String file) {
        String bgMusic = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing audio: " + bgMusic);

        try {
            Media play = new Media(bgMusic);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(javafx.util.Duration.ZERO));
            musicPlayer.play();
        } catch (Exception e) {
            musicEnabled = false;
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }

    }

    /**
     * plays sounds
     * @param file location of sound file
     */
    public static void playSound(String file) {
        String music = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing audio: " + music);

        try {
            Media play = new Media(music);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * stops audio
     */
    public static void stopAudio() {
        audioPlayer.stop();
    }
}
