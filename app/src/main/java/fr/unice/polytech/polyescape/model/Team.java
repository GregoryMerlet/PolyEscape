package fr.unice.polytech.polyescape.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the players of the game.
 */
public class Team implements Serializable {
    private String name;
    private List<String> successfulRooms;
    private Date startingDate;
    private Map<String, List<String>> hints;
    private int hintsUsed;

    public Team(String name, Date startingDate, List<String> rooms) {
        this.name = name;
        this.startingDate = startingDate;
        this.successfulRooms = new ArrayList<>();
        this.hints = new HashMap<>();
        for(String room : rooms)
            this.hints.put(room, new ArrayList<String>());
        this.hintsUsed = 0;
    }

    public void roomSuccessful(String roomName) {
        successfulRooms.add(roomName);
    }

    public int getFinishedRoomCount(){
        return this.successfulRooms.size();
    }

    public String getName() {
        return name;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public List<String> getHints(String roomTag){
        return this.hints.get(roomTag);
    }

    public void addHint(String roomTag, String hint){
        this.hints.get(roomTag).add(hint);
    }

    public List<String> getSuccessfulRooms() {
        return successfulRooms;
    }

    public boolean isRoomFinished(String roomTag){
        return successfulRooms.contains(roomTag);
    }

    public void useHint() {
        hintsUsed++;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    /**
     * Serializable part.
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        this.name = inputStream.readUTF();
        this.successfulRooms = (List<String>) inputStream.readObject();
        this.startingDate = (Date) inputStream.readObject();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeUTF(this.name);
        outputStream.writeObject(this.successfulRooms);
        outputStream.writeObject(this.startingDate);
    }
}
