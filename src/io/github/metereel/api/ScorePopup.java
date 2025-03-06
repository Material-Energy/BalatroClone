package io.github.metereel.api;

import io.github.metereel.Timer;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import java.util.Random;

import static io.github.metereel.Javatro.APP;
import static io.github.metereel.gui.HudDisplay.*;
import static processing.core.PApplet.radians;

public class ScorePopup {
    private final float rotation;
    private final PVector pos;
    private final Text display;

    private final Timer untilExpire = new Timer();
    private final String scoreType;

    public ScorePopup(PVector pos, float rotDelta, Score scoring){
        this.pos = pos;
        Random random = new Random();
        this.rotation = radians(random.nextInt(-90, 90)) + rotDelta;
        this.scoreType = scoring.toString();

        this.display = new Text(scoreType, APP.color(255), 35);
    }


    public ScorePopup(PVector pos, float rotDelta, String scoring){
        this.pos = pos;
        Random random = new Random();
        this.rotation = radians(random.nextInt(-90, 90)) + rotDelta;
        this.scoreType = scoring;

        this.display = new Text(scoring, APP.color(255), 35);
    }

    public void display(){
        if (scoreType.contains("Mult")){
            APP.fill(RED);
        } else if (scoreType.contains("Chips")){
            APP.fill(BLUE);
        } else if (scoreType.contains("Again")) {
            APP.fill(ORANGE);
        } else if (scoreType.contains("$")) {
            APP.fill(GOLD);
        }

        untilExpire.incrementTimer();
        APP.pushMatrix();


        APP.translate(pos.x, pos.y);
        APP.rotate(rotation);
        APP.square(0, 0,  -Math.abs(untilExpire.getTime() - 10) + 70);

        APP.popMatrix();

        display.display(pos.copy().sub(0, display.getSize() / 2.0f), rotation / 90.0f);
    }
}
