package io.github.metereel.card;

import io.github.metereel.Timer;
import io.github.metereel.api.Edition;
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
import static io.github.metereel.gui.ScorerHelper.currentlyPlayingHand;

public class Deck {
    public static final float AXIS = 0.6f * APP.width;
    private static final int MAX_SELECTED = 5;
    private final String deckType;

    private final ArrayList<PlayingCard> currentDeck = new ArrayList<>();
    private final Timer drawCooldown = new Timer();

    private final ArrayList<PlayingCard> discardPile = new ArrayList<>();
    private final ArrayList<PlayingCard> playingDeck = new ArrayList<>();
    private final ArrayList<PlayingCard> currentHand = new ArrayList<>();
    private int maxHandSize = 8;
    private int maxHands = 4;
    private int maxDiscards = 3;
    private int hands;
    private int discards;

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
                card.setEdition(Edition.FOIL);
                card.setPos(this.pos.x + 5.2f - offset, this.pos.y - 5.2f + offset);
                card.tick();
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
        this.discards = this.maxDiscards;
        this.hands = this.maxHands;

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
        discard(currentHand);
    }

    public int getHands(){
        return this.hands;
    }

    public int getDiscards(){
        return this.discards;
    }

    public void fillHand() {
        while (currentHand.size() < maxHandSize){
            if (this.playingDeck.isEmpty()) return;
            currentHand.add(this.playingDeck.removeFirst());
            currentHand.getLast().onDraw();

            currentHand.sort((card1, card2) -> {
                int rank1 = RANKS.indexOf(card1.getRank());
                int rank2 = RANKS.indexOf(card2.getRank());

                int suit1 = SUITS.indexOf(card1.getSuit());
                int suit2 = SUITS.indexOf(card2.getSuit());

                return -Integer.compare(rank1 * 10 + suit1, rank2 * 10 + suit2);
            });
        }
        currentHand.forEach(playingCard -> playingCard.setState(CardState.DRAWING));
    }

    public float calculateHandPos(int index, int totalCards){
        return (AXIS - (totalCards - index - totalCards / 2.0f) * (3.0f / 2 / totalCards) * Math.min(Math.abs(AXIS - APP.width * 0.1f), Math.abs(AXIS - APP.width * 0.9f)));
    }

    public void displayHand(){
        float baseX;
        float baseY = HudDisplay.HAND_Y;

        int displayedCards = currentHand.size();

        for (int i = 0; i < displayedCards; i++) {
            Card card = currentHand.get(i);

            baseX = calculateHandPos(i, displayedCards - 1);

            CardState state = card.getState();
            if (state == CardState.DRAWING) {
                if (card.getPos().dist(new PVector(baseX, baseY)) >= 0.1f) card.setTargetPos(baseX, baseY, 10);
                if (card.isFlipped() && card.lerpProgress() > 0.9f) card.flip();

                if (!card.hasTarget()){
                    card.setState(CardState.IDLE);
                }
            }
            else if (state == CardState.IDLE) {
                card.setPos(baseX, baseY);
            }
            if (card.isFlipped() && card.lerpProgress() > 0.5f) card.flip();
        }
        currentHand.forEach(Card::drawShadow);
        selectedCards.forEach(Card::drawShadow);

        currentHand.forEach(Card::display);
        selectedCards.forEach(Card::display);
        if (hoveringCard != null) hoveringCard.display();
    }

    public void displayDiscard(){
        discardPile.forEach(card -> {
            if (card.getState() == CardState.DISCARDING) {
                card.flip();
                return;
            }
            if (card.getPos().x >= APP.width) {
                card.setState(CardState.DISCARDING);
            } else {
                card.setTargetPos(APP.width + 20, HAND_BOX_TOP - CARD_WIDTH, 5);
            }
            card.display();
        });
    }

    public void tick(){
        if (currentlyPlayingHand) {
            drawCooldown.resetTimer();
        }
        drawCooldown.incrementTimer();
        if (drawCooldown.getTimeWithCycle(100) == 0) {
            fillHand();
        }

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
        this.discards--;
        discardPile.addAll(selectedCards);
        selectedCards.forEach(PlayingCard::onDiscard);
        currentHand.removeAll(selectedCards);
        drawCooldown.resetTimer();
        currentHand.forEach(playingCard -> playingCard.setState(CardState.DRAWING));
        selectedCards.clear();
    }

    public void discard(ArrayList<PlayingCard> cards){
        discardPile.addAll(cards);
    }

    public ArrayList<PlayingCard> playHand(){
        ArrayList<PlayingCard> playedHand = new ArrayList<>(selectedCards);
        this.hands--;
        currentHand.forEach(PlayingCard::onPlay);
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
