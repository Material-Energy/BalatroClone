package io.github.metereel.sprites;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static io.github.metereel.Main.APP;
import static processing.core.PApplet.print;
import static processing.core.PApplet.println;

public class SpriteSheet {
    private final PApplet app = APP;

    ArrayList<IDisplay> sprites = new ArrayList<>();
    PImage spritesheet;
    String file;
    private boolean loaded;

    public SpriteSheet(String file, IDisplay... sprites){
        this.file = file;
        this.sprites.addAll(Arrays.asList(sprites));
    }

    public void add(IDisplay sprite){
        this.sprites.add(sprite);
    }

    public void load(){
        spritesheet = app.loadImage("../resources/" + file + ".png");

        System.out.println("Loading Sprites for " + file);

        for (IDisplay sprite : sprites){
            sprite.apply(this.spritesheet.copy());
        }
        this.loaded = true;
    }

    public Sprite getSprite(String name){
        if (spritesheet == null) spritesheet = app.loadImage("../resources/" + file + ".png");

        for (IDisplay sprite : sprites){
            if (Objects.equals(sprite.getName(), name) && sprite instanceof Sprite) {

                if (sprite.hasNoImage()) {
                    println("No image found, attempting replacement");
                    sprite.apply(this.spritesheet.copy());
                }
                return (Sprite) sprite.copy();
            }
        }
        return null;
    }

    public Animation getAnim(String name){
        for (IDisplay anim : sprites){
            if (Objects.equals(anim.getName(), name) && anim instanceof Animation) {
                anim.apply(this.spritesheet);
                return (Animation) anim.copy();
            }
        }
        return null;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
