package com.example.xkbam.main;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.example.xkbam.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link bienvenida#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bienvenida extends Fragment {

    private static final String TAG = "bienvenida";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VideoView videoView;

    public bienvenida() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bienvenida.
     */
    // TODO: Rename and change types and number of parameters
    public static bienvenida newInstance(String param1, String param2) {
        bienvenida fragment = new bienvenida();
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
        View view = inflater.inflate(R.layout.fragment_bienvenida, container, false);

        // Initialize the VideoView
        videoView = view.findViewById(R.id.valky_video);

        // Set the video URI, assuming the video is in the raw folder
        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.valky;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // Set up media controller
        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Handle video preparation
        videoView.setOnPreparedListener(mp -> {
            // Log video preparation status
            Log.d(TAG, "Video prepared and starting.");
            videoView.start();
        });

        // Handle errors
        videoView.setOnErrorListener((mp, what, extra) -> {
            // Log errors
            Log.e(TAG, "Error playing video: " + what + ", " + extra);
            return true; // return true if the error is handled
        });

        // Log the video path for debugging
        Log.d(TAG, "Video path: " + videoPath);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
