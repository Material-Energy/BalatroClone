package io.github.metereel.gui;

import io.github.metereel.card.HandType;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import processing.core.PVector;

import static io.github.metereel.Constants.HUD;
import static io.github.metereel.Constants.handLevels;
import static io.github.metereel.Game.currentlyPlayingHand;
import static io.github.metereel.Helper.drawBubble;
import static io.github.metereel.Main.APP;
import static io.github.metereel.gui.HudDisplay.BLUE;
import static io.github.metereel.gui.HudDisplay.RED;


public class Scorer {
    private Apfloat chips;
    private Apfloat mult;

    private Apfloat currentScore;
    private final Apfloat requiredScore;

    public Scorer(Apfloat anteScore){
        chips = Apfloat.ZERO;
        mult = Apfloat.ZERO;
        requiredScore = anteScore;

        updateCurrentScore();
    }

    public void setBaseScore(HandType hand, int level){
        chips = new Apfloat(String.valueOf(hand.getChips(level)));
        mult = new Apfloat(String.valueOf(hand.getMult(level)));
        updateCurrentScore();
    }

    public void addChips(double amount){
        Apfloat addAmt = new Apfloat(String.valueOf(amount));
        chips = chips.add(addAmt);
    }

    public void powChips(double amount){
        chips = ApfloatMath.pow(chips, new Apfloat(String.valueOf(amount)));
    }

    public void addMult(double amount){
        mult = mult.add(new Apfloat(String.valueOf(amount)));
    }

    public void timesMult(double amount){
        mult = mult.multiply(new Apfloat(String.valueOf(amount)));
    }

    public void powMult(double amount){
        mult = ApfloatMath.pow(mult, new Apfloat(String.valueOf(amount)));
    }

    private void updateCurrentScore() {
        currentScore = chips.multiply(mult);
    }

    public boolean hasWon(){
        updateCurrentScore();
        return currentScore.compareTo(requiredScore) >= 0;
    }

    public String format(boolean isChips){
        if (isChips) {
            if (chips.scale() <= 7) {
                return chips.toString(true);
            } else {
                return chips.precision(3).toString();
            }
        } else {
            if (mult.scale() <= 7) {
                return mult.toString(true);
            } else {
                return mult.precision(3).toString();
            }
        }
    }

    public void display(HandType handType){
        display(handType, false);
    }

    public void display(HandType handType, boolean override){
        if (HUD.getDeck().getSelectedAmt() >= 1)
            setBaseScore(handType, handLevels.get(handType));
        PVector chipsPos = new PVector(APP.width * 0.1f, APP.height * 0.245f);
        PVector multPos = new PVector(APP.width * 0.2f, APP.height * 0.245f);


        if ((HUD.getDeck().getSelectedAmt() < 1 && !currentlyPlayingHand) || override) {
            new Text("0", APP.color(255), 25)
                    .display(chipsPos, 0.0f);
            new Text("0", APP.color(255), 25)
                    .display(multPos, 0.0f);

            chips = Apfloat.ZERO;
            mult = Apfloat.ZERO;
            return;
        }
        new Text(format(true), APP.color(255), 25)
                .display(chipsPos, 0.0f);
        new Text(format(false), APP.color(255), 25)
                .display(multPos, 0.0f);
    }

    public void drawBubbles() {
        PVector chipsPos = new PVector(APP.width * 0.1f, APP.height * 0.245f);
        PVector multPos = new PVector(APP.width * 0.2f, APP.height * 0.245f);

        PVector scoreSize = new PVector(75, 50);
        drawBubble(RED, multPos, scoreSize, 5);
        drawBubble(BLUE, chipsPos, scoreSize, 5);

        new Text("X", RED, 30)
                .display(new PVector(APP.width * .15f, APP.height * .245f), 0.0f);
    }
}
