package com.lib.audio.view;

import android.animation.Animator;
import android.content.Context;


import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lib.audio.R;
import com.lib.audio.core.AudioController;
import com.lib.audio.events.AudioLoadEvent;
import com.lib.audio.events.AudioPauseEvent;
import com.lib.audio.events.AudioStartEvent;
import com.lib.audio.model.AudioBean;
import com.lib.audio.view.adapter.MusicPagerAdapter;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 音乐播放页面唱针布局
 */
public class IndictorView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private Context mContext;

    /*
     * view相关
     */
    private ImageView mImageView;
    private ViewPager mViewPager;
    private MusicPagerAdapter mMusicPagerAdapter;
    /*
     * data
     */
    private AudioBean mAudioBean; //当前播放歌曲
    private ArrayList<AudioBean> mQueue; //播放队列

    public IndictorView(Context context) {
        this(context, null);
    }

    public IndictorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndictorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initData() {
        mQueue = AudioController.getInstance().getQueue();
        mAudioBean = AudioController.getInstance().getNowPlaying();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_view, this);
        mImageView = rootView.findViewById(R.id.tip_view);
        mViewPager = rootView.findViewById(R.id.view_pager);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mMusicPagerAdapter = new MusicPagerAdapter(mQueue, mContext, null);
        mViewPager.setAdapter(mMusicPagerAdapter);
        showLoadView(false);
        //要在UI初始化完，否则会多一次listener响应
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //指定要播放的position
        AudioController.getInstance().setPlayIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                showPlayView();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                showPauseView();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    // 播放音乐的时候，开始旋转
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新viewpager为load状态
        mAudioBean = event.mAudioBean;
        showLoadView(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新activity为暂停状态
        showPauseView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新activity为播放状态
        showPlayView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    private void showLoadView(boolean isSmooth) {
        mViewPager.setCurrentItem(mQueue.indexOf(mAudioBean), isSmooth);
    }

    private void showPauseView() {
        Animator anim = mMusicPagerAdapter.getAnim(mViewPager.getCurrentItem());
        if (anim != null) anim.pause();
    }

    private void showPlayView() {
        Animator anim = mMusicPagerAdapter.getAnim(mViewPager.getCurrentItem());
        if (anim != null) {
            if (anim.isPaused()) {
                anim.resume();
            } else {
                anim.start();
            }
        }
    }
}
