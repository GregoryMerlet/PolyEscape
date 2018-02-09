package fr.unice.polytech.polyescape.model;

import org.junit.Before;
import org.junit.Test;

import fr.unice.polytech.polyescape.R;

import static org.junit.Assert.*;

public class EscapeRoomTest {

    private EscapeRoom escapeRoom;

    @Before
    public void init(){
        this.escapeRoom = new EscapeRoom("Nom", R.drawable.plan);
    }

    @Test
    public void getName(){
        assertEquals("Nom", this.escapeRoom.getName());
    }

    @Test
    public void addPuzzle(){
        Puzzle puzzle = new Puzzle("Statement", "Hints", AnswerType.STRING);

        this.escapeRoom.addPuzzle(puzzle);

        assertEquals("Statement", this.escapeRoom.getPuzzle(0).getStatement());
    }

    @Test
    public void isTheLastPuzzle(){
        Puzzle puzzle1 = new Puzzle("Statement", "Hints", AnswerType.STRING);
        Puzzle puzzle2 = new Puzzle("Statement", "Hints", AnswerType.STRING);

        this.escapeRoom.addPuzzle(puzzle1);
        this.escapeRoom.addPuzzle(puzzle2);

        assertFalse(this.escapeRoom.isTheLastPuzzle(0));
        assertTrue(this.escapeRoom.isTheLastPuzzle(1));
    }

}
