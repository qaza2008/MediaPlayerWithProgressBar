package com.androidso.app.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.androidso.app.utils.MediaPlayers;
import com.androidso.app.utils.StorageUtils;
import com.androidso.app.utils.Utils;
import de.greenrobot.event.EventBus;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by mac on 15/6/16.
 */
public class MediaPlayService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    MediaPlayers mediaPlayers;
    String lastUrl;
    FinalHttp fh;

    LinkedList<MediaPlayers.CallBackProgressBar> progressBarList;
    public static int PLAY_TYPE_LOAD = 0;
    public static int PLAY_TYPE_ONLINE = 1;

    int playType = PLAY_TYPE_ONLINE;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MediaPlayService", "onCreate");
        progressBarList = new LinkedList<MediaPlayers.CallBackProgressBar>();
        fh = new FinalHttp();
        EventBus.getDefault().register(this);
        mediaPlayers = MediaPlayers.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mediaPlayers.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public void onEventMainThread(MediaPlayServiceAction mediaPlayServiceAction) {
        int action = mediaPlayServiceAction.action;

        switch (action) {
            case MediaPlayServiceAction.PLAY:
                play(mediaPlayServiceAction.progressBar, mediaPlayServiceAction.audioUrl);
                break;
            case MediaPlayServiceAction.PAUSE:
                pause();
                break;

            case MediaPlayServiceAction.STOP:
                stop();
                if (mediaPlayServiceAction.progressBar != null) {
                    mediaPlayServiceAction.progressBar.callBackProgressBar(0, 1);
                }

                break;
            case MediaPlayServiceAction.GET_DURACTION:
                getDatabasePath(mediaPlayServiceAction.audioUrl);

                break;

        }


    }

    public class MyBinder extends Binder {
        public MediaPlayService getService() {
            return MediaPlayService.this;
        }

    }

    private void GetDuration(String audioUrl) {

        playOrGetDuration(new MediaPlayers.CallBackProgressBar() {
            @Override
            public void callBackProgressBar(int positions, int durations) {

            }
        }, audioUrl, MediaPlayServiceAction.GET_DURACTION);
    }

    private void play(MediaPlayers.CallBackProgressBar progressBar, String audioUrl) {
        playOrGetDuration(progressBar, audioUrl, MediaPlayServiceAction.PLAY);

    }

    private void playOrGetDuration(final MediaPlayers.CallBackProgressBar progressBar, String audioUrl, final int action) {
        if (audioUrl.equals(lastUrl)) {
            if (mediaPlayers.isPlaying()) {
                stop();
                return;
            }
        } else {

            lastUrl = audioUrl;
        }
        for (MediaPlayers.CallBackProgressBar preProgressBar : progressBarList) {
            preProgressBar.callBackProgressBar(0, 1);
        }
        progressBarList.clear();

        if (mediaPlayers.isPlaying()) {
            stop();
        }
        if (progressBar != null) {
            progressBarList.add(progressBar);
        }

        if (playType == PLAY_TYPE_ONLINE) {
            //是用在线播放
            setMediaPlayerRes(progressBar, audioUrl);
            try {
//                mediaPlayers.play();
                mediaPlayers.playAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (playType == PLAY_TYPE_LOAD) {

            //使用先下载后播放
            final String filePath;
            if (MediaPlayServiceAction.isLocal.equals("false")) {
                String audioPath = Utils.getFileName(audioUrl);
                filePath = StorageUtils.getCacheDirectory(getApplicationContext()).getAbsolutePath() + "/" + audioPath;
            } else {
                filePath = audioUrl;
            }
            File f = new File(filePath);
            if (f.exists()) {
                if (action == MediaPlayServiceAction.GET_DURACTION) {
                    mediaPlayers.getMediaDuration(filePath);

                } else {
                    setMediaPlayerRes(progressBar, filePath);
                }

            } else {
                fh.download(audioUrl, filePath,
                        new AjaxCallBack<File>() {
                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                int progress = 0;
                                if (current != count && current != 0) {
                                    progress = (int) (current / (float) count * 100);
                                } else {
                                    progress = 100;
                                }
                            }

                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                if (action == MediaPlayServiceAction.GET_DURACTION) {
                                    mediaPlayers.getMediaDuration(filePath);
                                } else {
                                    setMediaPlayerRes(progressBar, filePath);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t, int errorNo, String strMsg) {
                                super.onFailure(t, errorNo, strMsg);
                                Toast.makeText(getApplicationContext(), "系统开小差,稍后重试", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        }
    }


    protected void setMediaPlayerRes(MediaPlayers.CallBackProgressBar progressBar, String filePath) {
        mediaPlayers.setCallBackProgressBar(progressBar);
        try {
            mediaPlayers.play(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        mediaPlayers.stop();

    }

    private void pause() {
        mediaPlayers.pause();

    }


    public static class MediaPlayServiceAction {
        public static final int PLAY = 0;
        public static final int PAUSE = 1;
        public static final int STOP = 2;
        public static final int GET_DURACTION = 3;


        public MediaPlayers.CallBackProgressBar progressBar;
        public String audioUrl;
        public int action;
        public static String isLocal;


        public MediaPlayServiceAction(int action, String audioUrl, MediaPlayers.CallBackProgressBar progressBar, String... lcoalUrl) {
            this.action = action;
            this.audioUrl = audioUrl;
            this.progressBar = progressBar;
            if (lcoalUrl.length > 0 && lcoalUrl[0] != null) {
                this.isLocal = lcoalUrl[0];
            } else {
                this.isLocal = "false";
            }
        }

        /**
         * 默认播放
         *
         * @param audioUrl
         * @param progressBar
         */
        public MediaPlayServiceAction(String audioUrl, MediaPlayers.CallBackProgressBar progressBar) {
            action = PLAY;
            this.audioUrl = audioUrl;
            this.progressBar = progressBar;
        }
    }

}
