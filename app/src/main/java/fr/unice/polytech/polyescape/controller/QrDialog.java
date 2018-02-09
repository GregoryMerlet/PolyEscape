package fr.unice.polytech.polyescape.controller;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.model.ClientApplication;
import fr.unice.polytech.polyescape.model.EscapeGame;

/**
 * Displays a dialog with the content of the QR code analyzed.
 */
public class QrDialog extends Dialog {
    private TextView text;
    private ImageView image;
    private EscapeGame escapeGame;

    public QrDialog(@NonNull final Context context, String response, EscapeGame escapeGame) {
        super(context);
        this.setContentView(R.layout.qr_dialog);
        this.setTitle("Message QR");

        this.escapeGame = escapeGame;
        text = (TextView) findViewById(R.id.qrText);
        image = (ImageView) findViewById(R.id.qrImage);

        parseResponse(response);

        ImageButton dialogButton = (ImageButton) findViewById(R.id.qrClose);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QrDialog.this.dismiss();
            }
        });
    }

    /**
     * Fills the content of the dialog by parsing the response and checking its type.
     */
    private void parseResponse(String response) {
        if (escapeGame.isAnInformation(response)) {
            if(escapeGame.getInformation(response).startsWith("http")){
                final float scale = getContext().getResources().getDisplayMetrics().density;

                text.setText("");
                Picasso.with(getContext()).load(escapeGame.getInformation(response)).into(image);
                image.getLayoutParams().height = (int)(200 * scale + 0.5f);
            } else {
                text.setText(escapeGame.getInformation(response));
                image.setImageResource(0);
                image.getLayoutParams().height = 0;
            }
            ClientApplication.client.sendQRCodeFound(response);
        } else {
            text.setText(R.string.qrWrongTag);
        }
    }
}
