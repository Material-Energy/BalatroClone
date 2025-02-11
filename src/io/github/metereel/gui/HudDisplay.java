package io.github.metereel.gui;

import io.github.metereel.Timer;
import io.github.metereel.card.*;
import org.apfloat.Apfloat;
import processing.core.PVector;

import java.util.ArrayList;

import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.CARD_WIDTH;
import static io.github.metereel.Helper.*;
import static io.github.metereel.Main.APP;
import static processing.core.PApplet.printArray;


public class HudDisplay {
    public static float HAND_Y = 0.75f * APP.height;
    public static float HAND_BOX_TOP = HAND_Y - CARD_HEIGHT * 2 / 3.0f;
    private final Deck currentDeck;

    private final Button discardButton;
    private final Button playHandButton;
    public static Card hoveringCard;
    private boolean isDragging = false;

    private HandType playingHand = HandType.HIGH_CARD;
    private Scorer scorer;

    private ArrayList<PlayingCard> playedHand;
    private boolean currentlyPlayingHand;
    private final Timer playingTimer = new Timer();

    public HudDisplay(){
        currentDeck = new Deck();

        discardButton = new Button(
                .69f * APP.width,
                .845f * APP.height,
                CARD_WIDTH * 2.0f,
                CARD_HEIGHT / 3.0f,
                APP.color(172, 50, 50),
                APP.color(94, 28, 28),
                new Text("Discard", APP.color(255), 15)
        );
        playHandButton = new Button(
                .21f * APP.width,
                .845f * APP.height,
                CARD_WIDTH * 2.0f,
                CARD_HEIGHT / 3.0f,
                APP.color(0, 148, 255),
                APP.color(0, 83, 163),
                new Text("Play Hand", APP.color(255), 15)
        );
    }
    
    public void initialize(){
        currentDeck.initializeTextures();
        currentDeck.setPlayingDeck();
        loadLevels();
        scorer = new Scorer(new Apfloat("300"));
    }

    public Deck getDeck() {
        return this.currentDeck;
    }

    public void display(){
        APP.background(APP.color(75, 105, 47));

        APP.fill(APP.color(255));
        APP.text(STR."(\{APP.mouseX}, \{APP.mouseY}) \{Math.round(APP.frameRate)} FPS",
                APP.width / 2.0f,
                30);
        drawPlayedHand();
        drawHand();

        Text handType = new Text(playingHand.toString(), APP.color(255), 15);
        PVector handTypePos = new PVector(.15f * APP.width, .18f * APP.height);
        PVector handTypeSize = new PVector(200, 50);
        boolean hasSelected = currentDeck.getSelectedAmt() > 0;

        drawBubble(APP.color(50), handTypePos, handTypeSize, 5);
        if (!hasSelected) {
            discardButton.setPressed(true);
            playHandButton.setPressed(true);

            if (currentlyPlayingHand){
                handType.display(handTypePos, 0.0f);
            }
        } else {
            handType.display(handTypePos, 0.0f);
        }
        scorer.display(playingHand);

        discardButton.display();
        playHandButton.display();

        currentDeck.displayDeck();
        currentDeck.displayDiscard();
    }

    private void drawPlayedHand() {
        if (this.playedHand != null){
            this.playedHand.forEach(Card::display);
        }
    }

    public void drawHand(){
        APP.fill(APP.color(50, 60, 57));
        float startX = currentDeck.calculateHandPos(0, currentDeck.getHandSize());
        float endX = currentDeck.calculateHandPos(currentDeck.getHandSize() - 1, currentDeck.getHandSize());
        APP.rect(startX - CARD_WIDTH, HAND_BOX_TOP, endX - startX + 2 * CARD_WIDTH, CARD_HEIGHT * 4 / 3.0f, 10.0f);

        currentDeck.displayHand();

        if (hoveringCard != null){
            Text cardName = hoveringCard.getName();
            cardName.setColor(APP.color(255));
            cardName.display(new PVector(.36f * APP.width, .845f * APP.height), 0f);
        }
    }

