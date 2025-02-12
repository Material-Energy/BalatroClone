package io.github.metereel.api;

import io.github.metereel.gui.Scorer;

import java.util.function.BiConsumer;

import static processing.core.PApplet.println;


public class Score {
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
