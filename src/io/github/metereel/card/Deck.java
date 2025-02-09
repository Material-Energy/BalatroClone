package io.github.metereel.card;

import io.github.metereel.HudDisplay;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.github.metereel.Constants.*;
import static io.github.metereel.HudDisplay.hoveringCard;
import static io.github.metereel.Main.APP;

public class Deck {
    public static final float AXIS = 0.4f * APP.width;
    private static final int MAX_SELECTED = 5;
    private final String deckType;

    private final ArrayList<Card> currentDeck = new ArrayList<>();

    private final ArrayList<Card> discardPile = new ArrayList<>();
    private final ArrayList<Card> playingDeck = new ArrayList<>();
    private final ArrayList<Card> currentHand = new ArrayList<>();
    private int maxHandSize = 8;

    private final ArrayList<Card> selectedCards = new ArrayList<>();

    private boolean isDragging = false;

    private final PVector pos = new PVector((float) 5 * APP.width / 6, HudDisplay.HAND_Y);

    public Deck(){
        deckType = "deckNormal";

        generateDeck();
    }

    public int getHandSize() {
        return maxHandSize;
    }

    public int getSelectedAmt() {
        return this.selectedCards.size();
    }

    protected void generateDeck() {
        float offset = 0;
        for (String rank: ranks){
            for (String suit: suits){
                String cardType = rank + suit;

                Card card = new PlayingCard(this, new Text(rank + " of " + suit), deckType, "cardEmpty", cardType);
                card.setPos(this.pos.x + offset, this.pos.y - offset);
                offset += 0.1f;

                currentDeck.add(card);
            }
        }
    }

    public void initializeTextures(){
        currentDeck.forEach(Card::updateSprite);
    }

    public void setPlayingDeck(){
        this.playingDeck.clear();
        this.playingDeck.addAll(this.currentDeck);

        Collections.shuffle(this.playingDeck);
    }

    public void displayDeck() {
        Consumer<Card> display = (card) -> {
            if (!card.isFlipped()) card.flip();
            card.display();
            };

        playingDeck.forEach(display);
    }

    public void clearHand(){
        currentHand.clear();
    }

    public void fillHand() {
        while (currentHand.size() < maxHandSize){
            if (this.playingDeck.isEmpty()) return;
            currentHand.add(this.playingDeck.removeFirst());
        }
    }

    public float calculateHandPos(int index, int totalCards){
        return (AXIS - (totalCards - index - totalCards / 2.0f) * (3.0f / 2 / totalCards) * Math.min(Math.abs(AXIS), Math.abs(AXIS - APP.width)));
    }

    public void displayHand(){
        float x;
        float y = HudDisplay.HAND_Y;

        int displayedCards = currentHand.size();

        for (int i = 0; i < displayedCards; i++) {
            Card card = currentHand.get(i);

            x = calculateHandPos(i, displayedCards);

            CardState state = card.getState();
            if (state != CardState.DISCARDING && state != CardState.DRAGGING) card.setTargetPos(x, y, 20);
            if (card.isFlipped() && card.lerpProgress() > 0.5f) card.flip();
        }

        currentHand.forEach(Card::display);
        selectedCards.forEach(Card::display);
        if (hoveringCard != null) hoveringCard.display();
    }

    public void tick(){
        fillHand();


        if (!isDragging && hoveringCard != null && !hoveringCard.isHovering()){
            hoveringCard = null;
        }

        currentHand.forEach(card -> {
            card.tick();
            if (card.isHovering() && hoveringCard == null){
                hoveringCard = card;
            }
        });
    }

    public void toggleSelected(Card card) {
        if (card == null) return;

        if (selectedCards.contains(card)){
            selectedCards.remove(card);
            card.setSelected(false);
        } else if (selectedCards.size() < MAX_SELECTED){
            selectedCards.add(card);
            selectedCards.sort((card1, card2) -> Integer.compare(currentHand.indexOf(card1), currentHand.indexOf(card2)));
            card.setSelected(true);
        }
    }

    public void stopDragging() {
        this.isDragging = false;
        if (hoveringCard == null) return;
        hoveringCard.setState(CardState.IDLE);
    }

    public void dragCard(Card draggedCard) {
        if (draggedCard == null) return;
        this.isDragging = true;
        draggedCard.setState(CardState.DRAGGING);

        if (draggedCard.hasNoTarget()) draggedCard.setTargetPos(APP.mouseX, APP.mouseY, 1);

        if (draggedCard.hasNoTarget()) return;

        AtomicBoolean hasSwapped = new AtomicBoolean(false);

        currentHand.forEach(card -> {
            if (card != draggedCard && card.getState() != CardState.SWAPPING){
                float deckPos = calculateHandPos(currentHand.indexOf(draggedCard), currentHand.size());
                float cardPos = card.getPos().x;

                float supposedPos = Math.signum(deckPos - cardPos);
                float actualPos = Math.signum(draggedCard.getPos().x - cardPos);

                int indexFrom = currentHand.indexOf(card);
                int indexTo = currentHand.indexOf(draggedCard);

                if (supposedPos * actualPos < 0.0f && Math.abs(indexFrom - indexTo) <= 1){
                    Collections.swap(currentHand, indexFrom, indexTo);
                    card.setState(CardState.SWAPPING);
                    hasSwapped.set(true);
                }
            }
        });

        currentHand.forEach(card -> {
            if (card.getState() == CardState.SWAPPING) card.setState(CardState.IDLE);
        });

        if (hasSwapped.get())
            selectedCards.sort((card1, card2) -> Integer.compare(currentHand.indexOf(card1), currentHand.indexOf(card2)));
    }

    public void discardSelected() {
        currentHand.removeAll(selectedCards);
        selectedCards.clear();
    }
}
