package fr.unice.polytech.polyescape.model;

import org.junit.Before;
import org.junit.Test;

import fr.unice.polytech.polyescape.R;

import static org.junit.Assert.*;

public class EscapeGameTest {
    private EscapeGame eg;

    @Before
    public void setup() {
        eg = new EscapeGame(60, 2);
    }

    @Test
    public void managementOfRooms() {
        assertEquals(0, eg.getEscapeRoomNumber());

        eg.addEscapeRoom("tag", new EscapeRoom("room1", R.drawable.math_room));

        assertTrue(eg.isARoom("tag"));
        assertFalse(eg.isARoom("noTag"));

        assertNotNull(eg.getEscapeRoom("tag"));
        assertNull(eg.getEscapeRoom("noTag"));

        assertEquals(1, eg.getEscapeRoomNumber());
    }

    @Test
    public void qrCodes() {
        eg.addInformation("tag", "information");

        assertEquals("information", eg.getInformation("tag"));
        assertNull(eg.getInformation("noTag"));
    }

    @Test
    public void time() {
        assertEquals(60, eg.getDuration());
    }
}
