package io.github.metereel;

import processing.core.PApplet;
import processing.core.PVector;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;

public class Main extends PApplet {
    public static PApplet APP = new Main();

    @Override
    public void settings(){
        size(500, 500);

        loadFaces();

        CARDS.load();
        FACES.load();

        test.updateSprite();
    }

    @Override
    public void draw(){
        background(255);

        test.display();
        FACES.getSprite("aceSpades").display(new PVector((float) APP.width / 2, (float) APP.height / 2), 0.0f, 1.0f);
    }

    @Override
    public void mousePressed(){
        test.setPos(mouseX, mouseY);
    }

    public static void main(String[] args) {
        String[] processingArgs = new String[]{"Balatro"};
        PApplet.runSketch(processingArgs, APP);
    }
}