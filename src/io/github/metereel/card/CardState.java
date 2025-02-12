package io.github.metereel.card;

public enum CardState {
    // When the card goes to the discard pile
    DISCARDING,
    // When the card is being dragged by the mouse
    DRAGGING,
    // When the card tries to be dragged to a new position in hand
    SWAPPING,
    // When the card is moving with animation
    DRAWING,
    PLAYING, // When the card is scoring
    IDLE // When the card is moving without animation
}
