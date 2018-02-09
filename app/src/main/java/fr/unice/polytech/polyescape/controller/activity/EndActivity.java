package fr.unice.polytech.polyescape.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.math.BigDecimal;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.controller.SoundPlayer;
import fr.unice.polytech.polyescape.model.ClientApplication;

/**
 * Displays a message of victory or defeat at the end of the game.
 */
public class EndActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_end);

        TextView bigText = (TextView) findViewById(R.id.bigText);
        TextView littleText = (TextView) findViewById(R.id.littleText);

        if (getIntent().getBooleanExtra("win", false)) {
            bigText.setText(R.string.youWin);

            int totalTime = new BigDecimal(getIntent().getLongExtra("totalTime", 0)).intValue();
            String response = "Vous avez terminé les " + getIntent().getIntExtra("roomDone", 0) + " salles en " +
                    parseTime(getIntent().getIntExtra("remainingMinutes", 0), getIntent().getIntExtra("remainingSeconds", 0), totalTime) +
                    "\nsur les " + totalTime + " minutes accordées.";
            littleText.setText(response);
        } else {
            bigText.setText(R.string.youLose);
            int rDone = getIntent().getIntExtra("roomDone", 0);
            String response = "Vous avez terminé " + rDone + " salle";
            if (rDone > 1) {
                response += "s";
            }
            response += ".\nIl ne vous en restait plus que " + getIntent().getIntExtra("roomToDo", 0) + ".";
            littleText.setText(response);
        }

        (findViewById(R.id.buttonBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlayer.playClic();
                startActivity(new Intent(EndActivity.this, HomeActivity.class));
            }
        });
    }

    /**
     * Writes a message with the time used to solved the game in a good format.
     */
    private String parseTime(int min, int sec, int totalMin) {
        String time = "";
        min = totalMin - min;
        sec = 60 - sec;
        if (sec != 60) {
            min--;
        }
        if (min != 0) {
            time += min + " minute";
            if (min > 1) {
                time += "s";
            }
            if (sec == 60) {
                return time;
            }
            time += " et ";
        }
        time += sec + " seconde";
        if (sec > 1) {
            time += "s";
        }
        return time;
    }

    /**
     * Catches the Back event.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    /**
     * Closes the connexion with the server.
     */
    @Override
    protected void onDestroy() {
        ClientApplication.client.close();
        super.onDestroy();
    }
}
