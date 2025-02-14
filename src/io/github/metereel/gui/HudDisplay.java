package io.github.metereel.gui;

import io.github.metereel.Game;
import io.github.metereel.card.*;
import org.apfloat.Apfloat;
import processing.core.PVector;


import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.CARD_WIDTH;
import static io.github.metereel.Game.inBlind;
import static io.github.metereel.gui.ScorerHelper.*;
import static io.github.metereel.Helper.*;
import static io.github.metereel.Main.APP;


public class HudDisplay {
    public static float HAND_Y = 0.75f * APP.height;
    public static float HAND_BOX_TOP = HAND_Y - CARD_HEIGHT * 2 / 3.0f;
    public static int RED = APP.color(235, 50, 50);
    public static int BLUE = APP.color(0, 148, 255);
    private Deck currentDeck;
    private int currentBlind = 0;
    private int ante = 1;


    private final Button discardButton;
    private final Button playHandButton;
    private final Button[] blindButtons;
    public static Card hoveringCard;
    private boolean isDragging = false;

    private HandType playingHand = HandType.HIGH_CARD;
    private Scorer scorer;


    public HudDisplay(){
        discardButton = new Button(
                .72f * APP.width,
                .855f * APP.height,
                CARD_WIDTH * 2.0f,
                CARD_HEIGHT / 3.0f,
                RED,
                APP.color(94, 28, 28),
                new Text("Discard", APP.color(255), 15)
        );
        playHandButton = new Button(
                .42f * APP.width,
                .855f * APP.height,
                CARD_WIDTH * 2.0f,
                CARD_HEIGHT / 3.0f,
                BLUE,
                APP.color(0, 83, 163),
                new Text("Play Hand", APP.color(255), 15)
        );

        blindButtons = new Button[]{
                new Button(0, 0, 175, 75, APP.color(225, 135, 40), APP.color(90), new Text("%s")),
                new Button(0, 0, 175, 75, APP.color(225, 135, 40), APP.color(90), new Text("%s")),
                new Button(0, 0, 175, 75, APP.color(225, 135, 40), APP.color(90), new Text("%s"))
        };
    }
    
    public void initialize(){
        currentDeck = new Deck();

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

        PVector handTypePos = new PVector(.15f * APP.width, .22f * APP.height);
        PVector handTypeSize = new PVector(250, 150);
        drawScorer(handTypePos, handTypeSize);

        drawPlayStats();

        if (inBlind) {
            drawButtons(handTypePos);

            scorer.display(playingHand);

            drawPlayedHand();
            drawHand();

            currentDeck.displayDiscard();
        } else {
            scorer.display(playingHand, true);
            drawBlinds();
        }
        currentDeck.displayDeck();
    }

    private void drawBlinds() {

        for (int i = 0; i < 3; i++) {
            PVector center = new PVector(APP.width * 0.38f + i * APP.width * 0.2f, APP.height * 0.75f);
            PVector size = new PVector(APP.width * 0.15f, APP.height * (currentBlind == i ? 0.9f : 0.8f));

            drawBubble(APP.color(75), center, size, 50);

            Button blindButton = blindButtons[i];
            blindButton.setPos(center.x, center.y - APP.width * (currentBlind == i ? 0.23f : 0.2f));
            if (currentBlind == i){
                blindButton.addArg("Select");
            } else {
                blindButton.addArg("Upcoming");
                blindButton.setPressed(true);
            }
            blindButton.display();
        }
    }

