package io.github.metereel.sprites;

import processing.core.PImage;
import processing.core.PVector;

import static processing.core.PConstants.ARGB;

public class Sprite extends IDisplay {
    private PImage image;

    public Sprite(int offsetX, int offsetY, int sizeWidth, int sizeHeight) {
        this(offsetX, offsetY, sizeWidth, sizeHeight, "");
    }

    public Sprite(int offsetX, int offsetY, int sizeWidth, int sizeHeight, String name) {
        super(offsetX, offsetY, sizeWidth, sizeHeight, name);
    }

    @Override
    public Sprite copy() {
        Sprite sprite = new Sprite(
                offsetX,
                offsetY,
                sizeWidth,
                sizeHeight,
                name
        );

        sprite.override(this.image);
        return sprite;
    }

    public void override(PImage image) {
        this.image = image.copy();
    }

    private void cropImage(int offsetX, int offsetY, int sizeWidth, int sizeHeight, PImage spritesheet) {
        this.image = app.createImage(sizeWidth, sizeHeight, ARGB);
        this.image.loadPixels();

        for (int i = 0; i < this.image.height; i++) {
            for (int j = 0; j < this.image.width; j++) {

                int imageIndex = i * this.image.width + j;
                int sheetIndex = (offsetY + i) * spritesheet.width + (offsetX + j);

                this.image.pixels[imageIndex] = spritesheet.pixels[sheetIndex];

            }
        }

        this.image.updatePixels();
    }

    public void layerSprite(Sprite other, int offsetX, int offsetY) {
        PImage layer = other.image;
        layer.loadPixels();
        image.loadPixels();

        for (int i = 0; i < layer.height; i++) {
            for (int j = 0; j < layer.width; j++) {

                int layerIndex = i * image.width + j;

                int imageIndex = (i + offsetY) * image.width + (j + offsetX);

                image.pixels[imageIndex] = mixColor(image.pixels[imageIndex], layer.pixels[layerIndex]);
            }
        }

        image.updatePixels();
    }

    private int mixColor(int color1, int color2) {
        int color1Alpha = color1 >> 24 & 0xFF;
        int color1Red = color1 >> 16 & 0xFF;
        int color1Green = color1 >> 8 & 0xFF;
        int color1Blue = color1 & 0xFF;

        int color2Alpha = color2 >> 24 & 0xFF;
        int color2Red = color2 >> 16 & 0xFF;
        int color2Green = color2 >> 8 & 0xFF;
        int color2Blue = color2 & 0xFF;

        float alpha, red, green, blue;
        alpha = Math.max(color1Alpha, color2Alpha);

        float color1ARat, color2ARat;
        color1ARat = (float) color1Alpha / (color1Alpha + color2Alpha);
        color2ARat = (float) color2Alpha / (color1Alpha + color2Alpha);

        if (color2Alpha >= 248.0f) {
            red = color2Red;
            green = color2Green;
            blue = color2Blue;
        } else if (Math.abs(color1Alpha - color2Alpha) <= 10.0f) {
            red = color1ARat * color1Red + color2ARat * color2Red;
            green = color1ARat * color1Green + color2ARat * color2Green;
            blue = color1ARat * color1Blue + color2ARat * color2Blue;
        } else if (color1Alpha > color2Alpha) {
            red = color1Red;
            green = color1Green;
            blue = color1Blue;
        } else {
            red = color2Red;
            green = color2Green;
            blue = color2Blue;
        }

        return app.color(red, green, blue, alpha);
    }

    @Override
    public Sprite apply(PImage spritesheet) {
        cropImage(offsetX, offsetY, sizeWidth, sizeHeight, spritesheet);
        return this;
    }

    @Override
    public void display(PVector pos, float rotation, float size) {
        if (image == null) return;
        app.pushMatrix();

        app.translate(pos.x, pos.y);
        app.rotate(rotation);
        app.imageMode(app.CENTER);
        app.scale(size);

        app.image(image, 0, 0);
        app.popMatrix();
    }
}
