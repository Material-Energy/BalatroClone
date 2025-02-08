package io.github.metereel;

import processing.core.PApplet;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;

public class Main extends PApplet {
    public static PApplet APP = new Main();

    @Override
    public void settings(){
        size(1000, 1000);

        loadFaces();

        CARDS.load();
        CARD_BACKS.load();
        FACES.load();

        hud.initialize();
    }

    @Override
    public void draw(){
        background(255);

        hud.getDeck().tick();
        hud.display();
    }

    @Override
    public void mouseClicked(){
        hud.onClick();
    }

    @Override
    public void mouseDragged() {
        hud.onDrag();
    }

    @Override
    public void mouseReleased() {
        hud.onRelease();
    }

    @Override
    public void mousePressed() {
        hud.onPressed();
    }

    public static void main(String[] args) {
        String[] processingArgs = new String[]{"Balatro"};
        PApplet.runSketch(processingArgs, APP);
    }
}