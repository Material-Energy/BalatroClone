package io.github.metereel.card;

import io.github.metereel.sprites.Sprite;
import io.github.metereel.text.Text;
import processing.core.PVector;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.withTilt;

public class Card {
    private Text name;
    // private Lore description;
    private Sprite cardFront;
    private Sprite cardBack;
    private boolean isFlipped = false;
    private boolean isShaking = false;

    private final String deckType;
    private String cardType;
    private String faceType;

    private final PVector pos = new PVector();
    private float rotation = 0.0f;
    private float size = 1.0f;

    public Card(Text name, String deckType, String cardType, String faceType){
        this.name = name;
        this.deckType = deckType;
        this.cardType = cardType;
        this.faceType = faceType;

    }

    public void onPlay(){

    }

    public void onDiscard(){

    }

    public void updateSprite(){
        this.cardFront = CARDS.getSprite(cardType);
        this.cardFront.layerSprite(FACES.getSprite(faceType), 0, 0);

        this.cardBack = CARD_BACKS.getSprite(deckType);
    }

    public void display(){
        float rot = this.rotation;
        if (isShaking){
            rot = withTilt(rot, 15, 0.25f);
        }
        if (isFlipped){
            if (cardBack != null) cardBack.display(pos, rot, size);
        } else {
            if (cardFront != null) cardFront.display(pos, rot, size);
        }
    }

    public void setPos(int x, int y) {
        this.pos.x = x;
        this.pos.y = y;
    }
}
