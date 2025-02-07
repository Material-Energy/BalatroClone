package io.github.metereel;

import processing.core.PApplet;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;

public class Main extends PApplet {
    public static PApplet APP = new Main();

    @Override
    public void settings(){
        size(500, 500);

        CARDS.load();
        FACES.load();

        loadFaces();

        test.updateSprite();
    }

    @Override
    public void draw(){
        background(255);

        test.display();
    }

    @Override
    public void mousePressed(){
    }

    public static void main(String[] args) {
        String[] processingArgs = new String[]{"Balatro"};
        PApplet.runSketch(processingArgs, APP);
    }
}