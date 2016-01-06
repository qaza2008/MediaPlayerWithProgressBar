title: MediaPlayProgress
date: 2015-12-28 17:46:27
categories:
- Android
tags:
- android
- mediaplayer
- progress


---
### 一.概述:
别(四声)说话,吻我! --- 生活需要激情! 
 下面要用生动形象的语言描述一下我想表达的东西:
 本文主要介绍网络音频播放,包括的功能如下:

* 直接播放网络的音频文件
* 采用先下载后播放的方式
* 采用帧动画展示播放语音的动画效果
* 采用圆形progressbar展示进度条
* 保证在listView中不会出现上线翻动出现动画缓存过去的现象--因为我遇到并解决了.
* 使用的一个service用于控制播放/暂停/停止,通过EventBus进行通信.

效果如下:

![image](http://7vihs8.com1.z0.glb.clouddn.com/mediaplayer_show.gif)


#### 引用:
环境: idea  ant编译

源码地址: [https://github.com/qaza2008/MediaPlayerWithProgressBar](https://github.com/qaza2008/MediaPlayerWithProgressBar)

欢迎fork and star!
<!-- more -->

### 二.代码示例:
主要还是说一下核心代码:

1. 核心一,MediaPlayers:

```
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

```
* 通过play()和playAsync()2钟方式播放音乐,感觉同样是网络的情况下差距不大.
* 通过hander将播放进度输出到回调函数中.

2. 核心二,MediaPlayService:
```
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
```
* 使用service用来整体控制音频播放,统一代码,规范流程.
* 使用到EventBus进行进程间通信,用于调用play,pause,stop等动作.
* 使用两种方式播放音频:在线播放和下载后播放.
* 通过CallBackProgressBar 中的void callBackProgressBar(int positions, int durations)更新进度条.


3. 核心3,处理listview滑动时会缓存动画播放效果.

```
 		progressBar.setMax(0);
        progressBar.setProgress(0);
        holder.rb_play_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "onClick", Toast.LENGTH_SHORT).show();
                String audioUrl = radio;
                if (TextUtils.isEmpty(audioUrl)) {
                    audioUrl = "";
                }
                int action = MediaPlayService.MediaPlayServiceAction.PLAY;
                progressBar.play = true;
                EventBus.getDefault().post(new MediaPlayService.MediaPlayServiceAction(action,
                        audioUrl, new MediaPlayers.CallBackProgressBar() {
                    @Override
                    public void callBackProgressBar(int positions, int durations) {
                        if (progressBar.play) {
                            progressBar.setMax(durations);
                            progressBar.setProgress(positions);
                            if (progressBar.getProgress() > 0) {
                                if (!animationDrawable.isRunning()) {
                                    animationDrawable.setOneShot(false);
                                    animationDrawable.start();
                                }
                            } else {
                                animationDrawable.stop();
                            }
                        } 
                    }
                }));

            }
        });
```
* 默认progress和max都是0,progressBar.play=false.
* 点击播放时,progressBar.play = true,通过EventBus将实现好的回调函数传递给MediaPlayService,进而传递给MediaPlayers,并且更新进度条.
* 在callBackProgressBar 对progressBar.play==true 的进行更新进度条.
* 主要是通过progressBar.play标志位进行判断是否更新的.


##### 核心代码完结!比海贼还快的结尾!

###### Done!


Welcome To My Website
---

[http://androidso.com](http://androidso.com)
--- 