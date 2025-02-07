package io.github.metereel.sprites;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static io.github.metereel.Main.APP;
import static processing.core.PApplet.printArray;

public class SpriteSheet {
    private final PApplet app = APP;

    ArrayList<IDisplay> sprites = new ArrayList<IDisplay>();
    PImage spritesheet;
    String file;

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
            sprite.apply(this.spritesheet);
        }
    }

    public Sprite getSprite(String name){
        for (IDisplay sprite : sprites){
            if (Objects.equals(sprite.getName(), name) && sprite instanceof Sprite) {
                return (Sprite) sprite.copy();
            }
        }
        return null;
    }

    public Animation getAnim(String name){
        for (IDisplay image : sprites){
            if (Objects.equals(image.getName(), name) && image instanceof Animation) {
                return (Animation) image.copy();
            }
        }
        return null;
    }
}
