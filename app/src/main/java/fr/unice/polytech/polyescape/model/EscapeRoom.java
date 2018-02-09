package fr.unice.polytech.polyescape.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a room of an Escape Game.
 * Contains 1 or many puzzles.
 */
public class EscapeRoom implements Serializable {
    private String roomName;
    private List<Puzzle> puzzles;
    private int mapPartDrawableID;

    EscapeRoom(String name, int mapPartDrawableID) {
        this.roomName = name;
        this.puzzles = new ArrayList<>();
        this.mapPartDrawableID = mapPartDrawableID;
    }

    void addPuzzle(Puzzle puzzle) {
        puzzles.add(puzzle);
    }

    public Puzzle getPuzzle(int index) {
        return puzzles.get(index);
    }

    public String getName() {
        return roomName;
    }

    public int getMapPartDrawableID() {
        return mapPartDrawableID;
    }

    public boolean isTheLastPuzzle(int index){
        return index == this.puzzles.size()-1;
    }

    /**
     * Serializable part.
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        this.roomName = inputStream.readUTF();
        this.puzzles = (List<Puzzle>) inputStream.readObject();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeUTF(this.roomName);
        outputStream.writeObject(this.puzzles);
    }
}
