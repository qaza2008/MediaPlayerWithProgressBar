package com.androidso.app.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidso.app.R;
import com.androidso.app.service.MediaPlayService;
import com.androidso.app.utils.MediaPlayers;
import com.androidso.app.view.RoundProgressBar;
import de.greenrobot.event.EventBus;

import java.util.List;

/**
 * Created by mac on 15/12/29.
 */
public class MainAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mRadioList;
    private LayoutInflater inflater;

    public MainAdapter(Context context, List<String> radioList) {
        this.mContext = context;
        this.mRadioList = radioList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {

        return mRadioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRadioList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_item, null);
            holder.play_btn = (ImageView) convertView.findViewById(R.id.play_btn);
            holder.rb_play_audio = (RoundProgressBar) convertView.findViewById(R.id.rb_play_audio);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String radio = (String) getItem(position);

        final ImageView playIv = holder.play_btn;
        playIv.setImageResource(R.drawable.voice_play_anim);
        final AnimationDrawable animationDrawable = (AnimationDrawable) playIv.getDrawable();
        animationDrawable.stop();
        final RoundProgressBar progressBar = holder.rb_play_audio;
        progressBar.play = false;
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


        return convertView;
    }

    class ViewHolder {
        ImageView play_btn;
        RoundProgressBar rb_play_audio;
    }
}
