package io.github.metereel.sprites;

import processing.core.PImage;
import processing.core.PVector;

public class Animation extends IDisplay{
    private int currentFrame = 0;
    private final int dtFrame;

    private final int frames;
    private Sprite[] sprites;

    public Animation(int frames, int dtFrame, int offsetX, int offsetY, int sizeWidth, int sizeHeight, String name){
        super(offsetX, offsetY, sizeWidth, sizeHeight, name);

        this.frames = frames;
        this.dtFrame = dtFrame;

        this.sprites = new Sprite[frames];
    }

    @Override
    public Animation copy(){
        Animation anim = new Animation(
                frames,
                dtFrame,
                offsetX,
                offsetY,
                sizeWidth,
                sizeHeight,
                name
        );

        anim.override(this.sprites);
        return anim;
    }

    public void override(Sprite[] sprites){
        this.sprites = new Sprite[sprites.length];

        System.arraycopy(sprites, 0, this.sprites, 0, this.sprites.length);
    }

    @Override
    public Animation apply(PImage spritesheet){
        for (int i = 0; i < frames; i++){
            sprites[i] = new Sprite(offsetX + i * sizeWidth, offsetY, sizeWidth, sizeHeight).apply(spritesheet);
        }

        return this;
    }

    @Override
    public void setShader(Shader shader) {
        super.setShader(shader);
        for (int i = 0; i < frames; i++){
            sprites[i].setShader(shader);
        }
    }

    @Override
    public void display(PVector pos, float rotation, float size){
        currentFrame %= dtFrame * frames;
        int frame = currentFrame / dtFrame;

        sprites[frame].display(pos, rotation, size);
        currentFrame++;
    }
}
