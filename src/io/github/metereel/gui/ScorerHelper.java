package io.github.metereel.gui;

import io.github.metereel.Timer;
import io.github.metereel.api.ScorePopup;
import io.github.metereel.card.Card;
import io.github.metereel.card.CardState;
import io.github.metereel.card.PlayingCard;
import io.github.metereel.card.jokers.JokerCard;
import org.apfloat.Apfloat;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.metereel.Constants.HUD;
import static io.github.metereel.Game.jokers;
import static io.github.metereel.Helper.activeCards;
import static io.github.metereel.Javatro.APP;
import static io.github.metereel.card.Deck.AXIS;
import static processing.core.PApplet.println;

public class ScorerHelper {

    public static Apfloat blindMult = new Apfloat("1.6");
    private static int CYCLE_TIME = 40;
    public static ArrayList<PlayingCard> playedHand = new ArrayList<>();
    private static ArrayList<PlayingCard> activeHand = new ArrayList<>();
    public static boolean currentlyPlayingHand;
    public static final Timer playingTimer = new Timer();

    private static ScorePopup scoreDisplay;
    private static boolean willEnd;

    private static Stage playedStage = Stage.ON_PLAY_JOKER;
    private static PlayingCard lastTriggeredPlayingCard;
    private static Card lastTriggeredCard;

