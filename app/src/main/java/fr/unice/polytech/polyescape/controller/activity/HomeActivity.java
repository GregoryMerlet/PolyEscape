package fr.unice.polytech.polyescape.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.controller.SoundPlayer;
import fr.unice.polytech.polyescape.model.ClientApplication;

/**
 * Displays the form to choose a team name.
 */
public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        (findViewById(R.id.buttonValidate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlayer.playClic();

                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                EditText teamBox = (EditText) findViewById(R.id.teamBox);
                String teamName = teamBox.getText().toString().trim();

                ClientApplication.client.sendStartGame(teamName);

                intent.putExtra("teamName", teamName);
                startActivity(intent);
            }
        });
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
