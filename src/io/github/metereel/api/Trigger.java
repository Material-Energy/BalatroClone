package io.github.metereel.api;

import io.github.metereel.gui.Scorer;

import java.util.ArrayList;

import static processing.core.PApplet.println;

public class Trigger {
    private int triggersLeft = 1;
    private int currentTrigger = 0;
    private final ArrayList<Score> triggerOrder;

    public Trigger(){
        this.triggerOrder = new ArrayList<>(2);
    }

    public Trigger(ArrayList<Score> triggerOrder){
        this();
        this.triggerOrder.addAll(triggerOrder);
    }

    public void addTrigger(Score score, int index){
        if (score.hasNoEffect()) return;
        while (index >= triggerOrder.size()){
            triggerOrder.add(Score.EMPTY);
        }
        triggerOrder.set(index, score);
    }

    public void removeTrigger(int index){
        this.triggerOrder.remove(index);
    }

    private void moveToNext(){
        currentTrigger++;
        if (currentTrigger == triggerOrder.size()){
            this.triggersLeft--;
            this.currentTrigger = 0;
        }
    }

    public void triggerNext(Scorer scorer){
        triggerOrder.get(currentTrigger).score(scorer);
        moveToNext();
    }

    public void reset(){
        triggersLeft = 1;
        currentTrigger = 0;
    }

    public void retrigger(){
        this.triggersLeft++;
    }

    public int getTriggersLeft(){
        return triggersLeft;
    }

    public Score getCurrentTrigger() {
        return triggerOrder.get(currentTrigger);
    }

    public void skip() {
        moveToNext();
    }
}
