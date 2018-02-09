package fr.unice.polytech.polyescape.model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Observer;

/**
 * Connects to the server.
 * Creates a socket and use it to communicate.
 */
public class Client extends AsyncTask<Void, Void, Void> {
    private Hints hints;
    private boolean connexionDone;
    private Socket socket = null;

    /**
     * Creates a Client object thanks to the IP address of the server and its port.
     * Throws an Exception to MainActivity if the connexion fail.
     */
    Client(String addr, int port) throws Exception {
        hints = new Hints();
        socket = new Socket(addr, port);
        connexionDone = true;
    }

    /**
     * Waits for the answers and stores it in the string response.
     */
    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                parseResponse(byteArrayOutputStream.toString("UTF-8"));
                byteArrayOutputStream.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tries to send a message to the server.
     */
    private void sendMessage(String message) {
        Log.d("MESSAGE", message);
        try {
            if (socket != null) {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Formalizes the requests to the server.
     */
    public void sendStartGame(String teamName) {
        sendMessage("STARTGAME\t" + teamName + "\t" + currentDate());
    }

    public void sendBeaconDetected(String beaconTag) {
        sendMessage("ZONE\t" + beaconTag + "\t" + currentDate());
    }

    public void sendMovingAwayBeacon() {
        sendMessage("ENDZONE\t" + currentDate());
    }

    public void sendAskHelp(String question) {
        sendMessage("HINT\t" + question + "\t" + currentDate());
    }

    public void sendPuzzleSolved(int position) {
        sendMessage("PUZZLESUCCESS\t" + position + "\t" + currentDate());
    }

    public void sendRoomSolved(String roomTag) {
        sendMessage("ROOMSUCCESS\t" + roomTag + "\t" + currentDate());
    }

    public void sendEscapeGameSolved() {
        sendMessage("ESCAPESUCCESS\t" + currentDate());
    }

    public void sendEscapeGameFailed() {
        sendMessage("ESCAPEFAIL");
    }

    public void sendQRCodeFound(String qrTag) {
        sendMessage("FLASHQR\t" + qrTag + "\t" + currentDate());
    }

    public void sendWrongAnswer(String answer) {
        sendMessage("WRONGANSWER\t" + answer + "\t" + currentDate());
    }

    /**
     * Catches the hints marked with the tag NEWHINT.
     */
    private void parseResponse(String firstResponse) {
        String[] data = firstResponse.split("\t");
        if ("NEWHINT".equals(data[0])) {
            hints.newHint(data[1]);
            Log.d("NEWHINT", data[1]);
        }
    }

    /**
     * Close the socket.
     */
    public void close() {
        if (socket != null) {
            try {
                socket.close();
                connexionDone = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String currentDate() {
        return DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }

    public boolean isConnexionDone() {
        return connexionDone;
    }

    public void addObserverToHints(Observer o) {
        hints.addObserver(o);
    }

    public void deleteObservableToHints(Observer o) {
        hints.deleteObserver(o);
    }
}