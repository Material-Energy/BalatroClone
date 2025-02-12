package io.github.metereel.api;

import io.github.metereel.gui.Scorer;

import java.util.ArrayList;

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
        this.triggerOrder.add(index, score);
    }

    public void removeTrigger(int index){
        this.triggerOrder.remove(index);
    }

    public void triggerNext(Scorer scorer){
        triggerOrder.get(currentTrigger).score(scorer);
        currentTrigger++;
        if (currentTrigger == triggerOrder.size()){
            this.triggersLeft--;
            this.currentTrigger = 0;
        }
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
}
