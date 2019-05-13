package model.sound;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

import java.util.concurrent.TimeUnit;

public class Song {
    AudioPlayer cancion;
    Minim minim;

    public Song (PApplet parent, String ruta) {
        minim = new Minim(parent);
        this.cancion = minim.loadFile(ruta);
    }

    public void play() {
        cancion.play();
    }

    public void pause() {
        cancion.pause();
    }

    public void stop() {
        cancion.pause();
        cancion.rewind();
    }

    public long songDuration() {
        return cancion.length();
    }

    public long songPosition() {
        return cancion.position();
    }

    public String timeLeft() {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(songDuration() - songPosition()),
                TimeUnit.MILLISECONDS.toSeconds(songDuration() - songPosition()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(songDuration() - songPosition()))
        );
    }

    public boolean isPlaying() {
        return cancion.isPlaying();
    }
}
