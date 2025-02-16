package io.github.metereel.gui;

import io.github.metereel.card.HandType;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import processing.core.PVector;

import java.math.RoundingMode;

import static io.github.metereel.Constants.HUD;
import static io.github.metereel.Constants.handLevels;
import static io.github.metereel.Game.inBlind;
import static io.github.metereel.Helper.withTilt;
import static io.github.metereel.gui.ScorerHelper.currentlyPlayingHand;
import static io.github.metereel.Helper.drawBubble;
import static io.github.metereel.Javatro.APP;
import static io.github.metereel.gui.HudDisplay.BLUE;
import static io.github.metereel.gui.HudDisplay.RED;
import static io.github.metereel.gui.ScorerHelper.playingTimer;


public class Scorer {
    private final float BUBBLE_POS = 0.225f;

    private Apfloat chips;
    private Apfloat mult;

    private Apfloat handScore;
    private Apfloat currentScore;
    private final Apfloat requiredScore;

    private boolean tiltChips = false;
    private boolean tileMult = false;

    public Scorer(Apfloat anteScore){
        chips = Apfloat.ZERO;
        mult = Apfloat.ZERO;

        handScore = Apfloat.ZERO;
        currentScore = Apfloat.ZERO;
        requiredScore = anteScore;

        updateCurrentScore();
    }

    public void setBaseScore(HandType hand, int level){
        chips = new Apfloat(String.valueOf(hand.getChips(level)));
        mult = new Apfloat(String.valueOf(hand.getMult(level)));
    }

    public void addChips(double amount){
        Apfloat addAmt = new Apfloat(String.valueOf(amount));
        chips = chips.add(addAmt);
        tiltChips = true;
        tileMult = false;
    }

    public void powChips(double amount){
        chips = ApfloatMath.pow(chips, new Apfloat(String.valueOf(amount)).precision(10)).precision(10);
        tiltChips = true;
        tileMult = false;
    }

    public void addMult(double amount){
        mult = mult.add(new Apfloat(String.valueOf(amount)));
        tiltChips = false;
        tileMult = true;
    }

    public void timesMult(double amount){
        mult = mult.multiply(new Apfloat(String.valueOf(amount)));
        tiltChips = false;
        tileMult = true;
    }

    public void powMult(double amount){
        mult = ApfloatMath.pow(mult, new Apfloat(String.valueOf(amount)));
        tiltChips = false;
        tileMult = true;
    }

    private void updateCurrentScore() {
        handScore = chips.multiply(mult);
    }

    public boolean hasWon(){
        updateCurrentScore();
        currentScore = currentScore.add(handScore);
        return currentScore.compareTo(requiredScore) >= 0;
    }

    public String format(boolean isChips){
        if (isChips) {
            if (chips.scale() <= 6) {
                return ApfloatMath.roundToPlaces(chips,1, RoundingMode.HALF_EVEN).toString(true);
            } else {
                return chips.precision(3).toString();
            }
        } else {
            if (mult.scale() <= 6) {
                return ApfloatMath.roundToPlaces(mult,1, RoundingMode.HALF_EVEN).toString(true);
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
        PVector chipsPos = new PVector(APP.width * 0.1f, APP.height * BUBBLE_POS);
        PVector multPos = new PVector(APP.width * 0.2f, APP.height * BUBBLE_POS);


        if ((HUD.getDeck().getSelectedAmt() < 1 && !currentlyPlayingHand) || override) {
            new Text("0", APP.color(255), 25)
                    .display(chipsPos, 0.0f);
            new Text("0", APP.color(255), 25)
                    .display(multPos, 0.0f);

            chips = Apfloat.ZERO;
            mult = Apfloat.ZERO;
            return;
        }

        float rotChips = 0.0f;
        float rotMult = 0.0f;
        if (tiltChips){
            rotChips = withTilt(playingTimer, 0.0f, 15.0f, 0.25f, false);
        } else if (tileMult){
            rotMult = withTilt(playingTimer, 0.0f, 15.0f, 0.25f, false);
        }

        new Text(format(true), APP.color(255), 25 - format(true).length())
                .display(chipsPos, rotChips);
        new Text(format(false), APP.color(255), 25 - format(true).length())
                .display(multPos, rotMult);
    }

    public void drawBubbles() {
        PVector chipsPos = new PVector(APP.width * 0.1f, APP.height * BUBBLE_POS);
        PVector multPos = new PVector(APP.width * 0.2f, APP.height * BUBBLE_POS);

        PVector scoreSize = new PVector(75, 50);
        drawBubble(RED, multPos, scoreSize, 5);
        drawBubble(BLUE, chipsPos, scoreSize, 5);

        new Text("X", RED, 30)
                .display(new PVector(APP.width * .15f, APP.height * BUBBLE_POS), 0.0f);
    }

    public Apfloat getRequirement() {
        return this.requiredScore;
    }

    public void drawRequirement() {
        String requirement;
        if (requiredScore.scale() <= 6) {
            requirement = ApfloatMath.roundToPlaces(requiredScore,1, RoundingMode.HALF_EVEN).toString(true);
        } else {
            requirement = requiredScore.precision(3).toString();
        }

        String currentScore;
        if (this.currentScore.scale() <= 6) {
            currentScore = ApfloatMath.roundToPlaces(this.currentScore,1, RoundingMode.HALF_EVEN).toString(true);
        } else {
            currentScore = this.currentScore.precision(3).toString();
        }

        if (inBlind) {
            new Text(requirement, APP.color(255), 20)
                    .display(new PVector(APP.width * .15f, APP.height * .11f), 0.0f);
            new Text(currentScore, APP.color(255), 20)
                    .display(new PVector(APP.width * .15f, APP.height * .275f), 0.0f);
        }
    }

    public void resetTilt() {
        this.tileMult = false;
        this.tiltChips = false;
    }
}
