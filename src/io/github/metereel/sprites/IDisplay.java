package io.github.metereel.sprites;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static io.github.metereel.Main.APP;

public abstract class IDisplay {
    protected static final PApplet app = APP;

    protected String name;

    protected int offsetX;
    protected int offsetY;
    protected int sizeWidth;
    protected int sizeHeight;

    public IDisplay(int offsetX, int offsetY, int sizeWidth, int sizeHeight, String name){

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.sizeWidth = sizeWidth;
        this.sizeHeight = sizeHeight;

        this.name = name;
    }

    public abstract void display(PVector pos, float rotation, float size);
    public abstract IDisplay apply(PImage spritesheet);
    public abstract IDisplay copy();

    public String getName(){
        return this.name;
    }
}
