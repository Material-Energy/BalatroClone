package io.github.metereel.sprites;

import io.github.metereel.Timer;
import processing.opengl.PShader;

import static io.github.metereel.Javatro.APP;
import static processing.core.PApplet.println;
import static processing.core.PApplet.second;

public class Shader {
    private final String filename;
    private PShader shader;

    public Shader(String filename){
        this.filename = filename;
    }

    public void initialize(){
        this.shader = APP.loadShader("../resources/shader/" + filename + ".fs");
    }

    public PShader asShader(){
        return this.shader;
    }

    public void updateShader(int width, int height, float seed){
        shader.set("tex_size", width, height);
        shader.set("tex_center", 0, .5f);
        shader.set("seed", seed);
    }

    public void periodic(){
        shader.set("timer", APP.millis() / 2000.0f);
    }
}
