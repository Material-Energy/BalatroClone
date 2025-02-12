package io.github.metereel;

public class Timer {
    private int time = 0;
    private int scheduledTime = -1;
    private Runnable scheduledCommand;

    public Timer(){

    }

    public Timer(int i) {
        this.time = i;
    }

    public void resetTimer() {
        this.time = 0;
    }

    public void incrementTimer() {
        this.time++;

        if (time >= scheduledTime && scheduledTime != -1){
            scheduledCommand.run();
            scheduledTime = -1;
        }
    }

    public int getTime(){
        return time;
    }

    public int getTimeWithCycle(int cycle){
        return time % cycle;
    }

    public void schedule(int time, Runnable command) {
        this.resetTimer();
        this.scheduledTime = time;
        this.scheduledCommand = command;
    }
}
