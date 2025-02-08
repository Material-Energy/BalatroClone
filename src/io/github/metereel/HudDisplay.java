package io.github.metereel;

import io.github.metereel.card.Deck;

import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.CARD_WIDTH;
import static io.github.metereel.Main.APP;


public class HudDisplay {
    public static float HAND_Y = 0.75f * APP.height;
    private final Deck currentDeck;

    public HudDisplay(){
        currentDeck = new Deck();
    }
    
    public void initialize(){
        currentDeck.initializeTextures();
        currentDeck.setPlayingDeck();
    }

    public Deck getDeck() {
        return this.currentDeck;
    }

    public void display(){
        APP.background(APP.color(75, 105, 47));

        drawHand();
        currentDeck.displayDeck();
    }

    public void drawHand(){
        APP.fill(APP.color(50, 60, 57));
        float startX = currentDeck.calculateHandPos(0, currentDeck.getHandSize());
        float endX = currentDeck.calculateHandPos(currentDeck.getHandSize() - 1, currentDeck.getHandSize());
        APP.rect(startX - CARD_WIDTH, HAND_Y - CARD_HEIGHT * 2 / 3.0f, endX - startX + 2 * CARD_WIDTH, CARD_HEIGHT * 4 / 3.0f, 10.0f);

        currentDeck.displayHand();
    }

    public void onClick() {
        currentDeck.toggleSelected(currentDeck.hoveringCard);
    }

    public void onDrag() {
        currentDeck.dragCard(currentDeck.hoveringCard);
    }

    public void onRelease() {
        currentDeck.stopDragging();
    }

    public void onPressed() {
        currentDeck.dragCard(currentDeck.hoveringCard);
    }
}
