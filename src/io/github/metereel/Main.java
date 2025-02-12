package io.github.metereel;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.awt.*;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Helper.*;

public class Main extends PApplet {
    public static PApplet APP = new Main();
    public static PFont FONT;

    @Override
    public void settings(){
        size(1500, (int) (1500 * 0.6f), P2D);

        loadFaces();

        CARDS.load();
        CARD_BACKS.load();
        FACES.load();

        HUD.initialize();
    }

    @Override
    public void setup() {
        FONT = APP.createFont("../resources/balatro.ttf", 32);
        textFont(FONT);
        PVector center = centerWindow();
        surface.setLocation((int) center.x, (int) center.y);
    }

    public static PVector centerWindow() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth()) / 2);
        int y = (int) ((dimension.getHeight()) / 2);
        return new PVector(x, y);
    }

    @Override
    public void draw(){

        Game.tick();
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