    private void drawPlayStats() {
        float statsY = APP.width * 0.25f;
        float statsX = APP.width * 0.15f;

        PVector outerSize = new PVector(120, 75);
        PVector innerSize = new PVector(110, 40);

        // Hands
        drawBubble(APP.color(50), new PVector(statsX - APP.width * 0.045f, statsY), outerSize, 5);
        drawBubble(APP.color(75), new PVector(statsX - APP.width * 0.045f, statsY + 10), innerSize, 5);

        APP.fill(APP.color(255));
        APP.textSize(20);
        APP.text("Hands", statsX - APP.width * 0.045f, statsY - 25);
        APP.fill(BLUE);
        APP.textSize(25);
        APP.text(currentDeck.getHands(), statsX - APP.width * 0.045f, statsY + 10);

        // Discards
        drawBubble(APP.color(50), new PVector(statsX + APP.width * 0.045f, statsY), outerSize, 5);
        drawBubble(APP.color(75), new PVector(statsX + APP.width * 0.045f, statsY + 10), innerSize, 5);

        APP.fill(APP.color(255));
        APP.textSize(20);
        APP.text("Discards", statsX + APP.width * 0.045f, statsY - 25);
        APP.fill(RED);
        APP.textSize(25);
        APP.text(currentDeck.getDiscards(), statsX + APP.width * 0.045f, statsY + 10);
    }

    private void drawButtons(PVector handTypePos) {
        Text handType = new Text(playingHand.toString(), APP.color(255), 15);
        boolean hasSelected = currentDeck.getSelectedAmt() > 0;

        if (!hasSelected) {
            discardButton.setPressed(true);
            playHandButton.setPressed(true);

            if (currentlyPlayingHand) {
                handType.display(handTypePos.add(0, -APP.width * 0.02f), 0.0f);
            }
        } else {
            discardButton.setPressed(currentDeck.getDiscards() <= 0);
            playHandButton.setPressed(currentDeck.getHands() <= 0);
            handType.display(handTypePos.add(0, -APP.width * 0.02f), 0.0f);
        }

        discardButton.display();
        playHandButton.display();
    }

    private void drawScorer(PVector handTypePos, PVector handTypeSize){
        APP.fill(APP.color(75));
        APP.rect(APP.width * 0.05f, 0,
                APP.width * 0.2f,
                APP.height);
        APP.fill(APP.color(255));
        APP.textSize(15);
        APP.text(STR."(\{APP.mouseX}, \{APP.mouseY}) \{Math.round(APP.frameRate)} FPS",
                APP.width / 2.0f,
                30);
        drawBubble(APP.color(50), handTypePos, handTypeSize, 5);
        scorer.drawBubbles();
    }

    private void drawPlayedHand() {
        if (playedHand != null){
            playedHand.forEach(Card::display);
        }
    }

    public void drawHand(){
        currentDeck.displayHand();

        if (hoveringCard != null){
            hoveringCard.displayName();
            Text cardName = hoveringCard.getName();
            cardName.setColor(APP.color(255));
            cardName.display(new PVector((discardButton.getPos().x + playHandButton.getPos().x) / 2.0f, .845f * APP.height), 0f);
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
        tryPressButtons();

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
        if (inBlind) {
            boolean isHoveringDiscard = discardButton.checkPressed();
            boolean isHoveringPlay = playHandButton.checkPressed();
            boolean hasSelected = currentDeck.getSelectedAmt() > 0;

            boolean hasDiscards = currentDeck.getDiscards() > 0;
            boolean hasHands = currentDeck.getHands() > 0;

            discardButton.setPressed(isHoveringDiscard && hasSelected && hasDiscards);
            playHandButton.setPressed(isHoveringPlay && hasSelected && hasHands);
        } else {
            Button blindButton = blindButtons[currentBlind];
            blindButton.setPressed(blindButton.checkPressed());
        }
    }

    private void tryPressButtons() {
        if (inBlind) {
            boolean hasSelected = currentDeck.getSelectedAmt() > 0;
            if (discardButton.checkClicked() && hasSelected) {
                currentDeck.discardSelected();
            } else if (playHandButton.checkClicked() && hasSelected) {
                startScoring(currentDeck.playHand());
            }
        } else {
            Button blindButton = blindButtons[currentBlind];
            if (blindButton.checkClicked()){
                inBlind = true;
                currentDeck.fillHand();
            }
        }
    }

    public void gameTick() {
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
        if (playedHand != null){
            ScorerHelper.scoreHand();
        }
    }


    public Scorer getScorer() {
        return this.scorer;
    }
}
