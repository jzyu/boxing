package com.bilibili.boxing_impl.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bilibili.boxing.model.entity.impl.VideoMedia;
import com.bilibili.boxing_impl.R;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

public class BoxingVideoViewFragment extends BoxingBaseFragment {
    private static final String BUNDLE_VIDEO = "com.bilibili.boxing_impl.ui.BoxingRawImageFragment.video";

    private VideoMedia media;
    private EMVideoView videoView;

    public static BoxingVideoViewFragment newInstance(@NonNull VideoMedia video) {
        BoxingVideoViewFragment fragment = new BoxingVideoViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_VIDEO, video);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        media = getArguments().getParcelable(BUNDLE_VIDEO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boxing_video_view, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoView = (EMVideoView) view.findViewById(R.id.em_video_view);
        videoView.post(new Runnable() {
            @Override
            public void run() {
                videoView.setVideoPath("file://" + media.getPath());
            }
        });
    }

    @Override
    void setUserVisibleCompat(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            videoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    videoView.start();
                }
            });
        } else {
            videoView.stopPlayback();
        }
    }
}
