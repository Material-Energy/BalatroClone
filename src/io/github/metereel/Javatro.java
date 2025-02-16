package io.github.metereel;

import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;

public class Javatro extends PApplet {
    public static final PApplet APP = new Javatro();

    @Override
    public void settings(){
        size(1500, (int) (1500 * 0.6f), P2D);

        loadFaces();
    }

    @Override
    public void setup() {


        Game.initialize();
        HUD.initialize();
    }

    public static PVector centerWindow() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - 750) / 2);
        int y = (int) ((dimension.getHeight() - 450) / 2);
        return new PVector(x, y);
    }

    @Override
    public void draw(){
        if (CARDS.isLoaded() && CARD_BACKS.isLoaded() && FACES.isLoaded())
            Game.tick();
        else
            print("Waiting for sprites to load");
    }

    @Override
    public void mouseClicked(){
        HUD.onClick();
    }

    @Override
    public void mouseDragged() {
        HUD.onDrag();
    }

    @Override
    public void mouseReleased() {
        HUD.onRelease();
    }

    @Override
    public void mousePressed() {
        HUD.onPressed();
    }

    @Override
    public void mouseMoved() {
        HUD.onMoved();
    }

    public static void main(String[] args) {
        String[] processingArgs = new String[]{"Balatro"};
        PApplet.runSketch(processingArgs, APP);
    }
}