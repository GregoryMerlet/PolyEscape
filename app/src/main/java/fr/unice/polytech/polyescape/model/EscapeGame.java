package fr.unice.polytech.polyescape.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an Escape Game which contains 1 or many rooms.
 */
public class EscapeGame implements Serializable {
    /**
     * Links the tag of a QR code to the corresponding information.
     */
    private Map<String, String> information;

    /**
     * Links the tag of a Beacon to the room which contains the Beacon.
     */
    private Map<String, EscapeRoom> escapeRooms;

    /**
     * Duration of the game in minutes.
     */
    private long duration;

    /**
     * Number of hints available in the Escape Game.
     */
    private int hints;

    EscapeGame(long duration, int hints) {
        escapeRooms = new HashMap<>();
        information = new HashMap<>();
        this.duration = duration;
        this.hints = hints;
    }

    void addEscapeRoom(String beaconTag, EscapeRoom room) {
        escapeRooms.put(beaconTag, room);
    }

    public boolean isARoom(String beaconTag){
        return escapeRooms.containsKey(beaconTag);
    }

    public EscapeRoom getEscapeRoom(String beaconTag) {
        return escapeRooms.get(beaconTag);
    }

    public int getEscapeRoomNumber(){
        return this.escapeRooms.size();
    }

    public List<String> getRooms() {
        return new ArrayList<>(escapeRooms.keySet());
    }

    void addInformation(String qrTag, String info) {
        information.put(qrTag, info);
    }

    public String getInformation(String qrTag) {
        return information.get(qrTag);
    }

    public boolean isAnInformation(String qrTag) {
        return information.containsKey(qrTag);
    }

    public int getHints() {
        return hints;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * Serializable part.
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        this.information = (Map<String, String>) inputStream.readObject();
        this.escapeRooms = (Map<String, EscapeRoom>) inputStream.readObject();
        this.duration = inputStream.readLong();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(this.information);
        outputStream.writeObject(this.escapeRooms);
        outputStream.writeLong(this.duration);
    }
}
