package io.github.metereel;

import io.github.metereel.card.HandType;
import io.github.metereel.card.PlayingCard;
import io.github.metereel.sprites.Sprite;
import processing.awt.PImageAWT;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.metereel.Constants.*;
import static io.github.metereel.Javatro.APP;
import static io.github.metereel.card.HandType.*;
import static processing.core.PApplet.*;

public class Helper {

    public static PFont fontFromString(String filename, int fontSize){
        String full = "resources/font/" + filename;
        println("Requesting " + full);
        BufferedInputStream stream = new BufferedInputStream(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(full)));

        Font base;
        try {
            base = Font.createFont(0, stream);

            return new PFont(base.deriveFont(fontSize * (float) APP.sketchPixelDensity()), true, null, true, APP.sketchPixelDensity());
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PImage imageFromString(String filename){
        String full = "resources/images/" + filename;
        println("Requesting " + full);
        BufferedInputStream stream = new BufferedInputStream(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(full)));
        byte[] bytes = loadBytes(stream);

        Image image = new ImageIcon(bytes).getImage();
        PImage img = new PImageAWT(image);

        img.parent = APP;
        return img;
    }

    public static PShader shaderFromString(String filename, URL vertURL){
        String full = "resources/shader/" + filename;
        println("Requesting " + full);
        URL url = ClassLoader.getSystemResource(full);

        return new PShader(APP, vertURL, url);
    }

