package io.github.metereel.card;

import io.github.metereel.Timer;
import io.github.metereel.sprites.Sprite;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;
import static io.github.metereel.gui.HudDisplay.hoveringCard;
import static io.github.metereel.Main.APP;
import static processing.core.PApplet.*;
import static processing.core.PVector.*;

public abstract class Card {
    private final Timer shakingTimer = new Timer();
    private final Timer lerpTimer = new Timer();

    private final Text name;
    // private Lore description;
    Sprite cardFront;
    Sprite cardBack;
    private boolean isFlipped = false;
    private boolean isShaking = false;
    private boolean isSelected = false;


    private final PVector pos = new PVector();
    private final PVector targetPos = new PVector(-1.0f, 0.0f);
    private final PVector startPos = new PVector();
    private int translationTime = 0;

    private CardState cardState = CardState.IDLE;

    private float rotation = 0.0f;
    private float size = 1.0f;
    private boolean hasTarget = false;

    public Card(Text name){
        this.name = name;
    }

    public void setTargetPos(float x, float y, int translationTime) {
        if (hasTarget()) return;
        hasTarget = true;
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

    public void setSize(float scale){
        this.size = scale;
    }

    public void setState(CardState state){
        this.cardState = state;
    }

    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Text getName() {
        return this.name;
    }

    public PVector getPos() {
        return this.pos.copy();
    }

    public float getSize(){
        return this.size;
    }

    public float getRotation(){
        return this.rotation;
    }

    public CardState getState() {
        return this.cardState;
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

    public boolean hasTarget() {
        return hasTarget;
    }

    public abstract void updateSprite();

    public void updateTargetPosition(){
        if (targetPos.dist(pos) <= 0.1f) hasTarget = false;
        if (hasTarget()) {
            lerpTimer.incrementTimer();
            PVector delta = lerp(startPos, targetPos, lerpTimer.getTime() / (float) translationTime);

            setPos(delta.x, delta.y);
        }
        else lerpTimer.resetTimer();
    }

    public boolean isHovering(){

        boolean withinCard = withinBounds(new PVector(APP.mouseX, APP.mouseY),
                pos,
                CARD_WIDTH * getSize(),
                CARD_HEIGHT * getSize());
        boolean noHovering = hoveringCard == null || hoveringCard == this;
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
