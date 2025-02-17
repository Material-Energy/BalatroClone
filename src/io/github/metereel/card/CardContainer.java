package io.github.metereel.card;

import io.github.metereel.gui.HudDisplay;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CardContainer <T extends Card>{
    private final ArrayList<T> storedCards;
    private int maxCards;
    
    public CardContainer(int maxCards){
        this(new ArrayList<>(maxCards), maxCards);
    }

    public CardContainer(ArrayList<T> storedCards, int maxCards) {
        this.storedCards = storedCards;
        this.maxCards = maxCards;
    }

    public ArrayList<T> getStored() {
        return storedCards;
    }
    
    public void updateMaxCards(int maxCards){
        this.maxCards = maxCards;
    }
    
    public void display(float baseY, float axis, float lowerBound, float upperBound){
        float baseX;

        for (int i = 0; i < storedCards.size(); i++){
            Card card = storedCards.get(i);
            if (card.cardFront == null){
                card.updateSprite();
            }


            baseX = calculateXPos(i, axis, lowerBound, upperBound);

            CardState state = card.getState();
            if (state == CardState.DRAWING) {
                if (card.getPos().dist(new PVector(baseX, baseY)) >= 0.1f) card.setTargetPos(baseX, baseY, 20);
                if (card.isFlipped() && card.lerpProgress() > 0.9f) card.flip();

                if (!card.hasTarget()){
                    card.setState(CardState.IDLE);
                }
            }
            else if (state == CardState.IDLE) {
                card.setPos(baseX, baseY);
            }
        }

        storedCards.forEach(Card::drawShadow);
        storedCards.forEach(Card::display);
    }
    
    public void forEach(Consumer<T> action){
        storedCards.forEach(action);
    }

    public boolean hasSpace() {
        return storedCards.size() < maxCards;
    }

    public void insert(T card) {
        this.storedCards.add(card);
    }

    public float calculateXPos(int index, float axis, float leftBound, float rightBound){
        return (axis - (-index + storedCards.size() / 2.0f) * (3.0f / 2 / storedCards.size()) * Math.min(
                Math.abs(axis - leftBound),
                Math.abs(axis - rightBound)
        ));
    }

    public void removeAll(ArrayList<T> selectedCards) {
        storedCards.removeAll(selectedCards);
    }

    public int indexOf(T card) {
        return storedCards.indexOf(card);
    }

    public void clear() {
        storedCards.clear();
    }

    public boolean contains(T card) {
        return storedCards.contains(card);
    }
}
