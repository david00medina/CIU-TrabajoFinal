package gui;

import control.algorithms.Statistics;
import control.algorithms.Transformation;
import control.kinect.Kinect;
import control.kinect.KinectAnathomy;
import control.kinect.KinectSelector;
import kinect4WinSDK.SkeletonData;
import model.postures.DancerData;
import model.sound.Song;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.sound.SoundFile;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameScreen extends Screen
{
    private static final int SCALE = 60;
    private static final int COLS = 60;
    private static final int ROWS = 60;
    private static final int THRESHOLD = 2000;
    private final int STEPS = 10;        // Cuantos cuadros tarda el vuelo del numero
    private final int STEPS_BIGGER = 30; // Cuantos cuadros dura el agrandamiento de la puntuacion total
    private static String BASE_POS_DIR;

    private Kinect kinect;
    private PShape floor;
    private Song song;

    private int currentPosture;
    private int flyingScore;

    private Boolean initialCount = false, startCount = false;
    private int counter = 3, flyingCounter = 0, biggerCounter = 0, time = 0, pausedTime = 0, totalScore = 0;
    private float xStep, yStep;

    private Boolean pause = false;

    private SoundFile start_beep, countdown_beep;

    private List<DancerData> ddl;

    GameScreen(PApplet parent, HashMap<UISelector, PImage> UIResources, String countdownBeep, String startBeep,
               String csvPath, String baseImgDir) {
        super(parent, UIResources, csvPath);

        kinect = new Kinect(this.parent, null, null, null);
        kinect.setHandRadius(0);

        BASE_POS_DIR = baseImgDir;

        createFloor();

        preprocessDancerData();

        currentPosture = new Random().nextInt(ddl.size());

        yStep = (this.parent.height * 0.85f - this.parent.height / 20.f - 180) / STEPS;
        xStep = (this.parent.width - this.parent.width / 8.f - this.parent.width / 2.f - 110) / STEPS;

        countdown_beep = new SoundFile(this.parent, countdownBeep);
        start_beep = new SoundFile(this.parent, startBeep);
    }

    private void preprocessDancerData() {
        ddl = readDancerDataFromCSV();
        translateToOrigin();
    }

    private void translateToOrigin() {
        for (DancerData dd :
                ddl) {
            Transformation.translateToOrigin(parent, dd, KinectAnathomy.SPINE);
        }
    }

    public void show() {

        if (initialCount)
        {
            if (startCount)
            {
                startCount = false;
                time = parent.millis();
            }
            if (parent.millis() - time >= 1000)
            {
                time = parent.millis();//also update the stored time

                if (counter <= 3) while(parent.millis() - time < 1000) {}

                if (counter >= 0)
                {
                    if (counter == 0)
                        start_beep.play();
                    else
                        countdown_beep.play();
                    parent.imageMode(parent.CENTER);
                    parent.image(UIResources.get(UISelector.getSelectorFromID(counter--)), parent.width / 2.f, parent.height / 2.f);
                }
                else
                {
                    initialCount = false;
                    counter = 3;
                    this.song.play();
                }
            }
            return;
        }

        kinect.doSkeleton(true);
        kinect.refresh(KinectSelector.NONE, true);

        DancerData liveDancer = new DancerData(parent, kinect);
        Transformation.translateToOrigin(parent, liveDancer, KinectAnathomy.SPINE);

        DancerData ddCSV = ddl.get(currentPosture);

        if (counter == -1)
        {
            if (flyingCounter == STEPS)
            {
                flyingCounter = 0;
                biggerCounter = 1;
                totalScore += flyingScore;
            }
            else if (biggerCounter == 0)
                ++flyingCounter;
            else if (biggerCounter == STEPS_BIGGER)
            {
                biggerCounter = 0;
                counter = 3;
                time = parent.millis();
                currentPosture = new Random().nextInt(ddl.size());
            }
            else
                ++biggerCounter;
        }
        else
        {
            parent.imageMode(parent.CENTER);
            if (counter > 0) {
                PImage img = UIResources.get(UISelector.getSelectorFromID(counter));
                parent.image(img, parent.width / 2.f, parent.height / 10.f, img.width / 4, img.height / 4);
            } else if (counter == 0) {
                PImage img = UIResources.get(UISelector.NOW);
                parent.image(img, parent.width / 2.f, parent.height / 10.f);
            }
            if (!pause && parent.millis() - time - pausedTime >= 1000)
            {
                if (pausedTime > 0)
                    pausedTime = 0;

                time = parent.millis();//also update the stored time
                --counter;
            }
        }
        // Mostramos el moment score
        parent.pushMatrix();
        parent.translate(0,-80,200);
        parent.ellipseMode(parent.CENTER);
        parent.pushStyle();
        parent.stroke(255, 50, 50);
        parent.strokeWeight(10);
        parent.fill(0);
        parent.ellipse(parent.width / 2.f, parent.height * .86f, parent.width / 4.8f, parent.height / 10.f);
        parent.popStyle();

        parent.fill(255, 50, 50);
        parent.textSize(parent.height / 15.72f);
        parent.textAlign(parent.CENTER, parent.CENTER);

        if (biggerCounter == 0)
        {
            parent.textMode(parent.SHAPE);
            if (flyingCounter > 0)
                parent.text(flyingScore, parent.width / 2.f + xStep * (flyingCounter - 1), parent.height * 0.85f - yStep * (flyingCounter - 1));
            else {
                flyingScore = getScore(liveDancer, ddCSV);
                parent.text(flyingScore, parent.width / 2f, parent.height * 0.85f);
            }
        }
        parent.popMatrix();

        parent.imageMode(parent.CORNER);

        // Mostramos los botones "atras" y "pausa"
        if (mouseOverButtonBack())
        {
            if (parent.mousePressed)
                parent.image(UIResources.get(UISelector.BACK_PRESSED), parent.width / 30, parent.height / 16);
            else
                parent.image(UIResources.get(UISelector.BACK_OVER), parent.width / 30, parent.height / 16);
        }
        else
            parent.image(UIResources.get(UISelector.BACK), parent.width / 30, parent.height / 16);

        if (mouseOverButtonPause())
        {
            if (parent.mousePressed)
                parent.image(UIResources.get(UISelector.PAUSE_PRESSED), parent.width / 10, parent.height / 16);
            else
                parent.image(UIResources.get(UISelector.PAUSE_OVER), parent.width / 10, parent.height / 16);
        }
        else
            parent.image(UIResources.get(UISelector.PAUSE), parent.width / 10, parent.height / 16);


        // Mostramos el tiempo restante de la cancion
        parent.pushMatrix();
        parent.pushStyle();
        parent.translate(130,-100,160);
        parent.imageMode(parent.CENTER);
        parent.image(UIResources.get(UISelector.CHRONO), parent.width / 30, parent.height - parent.height / 13 + 5, 20,20);
        parent.textMode(parent.SHAPE);
        parent.fill(0);
        parent.textSize(parent.height / 15.72f - 15);
        parent.textAlign(parent.LEFT, parent.DOWN);
        parent.text(song.timeLeft(), parent.width / 16.f, parent.height - parent.height / 20.f, 0); // Aqui cambiar scr a la variable de MainScreen
        parent.popStyle();
        parent.popMatrix();

        parent.pushStyle();
        parent.fill(255);
        parent.rectMode(parent.CENTER);
        parent.stroke(0, 255, 90);
        parent.rect(90,parent.height / 10 * 9 - 15,140,40, 1);
        parent.popStyle();

        // Mostramos el score total
        parent.fill(0);
        parent.rectMode(parent.CENTER);
        parent.stroke(0, 255, 90);
        if (biggerCounter == 0)
            parent.rect(parent.width - parent.width / 8.f, parent.height / 17.5f, parent.width / 5.f, parent.height / 15.f);
        else
            parent.rect(parent.width - parent.width / 8.f, parent.height / 17.5f, parent.width / 5 * 1.2f, parent.height / 15.f * 1.2f);

        parent.fill(0, 255, 90);
        parent.textAlign(parent.CENTER, parent.CENTER);

        parent.textMode(parent.SHAPE);
        if (biggerCounter == 0)
            parent.textSize(parent.height / 15.72f);
        else
            parent.textSize(parent.height / 15.72f * 1.2f);
        parent.text(totalScore, parent.width - parent.width / 8f, parent.height / 20f);


        // Mostramos la postura y su rama
        parent.imageMode(parent.CORNER);
        PImage img = parent.loadImage(BASE_POS_DIR + ddl.get(currentPosture).getDancerUUID() + ".png");
        img.resize(parent.width / 4, 0);
        parent.image(img, parent.width - img.width, parent.height - img.height);
        parent.stroke(255, 100, 255);
        parent.line(parent.width - img.width, parent.height, parent.width - img.width, parent.height - img.height);
        parent.line(parent.width - img.width, parent.height - img.height, parent.width, parent.height - img.height);

        makeFloor();
    }

    private DancerData getDancerDataByUUID(String uuid) {
        for (DancerData dd :
                ddl) {
            if (dd.getDancerUUID().equals(uuid)) return dd;
        }
        return null;
    }

    private int getScore(DancerData ddK, DancerData ddCSV) {
        double err = Statistics.euclideanMSE(parent, ddK, ddCSV);
        if (err > 2000 || ddCSV.getAnathomyData().size() == 0 || ddK.getAnathomyData().size() == 0) return 0;
        return (int) Math.abs(err - THRESHOLD);
    }

    public Boolean mouseOverButtonBack()
    {
        return !initialCount && parent.mouseX >= parent.width / 30 && parent.mouseX <= parent.width / 30 + UIResources.get(UISelector.BACK).width
                && parent.mouseY >= parent.height / 16 && parent.mouseY <= parent.height / 16 + UIResources.get(UISelector.BACK).height;
    }
    public Boolean mouseOverButtonPause()
    {
        return !initialCount && parent.mouseX >= parent.width / 10 && parent.mouseX <= parent.width / 10 + UIResources.get(UISelector.PAUSE).width
                && parent.mouseY >= parent.height / 16 && parent.mouseY <= parent.height / 16 + UIResources.get(UISelector.PAUSE).height;
    }

    private void makeFloor() {
        parent.pushMatrix();
        parent.translate(-ROWS * SCALE / 2.f, 400, -COLS * SCALE /2.f);
        parent.shape(floor);
        parent.popMatrix();
    }

    private void createFloor() {
        parent.stroke(255);
        parent.noFill();

        floor = parent.createShape();
        for (int z = 0; z < COLS; z++) {
            floor.beginShape(parent.QUAD_STRIP);
            for (int x = 0; x < ROWS; x++) {
                floor.vertex(x * SCALE, 0, z * SCALE);
                floor.vertex(x * SCALE, 0, (z+1) * SCALE);
            }
            floor.endShape();
        }
    }

    void setSong(Song song) {
        this.song = song;
    }

    Song getSong() {
        return song;
    }

    int getScore() {
        return totalScore;
    }

    void setInitialCount()
    {
        biggerCounter = 0;
        flyingCounter = 0;
        initialCount = true;
        startCount = true;
        counter = 3;
        totalScore = 0;
        pause = false;
        pausedTime = 0;
    }

    void pause()
    {
        pause = !pause;
        if (pause)
        {
            pausedTime = parent.millis();
            song.pause(); // Aqui cambiar scr a la variable de MainScreen
        }
        else
        {
            pausedTime = parent.millis() - pausedTime;
            song.play(); // Aqui cambiar scr a la variable de MainScreen
        }
    }

    void appearEvent(SkeletonData _s) {
        kinect.appearEvent(_s);
    }

    void disappearEvent(SkeletonData _s) {
        kinect.disappearEvent(_s);
    }

    void moveEvent(SkeletonData _b, SkeletonData _a) {
        kinect.moveEvent(_b, _a);
    }

    void setMask(PImage mask) {
        kinect.setMask(mask);
    }
}
