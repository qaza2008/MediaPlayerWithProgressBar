package com.androidso.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.androidso.app.adapter.MainAdapter;
import com.androidso.app.service.MediaPlayService;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private ListView listView;
    private MainAdapter adapter;
    private List<String> radioList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.lv);
        radioList = new ArrayList<>();
        radioList.add("http://storage.360buyimg.com/ershou.paipai.com/voice/20151229-6fbe943c-8430-4e29-a3ba-440e49fd6b96.amr");
        radioList.add("http://sc1.111ttt.com:8282/2016/4/01/06/194062152475.mp3?tflag=1452090549&pin=57ba1d92c75dedbdd1a32e01977a3b88&ip=222.129.24.129#.mp3");
        radioList.add("http://sc1.111ttt.com:8282/2016/1/01/06/194062022279.mp3?tflag=1452090595&pin=8fbeb0fb73199f92482f4c1049b40672&ip=222.129.24.129#.mp3");
        radioList.add("http://sc1.111ttt.com:8282/2016/3/01/06/194062020320.mp3?tflag=1452091445&pin=f925cc135704ff665eea07847b15d3f2&ip=222.129.24.129#.mp3");
        radioList.add("http://sc1.111ttt.com:8282/2016/1/01/06/194061710226.mp3?tflag=1452091488&pin=5239a96112a5fefdb02912fb485e2713&ip=222.129.24.129#.mp3");
        radioList.add("http://sc1.111ttt.com:8282/2016/1/01/06/194061620553.mp3?tflag=1452091522&pin=458a96f60e7eebd3372476e319562809&ip=222.129.24.129#.mp3");
        adapter = new MainAdapter(MyActivity.this, radioList);
        listView.setAdapter(adapter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new MediaPlayService.MediaPlayServiceAction(MediaPlayService.MediaPlayServiceAction.STOP,
                null, null));

    }
}
