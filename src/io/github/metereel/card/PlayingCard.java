package io.github.metereel.card;

import io.github.metereel.Helper;
import io.github.metereel.api.Enhancement;
import io.github.metereel.api.Score;
import io.github.metereel.gui.Scorer;
import io.github.metereel.gui.Text;
import io.github.metereel.sprites.Shaders;

import java.util.Objects;
import java.util.Random;

import static io.github.metereel.Constants.*;
import static processing.core.PApplet.*;

public class PlayingCard extends Card{
    private final Deck deck;

    private final String deckType;
    private String cardType;
    private String rankSuit;
    private String rank;
    private String suit;
    private final Random random = new Random();

    private Enhancement enhancement = Enhancement.NORMAL;

    public PlayingCard(Deck deck, Text name, String deckType, String cardType, String rankSuit) {
        super(name);
        this.deck = deck;
        this.deckType = deckType;
        this.cardType = cardType;
        this.rankSuit = rankSuit;

        this.rank = rankSuit.split(" ")[0];
        this.suit = rankSuit.split(" ")[1];

        addTriggers();
    }

    public String getRankSuit() {
        return this.rankSuit;
    }

    public String getRank(){
        return this.rank;
    }

    public String getSuit(){
        return this.suit;
    }

    public void setRankSuit(String rankSuit){
        this.rankSuit = rankSuit;

        this.rank = rankSuit.split(" ")[0];
        this.suit = rankSuit.split(" ")[1];
        updateSprite();
    }

    public void setEnhancement(Enhancement enhancement){
        this.enhancement = enhancement;
        this.cardType = enhancement.getCardType();
        updateSprite();
    }

    public void upgradeCard(int shift){
        this.setRankSuit(Helper.upgradeCard(rankSuit, shift));
    }

    @Override
    protected void addTriggers() {
        trigger.addTrigger(new Score(Score.Type.ADD_CHIPS, getChips()), BASE);
    }

    @Override
    public void updateSprite(){
        this.cardFront = CARDS.getSprite(cardType);
        this.cardFront.layerSprite(FACES.getSprite(rankSuit), 0, 0);

        this.cardBack = CARD_BACKS.getSprite(deckType);

        super.updateSprite();
    }

    private float getChips() {
        return RankChips.getChips(this.getRank());
    }

    public boolean finishedTriggering() {
        return this.trigger.getTriggersLeft() <= 0;
    }

    public void resetTrigger() {
        this.trigger.reset();
    }

    public void tryTrigger(Scorer scorer) {
        this.trigger.triggerNext(scorer);
        this.setSize(1.65f);
        this.setRotation(radians(5));
        setIgnore(true);
    }

    public void onPlay(){
        this.setRotation(0.0f);
    }

    public void onDiscard(){
        this.setRotation(0.0f);
    }

    public void onDraw(){
        this.setRotation(radians(random.nextInt(-2, 2)));
    }

    public Score getCurrentTrigger() {
        return this.trigger.getCurrentTrigger();
    }

    public void skipTrigger() {
        this.trigger.skip();
    }

    public void reset() {
        this.setRotation(0.0f);
        this.setSize(1.0f);
        this.isFlipped = false;
        resetTrigger();
        this.resetTimers();
        this.setSelected(false);
    }


    enum RankChips {
        Ace("Ace", 11),
        King("King", 10),
        Queen("Queen", 10),
        Jack("Jack", 10),
        Ten("10", 10),
        Nine("9", 9),
        Eight("8", 8),
        Seven("7", 7),
        Six("6", 6),
        Five("5", 5),
        Four("4", 4),
        Three("3", 3),
        Two("2", 2);

        private final String rank;
        private final int chips;

        RankChips(String rank, int chips) {
            this.rank = rank;
            this.chips = chips;
        }

        static int getChips(String rank){
            for (RankChips chips1 : values()){
                if (Objects.equals(rank, chips1.rank)){
                    return chips1.chips;
                }
            }
            return 0;
        }
    }
}
