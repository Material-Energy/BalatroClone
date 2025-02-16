package io.github.metereel;

import io.github.metereel.sprites.Shaders;
import processing.core.PFont;
import processing.core.PVector;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Constants.FACES;
import static io.github.metereel.Helper.*;
import static io.github.metereel.Javatro.*;

public class Game {
    public static boolean inBlind = false;
    public static boolean bySuit = false;

    public static void tick() {
        HUD.display();
        if (inBlind){
            HUD.getDeck().tick();
            HUD.gameTick();
        }
    }

    public static void initialize() {
        // Load Sprites
        CARDS.load();
        CARD_BACKS.load();
        FACES.load();

        // Load Font
        PFont font = fontFromString("balatro.ttf", 32);
        APP.noStroke();
        APP.textFont(font);

        // Center Window
        PVector center = centerWindow();
        APP.getSurface().setLocation((int) center.x, (int) center.y);

        Shaders.initialize();
    }
}
