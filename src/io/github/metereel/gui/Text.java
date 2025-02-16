package io.github.metereel.gui;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static io.github.metereel.Javatro.APP;
import static processing.core.PConstants.CENTER;

public class Text {
    private static final PApplet app = APP;

    private final String text;
    private int textCol;
    private int size;
    ArrayList<Object> args = new ArrayList<>();

    public Text(String text){
        this(text, 14);
    }

    public Text(String text, int size){
        this(text, app.color(255), size);
    }

    public Text(String text, int textCol, int size){
        this.text = text;
        this.textCol = textCol;
        this.size = size;
    }

    public void setColor(int textCol){
        this.textCol = textCol;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void addArg(Object arg){
        this.args.add(arg);
    }

    public String sub(){
        String sub = text;
        for (Object arg : args){
            sub = sub.replaceFirst("%s", arg.toString());
        }
        return sub;
    }

    public void display(PVector pos, float rotation){
        app.fill(textCol);
        app.textSize(size);
        app.textAlign(CENTER, CENTER);

        app.pushMatrix();
        app.translate(pos.x, pos.y);
        app.rotate(rotation);
        app.text(sub(), 0, 0);

        app.popMatrix();
        this.args.clear();
    }

    @Override
    public String toString() {
        return text;
    }

    public int getSize() {
        return size;
    }
}
