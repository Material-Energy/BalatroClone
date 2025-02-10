package io.github.metereel;

import processing.core.PApplet;
import processing.core.PFont;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;

public class Main extends PApplet {
    public static PApplet APP = new Main();
    public static PFont FONT;

    @Override
    public void settings(){
        size(1000, 1000, P2D);

        loadFaces();

        CARDS.load();
        CARD_BACKS.load();
        FACES.load();

        hud.initialize();
    }

    @Override
    public void setup() {
        FONT = APP.createFont("../resources/balatro.ttf", 32);
        textFont(FONT);
        surface.setResizable(true);
    }

    @Override
    public void draw(){

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

    @Override
    public void mouseMoved() {
        hud.onMoved();
    }

    public static void main(String[] args) {
        String[] processingArgs = new String[]{"Balatro"};
        PApplet.runSketch(processingArgs, APP);
    }
}