package gui;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.Table;
import processing.data.TableRow;

import java.util.HashMap;

public class RankingScreen extends Screen {
    private final String RANKING_CSV;

    private PApplet parent;
    private Table ranking;
    private PImage title;
    private boolean isFirstRun = true;

    RankingScreen(PApplet parent, HashMap<UISelector, PImage> uiResources, String rankingCSV) {
        super(parent, uiResources);
        this.parent = parent;

        RANKING_CSV = rankingCSV;

        ranking = this.parent.loadTable(rankingCSV,"header");

        title = UIResources.get(UISelector.TITLE);
        title.resize(0, 120);
    }

    public void show() {
        parent.textMode(parent.SHAPE);
        parent.rectMode(parent.CORNER);
        parent.textMode(parent.CORNER);
        parent.textAlign(parent.CORNER, parent.CORNER);
        screenRanking();
    }

    void loadScore(String name, int score){
        TableRow res = ranking.findRow(name, "Name");
        if (res == null) {
            TableRow newRow = ranking.addRow();
            newRow.setString("Name", name);
            newRow.setInt("Score", score);
        } else if (res != null && res.getInt("Score") < score) {
            res.setString("Name", name);
            res.setInt("Score", score);
        }
        parent.saveTable(ranking, RANKING_CSV);
    }

    void screenRanking(){
        ranking.setColumnType(1, "int");

        if (isFirstRun) {
            ranking.sortReverse("Score");
            isFirstRun = false;
        }

        parent.fill(255);
        parent.textSize(26);
        parent.text("Ranking: ", 100, parent.height/5-30 );
        parent.textSize(18);
        int Yvalue = parent.height/4;
        parent.text("Nombre ", 100,Yvalue-10 );
        parent.text("Puntuación ", 300, Yvalue-10 );
        showRankingImg(Yvalue);
        int max10 = 0; //Solo mostramos un max de 10
        for (TableRow row : ranking.rows()) {
            Yvalue +=25;
            parent.text(row.getString("Name"), 100, Yvalue );
            parent.text(row.getString("Score"), 300, Yvalue );
            if(++max10 >= 10) break;
        }

        //Boton de Continuar
        parent.fill(85, 154, 232);
        parent.rect(parent.width/2-100/2, parent.height-70, 100, 50,7); //rectangulo para el texto
        parent.fill(255);
        parent.text("Continuar", parent.width/2 - 40, parent.height-38 );
    }

    void showRankingImg(int Yvalue){
        parent.image(UIResources.get(UISelector.CROWN), 240, parent.height/5-66);
        parent.image(UIResources.get(UISelector.GOLD_MEDAL), 360, Yvalue+10);
        parent.image(UIResources.get(UISelector.SILVER_MEDAL), 360, Yvalue+35);
        parent.image(UIResources.get(UISelector.BRONZE_MEDAL), 360, Yvalue+60);
        parent.image(title, 420, 30);
    }

    void reset() {
        isFirstRun = true;
    }

    boolean mouseOverContinue() {
        return (parent.mouseX >= parent.width / 2 - 100 / 2 && parent.mouseX <= parent.width / 2 - 100 / 2 + 100)
                && (parent.mouseY >= parent.height - 70 && parent.mouseY <= parent.height - 70 + 50);
    }
}
