package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.R;

public class TutorialFragment extends Fragment {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LOGO_ID = "logoId";
    public static final String DOTS_ID = "dotsId";

    private String title;
    private String description;
    private int logoId;
    private int dotsId;

    public TutorialFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Title.
     * @param description Description.
     * @param logoId logoId
     * @param dotsId dotsId
     * @return A new instance of fragment TutorialFragment.
     */
    public static TutorialFragment newInstance(String title, String description, int logoId, int dotsId) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESCRIPTION, description);
        args.putInt(LOGO_ID, logoId);
        args.putInt(DOTS_ID, dotsId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            description = getArguments().getString(DESCRIPTION);
            logoId = getArguments().getInt(LOGO_ID);
            dotsId = getArguments().getInt(DOTS_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        TextView titleTextView = (TextView) view.findViewById(R.id.text_title);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.text_description);
        ImageView logoImageView = (ImageView) view.findViewById(R.id.image_logo);
        ImageView dotsImageView = (ImageView) view.findViewById(R.id.image_dots);
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        logoImageView.setImageResource(logoId);
        dotsImageView.setImageResource(dotsId);
        return view;
    }
}
