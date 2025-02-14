package io.github.metereel.api;

import io.github.metereel.Timer;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import java.util.Random;

import static io.github.metereel.Main.APP;
import static io.github.metereel.gui.HudDisplay.*;
import static processing.core.PApplet.radians;

public class ScorePopup {
    private final float rotation;
    private final PVector pos;
    private final Text display;

    private final Timer untilExpire = new Timer();
    private final Score scoreType;

    public ScorePopup(PVector pos, float rotDelta, Score scoring){
        this.pos = pos;
        Random random = new Random();
        this.rotation = radians(random.nextInt(-90, 90)) + rotDelta;
        this.scoreType = scoring;

        this.display = new Text(scoring.toString(), APP.color(255), 35);
    }

    public void display(){
        untilExpire.incrementTimer();
        APP.pushMatrix();

        if (scoreType.isMult()){
            APP.fill(RED);
        } else if (scoreType.isChips()){
            APP.fill(BLUE);
        }

        APP.translate(pos.x, pos.y);
        APP.rotate(rotation);
        APP.square(-10, 0,  -Math.abs(untilExpire.getTime() - 10) + 70);

        APP.popMatrix();

        display.display(pos, rotation / 90.0f);
    }
}
