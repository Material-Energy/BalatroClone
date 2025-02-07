package io.github.metereel;

import io.github.metereel.sprites.Sprite;

import static io.github.metereel.Constants.*;
import static processing.core.PApplet.radians;

public class Helper {
    public static void loadFaces(){
        for (int i = 0; i < num.length; i++){
            for (int j = 0; j < suit.length; j++){
                FACES.add(new Sprite(71 * i, 92 * j, 71, 92, num[i] + suit[j]));
            }
        }
    }

    public static float withTilt(float currentDeg, float maxTiltDeg){
        return withTilt(currentDeg, maxTiltDeg, 1.0f);
    }

    public static float withTilt(float currentDeg, float maxTiltDeg, float cycle){
        timer++;
        timer %= (int) (cycle * maxTiltDeg * 4);
        return radians(currentDeg + Math.abs((timer / cycle) % (maxTiltDeg * 4) - maxTiltDeg * 2) - maxTiltDeg);
    }
}
