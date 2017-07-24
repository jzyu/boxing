package com.bilibili.boxing_impl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bilibili.boxing.AbsBoxingViewActivity;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.VideoMedia;
import com.bilibili.boxing.model.task.IMediaTask;
import com.bilibili.boxing_impl.R;
import com.bilibili.boxing_impl.view.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

public class BoxingVideoViewActivity extends AbsBoxingViewActivity {

    HackyViewPager mGallery;
    ProgressBar mProgressBar;

    private boolean mNeedAllCount = true;
    private boolean mFinishLoading;
    private int mCurrentPage;
    private int mTotalCount;
    private int mStartPos;
    private int mPos;

    private Toolbar mToolbar;
    private VideosAdapter mAdapter;
    private VideoMedia mCurrentVideoItem;
    private Button mOkBtn;
    private ArrayList<BaseMedia> mVideos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_boxing_video_view);
        createToolbar();
        initData();
        initView();
        startLoading();
    }

    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.nav_top_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initData() {
        mStartPos = getStartPos();
        mVideos = new ArrayList<>();
    }

    private void initView() {
        mAdapter = new VideosAdapter(getSupportFragmentManager());
        mOkBtn = (Button) findViewById(R.id.image_items_ok);
        mGallery = (HackyViewPager) findViewById(R.id.pager);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mGallery.setAdapter(mAdapter);
        mGallery.addOnPageChangeListener(new OnPagerChangeListener());

        mOkBtn.setText("选择");
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishByBackPressed(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishByBackPressed(true);
    }

    private void finishByBackPressed(boolean isBack) {
        Intent intent = new Intent();
        if (! isBack) {
            intent.putExtra(Boxing.EXTRA_SELECTED_MEDIA, mCurrentVideoItem);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void startLoading() {
        loadMedia("", mStartPos, mCurrentPage);
        mAdapter.setMedias(mVideos);
    }

    private void loadMedia(String albumId, int startPos, int page) {
        this.mPos = startPos;
        loadMedias(page, albumId);
    }

    @Override
    public void showMedia(@Nullable List<BaseMedia> medias, int totalCount) {
        if (medias == null || totalCount <= 0) {
            return;
        }
        mVideos.addAll(medias);
        mAdapter.notifyDataSetChanged();
        setupGallery();

        if (mToolbar != null && mNeedAllCount) {
            mToolbar.setTitle(getString(R.string.boxing_image_preview_title_fmt,
                    String.valueOf(++mPos), String.valueOf(totalCount)));
            mNeedAllCount = false;
        }
        loadOtherPagesInAlbum(totalCount);
    }

    private void loadOtherPagesInAlbum(int totalCount) {
        mTotalCount = totalCount;
        if (mCurrentPage <= (mTotalCount / IMediaTask.PAGE_LIMIT)) {
            mCurrentPage++;
            loadMedia("", mStartPos, mCurrentPage);
        }
    }

    private void setupGallery() {
        int startPos = mStartPos;
        if (mGallery == null || startPos < 0) {
            return;
        }
        if (startPos < mVideos.size() && ! mFinishLoading) {
            mGallery.setCurrentItem(mStartPos, false);
            mCurrentVideoItem = (VideoMedia) mVideos.get(startPos);
            mProgressBar.setVisibility(View.GONE);
            mGallery.setVisibility(View.VISIBLE);
            mFinishLoading = true;
            invalidateOptionsMenu();
        } else if (startPos >= mVideos.size()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mGallery.setVisibility(View.GONE);
        }
    }

    private class VideosAdapter extends FragmentStatePagerAdapter {
        private ArrayList<BaseMedia> mMedias;

        VideosAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return BoxingVideoViewFragment.newInstance((VideoMedia) mMedias.get(i));
        }

        @Override
        public int getCount() {
            return mMedias == null ? 0 : mMedias.size();
        }

        public void setMedias(ArrayList<BaseMedia> medias) {
            this.mMedias = medias;
            notifyDataSetChanged();
        }
    }

    private class OnPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            if (mToolbar != null && position < mVideos.size()) {
                mToolbar.setTitle(getString(R.string.boxing_image_preview_title_fmt,
                        String.valueOf(position + 1), String.valueOf(mTotalCount)));
                mCurrentVideoItem = (VideoMedia) mVideos.get(position);
                invalidateOptionsMenu();
            }
        }
    }
}
