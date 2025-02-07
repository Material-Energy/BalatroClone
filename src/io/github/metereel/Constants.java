package io.github.metereel;

import io.github.metereel.card.Card;
import io.github.metereel.sprites.Sprite;
import io.github.metereel.sprites.SpriteSheet;
import io.github.metereel.text.Text;

public class Constants {
    public static final SpriteSheet CARD_BACKS = new SpriteSheet("cards");
    public static final SpriteSheet CARDS = new SpriteSheet("cards",
            new Sprite(71, 0, 71, 95, "cardEmpty"));
    public static final SpriteSheet FACES = new SpriteSheet( "faces");

    public static final String[] num = "2 3 4 5 6 7 8 9 10 jack queen king ace".split(" ");
    public static final String[] suit = "Diamonds Hearts Clubs Spades".split(" ");

    public static int timer = 0;

    public static Card test = new Card(new Text("test"), "", "cardEmpty", "2Diamonds");
}
