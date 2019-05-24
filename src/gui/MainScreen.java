package gui;

import model.sound.Song;
import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.util.ArrayList;

class MainScreen extends Screen
{
    private PImage titulo;
    private ArrayList<String> SongsNames;
    private ArrayList<Song> Songs;
    private int currentSong = 0;
    private int numOfSongs = 0;

    MainScreen(PApplet parent, String directory)
    {
        super(parent);

        this.parent = parent;
        SongsNames = new ArrayList<String>();
        Songs = new ArrayList<Song>();
        titulo = parent.loadImage(".\\res\\images\\titulo.png");
        File f = null;
        String[] paths;
        try
        {
            // create new file
            f = new File(directory);

            // array of files and directory
            paths = f.list();

            // for each name in the path array
            for (String path:paths)
            {
                ++numOfSongs;
                SongsNames.add(path);
                Songs.add(new Song(parent, directory + "/" + path));
            }
        }
        catch(Exception e)
        {
            // if any error occurs
            e.printStackTrace();
        }
        if (numOfSongs > 0)
            Songs.get(0).play();
    }
    void show()
    {
        parent.background(10);
        parent.imageMode(parent.CENTER);
        parent.image(titulo, parent.width/2, parent.height / 3, parent.width / 1.5f, parent.height / 1.5f);

        // Anadimos el boton "JUGAR"
        parent.strokeWeight(5);
        if (mouseOverButtonJugar())
        {
            if (parent.mousePressed)
                parent.fill(100, 0, 0);
            else
                parent.fill(255, 0, 0);

            parent.stroke(100, 255, 0);
        }
        else
        {
            parent.stroke(0, 100, 255);
            parent.fill(255, 0, 0);
        }

        parent.rectMode(parent.CENTER);
        parent.rect(parent.width/2, parent.height/1.125f, parent.width / 7, parent.height / 12, 8);

        // Escribimos "JUGAR" en el boton
        parent.fill(0);
        parent.textSize(parent.height / 30.72f);
        parent.textAlign(parent.CENTER, parent.CENTER);
        parent.text("JUGAR", parent.width/2, parent.height/1.13f);

        // Hacemos reproducir la cancion

        // Anadimos campo de titulo de cancion
        parent.strokeWeight(1);
        parent.stroke(250);

        parent.fill(150, 150, 150);
        parent.rectMode(parent.CENTER);
        parent.rect(parent.width/2, parent.height/1.35f, parent.width / 2, parent.height / 13, 2);

        // Anadimos el titulo de la cancion
        parent.fill(0);
        parent.textSize(parent.height / 30.72f);
        parent.textAlign(parent.CENTER, parent.CENTER);
        String song = SongsNames.get(currentSong);
        parent.text(song.substring(0, song.lastIndexOf('.')), parent.width/2, parent.height/1.36f);


        // Anadimos los botones "previa cancion" y "proxima cancion"
        parent.strokeWeight(3);
        if (currentSong == 0)
        {
            parent.stroke(50, 50, 50);
            parent.fill(100, 100, 100);
        }
        else
        {
            if (mouseOverButtonPrevious())
            {
                if (parent.mousePressed)
                    parent.fill(0, 100, 0);
                else
                    parent.fill(0, 255, 0);

                parent.stroke(100, 255, 0);
            }
            else
            {
                parent.stroke(0, 100, 255);
                parent.fill(0, 255, 0);
            }
        }
        parent.triangle(parent.width / 5.5f, parent.height/1.35f, parent.width / 4.5f, parent.height/1.35f - parent.height / 26, parent.width / 4.5f, parent.height/1.35f + parent.height / 26);

        if (currentSong == numOfSongs - 1)
        {
            parent.stroke(50, 50, 50);
            parent.fill(100, 100, 100);
        }
        else
        {
            if (mouseOverButtonNext())
            {
                if (parent.mousePressed)
                    parent.fill(0, 100, 0);
                else
                    parent.fill(0, 255, 0);

                parent.stroke(100, 255, 0);
            }
            else
            {
                parent.stroke(0, 100, 255);
                parent.fill(0, 255, 0);
            }
        }
        parent.triangle(parent.width - parent.width / 5.5f, parent.height/1.35f, parent.width - parent.width / 4.5f, parent.height/1.35f - parent.height / 26, parent.width - parent.width / 4.5f, parent.height/1.35f + parent.height / 26);

    }
    public Boolean mouseOverButtonJugar()
    {
        return parent.mouseX >= parent.width/2 - parent.width / 14 && parent.mouseX <= parent.width/2 + parent.width / 14
                && parent.mouseY >= parent.height/1.125f - parent.height / 24 && parent.mouseY <= parent.height/1.125f + parent.height / 24;
    }
    public Boolean mouseOverButtonPrevious()
    {
        if (currentSong == 0)
            return false;

        float coef = PApplet.map(parent.mouseX, parent.width / 5.5f, parent.width / 4.5f, 1, 0);
        return parent.mouseX >= parent.width / 5.5f && parent.mouseX <= parent.width / 4.5f
                && parent.mouseY >= parent.height/1.35f - parent.height / 26 + (parent.height / 26 * coef)
                && parent.mouseY <= parent.height/1.35f + parent.height / 26 - (parent.height / 26 * coef);
    }
    public Boolean mouseOverButtonNext()
    {
        if (currentSong == numOfSongs - 1)
            return false;

        float coef = PApplet.map(parent.mouseX, parent.width - parent.width / 5.5f, parent.width - parent.width / 4.5f, 1, 0);
        return parent.mouseX >= parent.width - parent.width / 4.5f && parent.mouseX <= parent.width - parent.width / 5.5f
                && parent.mouseY >= parent.height/1.35f - parent.height / 26 + (parent.height / 26 * coef)
                && parent.mouseY <= parent.height/1.35f + parent.height / 26 - (parent.height / 26 * coef);
    }
    public int getNumberOfSongs()
    {
        return numOfSongs;
    }
    public void setNextSong()
    {
        Songs.get(currentSong).stop();
        Songs.get(++currentSong).play();
    }
    public void setPreviousSong()
    {
        Songs.get(currentSong).stop();
        Songs.get(--currentSong).play();
    }
    public int getCurrentSongID()
    {
        return currentSong;
    }
    // No se si se necesita
    public Song getCurrentSong()
    {
        return Songs.get(currentSong);
    }
}

