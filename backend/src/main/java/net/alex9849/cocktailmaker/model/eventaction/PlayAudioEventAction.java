package net.alex9849.cocktailmaker.model.eventaction;

import javax.persistence.DiscriminatorValue;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@DiscriminatorValue("PlayAudio")
public class PlayAudioEventAction extends FileEventAction {
    private boolean onRepeat;
    private int volume;

    public boolean isOnRepeat() {
        return onRepeat;
    }

    public void setOnRepeat(boolean onRepeat) {
        this.onRepeat = onRepeat;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String getDescription() {
        String desc = "Play audiofile: " + getFileName() + " (Volume: " + this.volume + "%)";
        if(this.onRepeat) {
            desc += " (Repeating)";
        }
        return desc;
    }

    @Override
    public void trigger(RunningAction runningAction) {
        CountDownLatch syncLatch = new CountDownLatch(1);
        Clip clip = null;

        try {
            clip = AudioSystem.getClip();
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    syncLatch.countDown();
                }
            });
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(getFile()));
            clip.open(audioInputStream);
            clip.loop(onRepeat? Clip.LOOP_CONTINUOUSLY : 0);
            FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if(floatControl != null) {
                float volume = ((floatControl.getMaximum() - floatControl.getMinimum()) / 100 * this.volume) + floatControl.getMinimum();
                volume = Math.min(volume, floatControl.getMaximum());
                volume = Math.max(volume, floatControl.getMinimum());
                floatControl.setValue(volume);
            }
            clip.start();
            syncLatch.await();
        } catch (InterruptedException e) {
            if(clip != null) {
                clip.stop();
                clip.close();
            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        } finally {
            if(clip != null) {
                clip.stop();
                clip.close();
            }
        }
    }
}
