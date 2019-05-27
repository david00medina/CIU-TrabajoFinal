package gui;

import kinect4WinSDK.SkeletonData;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;

public class KinectMe extends PApplet {
    private static final String SOUND_DIR = ".\\res\\sound";
    private static final String POSTURES_CSV = ".\\res\\data\\dance_postures.csv";
    private static final String RANKING_CSV = "..\\res\\data\\ranking.csv";
    private static final String BASE_IMG_DIR = ".\\res\\images";

    private MainScreen screen;
    private GameScreen gameScreen;
    private PreludeScreen preludeScreen;
    private RankingScreen rankingScreen;

    private boolean isGameScreen = false;
    private boolean isPreludeScreen = false;
    private boolean isRankingScreen = false;

    private HashMap<UISelector, PImage> UIResources;

    @Override
    public void settings() {
        super.settings();
        size(640, 480, P3D);
//        fullScreen(P3D);
    }


    @Override
    public void setup() {
        super.setup();
//        smooth();
        stroke(255);

        initializeUI();

        screen = new MainScreen(this, UIResources, SOUND_DIR + "\\songs");
        gameScreen = new GameScreen(this, UIResources, SOUND_DIR + "\\fx\\count_beep.wav",
                SOUND_DIR + "\\fx\\start_beep.wav", POSTURES_CSV, BASE_IMG_DIR + "\\postures\\");
        gameScreen.setInitialCount();
        preludeScreen = new PreludeScreen(this, UIResources, BASE_IMG_DIR + "\\masks\\");
        rankingScreen = new RankingScreen(this, UIResources, RANKING_CSV);
    }

    private void initializeUI() {
        UIResources = new HashMap<>();
        UIResources.put(UISelector.NOW, loadImage(BASE_IMG_DIR + "\\ui\\ya.png", "png"));
        UIResources.put(UISelector.START, loadImage(BASE_IMG_DIR + "\\ui\\start.png", "png"));
        UIResources.put(UISelector.ONE, loadImage(BASE_IMG_DIR + "\\ui\\1.png", "png"));
        UIResources.put(UISelector.TWO, loadImage(BASE_IMG_DIR + "\\ui\\2.png", "png"));
        UIResources.put(UISelector.THREE, loadImage(BASE_IMG_DIR + "\\ui\\3.png", "png"));
        UIResources.put(UISelector.CHRONO, loadImage(BASE_IMG_DIR + "\\ui\\stopwatch.png", "png"));
        UIResources.put(UISelector.BACK, loadImage(BASE_IMG_DIR + "\\ui\\back.png", "png"));
        UIResources.put(UISelector.BACK_OVER, loadImage(BASE_IMG_DIR + "\\ui\\back_over.png", "png"));
        UIResources.put(UISelector.BACK_PRESSED, loadImage(BASE_IMG_DIR + "\\ui\\back_pressed.png", "png"));
        UIResources.put(UISelector.PAUSE, loadImage(BASE_IMG_DIR + "\\ui\\pause.png", "png"));
        UIResources.put(UISelector.PAUSE_OVER, loadImage(BASE_IMG_DIR + "\\ui\\pause_over.png", "png"));
        UIResources.put(UISelector.PAUSE_PRESSED, loadImage(BASE_IMG_DIR + "\\ui\\pause_pressed.png", "png"));
        UIResources.put(UISelector.TITLE, loadImage(BASE_IMG_DIR + "\\ui\\titulo.png", "png"));
        UIResources.put(UISelector.CROWN, loadImage(BASE_IMG_DIR + "\\ui\\crown.png", "png"));
        UIResources.put(UISelector.GOLD_MEDAL, loadImage(BASE_IMG_DIR + "\\ui\\gold-medal.png", "png"));
        UIResources.put(UISelector.SILVER_MEDAL, loadImage(BASE_IMG_DIR + "\\ui\\silver-medal.png", "png"));
        UIResources.put(UISelector.BRONZE_MEDAL, loadImage(BASE_IMG_DIR + "\\ui\\bronze-medal.png", "png"));

        UIResources.get(UISelector.NOW).resize(width / 10, 0);
        UIResources.get(UISelector.BACK).resize(0, UIResources.get(UISelector.BACK).height * height / 768);
        UIResources.get(UISelector.BACK_OVER).resize(0, UIResources.get(UISelector.BACK).height);
        UIResources.get(UISelector.BACK_PRESSED).resize(0, UIResources.get(UISelector.BACK).height);
        UIResources.get(UISelector.PAUSE).resize(0, UIResources.get(UISelector.PAUSE).height * height / 768);
        UIResources.get(UISelector.PAUSE_OVER).resize(0, UIResources.get(UISelector.PAUSE).height);
        UIResources.get(UISelector.PAUSE_PRESSED).resize(0, UIResources.get(UISelector.PAUSE).height);
        UIResources.get(UISelector.CHRONO).resize(0, height / 15);
        UIResources.get(UISelector.CROWN).resize(0,50);
        UIResources.get(UISelector.GOLD_MEDAL).resize(0,20);
        UIResources.get(UISelector.SILVER_MEDAL).resize(0,20);
        UIResources.get(UISelector.BRONZE_MEDAL).resize(0,20);
    }

