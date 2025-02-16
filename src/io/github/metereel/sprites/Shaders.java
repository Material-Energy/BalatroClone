package io.github.metereel.sprites;

public class Shaders {
    public static Shader FOIL = new Shader("foil");
    public static Shader HOLOGRAPHIC = new Shader("holo");
    public static Shader POLYCHROME = new Shader("poly");
    public static Shader NEGATIVE = new Shader("negative");

    public static void initialize(){
        FOIL.initialize();
        HOLOGRAPHIC.initialize();
        POLYCHROME.initialize();
        NEGATIVE.initialize();
    }
}
