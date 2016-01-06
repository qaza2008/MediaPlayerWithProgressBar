package com.androidso.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

/**
 * Description:
 * TODO 网络异步待调整
 * 调用时自己实现接口
 * Created by liuxuegang1 on 2015/6/15.
 */
public class MediaPlayers extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    private static MediaPlayers mediaPlayer = null;

    private static class SingletonHolder {
        static final MediaPlayers INSTANCE = new MediaPlayers();
    }

    public static MediaPlayers getInstance() {
        mediaPlayer = SingletonHolder.INSTANCE;
        return mediaPlayer;
    }

    private MediaPlayers() {
    }
    //播放停止标识 true 播放 false停止播放，进度条为100
//    public boolean isPlay=true;

    private Context context;
    private String path;
    private CallBackProgressBar callBackProgressBar;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2002://播放中
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Log.d("MediaPlayers", " 播放 getCurrentPositions()" + getCurrentPositions() + "----" + getDurations());
                        if (getCurrentPositions() != 0) {
                            Log.d("MediaPlayers", "getCurrentPositions()" + getCurrentPositions() + "----" + getDurations());
                            if (callBackProgressBar != null) {
                                if (getCurrentPositions() != 0) {
                                    callBackProgressBar.callBackProgressBar(getCurrentPositions(), getDurations());
                                }
                            }
                        } else {
                            Log.d("MediaPlayers", "还没有开始播放");
                        }
                        //TODO 音频文件特别短的时候需要调整
                        mHandler.sendEmptyMessageDelayed(2002, 10);
                    }
                    break;
                case 2003://停止播放
//                    callBackProgressBar.callBackProgressBar(getCurrentPositions(),getDurations());
                    if (callBackProgressBar != null) {
                        callBackProgressBar.callBackProgressBar(0, 1);
                    }
                    break;
                case 2004://播放结束
                    Log.d("MediaPlayers", "播放结束了");
                    if (callBackProgressBar != null) {
                        callBackProgressBar.callBackProgressBar(0, 1);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("MediaPlayers", "播放完成了");
//        isPlay=false;
        mHandler.sendEmptyMessage(2004);
        mediaPlayer.stop();
    }

    /**
     * 进度条展示
     */
    public interface CallBackProgressBar {
        /**
         * @param positions 当前进度
         * @param durations 总时长
         */
        void callBackProgressBar(int positions, int durations);
    }

    public void setCallBackProgressBar(CallBackProgressBar callBackProgressBar) {
        this.callBackProgressBar = callBackProgressBar;
    }


    /**
     * 本地文件播放
     *
     * @throws IOException
     */
    public void play() throws IOException {
        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
            mediaPlayer.reset();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();

            mediaPlayer.start();
            mHandler.sendEmptyMessage(2002);
        }
    }

    /**
     * 网络异步prepare
     *
     * @throws IOException
     */

    public void playAsync() throws IOException {
        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
            mediaPlayer.reset();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mHandler.sendEmptyMessage(2002);

                }
            });

        }


    }


    /**
     * 停止播放
     */
    @Override
    public void stop() {
        if (mediaPlayer != null) {
            super.stop();
            mHandler.sendEmptyMessage(2003);
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mediaPlayer != null) {
            super.pause();
            mHandler.removeMessages(2002);
        }
    }

    /**
     * 总长度
     *
     * @return
     */
    public int getDurations() {
        if (mediaPlayer != null) {
            //如果不在播放状态，则停止更新
            //播放器进度条，防止界面报错
            if (!mediaPlayer.isPlaying()) {
//                PaiPaiLog.i("MediaPlayers", "播放器停止播放,跳过获取位置");
                return 0;
            }
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 当前位置 在结束的时候当前位置为0
     *
     * @return
     */
    public int getCurrentPositions() {
        if (mediaPlayer != null) {
            //如果不在播放状态，则停止更新
            //播放器进度条，防止界面报错
            if (!mediaPlayer.isPlaying()) {
//                PaiPaiLog.i("MediaPlayers", "播放器停止播放,跳过获取位置");
                return 0;
            }
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public void play(String filePath) throws IOException {
        setPath(filePath);
        play();

    }

    public void getMediaDuration(String filePath) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        long duration = mediaPlayer.getDuration();
//                        GetMediaDuration
                        //  EventBus.getDefault().post(new GoodsDescribeActivity.GetMediaDuration(duration));

                    }
                });
                mediaPlayer.prepare();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