    @Override
    public void draw() {
        background(10, 255);
        rect(0,0,width,height);
        this.clear();
        if (isGameScreen) {
            gameScreen.show();
            if ("00:00".equals(gameScreen.getSong().timeLeft())) {
                isGameScreen = false;
                isRankingScreen = true;
                rankingScreen.loadScore(preludeScreen.getPlayerName(), gameScreen.getScore());
            }
        } else if (isPreludeScreen) {
            preludeScreen.show();
        } else if (isRankingScreen) {
            rankingScreen.show();
        } else {
            screen.show();
        }
    }

    @Override
    public void mouseReleased() {
        if (!isGameScreen && !isPreludeScreen && !isRankingScreen && screen.mouseOverButtonJugar()) {
            isPreludeScreen = true;
        } else if (!isGameScreen && !isPreludeScreen && !isRankingScreen && screen.mouseOverButtonPrevious()) {
            screen.setPreviousSong();
        } else if (!isGameScreen && !isPreludeScreen && !isRankingScreen && screen.mouseOverButtonNext()) {
            screen.setNextSong();
        } else if (!isGameScreen && isPreludeScreen && !isRankingScreen && preludeScreen.mouseOverPlay()) {
            isGameScreen = true;
            isPreludeScreen = false;
            screen.getCurrentSong().stop();
            gameScreen.setSong(screen.getCurrentSong());
            gameScreen.setMask(preludeScreen.getSelectedMask());
        } else if (!isGameScreen && isPreludeScreen && !isRankingScreen && preludeScreen.mouseOverBack()) {
            isPreludeScreen = false;
            screen.getCurrentSong().stop();
            screen.getCurrentSong().play();
        } else if (isGameScreen && !isPreludeScreen && !isRankingScreen && gameScreen.mouseOverButtonPause()) {
            gameScreen.pause();
        } else if (isGameScreen && !isPreludeScreen && !isRankingScreen && gameScreen.mouseOverButtonBack()) {
            screen.getCurrentSong().stop();
            screen.getCurrentSong().play();
            isGameScreen = false;
            isPreludeScreen = false;
            gameScreen.setInitialCount();
        } else if (!isGameScreen && isPreludeScreen && !isRankingScreen) {
            preludeScreen.mouseOverLeft();
            preludeScreen.mouseOverRight();
        } else if (!isGameScreen && !isPreludeScreen && isRankingScreen && rankingScreen.mouseOverContinue()) {
            isRankingScreen = false;
            rankingScreen.reset();
            gameScreen.setInitialCount();
            screen.getCurrentSong().stop();
            screen.getCurrentSong().play();
        }
    }

    @Override
    public void keyPressed() {
        if (!isGameScreen && isPreludeScreen) {
            preludeScreen.keyboardTextArea();
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
