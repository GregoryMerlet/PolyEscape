package fr.unice.polytech.polyescape.controller.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.github.lzyzsd.circleprogress.CircleProgress;

import java.util.Observable;
import java.util.Observer;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.controller.QrDialog;
import fr.unice.polytech.polyescape.controller.SoundPlayer;
import fr.unice.polytech.polyescape.controller.activity.GameActivity;
import fr.unice.polytech.polyescape.model.AnswerType;
import fr.unice.polytech.polyescape.model.ClientApplication;
import fr.unice.polytech.polyescape.model.EscapeGame;
import fr.unice.polytech.polyescape.model.EscapeRoom;
import fr.unice.polytech.polyescape.model.Team;
import fr.unice.polytech.polyescape.view.HintsAdapter;

/**
 * Manages the answer of a puzzle.
 * Checks and shows if the answer is right.
 */
public class AnswerFragment extends Fragment implements Observer {
    private static final String ROOMTAG = "roomTag";
    private static final String PUZZLEID = "puzzleID";
    private static final String EG = "escapeGame";
    private static final String TEAM = "team";

    private View rootView;
    private String roomTag;
    private int puzzleID;
    private EscapeGame escapeGame;
    private Team team;
    private HintsAdapter hintsRecyclerAdapter;
    private ListView hintsList;
    private CircleProgress circleProgress;
    private Button askHintButton;

    public static AnswerFragment newInstance(String roomTag, EscapeGame escapeGame, Team team) {
        return newInstance(roomTag, 0, escapeGame, team);
    }

    public static AnswerFragment newInstance(String roomTag, int puzzleID, EscapeGame escapeGame, Team team) {
        AnswerFragment fragment = new AnswerFragment();

        Bundle args = new Bundle();
        args.putString(ROOMTAG, roomTag);
        args.putInt(PUZZLEID, puzzleID);
        args.putSerializable(EG, escapeGame);
        args.putSerializable(TEAM, team);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.roomTag = getArguments().getString(ROOMTAG);
        this.puzzleID = getArguments().getInt(PUZZLEID);
        this.escapeGame = (EscapeGame) getArguments().getSerializable(EG);
        this.team = (Team) getArguments().getSerializable(TEAM);

        rootView = inflater.inflate(R.layout.fragment_answer, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        ClientApplication.client.addObserverToHints(this);

        /* Graphics component */
        TextView roomName = (TextView) rootView.findViewById(R.id.roomName);
        TextView enigmaDescription = (TextView) rootView.findViewById(R.id.enigmaDescription);
        final TextView answerError = (TextView) rootView.findViewById(R.id.answerError);
        final EditText answerEditText = (EditText) rootView.findViewById(R.id.answerEditText);
        restrictAnswerType(answerEditText);
        Button answerButton = (Button) rootView.findViewById(R.id.answerButton);
        hintsList = (ListView) rootView.findViewById(R.id.hintListView);

        final EscapeRoom currentEscapeRoom = this.escapeGame.getEscapeRoom(this.roomTag);
        roomName.setText(currentEscapeRoom.getName());
        enigmaDescription.setText(currentEscapeRoom.getPuzzle(this.puzzleID).getStatement());

        /* Hints progress */
        circleProgress = (CircleProgress) rootView.findViewById(R.id.remainingHintsGauge);
        askHintButton = (Button) rootView.findViewById(R.id.askHintButton);
        circleProgress.setMax(escapeGame.getHints());
        circleProgress.setProgress(escapeGame.getHints() - team.getHintsUsed());
        if (circleProgress.getProgress() == 0) {
            askHintButton.setEnabled(false);
        }
        ((TextView) rootView.findViewById(R.id.remainingHints)).setText(String.valueOf(circleProgress.getProgress()));

        hintsRecyclerAdapter = new HintsAdapter(this.getActivity(), this.team.getHints(this.roomTag));
        hintsList.setAdapter(hintsRecyclerAdapter);

        /* Treatment of an answer */
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlayer.playClic();

                String answerContent = answerEditText.getText().toString().trim();
                answerEditText.setText("");
                if (!currentEscapeRoom.getPuzzle(AnswerFragment.this.puzzleID).isTheAnswerRight(answerContent.toUpperCase())) {
                    ClientApplication.client.sendWrongAnswer(answerContent);
                    answerError.setText("La réponse \"" + answerContent + "\" n'est pas la bonne réponse.");
                    answerEditText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                }
                else {
                    FragmentManager fragmentManager = AnswerFragment.this.getFragmentManager();

                    final Dialog dialog = new Dialog(AnswerFragment.this.getActivity());
                    dialog.setContentView(R.layout.dialog);
                    dialog.setTitle("Bien joué !");
                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SoundPlayer.playClic();
                            dialog.dismiss();
                        }
                    });

