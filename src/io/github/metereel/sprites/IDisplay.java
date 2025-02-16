package io.github.metereel.sprites;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;

import static io.github.metereel.Javatro.APP;

public abstract class IDisplay {
    protected static final PApplet app = APP;

    protected String name;

    protected int offsetX;
    protected int offsetY;
    protected int sizeWidth;
    protected int sizeHeight;

    protected Shader shader;

    public IDisplay(int offsetX, int offsetY, int sizeWidth, int sizeHeight, String name){

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.sizeWidth = sizeWidth;
        this.sizeHeight = sizeHeight;

        this.name = name;
    }

    public void setShader(Shader shader){
        this.shader = shader;
    }

    public void removeShader(){
        this.shader = null;
    }

    public boolean hasShader(){
        return this.shader != null;
    }

    public abstract void display(PVector pos, float rotation, float size);
    public abstract IDisplay apply(PImage spritesheet);
    public abstract IDisplay copy();

    public String getName(){
        return this.name;
    }

    public boolean hasNoImage() {
        return false;
    }
}
