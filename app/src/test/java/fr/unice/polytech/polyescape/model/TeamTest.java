package fr.unice.polytech.polyescape.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class TeamTest {
    private Team team;
    private Date date;
    private static final String TEAM_NAME = "teamName";

    @Before
    public void setup() {
        date = new Date();
        List<String> rooms = new ArrayList<>();
        team = new Team(TEAM_NAME, date, rooms);
    }

    @Test
    public void finishRooms() {
        assertFalse(team.isRoomFinished("room"));
        team.roomSuccessful("room");
        assertTrue(team.isRoomFinished("room"));
    }

    @Test
    public void finishRoomsCount() {
        assertEquals(0, team.getFinishedRoomCount());
        team.roomSuccessful("room1");
        assertEquals(1, team.getFinishedRoomCount());
        team.roomSuccessful("room2");
        assertEquals(2, team.getFinishedRoomCount());
    }

    @Test
    public void rightDate() {
        assertEquals(date, team.getStartingDate());
    }

    @Test
    public void rightName() {
        assertEquals(TEAM_NAME, team.getName());
    }
}
