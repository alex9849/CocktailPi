package net.alex9849.cocktailmaker.model.eventaction;

import javax.persistence.DiscriminatorValue;
import javax.sound.sampled.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@DiscriminatorValue("PlayAudio")
public class PlayAudioEventAction extends FileEventAction {
    private boolean onRepeat;
    private AudioInputStream audioInputStream;

    public boolean isOnRepeat() {
        return onRepeat;
    }

    public void setOnRepeat(boolean onRepeat) {
        this.onRepeat = onRepeat;
    }

    @Override
    public AudioInputStream getFileInputStream() {
        return audioInputStream;
    }

    public void setAudioInputStream(AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }

    @Override
    protected String generateDescription() {
        return "Play audiofile: " + getFileName();
    }

    @Override
    public void trigger() {
        CountDownLatch syncLatch = new CountDownLatch(1);
        Clip clip = null;

        try {
            clip = AudioSystem.getClip();
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    syncLatch.countDown();
                }
            });

            clip.open(audioInputStream);
            clip.loop(onRepeat? Clip.LOOP_CONTINUOUSLY : 0);
            clip.start();
            syncLatch.await();
        } catch (InterruptedException e) {
            if(clip != null) {
                clip.drain();
                clip.stop();
                clip.close();
            }
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        } finally {
            if(clip != null) {
                clip.drain();
                clip.stop();
                clip.close();
            }
        }
    }
}
