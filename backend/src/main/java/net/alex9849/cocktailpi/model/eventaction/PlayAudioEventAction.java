package net.alex9849.cocktailpi.model.eventaction;

import jakarta.persistence.DiscriminatorValue;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@DiscriminatorValue("PlayAudio")
public class PlayAudioEventAction extends FileEventAction {
    private boolean onRepeat;
    private int volume;
    private String soundDevice;

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

    public String getSoundDevice() {
        return soundDevice;
    }

    public void setSoundDevice(String soundDevice) {
        this.soundDevice = soundDevice;
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
            Line.Info sourceInfo = new Line.Info(SourceDataLine.class);
            for(Mixer.Info info : AudioSystem.getMixerInfo()) {
                Mixer mixer = AudioSystem.getMixer(info);
                if(!mixer.isLineSupported((sourceInfo))) {
                    continue;
                }
                if(!Objects.equals(mixer.getMixerInfo().getName(), this.soundDevice)) {
                    continue;
                }
                clip = AudioSystem.getClip(info);
            }
            if(clip == null) {
                throw new IllegalStateException("Sound device \"" + this.soundDevice + "\" not found!");
            }

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
        } catch (Exception e) {
            e.printStackTrace();
            runningAction.addLog(e);
        } finally {
            if(clip != null) {
                clip.stop();
                clip.close();
            }
        }
    }
}
