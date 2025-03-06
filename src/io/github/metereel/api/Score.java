package io.github.metereel.api;

import io.github.metereel.gui.Scorer;

import java.util.function.BiConsumer;


public class Score {
    public static final Score EMPTY = new Score(Type.NO_SCORE, 0.0f);
    private final Type scoreType;
    private final float scoreAmt;

    public Score(Type scoreType, float scoreAmt){
        this.scoreType = scoreType;
        this.scoreAmt = scoreAmt;
    }


    public void score(Scorer scorer) {
        scoreType.score(scorer, scoreAmt);
    }

    public boolean hasNoEffect() {
        return scoreType == Type.NO_SCORE;
    }

    @Override
    public String toString() {
        String scoringType = "No Effect!";
        if (scoreType == Type.ADD_CHIPS || scoreType == Type.POW_CHIPS){
            scoringType = "Chips";
        } else if (!hasNoEffect()){
            scoringType = "Mult";
        }

        String symbol = "";
        if (scoreType == Type.POW_CHIPS || scoreType == Type.POW_MULT){
            symbol = "^";
        } else if (scoreType == Type.TIMES_MULT){
            symbol = "x";
        } else if (scoreType == Type.ADD_CHIPS || scoreType == Type.ADD_MULT){
            symbol = "+";
        }

        String score = "";
        if (((int) scoreAmt) * 10 == Math.round(scoreAmt * 10)){
            score += ((int) scoreAmt);
        } else {
            score += scoreAmt;
        }

        return symbol + score + " " + scoringType;
    }

    public boolean isChips(){
        return this.scoreType == Type.ADD_CHIPS || this.scoreType == Type.POW_CHIPS;
    }

    public boolean isMult(){
        return this.scoreType == Type.ADD_MULT || this.scoreType == Type.TIMES_MULT || this.scoreType == Type.POW_MULT;
    }

    public enum Type {
        NO_SCORE((_, _) -> {}),
        ADD_CHIPS(Scorer::addChips),
        POW_CHIPS(Scorer::powChips),
        ADD_MULT(Scorer::addMult),
        TIMES_MULT(Scorer::timesMult),
        POW_MULT(Scorer::powMult);

        private final BiConsumer<Scorer, Float> score;

        Type(BiConsumer<Scorer, Float> onScore){
            this.score = onScore;
        }

        public void score(Scorer scorer, float amount){
            score.accept(scorer, amount);
        }
    }
}
