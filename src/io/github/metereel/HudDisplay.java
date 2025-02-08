package io.github.metereel;

import io.github.metereel.card.Deck;


public class HudDisplay {
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
        currentDeck.displayDeck();
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
