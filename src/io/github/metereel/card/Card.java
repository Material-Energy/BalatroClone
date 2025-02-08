package io.github.metereel.card;

import io.github.metereel.Helper;
import io.github.metereel.Timer;
import io.github.metereel.sprites.Sprite;
import io.github.metereel.text.Text;
import processing.core.PVector;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;
import static io.github.metereel.Main.APP;
import static processing.core.PApplet.*;
import static processing.core.PVector.*;

public class Card {
    private final Timer shakingTimer = new Timer();
    private final Timer lerpTimer = new Timer();

    private final Deck deck;
    private final Text name;
    // private Lore description;
    private Sprite cardFront;
    private Sprite cardBack;
    private boolean isFlipped = false;
    private boolean isShaking = false;
    private boolean isSelected = false;

    private final String deckType;
    private String cardType;
    private String rankSuit;

    private final PVector pos = new PVector();
    private final PVector targetPos = new PVector(-1.0f, 0.0f);
    private final PVector startPos = new PVector();
    private int translationTime = 0;

    private CardState cardState = CardState.IDLE;

    private float rotation = 0.0f;
    private float size = 1.0f;

    public Card(Deck deck, Text name, String deckType, String cardType, String rankSuit){
        this.deck = deck;
        this.name = name;
        this.deckType = deckType;
        this.cardType = cardType;
        this.rankSuit = rankSuit;

    }

    public void setTargetPos(float x, float y, int translationTime) {
        this.targetPos.x = x;
        this.targetPos.y = y;
        this.translationTime = translationTime;

        this.startPos.x = this.getPos().x;
        this.startPos.y = this.getPos().y;
    }

    public void setPos(float x, float y){
        this.pos.x = x;
        this.pos.y = y;
    }

    public void setSelected(boolean b) {
        this.isSelected = b;
    }

    public PVector getPos() {
        return this.pos.copy();
    }

    public float getRotation(){
        return this.rotation;
    }

    public void setSize(float scale){
        this.size = scale;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public float getSize(){
        return this.size;
    }

    public CardState getState() {
        return this.cardState;
    }

    public void setState(CardState state){
        this.cardState = state;
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public void upgradeCard(int shift){
        this.rankSuit = Helper.upgradeCard(rankSuit, shift);
        updateSprite();
    }

    public void setRankSuit(String rankSuit){
        this.rankSuit = rankSuit;
        updateSprite();
    }

    public void setCardType(String cardType){
        this.cardType = cardType;
        updateSprite();
    }

    public void flip(){
        this.isFlipped = !isFlipped;
    }

    public boolean isFlipped(){
        return this.isFlipped;
    }

    public float lerpProgress(){
        return (float) lerpTimer.getTime() / translationTime;
    }

    public boolean hasNoTarget() {
        return targetPos.x == -1;
    }

    public void updateSprite(){
        this.cardFront = CARDS.getSprite(cardType);
        this.cardFront.layerSprite(FACES.getSprite(rankSuit), 0, 0);

        this.cardBack = CARD_BACKS.getSprite(deckType);
    }

    public void updateTargetPosition(){
        if (targetPos.dist(pos) <= 0.1f) targetPos.x = -1;
        if (!hasNoTarget()) {
            lerpTimer.incrementTimer();
            PVector delta = lerp(startPos, targetPos, lerpTimer.getTime() / (float) translationTime);

            setPos(delta.x, delta.y);
        }
        else lerpTimer.resetTimer();
    }

    public boolean isHovering(){
        float upperX = this.getPos().x + (float) CARD_WIDTH * getSize() / 2;
        float lowerX = this.getPos().x - (float) CARD_WIDTH * getSize() / 2;

        float upperY = this.getPos().y + (float) CARD_HEIGHT * getSize() / 2;
        float lowerY = this.getPos().y - (float) CARD_HEIGHT * getSize() / 2;

        boolean withinCard = withinBounds(new PVector(APP.mouseX, APP.mouseY), lowerX, upperX, lowerY, upperY);
        boolean noHovering = deck.hoveringCard == null || deck.hoveringCard == this;
        return noHovering && withinCard && getState() != CardState.DRAGGING;
    }

    public void updateStatus(){

        if (isSelected()){
            this.setRotation(0.0f);
            this.setSize(1.2f);
        }

        if (isHovering()){
            if (!isSelected()){
                this.setRotation(radians(10.0f));
                this.setSize(1.2f);
            } else {
                this.setSize(1.4f);
            }
        } else if (!isSelected()){
            this.setSize(1.0f);
            this.setRotation(0.0f);
        }
    }

    public void tick(){
        updateTargetPosition();
        updateStatus();
    }

    public void display(){

        float rot = getRotation();
        if (isShaking){
            rot = withTilt(shakingTimer, rot, 15, 0.25f);
        } else {
            shakingTimer.resetTimer();
        }
        if (isFlipped){
            if (cardBack != null) cardBack.display(pos, rot, size);
        } else {
            if (cardFront != null) cardFront.display(pos, rot, size);
        }
    }

    @Override
    public String toString() {
        return name + "{targetPos = " + targetPos + "}";
    }

    public void shakeFor(int i) {
        this.isShaking = true;
        shakingTimer.schedule(i, () -> this.isShaking = false);
    }
}
