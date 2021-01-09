package fr.ubx.poo.game;

/**
 * Signals that a wanted position is not found
 */
public class PositionNotFoundException extends Exception {
    public PositionNotFoundException(String message) {
        super(message);
    }
}
