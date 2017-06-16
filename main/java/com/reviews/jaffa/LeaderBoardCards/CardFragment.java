package com.reviews.jaffa.LeaderBoardCards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.reviews.jaffa.R;


public class CardFragment extends Fragment {

    private CardView mCardView;
    String leaderboardname, leaderboardfollowers, leaderboardratings, leaderboardfbId;
    TextView cardtitle,cardfollowers,cardreviews;
    Button cardfollowbutton;
    ImageView cardrank;

    public static CardFragment newInstance(String leaderboardname, String leaderboardfollowers, String leaderboardratings, String leaderboardfbId) {
        CardFragment fragment = new CardFragment();
        Bundle bundleFeatures = new Bundle();
        bundleFeatures.putString("leaderboardname", leaderboardname);
        bundleFeatures.putString("leaderboardfollowers", leaderboardfollowers);
        bundleFeatures.putString("leaderboardratings", leaderboardratings);
        bundleFeatures.putString("leaderboardfbId", leaderboardfbId);
        fragment.setArguments(bundleFeatures);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            leaderboardname = getArguments().getString("leaderboardname");
            leaderboardfollowers = getArguments().getString("leaderboardfollowers");
            leaderboardratings = getArguments().getString("leaderboardratings");
            leaderboardfbId = getArguments().getString("leaderboardfbId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard_card, container, false);
        cardtitle = (TextView) view.findViewById(R.id.cardtitle);
        cardfollowers = (TextView) view.findViewById(R.id.cardfollowers);
        cardreviews = (TextView) view.findViewById(R.id.cardreviews);
        cardfollowbutton = (Button) view.findViewById(R.id.cardfollowbutton);
        cardrank = (ImageView) view.findViewById(R.id.cardrank);

        cardtitle.setText(leaderboardname);
        cardfollowers.setText(leaderboardfollowers+" followers");
        cardreviews.setText(leaderboardratings+" reviews");

        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }
}
