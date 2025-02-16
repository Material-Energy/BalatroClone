package io.github.metereel.gui;

import processing.core.PVector;

import static io.github.metereel.Helper.*;
import static io.github.metereel.Javatro.APP;

public class Button {
    private final PVector pos = new PVector();
    private final Text text;
    private final int fillCol;
    private final int clickedCol;
    private final float width;
    private final float height;

    private boolean isPressed = false;

    public Button(float x, float y, float width, float height, int fillCol, int clickedCol, Text text){
        this.pos.x = x;
        this.pos.y = y;
        this.width = width;
        this.height = height;
        this.fillCol = fillCol;
        this.clickedCol = clickedCol;
        this.text = text;
    }

    public void display(){
        if (isPressed) {
            APP.fill(clickedCol);
        } else {
            APP.fill(fillCol);
        }
        APP.rect(pos.x - width / 2.0f, pos.y - height / 2.0f, width, height, Math.min(width, height) / 5.0f);
        text.display(pos, 0.0f);
    }

    public boolean checkPressed(){
        return withinBounds(
                new PVector(APP.mouseX, APP.mouseY),
                pos,
                width,
                height);
    }

    public boolean checkClicked(){
        return isPressed;
    }

    public void setPressed(boolean b) {
        this.isPressed = b;
    }

    public PVector getPos() {
        return this.pos.copy();
    }

    public void setPos(float x, float y){
        this.pos.x = x;
        this.pos.y = y;
    }

    public void addArg(String arg) {
        text.addArg(arg);
    }
}
