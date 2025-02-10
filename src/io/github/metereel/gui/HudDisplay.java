package io.github.metereel.gui;

import io.github.metereel.card.*;
import processing.core.PVector;

import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.CARD_WIDTH;
import static io.github.metereel.Main.APP;


public class HudDisplay {
    public static float HAND_Y = 0.75f * APP.height;
    public static float HAND_BOX_TOP = HAND_Y - CARD_HEIGHT * 2 / 3.0f;
    private final Deck currentDeck;

    private final Button discardButton;
    public static Card hoveringCard;
    private boolean isDragging = false;
    private HandType playingHand = HandType.HIGH_CARD;

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
        APP.text(STR."(\{APP.mouseX}, \{APP.mouseY}) \{Math.round(APP.frameRate)} FPS",
                APP.width / 2.0f,
                30);

        APP.text(playingHand.toString(), APP.width / 2.0f, APP.height / 2.0f);

        drawHand();
        boolean hasDiscard = currentDeck.getSelectedAmt() > 0;
        if (!hasDiscard) discardButton.setPressed(true);
        discardButton.display();

        currentDeck.displayDeck();
        currentDeck.displayDiscard();
    }

    public void drawHand(){
        APP.fill(APP.color(50, 60, 57));
        float startX = currentDeck.calculateHandPos(0, currentDeck.getHandSize());
        float endX = currentDeck.calculateHandPos(currentDeck.getHandSize() - 1, currentDeck.getHandSize());
        APP.rect(startX - CARD_WIDTH, HAND_BOX_TOP, endX - startX + 2 * CARD_WIDTH, CARD_HEIGHT * 4 / 3.0f, 10.0f);

        currentDeck.displayHand();

        if (hoveringCard != null){
            Text cardName = hoveringCard.getName();
            cardName.setColor(APP.color(255));
            cardName.display(new PVector(.36f * APP.width, .845f * APP.height), 0f);
        }
    }

    public void onClick() {
        checkDiscardButton();

        if (hoveringCard == null) return;
        if (hoveringCard instanceof PlayingCard playingCard) {
            playingHand = currentDeck.selectPlayingCard(playingCard);
        }
    }

    public void onDrag() {
        this.isDragging = true;
        if (hoveringCard == null) return;
        hoveringCard.setState(CardState.DRAGGING);
        hoveringCard.setPos(APP.mouseX, APP.mouseY);

        if (hoveringCard instanceof PlayingCard playingCard) currentDeck.dragPlayingCard(playingCard);
    }

    public void onRelease() {
        checkDiscardButton();
        boolean hasDiscard = currentDeck.getSelectedAmt() > 0;
        if (discardButton.checkClicked() && hasDiscard){
            currentDeck.discardSelected();
        }

        this.isDragging = false;
        if (hoveringCard == null) return;
        if (hoveringCard instanceof PlayingCard playingCard) currentDeck.stopDragging(playingCard);
    }

    public void onPressed() {
        this.isDragging = true;
        checkDiscardButton();

        if (hoveringCard == null) return;
        hoveringCard.setState(CardState.DRAGGING);
        hoveringCard.setPos(APP.mouseX, APP.mouseY);

        if (hoveringCard instanceof PlayingCard playingCard) currentDeck.dragPlayingCard(playingCard);
    }

    public void onMoved() {
        checkDiscardButton();
    }

    private void checkDiscardButton(){
        boolean isHovering = discardButton.checkPressed();
        boolean hasDiscard = currentDeck.getSelectedAmt() > 0;

        discardButton.setPressed(isHovering && hasDiscard);
    }

    public void tick() {
        if (!isDragging && hoveringCard != null && !hoveringCard.isHovering()){
            hoveringCard = null;
        }

        currentDeck.getCurrentHand().forEach(card -> {
            card.tick();
            if (card.isHovering() && hoveringCard == null){
                hoveringCard = card;
            }
        });

        currentDeck.tick();
    }
}
