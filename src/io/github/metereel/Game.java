package io.github.metereel;

import static io.github.metereel.Constants.HUD;

public class Game {
    public static boolean inBlind = false;

    public static void tick() {
        HUD.display();
        if (inBlind){
            HUD.getDeck().tick();
            HUD.gameTick();
        }
    }
}
