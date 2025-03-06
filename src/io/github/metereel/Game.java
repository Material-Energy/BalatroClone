package io.github.metereel;

import io.github.metereel.card.CardContainer;
import io.github.metereel.card.jokers.JokerCard;
import io.github.metereel.card.jokers.Jokers;
import io.github.metereel.gui.ScorerHelper;
import io.github.metereel.sprites.Shaders;
import processing.core.PFont;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Constants.FACES;
import static io.github.metereel.Helper.*;
import static io.github.metereel.Javatro.*;
import static io.github.metereel.card.jokers.Jokers.retrieveJoker;
import static io.github.metereel.gui.ScorerHelper.playedHand;

public class Game {
    public static boolean inBlind = false;
    public static boolean bySuit = false;

    public static CardContainer<JokerCard> jokers = new CardContainer<>(5);

    public static void tick() {
        HUD.display();
        if (inBlind){
            HUD.getDeck().tick();
            HUD.gameTick();

            if (!playedHand.isEmpty()){
                ScorerHelper.scoreHand();
            }

            jokers.forEach(JokerCard::tick);
        }
    }

    public static void initialize() {
        // Load Sprites
        CARDS.load();
        CARD_BACKS.load();
        FACES.load();
        JOKERS.load();

        // Load Font
        PFont font = fontFromString("balatro.ttf", 32);
        APP.noStroke();
        APP.textFont(font);

        // Center Window
        PVector center = centerWindow();
        APP.getSurface().setLocation((int) center.x, (int) center.y);

        Shaders.initialize();
        Jokers.initialize();

        jokers.insert(retrieveJoker("Hanging Chad"));
    }
}
