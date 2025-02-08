package io.github.metereel;

import io.github.metereel.sprites.Sprite;
import processing.core.PVector;

import static io.github.metereel.Constants.*;
import static processing.core.PApplet.*;

public class Helper {
    public static void loadFaces(){
        for (int i = 0; i < ranks.length; i++){
            for (int j = 0; j < suits.length; j++){
                FACES.add(new Sprite(CARD_WIDTH * i, CARD_HEIGHT * j, CARD_WIDTH, CARD_HEIGHT, ranks[i] + suits[j]));
            }
        }
    }

    public static float withTilt(Timer timer, float currentDeg, float maxTiltDeg){
        return withTilt(timer, currentDeg, maxTiltDeg, 1.0f);
    }

    public static float withTilt(Timer timer, float currentDeg, float maxTiltDeg, float cycle){
        timer.incrementTimer();
        int time = timer.getTimeWithCycle((int) (cycle * maxTiltDeg * 4));
        return radians(currentDeg + Math.abs((time / cycle) % (maxTiltDeg * 4) - maxTiltDeg * 2) - maxTiltDeg);
    }

    public static boolean withinBounds(PVector pos, float lowerX, float upperX, float lowerY, float upperY){
        return lowerX <= pos.x && pos.x <= upperX && lowerY <= pos.y && pos.y <= upperY;
    }

    public static String upgradeCard(String from, int shift){
        String rank = from.split("[A-Z]")[0];
        String suit = from.replace(rank, "");

        int index = -1;
        for (int i = 0; i < ranks.length; i ++){
            if (rank.equals(ranks[i])){
                index = i;
            }
        }
        index = (index + shift) % ranks.length;

        return ranks[index] + suit;
    }
}
