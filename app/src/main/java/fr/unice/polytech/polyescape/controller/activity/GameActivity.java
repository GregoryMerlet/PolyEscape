package fr.unice.polytech.polyescape.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.controller.SoundPlayer;
import fr.unice.polytech.polyescape.controller.fragment.AnswerFragment;
import fr.unice.polytech.polyescape.controller.fragment.MapFragment;
import fr.unice.polytech.polyescape.model.ClientApplication;
import fr.unice.polytech.polyescape.model.EscapeFactory;
import fr.unice.polytech.polyescape.model.EscapeGame;
import fr.unice.polytech.polyescape.model.Team;

/**
 * Heart of the system, displays the map, the puzzles and the data of the team currently playing.
 */
public class GameActivity extends Activity implements BeaconConsumer {
    private EscapeGame escapeGame;
    private Team team;
    private ImageView roomCount;
    private TextView timeCountTextView;
    private ProgressBar timeCountProgressBar;
    private BeaconManager beaconManager;
    private String actualRoom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        /* Beacon management */
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
               setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        String teamName = getIntent().getStringExtra("teamName");

        this.escapeGame = EscapeFactory.makeEscapeGame();
        this.team = new Team(teamName, Calendar.getInstance().getTime(), this.escapeGame.getRooms());

        /* Graphics objects */
        TextView teamNameTextView = (TextView) findViewById(R.id.teamName);
        roomCount = (ImageView) findViewById(R.id.roomCount);
        timeCountTextView = (TextView) findViewById(R.id.timeCount);
        timeCountProgressBar = (ProgressBar) findViewById(R.id.timeCount_progress_bar);

        teamNameTextView.setText(getString(R.string.teamText) + " " + teamName);
        timeCountProgressBar.setMax((int) this.escapeGame.getDuration() * 60);
        timeCountProgressBar.setProgress(0);
        roomCount.setBackgroundResource(R.drawable.suivi_none);

        /* Time management */
        Date endingDate = new Date();
        endingDate.setTime(this.team.getStartingDate().getTime() + this.escapeGame.getDuration() * 60000);
        final long remainingTime = endingDate.getTime() - this.team.getStartingDate().getTime();

        new CountDownTimer(remainingTime, 1000) {
            public void onTick(long millisUntilFinished) {
                long minutesUntilFinished = millisUntilFinished / 60000;
                long secondsUntilFinished = (millisUntilFinished - minutesUntilFinished * 60000) / 1000;
                String secondsUntilFinishedFormatted = String.valueOf(secondsUntilFinished);
                if(secondsUntilFinishedFormatted.length() == 1)
                    secondsUntilFinishedFormatted = "0" + secondsUntilFinishedFormatted;

                timeCountTextView.setText(minutesUntilFinished + ":" + secondsUntilFinishedFormatted);
                timeCountProgressBar.setProgress((int) millisUntilFinished / 1000);
            }

            public void onFinish() {
                ClientApplication.client.sendEscapeGameFailed();
                SoundPlayer.playGameOver();
                beaconManager.unbind(GameActivity.this);

                Intent intent = new Intent(GameActivity.this, EndActivity.class);
                intent.putExtra("win", false);
                intent.putExtra("roomDone", team.getFinishedRoomCount());
                intent.putExtra("roomToDo", escapeGame.getEscapeRoomNumber());
                startActivity(intent);
            }
        }.start();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, MapFragment.newInstance(this.escapeGame, this.team)).commit();
    }

    /**
     * Invoked when the team succeed the puzzles of a room.
     * Displays the EndActivity if it's the last room.
     * Sends also the info to the server.
     */
    public void finishedRoom(String roomTag) {
        team.roomSuccessful(roomTag);
        switch(team.getFinishedRoomCount()){
            case 0 :
                roomCount.setBackgroundResource(R.drawable.suivi_none);
                break;
            case 1 :
                roomCount.setBackgroundResource(R.drawable.suivi_one);
                break;
            case 2 :
                roomCount.setBackgroundResource(R.drawable.suivi_two);
                break;
            default :
                roomCount.setBackgroundResource(R.drawable.suivi_two);
                break;
        }
        if (team.getFinishedRoomCount() == escapeGame.getEscapeRoomNumber()) {
            ClientApplication.client.sendEscapeGameSolved();
            SoundPlayer.playVictoryGame();
            beaconManager.unbind(this);

            Intent intent = new Intent(GameActivity.this, EndActivity.class);
            intent.putExtra("win", true);
            intent.putExtra("roomDone", team.getFinishedRoomCount());
            intent.putExtra("remainingMinutes", Integer.parseInt(timeCountTextView.getText().toString().split(":")[0]));
            intent.putExtra("remainingSeconds", Integer.parseInt(timeCountTextView.getText().toString().split(":")[1]));
            intent.putExtra("totalTime", escapeGame.getDuration());

            startActivity(intent);
        } else {
            ClientApplication.client.sendRoomSolved(roomTag);
            SoundPlayer.playSongVictory();
        }
    }

    /**
     * Catches the Back event.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    /**
     * Stops following the beacons when the game is finished.
     */
    @Override
    protected void onDestroy() {
        beaconManager.unbind(this);
        ClientApplication.client.close();
        super.onDestroy();
    }

    /**
     * Reacts to the change of beacon / room.
     */
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() != 0) {
                    Beacon nearestBeacon = getNearestBeacon(beacons);
                    FragmentManager fragmentManager = getFragmentManager();
                    if (nearestBeacon.getDistance() < 3 && GameActivity.this.escapeGame.isARoom(nearestBeacon.getBluetoothAddress()) && !GameActivity.this.team.isRoomFinished(nearestBeacon.getBluetoothAddress()) && !actualRoom.equals(nearestBeacon.getBluetoothAddress())) {
                        actualRoom = nearestBeacon.getBluetoothAddress();
                        ClientApplication.client.sendBeaconDetected(actualRoom);
                        fragmentManager.beginTransaction().replace(R.id.flContent, AnswerFragment.newInstance(nearestBeacon.getBluetoothAddress(), GameActivity.this.escapeGame, GameActivity.this.team)).commit();
                    } else if (!actualRoom.equals("") && (nearestBeacon.getDistance() >= 3 || !GameActivity.this.escapeGame.isARoom(nearestBeacon.getBluetoothAddress()) || GameActivity.this.team.isRoomFinished(nearestBeacon.getBluetoothAddress()))) {
                        actualRoom = "";
                        ClientApplication.client.sendMovingAwayBeacon();
                        fragmentManager.beginTransaction().replace(R.id.flContent, MapFragment.newInstance(GameActivity.this.escapeGame, GameActivity.this.team)).commit();
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the right room to show by comparing the distance between the tablet and the beacons.
     */
    private Beacon getNearestBeacon(Collection<Beacon> beacons){
        Beacon nearestBeacon = beacons.iterator().next();
        for (Beacon beacon : beacons)
            if (nearestBeacon.getDistance() > beacon.getDistance())
                nearestBeacon = beacon;
        return nearestBeacon;
    }
}