    public void onClick() {
        if (currentlyPlayingHand) return;
        checkButtons();

        if (hoveringCard == null) return;
        if (hoveringCard instanceof PlayingCard playingCard) {
            playingHand = currentDeck.selectPlayingCard(playingCard);
        }
    }

    public void onDrag() {
        if (currentlyPlayingHand) return;

        this.isDragging = true;
        if (hoveringCard == null) return;
        hoveringCard.setState(CardState.DRAGGING);
        hoveringCard.setPos(APP.mouseX, APP.mouseY);

        if (hoveringCard instanceof PlayingCard playingCard) currentDeck.dragPlayingCard(playingCard);
    }

    public void onRelease() {
        checkButtons();
        boolean hasSelected = currentDeck.getSelectedAmt() > 0;
        if (discardButton.checkClicked() && hasSelected){
            currentDeck.discardSelected();
        } else if (playHandButton.checkClicked() && hasSelected){
            animateScoring(currentDeck.playHand());
        }

        this.isDragging = false;
        if (hoveringCard == null) return;
        if (hoveringCard instanceof PlayingCard playingCard) currentDeck.stopDragging(playingCard);
    }

    public void onPressed() {
        if (currentlyPlayingHand) return;
        this.isDragging = true;
        checkButtons();

        if (hoveringCard == null) return;
        hoveringCard.setState(CardState.DRAGGING);
        hoveringCard.setPos(APP.mouseX, APP.mouseY);

        if (hoveringCard instanceof PlayingCard playingCard) currentDeck.dragPlayingCard(playingCard);
    }

    public void onMoved() {
        checkButtons();
    }

    private void checkButtons(){
        boolean isHoveringDiscard = discardButton.checkPressed();
        boolean isHoveringPlay = playHandButton.checkPressed();
        boolean hasSelected = currentDeck.getSelectedAmt() > 0;

        discardButton.setPressed(isHoveringDiscard && hasSelected);
        playHandButton.setPressed(isHoveringPlay && hasSelected);
    }

    public void tick() {
        if (!isDragging && hoveringCard != null && !hoveringCard.isHovering()){
            hoveringCard = null;
        }


        currentDeck.getCurrentHand().forEach(card -> {
            card.tick();
            if (card.isHovering() && hoveringCard == null){
                hoveringCard = card;
            }
        });


        currentDeck.tick();
        if (this.playedHand != null){
            this.playedHand.forEach(Card::tick);

            playingTimer.incrementTimer();
            if (playingTimer.getTimeWithCycle(20) == 0) {
                ArrayList<PlayingCard> temp = new ArrayList<>(playedHand);
                temp.removeIf(playingCard -> !playingCard.isSelected());

                if (temp.getLast().hasTriggered()){
                    this.currentlyPlayingHand = false;
                    this.currentDeck.discard(this.playedHand);
                    this.playedHand = null;
                } else {
                    for (PlayingCard card : playedHand) {
                        card.setIgnore(false);
                        if (card.hasTriggered() || !card.isSelected()) continue;
                        card.tryTrigger(scorer);
                        break;
                    }
                }
            }
        }
    }

    private void animateScoring(ArrayList<PlayingCard> playingCards) {
        this.playedHand = playingCards;
        this.currentlyPlayingHand = true;
        playingTimer.resetTimer();

        ArrayList<PlayingCard> activeCards = activeCards(playingCards);
        printArray(activeCards);

        for (PlayingCard card : playedHand){
            card.setSelected(false);
            card.setState(CardState.DRAGGING);
            float x = (playingCards.indexOf(card) - playingCards.size() * 0.5f) * APP.width * 0.1f + APP.width * 0.5f;
            card.setTargetPos(x, APP.width * .5f, 10);
            if (activeCards.contains(card)){
                card.setSelected(true);
                card.resetTrigger();
            }
        }
    }

    public boolean currentlyPlaying() {
        return this.currentlyPlayingHand;
    }
}
