package io.github.metereel.card.jokers;


import java.util.HashMap;

public class Jokers {
    private static final JokerCard HANGING_CHAD = new HangingChad();

    private static final HashMap<String, JokerCard> jokerLookup = new HashMap<>();

    public static void initialize(){
        jokerLookup.put("Hanging Chad", HANGING_CHAD);
    }

    public static JokerCard retrieveJoker(String name){
        for (String key : jokerLookup.keySet()){
            if (key.matches(name)){
                return jokerLookup.get(key).copy();
            }
        }

        return null;
    }
}
