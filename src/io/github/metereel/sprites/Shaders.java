package io.github.metereel.sprites;

public class Shaders {
    public static final Shader FOIL = new Shader("foil");
    public static final Shader HOLOGRAPHIC = new Shader("holo");
    public static final Shader POLYCHROME = new Shader("poly");
    public static final Shader NEGATIVE = new Shader("negative");

    public static void initialize(){
        FOIL.initialize();
        HOLOGRAPHIC.initialize();
        POLYCHROME.initialize();
        NEGATIVE.initialize();
    }
}
