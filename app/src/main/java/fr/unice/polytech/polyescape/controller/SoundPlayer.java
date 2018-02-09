package fr.unice.polytech.polyescape.controller;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import fr.unice.polytech.polyescape.R;

/**
 * Launches a sound effect when it's called.
 */
public class SoundPlayer {
    private static SoundPool soundPool;
    private static int soundVictory;
    private static int soundClic;
    private static int soundNotif;
    private static int soundVictoryGame;
    private static int soundGameOver;

    /**
     * Prepares the sounds.
     */
    public static void initSoundPlayer(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        soundVictory = soundPool.load(context, R.raw.win, 1);
        soundClic = soundPool.load(context, R.raw.clic, 1);
        soundNotif = soundPool.load(context, R.raw.notif, 1);
        soundVictoryGame = soundPool.load(context, R.raw.win_game, 1);
        soundGameOver = soundPool.load(context, R.raw.game_over, 1);
    }

    /**
     * Used to play a specific sound.
     */
    public static void playSongVictory() {
        soundPool.play(soundVictory, 0.9f, 0.9f, 1, 0, 1);
    }

    public static void playClic() {
        soundPool.play(soundClic, 0.9f, 0.9f, 1, 0, 1);
    }

    public static void playNotif() {
        soundPool.play(soundNotif, 0.9f, 0.9f, 1, 0, 1);
    }

    public static void playVictoryGame() {
        soundPool.play(soundVictoryGame, 0.9f, 0.9f, 1, 0, 1);
    }

    public static void playGameOver() {
        soundPool.play(soundGameOver, 0.9f, 0.9f, 1, 0, 1);
    }
}
