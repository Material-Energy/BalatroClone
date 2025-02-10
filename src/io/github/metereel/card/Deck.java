package io.github.metereel.card;

import io.github.metereel.gui.HudDisplay;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.getHand;
import static io.github.metereel.gui.HudDisplay.HAND_BOX_TOP;
import static io.github.metereel.gui.HudDisplay.hoveringCard;
import static io.github.metereel.Main.APP;

public class Deck {
    public static final float AXIS = 0.5f * APP.width;
    private static final int MAX_SELECTED = 5;
    private final String deckType;

    private final ArrayList<PlayingCard> currentDeck = new ArrayList<>();

    private final ArrayList<PlayingCard> discardPile = new ArrayList<>();
    private final ArrayList<PlayingCard> playingDeck = new ArrayList<>();
    private final ArrayList<PlayingCard> currentHand = new ArrayList<>();
    private int maxHandSize = 8;

    private final ArrayList<PlayingCard> selectedCards = new ArrayList<>();

    private final PVector pos = new PVector(.9f * APP.width, HudDisplay.HAND_Y);

    public Deck(){
        deckType = "Deck Normal";

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
        for (String rank: RANKS){
            for (String suit: SUITS){
                String cardType = STR."\{rank} \{suit}";

                PlayingCard card = new PlayingCard(this, new Text(STR."\{rank} of \{suit}"), deckType, "Card Empty", cardType);
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
        this.playingDeck.forEach(card -> card.setState(CardState.DRAWING));
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

            currentHand.sort((card1, card2) -> {
                int rank1 = RANKS.indexOf(card1.getRankSuit().split(" ")[0]);
                int rank2 = RANKS.indexOf(card2.getRankSuit().split(" ")[0]);

                int suit1 = SUITS.indexOf(card1.getRankSuit().split(" ")[1]);
                int suit2 = SUITS.indexOf(card2.getRankSuit().split(" ")[1]);

                return Integer.compare(rank1 * 10 + suit1, rank2 * 10 + suit2);
            });
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
            if (state == CardState.DRAWING) {
                if (card.getPos().dist(new PVector(x, y)) >= 0.1f) card.setTargetPos(x, y, 10);
                if (card.isFlipped() && card.lerpProgress() > 0.9f) card.flip();

                if (!card.hasTarget()){
                    card.setState(CardState.IDLE);
                }
            }
            else if (state == CardState.IDLE) {
                card.setPos(x, y);
            }
            if (card.isFlipped() && card.lerpProgress() > 0.5f) card.flip();
        }

        currentHand.forEach(Card::display);
        selectedCards.forEach(Card::display);
        if (hoveringCard != null) hoveringCard.display();
    }

    public void displayDiscard(){
        discardPile.forEach(card -> {
            if (card.getState() == CardState.DISCARDING) return;
            if (card.getPos().x >= APP.width) {
                card.setState(CardState.DISCARDING);
            } else if (card.getPos().y <= HAND_BOX_TOP - CARD_WIDTH && !card.isFlipped()){
                card.flip();
            } else if (card.getPos().y <= HAND_BOX_TOP - CARD_WIDTH){
                card.setTargetPos(APP.width + 20, HAND_BOX_TOP - CARD_WIDTH, 4);
            } else {
                card.setTargetPos(card.getPos().x, HAND_BOX_TOP - CARD_WIDTH, 2);
            }
            card.display();
        });
    }

    public void tick(){
        fillHand();

        discardPile.forEach(Card::tick);
    }

    public HandType selectPlayingCard(PlayingCard card) {
        if (selectedCards.contains(card)){
            selectedCards.remove(card);
            hoveringCard.setSelected(false);
        } else if (selectedCards.size() < MAX_SELECTED){
            selectedCards.add(card);
            hoveringCard.setSelected(true);
            selectedCards.sort((card1, card2) -> Integer.compare(currentHand.indexOf(card1), currentHand.indexOf(card2)));
        }

        return getHand(selectedCards);
    }

    public void dragPlayingCard(PlayingCard draggedCard) {
        if (draggedCard == null) return;

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
            if (card.getState() == CardState.SWAPPING) card.setState(CardState.DRAWING);
        });

        if (hasSwapped.get())
            selectedCards.sort((card1, card2) -> Integer.compare(currentHand.indexOf(card1), currentHand.indexOf(card2)));
    }

    public void discardSelected() {
        discardPile.addAll(selectedCards);
        currentHand.removeAll(selectedCards);
        selectedCards.clear();
    }

    public ArrayList<PlayingCard> playHand(){
        ArrayList<PlayingCard> playedHand = new ArrayList<>(selectedCards);
        currentHand.removeAll(selectedCards);
        selectedCards.clear();
        return playedHand;
    }

    public ArrayList<PlayingCard> getCurrentHand() {
        return this.currentHand;
    }

    public void stopDragging(PlayingCard card) {
        card.setState(CardState.DRAWING);
    }
}