                    if (!currentEscapeRoom.isTheLastPuzzle(AnswerFragment.this.puzzleID)) {
                        ((TextView) dialog.findViewById(R.id.dialogContent)).setText(R.string.enigmaSolved);
                        ClientApplication.client.sendPuzzleSolved(AnswerFragment.this.puzzleID);
                        SoundPlayer.playSongVictory();
                        dialog.show();
                        fragmentManager.beginTransaction().replace(R.id.flContent, AnswerFragment.newInstance(AnswerFragment.this.roomTag, AnswerFragment.this.puzzleID + 1, AnswerFragment.this.escapeGame, AnswerFragment.this.team)).commit();
                    }
                    else {
                        ((GameActivity) AnswerFragment.this.getActivity()).finishedRoom(roomTag);
                        ((TextView) dialog.findViewById(R.id.dialogContent)).setText(R.string.roomSolved);
                        dialog.show();
                        fragmentManager.beginTransaction().replace(R.id.flContent, MapFragment.newInstance(AnswerFragment.this.escapeGame, AnswerFragment.this.team)).commit();
                    }
                }
            }
        });

        /* QR */
        (rootView.findViewById(R.id.qrButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forFragment(AnswerFragment.this).initiateScan();
            }
        });

        /* Ask help */
        askHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlayer.playClic();

                final String[] options = { getString(R.string.help1),
                        getString(R.string.help2),
                        getString(R.string.help3),
                        getString(R.string.help4)};
                final int[] selected = {0};

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AnswerFragment.this.getActivity());
                alertDialogBuilder.setTitle("Choisissez votre demande")
                        .setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected[0] = which;
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                SoundPlayer.playClic();
                                useHint();
                                ClientApplication.client.sendAskHelp(options[selected[0]]);
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SoundPlayer.playClic();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Catches the message of the QR code and displays the QrDialog.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan annulé", Toast.LENGTH_LONG).show();
            } else {
                QrDialog qrDialog = new QrDialog(getActivity(), result.getContents(), escapeGame);
                qrDialog.show();
            }
        }
    }

    @Override
    public void onDestroy() {
        ClientApplication.client.deleteObservableToHints(this);
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        team.addHint(roomTag, (String) arg);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hintsRecyclerAdapter.notifyDataSetChanged();
                hintsList.setSelection(hintsList.getCount() - 1);
                SoundPlayer.playNotif();
            }
        });
    }

    /**
     * Updates the CircleProgress once the hint used.
     */
    private void useHint() {
        team.useHint();
        circleProgress.setProgress(escapeGame.getHints() - team.getHintsUsed());
        ((TextView) rootView.findViewById(R.id.remainingHints)).setText(String.valueOf(circleProgress.getProgress()));
        if (circleProgress.getProgress() == 0) {
            askHintButton.setEnabled(false);
        }
    }

    /**
     * Restricts the use of the keyboard.
     */
    private void restrictAnswerType(EditText answerEditText) {
        AnswerType answerType = escapeGame.getEscapeRoom(roomTag).getPuzzle(this.puzzleID).getAnswerType();

        if (answerType == AnswerType.STRING) {
            answerEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (answerType == AnswerType.NUMBERS) {
            answerEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (answerType == AnswerType.FOUR_NUMBERS) {
            answerEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            answerEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        }
    }
}