    public static PShader shaderFromString(String filename){
        return shaderFromString(filename, PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/TexVert.glsl"));
    }


    public static void loadFaces(){
        for (int i = 0; i < RANKS.size(); i++){
            for (int j = 0; j < SUITS_TEXTURE.size(); j++){
                FACES.add(new Sprite(CARD_WIDTH * i, CARD_HEIGHT * j, CARD_WIDTH, CARD_HEIGHT, RANKS.get(i) + " " + SUITS_TEXTURE.get(j)));
            }
        }
    }

    public static void loadLevels(){
        handLevels.clear();
        for (HandType type: HandType.values()){
            handLevels.put(type, 1);
        }
    }

    public static float withTilt(Timer timer, float currentDeg, float maxTiltDeg, boolean incrementTime){
        return withTilt(timer, currentDeg, maxTiltDeg, 1.0f, incrementTime);
    }

    public static float withTilt(Timer timer, float currentDeg, float maxTiltDeg, float cycle, boolean incrementTime){
        if (incrementTime) timer.incrementTimer();
        int time = timer.getTimeWithCycle((int) (cycle * maxTiltDeg * 4));
        return radians(currentDeg + Math.abs((time / cycle) % (maxTiltDeg * 4) - maxTiltDeg * 2) - maxTiltDeg);
    }

    public static boolean withinBounds(PVector pos, PVector center, float width, float height){
        float upperX = center.x + width / 2;
        float lowerX = center.x - width / 2;

        float upperY = center.y + height / 2;
        float lowerY = center.y - height / 2;

        return lowerX <= pos.x && pos.x <= upperX && lowerY <= pos.y && pos.y <= upperY;
    }

    public static String upgradeCard(String from, int shift){
        String rank = from.split(" ")[0];
        String suit = from.split(" ")[1];

        int index = (RANKS.indexOf(rank) + shift) % RANKS.size();

        return RANKS.get(index) + suit;
    }

    public static void drawBubble(int color, PVector pos, PVector size, int padding){
        APP.fill(color);
        APP.rect(pos.x - size.x * 0.5f, pos.y - size.y * 0.5f, size.x, size.y, padding);
    }

    public static HandType getHand(ArrayList<PlayingCard> hand){
        if (hand.size() <= 1) return HIGH_CARD;

        ArrayList<String> ranks = new ArrayList<>(5);
        ArrayList<String> suits = new ArrayList<>(5);

        for (PlayingCard card : hand){
            ranks.add(card.getRank());
            suits.add(card.getSuit());
        }

        Map<String, Long> freqRanks = ranks.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<String, Long> freqSuits = suits.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        boolean isFlush = false;
        for (String suit: freqSuits.keySet()){
            if (freqSuits.get(suit) == 5){
                isFlush = true;
                break;
            }
        }

        int maxCardCount = 1;
        int secondCardCount = 0;
        for (String rank : freqRanks.keySet()){
            int quantity = Math.toIntExact(freqRanks.get(rank));
            if (quantity > maxCardCount){
                secondCardCount = maxCardCount;
                maxCardCount = quantity;
            } else if (quantity > secondCardCount){
                secondCardCount = quantity;
            }
        }

        boolean isHouse = maxCardCount == 3 && secondCardCount == 2;

        boolean isStraight = false;
        AtomicInteger consecutive = new AtomicInteger(0);
        ArrayList<Integer> diff = new ArrayList<>(4);
        ranks.sort(Comparator.comparingInt(RANKS::indexOf));
        for (int i = 0; i < ranks.size() - 1; i++){
            int rank1 = RANKS.indexOf(ranks.get(i));
            int rank2 = RANKS.indexOf(ranks.get(i + 1));

            diff.add(rank2 - rank1);
        }
        diff.forEach(integer -> {
            if (integer == 1) consecutive.set(consecutive.get() + 1);
        });

        // low ace flush i fucking hate you
        if (Objects.equals(ranks.getLast(), "Ace") && consecutive.get() != 4){
            if (!Objects.equals(ranks.get(ranks.size() - 2), "King")){
                isStraight = consecutive.get() == 3;
            }
        } else {
            isStraight = consecutive.get() == 4;
        }


        if (maxCardCount == 5 && isFlush){
            return FLUSH_FIVE;
        } else if (isHouse && isFlush){
            return FLUSH_HOUSE;
        } else if (maxCardCount == 5){
            return FIVE_OF_A_KIND;
        }
        else if (isStraight && isFlush){
            return STRAIGHT_FLUSH;
        } else if (maxCardCount == 4){
            return FOUR_OF_A_KIND;
        } else if (isHouse) {
             return FULL_HOUSE;
        } else if (isFlush){
            return FLUSH;
        } else if (isStraight){
            return STRAIGHT;
        } else if (maxCardCount == 3){
            return THREE_OF_A_KIND;
        } else if (maxCardCount == 2 && secondCardCount == 2){
            return TWO_PAIR;
        } else if (maxCardCount == 2){
            return PAIR;
        }
        return HIGH_CARD;
    }

    public static ArrayList<PlayingCard> activeCards(ArrayList<PlayingCard> hand){
        HandType handType = getHand(hand);
        ArrayList<PlayingCard> outputHand = new ArrayList<>();

        ArrayList<String> ranks = new ArrayList<>(5);
        for (PlayingCard card : hand){
            ranks.add(card.getRank());
        }
        HashMap<String, Long> freqRanks = new HashMap<>(ranks.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));


        switch (handType){
            case FLUSH_FIVE, FLUSH_HOUSE, FIVE_OF_A_KIND, FULL_HOUSE -> {
                return hand;
            }
            case STRAIGHT_FLUSH, STRAIGHT, FLUSH -> {
                // TODO: ADD CHECK FOR FOUR FUCKING FINGERS
                return hand;
            }
            case FOUR_OF_A_KIND, THREE_OF_A_KIND, PAIR -> {
                int mostFreq = 0;
                String mostRank = "";

                for (String rank : freqRanks.keySet()){
                    if (freqRanks.get(rank) > mostFreq){
                        mostFreq = Math.toIntExact(freqRanks.get(rank));
                        mostRank = rank;
                    }
                }
                final String finalMostRank = mostRank;

                hand.forEach(playingCard -> {
                    if (Objects.equals(playingCard.getRank(), finalMostRank)){
                        outputHand.add(playingCard);
                    }
                });
                return outputHand;
            }
            case TWO_PAIR -> {
                String mostRank = "";
                String secondRank = "";

                for (String rank : freqRanks.keySet()){
                    if (freqRanks.get(rank) == 2){
                        if (Objects.equals(mostRank, "")) {
                            mostRank = rank;
                        } else {
                            secondRank = rank;
                        }
                    }
                }
                final String finalMostRank = mostRank;
                final String finalSecondRank = secondRank;

                hand.forEach(playingCard -> {
                    String cardRank = playingCard.getRank();
                    if (Objects.equals(cardRank, finalMostRank) || Objects.equals(cardRank, finalSecondRank)){
                        outputHand.add(playingCard);
                    }
                });
                return outputHand;
            }
            default -> {
                outputHand.addAll(hand);
                outputHand.sort(Comparator.comparingInt(card -> RANKS.indexOf(card.getRank())));
                outputHand.removeIf(playingCard -> outputHand.indexOf(playingCard) != outputHand.size() - 1);
                return outputHand;
            }
        }
    }
}
