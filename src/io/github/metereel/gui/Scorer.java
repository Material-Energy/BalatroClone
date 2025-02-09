package io.github.metereel.gui;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;


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

    public void addChips(double amount){
        chips = chips.add(new Apfloat(String.valueOf(amount)));
    }

    public void timeChips(double amount){
        chips = chips.multiply(new Apfloat(String.valueOf(amount)));
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
}
