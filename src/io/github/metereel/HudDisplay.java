package io.github.metereel;

import io.github.metereel.card.Deck;
import io.github.metereel.gui.Button;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.CARD_WIDTH;
import static io.github.metereel.Main.APP;


public class HudDisplay {
    public static float HAND_Y = 0.75f * APP.height;
    private final Deck currentDeck;

    private final Button discardButton;

    public HudDisplay(){
        currentDeck = new Deck();

        discardButton = new Button(
                .21f * APP.width,
                .845f * APP.height,
                CARD_WIDTH * 2.0f,
                CARD_HEIGHT / 3.0f,
                APP.color(172, 50, 50),
                APP.color(94, 28, 28),
                new Text("Discard", APP.color(255), 15)
        );
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

        APP.fill(APP.color(255));
        APP.text("(" + APP.mouseX + ", " + APP.mouseY + ")",
                APP.width / 2.0f,
                30);

        drawHand();
        boolean hasDiscard = currentDeck.getSelectedAmt() > 0;
        if (!hasDiscard) discardButton.setPressed(true);
        discardButton.display();

        currentDeck.displayDeck();
    }

    public void drawHand(){
        APP.fill(APP.color(50, 60, 57));
        float startX = currentDeck.calculateHandPos(0, currentDeck.getHandSize());
        float endX = currentDeck.calculateHandPos(currentDeck.getHandSize() - 1, currentDeck.getHandSize());
        APP.rect(startX - CARD_WIDTH, HAND_Y - CARD_HEIGHT * 2 / 3.0f, endX - startX + 2 * CARD_WIDTH, CARD_HEIGHT * 4 / 3.0f, 10.0f);

        currentDeck.displayHand();

        if (currentDeck.hoveringCard != null){
            Text cardName = currentDeck.hoveringCard.getName();
            cardName.display(new PVector(.36f * APP.width, .845f * APP.height), 0f);
        }
    }

    public void onClick() {
        currentDeck.toggleSelected(currentDeck.hoveringCard);
        checkDiscardButton();
    }

    public void onDrag() {
        currentDeck.dragCard(currentDeck.hoveringCard);
    }

    public void onRelease() {
        currentDeck.stopDragging();
        checkDiscardButton();

        boolean hasDiscard = currentDeck.getSelectedAmt() > 0;
        if (discardButton.checkClicked() && hasDiscard){
            currentDeck.discardSelected();
        }
    }

    public void onPressed() {
        currentDeck.dragCard(currentDeck.hoveringCard);
        checkDiscardButton();
    }

    public void onMoved() {
        checkDiscardButton();
    }

    private void checkDiscardButton(){
        boolean isHovering = discardButton.checkPressed();
        boolean hasDiscard = currentDeck.getSelectedAmt() > 0;

        discardButton.setPressed(isHovering && hasDiscard);
    }
}