    public static void scoreHand() {
        playedHand.forEach(Card::tick);
        if (scoreDisplay != null) {
            scoreDisplay.display();
        }

        playingTimer.incrementTimer();
        
        if (playingTimer.getTimeWithCycle(CYCLE_TIME) == 0 && playedStage != Stage.FINISHED_SCORING){
            playedHand.forEach(card -> card.setTriggered(false));

            if (playedStage != Stage.FINISHED_SCORING) {
                boolean triggered = false;
                while (!triggered) {
                    switch (playedStage) {
                        case ON_PLAY_JOKER -> triggered = onPlayJoker();
                        case TRIGGER_CARD -> triggered = triggerCard();
                        case CARD_JOKER -> triggered = cardJoker();
                        case POST_TRIGGER_JOKER -> triggered = postTriggerJoker();
                    }
                }
            } else {
                playedHand.forEach(playingCard -> playingCard.setSelected(false));
                jokers.forEach(jokerCard -> jokerCard.setSelected(false));

                HUD.getDeck().discard(playedHand);
                HUD.getScorer().updateCurrentScore();
                currentlyPlayingHand = false;
                playingTimer.resetTimer();


                if (HUD.getScorer().hasWon()){
                    HUD.getDeck().clearHand();
                } else {
                    playedHand.clear();
                }
            }

            if (lastTriggeredCard != null){
                float x = lastTriggeredCard.getPos().x;
                float y = APP.width * 0.15f;

                String triggerText = lastTriggeredCard.getTriggerText();
                System.out.println("<" + triggerText + ">");
                if (!Objects.equals(triggerText, "")) {
                    scoreDisplay = new ScorePopup(new PVector(x, y), 0.0f, triggerText);
                }
            }
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME) == CYCLE_TIME / 2) {
            playedHand.forEach(playingCard -> playingCard.setSelected(false));
            jokers.forEach(jokerCard -> jokerCard.setSelected(false));
            scoreDisplay = null;
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME * 4) == 0 && playedStage == Stage.FINISHED_SCORING){
            if (HUD.getScorer().hasWon()){
                playedHand.clear();
                HUD.blindWon();
            }
        }
    }

    private static boolean postTriggerJoker() {
        boolean triggeredJoker = false;
        for (JokerCard jokerCard : jokers.getStored()){
            while (!jokerCard.finishedTriggering() && jokerCard.getCurrentTrigger().hasNoEffect()){
                jokerCard.skipTrigger();
            }
            if (jokerCard.finishedTriggering()) {
                if (jokerCard.cannotTrigger(playedStage)) continue;

                jokerCard.postTrigger();
                lastTriggeredCard = jokerCard;
                triggeredJoker = true;
                jokerCard.setTriggered(true);

                break;
            }

            jokerCard.triggerNext();
            lastTriggeredCard = jokerCard;
            triggeredJoker = true;
            jokerCard.setTriggered(true);
            break;
        }

        if (!triggeredJoker) {
            playedStage = Stage.FINISHED_SCORING;
            return false;
        }

        return true;
    }

    private static boolean cardJoker() {
        boolean triggeredJoker = false;
        for (JokerCard jokerCard : jokers.getStored()){
            if (jokerCard.cannotTrigger(playedStage)) continue;
            System.out.print(lastTriggeredPlayingCard.getTrigger().getTriggersLeft() + " ");

            if (!jokerCard.onCardTrigger(lastTriggeredPlayingCard, activeHand)) continue;
            System.out.println(lastTriggeredPlayingCard.getTrigger().getTriggersLeft());

            lastTriggeredCard = jokerCard;
            triggeredJoker = true;
            jokerCard.setTriggered(true);
            break;
        }
        if (!triggeredJoker) {
             if (activeHand.getLast() == lastTriggeredCard){
                playedStage = Stage.POST_TRIGGER_JOKER;
             } else {
                playedStage = Stage.TRIGGER_CARD;
             }
             return false;
        } else if (!lastTriggeredPlayingCard.finishedTriggering()){
            playedStage = Stage.TRIGGER_CARD;
        }

        return true;
    }

    private static boolean triggerCard() {
        boolean finishedTriggering = false;
        for (PlayingCard card : activeHand){
            while (!card.finishedTriggering() && card.getCurrentTrigger().hasNoEffect()){
                card.skipTrigger();
            }
            if (card.finishedTriggering()) continue;

            System.out.println(card);

            card.triggerNext();
            card.setTriggered(true);
            if (card.finishedTriggering()){
                finishedTriggering = true;
                lastTriggeredPlayingCard = card;
                lastTriggeredCard = card;
            }
            break;
        }
        if (finishedTriggering) playedStage = Stage.CARD_JOKER;

        return true;
    }

    private static boolean onPlayJoker() {
        boolean triggeredJoker = false;
        for (JokerCard jokerCard : jokers.getStored()){
            if (jokerCard.cannotTrigger(playedStage)) continue;

            jokerCard.onHandPlayed();
            lastTriggeredCard = jokerCard;
            triggeredJoker = true;
            jokerCard.setTriggered(true);
            break;
        }

        if (!triggeredJoker) {
            playedStage = Stage.TRIGGER_CARD;
            return false;
        }

        return true;
    }

    public static void startScoring(ArrayList<PlayingCard> playingCards) {
        reset();
        playedHand.addAll(playingCards);

        activeHand = activeCards(playingCards);
        for (PlayingCard card : playedHand){
            card.setSelected(false);
            card.setState(CardState.PLAYING);

            float x = (playingCards.indexOf(card) - playingCards.size() * 0.5f) * APP.width * 0.1f + AXIS;
            card.setTargetPos(x, APP.height * .4f, 25, true);

            if (activeHand.contains(card)){
                card.setSelected(true);
                card.resetTrigger();
            }
        }
    }

    private static void reset(){
        playedHand.clear();
        currentlyPlayingHand = true;
        lastTriggeredCard = null;
        lastTriggeredPlayingCard = null;
        playingTimer.resetTimer();
        CYCLE_TIME = 40;

        playedStage = Stage.ON_PLAY_JOKER;
        jokers.forEach(JokerCard::resetTrigger);
    }
    
    public enum Stage{
        ON_PLAY_JOKER,
        TRIGGER_CARD,
        CARD_JOKER,
        POST_TRIGGER_JOKER,
        ROUND_END_JOKER,
        FINISHED_SCORING
    }
}
