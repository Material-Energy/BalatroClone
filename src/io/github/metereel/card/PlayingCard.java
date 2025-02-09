package io.github.metereel.card;

import io.github.metereel.Helper;
import io.github.metereel.gui.Text;

import static io.github.metereel.Constants.*;

public class PlayingCard extends Card{
    private final Deck deck;

    private final String deckType;
    private String cardType;
    private String rankSuit;

    public PlayingCard(Deck deck, Text name, String deckType, String cardType, String rankSuit) {
        super(name);
        this.deck = deck;
        this.deckType = deckType;
        this.cardType = cardType;
        this.rankSuit = rankSuit;
    }

    public void setRankSuit(String rankSuit){
        this.rankSuit = rankSuit;
        updateSprite();
    }

    public void setCardType(String cardType){
        this.cardType = cardType;
        updateSprite();
    }

    public void upgradeCard(int shift){
        this.rankSuit = Helper.upgradeCard(rankSuit, shift);
        updateSprite();
    }

    public void updateSprite(){
        this.cardFront = CARDS.getSprite(cardType);
        this.cardFront.layerSprite(FACES.getSprite(rankSuit), 0, 0);

        this.cardBack = CARD_BACKS.getSprite(deckType);
    }
}
