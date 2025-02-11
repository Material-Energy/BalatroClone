package io.github.metereel.card;

import io.github.metereel.Helper;
import io.github.metereel.gui.Scorer;
import io.github.metereel.gui.Text;

import java.util.Objects;

import static io.github.metereel.Constants.*;

public class PlayingCard extends Card{
    private final Deck deck;

    private final String deckType;
    private String cardType;
    private String rankSuit;
    private String rank;
    private String suit;
    private boolean triggered = false;

    public PlayingCard(Deck deck, Text name, String deckType, String cardType, String rankSuit) {
        super(name);
        this.deck = deck;
        this.deckType = deckType;
        this.cardType = cardType;
        this.rankSuit = rankSuit;

        this.rank = rankSuit.split(" ")[0];
        this.suit = rankSuit.split(" ")[1];
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

    public void setCardType(String cardType){
        this.cardType = cardType;
        updateSprite();
    }

    public void upgradeCard(int shift){
        this.setRankSuit(Helper.upgradeCard(rankSuit, shift));
    }

    public void updateSprite(){
        this.cardFront = CARDS.getSprite(cardType);
        this.cardFront.layerSprite(FACES.getSprite(rankSuit), 0, 0);

        this.cardBack = CARD_BACKS.getSprite(deckType);
    }

    private double getChips() {
        return RankChips.getChips(this.getRank());
    }

    public boolean hasTriggered() {
        return triggered;
    }

    public void resetTrigger() {
        this.triggered = false;
    }

    public void tryTrigger(Scorer scorer) {
        triggered = true;

        scorer.addChips(this.getChips());
        this.setSize(1.4f);
        setIgnore(true);
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
