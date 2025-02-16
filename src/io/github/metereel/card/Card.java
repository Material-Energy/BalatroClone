package io.github.metereel.card;

import io.github.metereel.Timer;
import io.github.metereel.api.Edition;
import io.github.metereel.api.Trigger;
import io.github.metereel.sprites.Shaders;
import io.github.metereel.sprites.Sprite;
import io.github.metereel.gui.Text;
import processing.core.PVector;

import java.util.Random;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;
import static io.github.metereel.gui.HudDisplay.hoveringCard;
import static io.github.metereel.Javatro.APP;
import static processing.core.PVector.*;

public abstract class Card {
    private final Random random = new Random();
    private final float CARD_CYCLE = (float) random.nextInt(48, 72);

    private final Timer shakingTimer = new Timer();
    private final Timer lerpTimer = new Timer();
    private final Timer floatTimer = new Timer(random.nextInt((int) (20 * CARD_CYCLE)));

    private final Text name;
    // private Lore description;
    protected Sprite cardFront;
    protected Sprite cardBack;
    protected boolean isFlipped = false;
    private boolean isShaking = false;
    private boolean isSelected = false;
    private boolean ignore = false;


    private final PVector pos = new PVector();
    private final PVector targetPos = new PVector(-1.0f, 0.0f);
    private final PVector startPos = new PVector();
    private int translationTime = 0;

    private CardState cardState = CardState.IDLE;
    protected Edition edition = Edition.NORMAL;
    protected Trigger trigger = new Trigger();

    private float rotation = 0.0f;
    private float size = 1.0f;
    private boolean hasTarget = false;

    public Card(Text name){
        this.name = name;
    }

    protected abstract void addTriggers();

    public void setTargetPos(float x, float y, int translationTime){
        setTargetPos(x, y, translationTime, false);
    }

    public void setTargetPos(float x, float y, int translationTime, boolean override) {
        if (hasTarget() && !override) return;
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

    public void setEdition(Edition edition){
        this.edition = edition;
        this.trigger.addTrigger(edition.getScore(), EDITION);
        this.updateSprite();
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        if (!ignore) {
            this.setRotation(0.0f);
        }
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

    public void updateSprite(){
        switch (edition){
            case NORMAL -> this.cardFront.removeShader();
            case FOIL -> this.cardFront.setShader(Shaders.FOIL);
            case HOLOGRAPHIC -> this.cardFront.setShader(Shaders.HOLOGRAPHIC);
            case POLYCHROME -> this.cardFront.setShader(Shaders.POLYCHROME);
            case NEGATIVE -> this.cardFront.setShader(Shaders.NEGATIVE);
//            case FOIL -> cardFront.applyMask(color -> {
//                float alpha = APP.alpha(color);
//                float red = APP.red(color);
//                float green = APP.green(color);
//                float blue = APP.blue(color);
//
//                int deltaBlue = 60;
//
//                red = Math.max(0, red - deltaBlue);
//                green = Math.max(0, green - deltaBlue);
//                return APP.color(red, green, blue, alpha);
//            });
//
//            case HOLOGRAPHIC -> cardFront.applyMask(color -> {
//                float alpha = APP.alpha(color);
//                float red = APP.red(color);
//                float green = APP.green(color);
//                float blue = APP.blue(color);
//
//                int deltaRed = 60;
//
//                blue = Math.max(0, blue - deltaRed);
//                green = Math.max(0, green - deltaRed);
//                return APP.color(red, green, blue, alpha);
//            });
        }
    }

    public void updateTargetPosition(){
        if (targetPos.dist(pos) <= 0.1f) {
            hasTarget = false;
        }
        if (hasTarget()) {
            lerpTimer.incrementTimer();
            if (lerpTimer.getTime() / (float) translationTime > 1){
                lerpTimer.resetTimer();
            }
            PVector delta = lerp(startPos, targetPos, lerpTimer.getTime() / (float) translationTime);

            setPos(delta.x, delta.y);
        }
        else lerpTimer.resetTimer();
    }

    public boolean isHovering(){

        boolean withinCard = withinBounds(new PVector(APP.mouseX, APP.mouseY),
                pos,
                CARD_WIDTH,
                CARD_HEIGHT);
        boolean noHovering = hoveringCard == null || hoveringCard == this;
        return noHovering && withinCard && getState() != CardState.DRAGGING;
    }

    public void updateStatus(){
        if (ignore) return;

        if (isSelected()){
            this.setSize(1.45f);
        }

        if (isHovering()){
            if (!isSelected()){
                this.setSize(1.45f);
            } else {
                this.setSize(1.6f);
            }
        } else if (!isSelected()){
            this.setSize(1.25f);
        }
    }

    public void tick(){
        updateTargetPosition();
        updateStatus();
    }

    public void drawShadow(){
        APP.pushMatrix();
        PVector translateTo = this.getPos().add(-10, +10);
        APP.translate(translateTo.x, translateTo.y);
        APP.rotate(withTilt(floatTimer, getRotation(), 3, CARD_CYCLE, true));
        APP.scale(this.getSize());
        drawBubble(APP.color(30, 75), new PVector(0, 0), new PVector(CARD_WIDTH, CARD_HEIGHT), 5);
        APP.popMatrix();
    }

    public void display(){

        float rot = getRotation();
        if (getState() == CardState.IDLE) {
            floatTimer.incrementTimer();
            rot = withTilt(floatTimer, rot, 3, CARD_CYCLE, true);
        }

        if (isShaking){
            rot = withTilt(shakingTimer, rot, 15, 0.25f, true);
        } else {
            shakingTimer.resetTimer();
        }
        if (isFlipped){
            if (cardBack != null) cardBack.display(pos, rot, size);
        } else {
            cardFront.forceUpdateShader();
            if (cardFront != null) cardFront.display(pos, rot, size);
        }
    }

    @Override
    public String toString() {
        return name + "{pos = "+pos + ", targetPos = " + targetPos + " startPos = " + startPos + " size = " + size + "}";
    }

    public void shakeFor(int i) {
        this.isShaking = true;
        shakingTimer.schedule(i, () -> this.isShaking = false);
    }

    public void displayName() {
    }

    protected void resetTimers() {
        floatTimer.resetTimer();
        shakingTimer.resetTimer();
        lerpTimer.resetTimer();
    }
}
