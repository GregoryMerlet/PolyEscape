package fr.unice.polytech.polyescape.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents a puzzle which has a description and a response.
 */
public class Puzzle implements Serializable {
    private String statement;
    private String answer;
    private AnswerType answerType;

    Puzzle(String statement, String answer, AnswerType answerType) {
        this.statement = statement;
        this.answer = answer;
        this.answerType = answerType;
    }

    public String getStatement() {
        return statement;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public boolean isTheAnswerRight(String answer){
        return this.answer.equals(answer);
    }

    /**
     * Serializable part.
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        this.statement = inputStream.readUTF();
        this.answer = inputStream.readUTF();
        this.answerType = (AnswerType) inputStream.readObject();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeUTF(this.statement);
        outputStream.writeUTF(this.answer);
        outputStream.writeObject(this.answerType);
    }
}
