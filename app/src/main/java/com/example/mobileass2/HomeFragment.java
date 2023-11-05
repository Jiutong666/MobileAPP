package com.example.mobileass2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button shareTextButton = view.findViewById(R.id.shareTextButton);
        shareTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DropTextActivity.class);
            startActivity(intent);
        });

        Button sharePictureButton = view.findViewById(R.id.sharePictureButton);
        sharePictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DropPictureActivity.class);
            startActivity(intent);
        });

        Button shareVideoButton = view.findViewById(R.id.shareVideoButton);
        shareVideoButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DropVideoActivity.class);
            startActivity(intent);
        });


        // Find the FloatingActionButton and ButtonContainer
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        LinearLayout buttonContainer = view.findViewById(R.id.buttonContainer);

        fabAdd.setOnClickListener(v -> {
            if (buttonContainer.getVisibility() == View.GONE) {
                buttonContainer.setVisibility(View.VISIBLE);
                buttonContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
            } else {
                buttonContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                buttonContainer.postOnAnimation(() -> buttonContainer.setVisibility(View.GONE));
            }
        });

        return view;
    }

}