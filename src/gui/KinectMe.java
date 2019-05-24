package gui;

import kinect4WinSDK.SkeletonData;
import processing.core.PApplet;

public class KinectMe extends PApplet {
    private static final String SONGS_DIR = ".\\res\\songs";
    private static final String POSTURES_CSV = ".\\res\\dance_postures.csv";
    private static final String POSTURES_IMG_DIR = ".\\res\\images\\postures";

    private MainScreen screen;
    private GameScreen gameScreen;
    private boolean isGameScreen = false;


    @Override
    public void settings() {
        super.settings();
        size(640, 480, P3D);
//        fullScreen();
    }


    @Override
    public void setup() {
        super.setup();
        smooth();
        stroke(255);

        screen = new MainScreen(this, SONGS_DIR);
        gameScreen = new GameScreen(this,  POSTURES_CSV, POSTURES_IMG_DIR);
    }

    @Override
    public void draw() {
        background(10, 255);
        rect(0,0,width,height);
        this.clear();
        if (isGameScreen) {
            gameScreen.show();
            lights();
        } else {
            screen.show();
        }
    }

    @Override
    public void mouseClicked() {
        if (screen.mouseOverButtonJugar()) {
            isGameScreen = true;
            screen.getCurrentSong().stop();
            gameScreen.setSong(screen.getCurrentSong());
        } else if (screen.mouseOverButtonPrevious()) {
            screen.setPreviousSong();
        } else if (screen.mouseOverButtonNext()) {
            screen.setNextSong();
        }
    }

    public void appearEvent(SkeletonData _s) {
        gameScreen.appearEvent(_s);
    }

    public void disappearEvent(SkeletonData _s) {
        gameScreen.disappearEvent(_s);
    }

    public void moveEvent(SkeletonData _b, SkeletonData _a) {
        gameScreen.moveEvent(_b, _a);
    }

    public static void main(String[] args) {
        PApplet.main("gui.KinectMe");
    }
}
