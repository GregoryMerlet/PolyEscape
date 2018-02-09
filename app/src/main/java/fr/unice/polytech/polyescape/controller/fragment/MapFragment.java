package fr.unice.polytech.polyescape.controller.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.unice.polytech.polyescape.R;
import fr.unice.polytech.polyescape.model.EscapeGame;
import fr.unice.polytech.polyescape.model.Team;

/**
 * Displays the map of the Escape Game.
 * Highlights the room to go.
 */
public class MapFragment extends Fragment {
    private static final String EG = "escapeGame";
    private static final String TEAM = "team";

    private View rootView;
    private EscapeGame escapeGame;
    private Team team;

    public static MapFragment newInstance(EscapeGame escapeGame, Team team) {
        MapFragment fragment = new MapFragment();

        Bundle args = new Bundle();
        args.putSerializable(EG, escapeGame);
        args.putSerializable(TEAM, team);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.escapeGame = (EscapeGame) getArguments().getSerializable(EG);
        this.team = (Team) getArguments().getSerializable(TEAM);

        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        ImageView map = (ImageView) rootView.findViewById(R.id.imageMap);
        Bitmap finalMap = BitmapFactory.decodeResource(getResources(), R.drawable.plan);

        for (String roomTag : this.team.getSuccessfulRooms()) {
            Bitmap roomBitmap = BitmapFactory.decodeResource(getResources(), this.escapeGame.getEscapeRoom(roomTag).getMapPartDrawableID());
            finalMap = overlay(finalMap, roomBitmap);
        }

        map.setImageBitmap(finalMap);
    }

    /**
     * Merges two pictures with the same size.
     */
    private static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
}
