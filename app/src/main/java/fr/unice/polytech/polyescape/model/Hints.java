package fr.unice.polytech.polyescape.model;

import java.util.Observable;

/**
 * Is an observable String.
 * Allows sharing a hint (given by the server) from the Client to the AnswerFragment
 */
class Hints extends Observable {
    void newHint(String hint) {
        setChanged();
        notifyObservers(hint);
    }
}
