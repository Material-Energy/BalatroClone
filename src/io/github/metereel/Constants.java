package io.github.metereel;

import io.github.metereel.gui.HudDisplay;
import io.github.metereel.sprites.Sprite;
import io.github.metereel.sprites.SpriteSheet;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
    public static final int CARD_WIDTH = 71;
    public static final int CARD_HEIGHT = 95;

    public static final SpriteSheet CARD_BACKS = new SpriteSheet("cards",
            new Sprite(213, 190, CARD_WIDTH, CARD_HEIGHT, "Deck Normal")
    );
    public static final SpriteSheet CARDS = new SpriteSheet("cards",
            new Sprite(71, 0, CARD_WIDTH, CARD_HEIGHT, "Card Empty"));

    public static final SpriteSheet FACES = new SpriteSheet( "faces");

    public static final ArrayList<String> RANKS = new ArrayList<>(Arrays.asList("2 3 4 5 6 7 8 9 10 Jack Queen King Ace".split(" ")));
    public static final ArrayList<String> SUITS = new ArrayList<>(Arrays.asList("Hearts Clubs Diamonds Spades".split(" ")));

    public static final HudDisplay HUD = new HudDisplay();
}
