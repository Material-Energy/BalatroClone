package io.github.metereel.api;

import static io.github.metereel.api.Score.Type.*;

public enum Edition {
    NORMAL(NO_SCORE, 0),
    FOIL(ADD_CHIPS, 100),
    MINT(POW_CHIPS, 1.1f),
    HOLOGRAPHIC(ADD_MULT, 10),
    POLYCHROME(TIMES_MULT, 2),
    GILDED(POW_MULT, 1.1f),
    NEGATIVE(NO_SCORE, 0);

    private final Score score;

    Edition(Score.Type type, float i) {
        score = new Score(type, i);
    }

    public Score getScore(){
        return this.score;
    }
}
