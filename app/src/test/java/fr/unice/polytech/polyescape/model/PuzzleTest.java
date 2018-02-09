package fr.unice.polytech.polyescape.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PuzzleTest {

    private Puzzle puzzle;

    @Before
    public void init(){
        this.puzzle = new Puzzle("Statement", "Hints", AnswerType.STRING);
    }

    @Test
    public void checkAnswer(){
        assertFalse(this.puzzle.isTheAnswerRight("WrongAnswer"));
        assertTrue(this.puzzle.isTheAnswerRight("Hints"));
    }

    @Test
    public void getStatement(){
        assertEquals("Statement", this.puzzle.getStatement());
    }

    @Test
    public void getAnswer(){
        assertEquals(AnswerType.STRING, this.puzzle.getAnswerType());
    }
}
