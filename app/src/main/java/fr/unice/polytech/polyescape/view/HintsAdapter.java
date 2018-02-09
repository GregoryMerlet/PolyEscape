package fr.unice.polytech.polyescape.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.controller.SoundPlayer;

/**
 * Updates the list of hints in the AnswerFragment.
 */
public class HintsAdapter extends ArrayAdapter<String> {
    public HintsAdapter(Context context, List<String> hints){
        super(context, 0, hints);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hint_adapter,parent, false);
        }

        HintViewHolder viewHolder = (HintViewHolder) convertView.getTag();
        if (viewHolder == null){
            viewHolder = new HintViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.hintName);
            viewHolder.text = (TextView) convertView.findViewById(R.id.hintText);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.hintImage);
            convertView.setTag(viewHolder);
        }

        final String hint = getItem(position);

        viewHolder.name.setText("Indice " + (position + 1));

        if (hint.startsWith("http")) {
            final float scale = getContext().getResources().getDisplayMetrics().density;

            viewHolder.text.setText("");
            Picasso.with(getContext()).load(hint).into(viewHolder.image);
            viewHolder.image.getLayoutParams().height = (int)(200 * scale + 0.5f);

            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundPlayer.playClic();
                    final Dialog dialog = new Dialog(HintsAdapter.this.getContext());
                    dialog.setContentView(R.layout.pic_dialog);
                    dialog.setTitle("Image");
                    Picasso.with(getContext()).load(hint).into((ImageView) dialog.findViewById(R.id.bigPicture));

                    (dialog.findViewById(R.id.picClose)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        } else {
            viewHolder.text.setText(hint);
            viewHolder.image.getLayoutParams().height = 0;
        }

        return convertView;
    }

    /**
     * Graphic form of a hint.
     */
    private class HintViewHolder {
        public TextView name;
        public TextView text;
        public ImageView image;
    }
}
