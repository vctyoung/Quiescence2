package com.example.victor.quiescence;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class About extends AppCompatActivity {

    private TextView email;
    private TextView web;
    private TextSwitcher contents;
    private TextView name;
    int curStr = 0;
    private BitHandler bitHandler;

    private Handler handler = new Handler();

    String[] strs = new String[]{
            " Quiescence is an Android application which is developed to help students find quiet studying areas at Concordia.",
            "Team members:" + "\n" + "E.,Geogres" + "\n" + "K.,Daniel" + "\n" + "A.,Steve" + "\n" + "M.,Mohammad" + "\n" + "Y.,Xiaojing",
            "Learn more or share your options with us by clicking the link below."+"\n"+"Team E may you the best!"+"\n"+"\n"+"Thank you!",

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation in = AnimationUtils.loadAnimation(this, R.anim.in);
        Animation out = AnimationUtils.loadAnimation(this,R.anim.out);


        setContentView(R.layout.activity_help);
        name = (TextView) findViewById(R.id.textView5);
        email = (TextView) findViewById(R.id.textView6);
        web = (TextView) findViewById(R.id.textView8);
        contents = (TextSwitcher) findViewById(R.id.content);

        contents.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(About.this);
                tv.setTextSize(20);
                tv.setTextColor(Color.BLACK);
                tv.setFontFeatureSettings("causal");
                return tv;
            }
        });
        contents.setInAnimation(in);
        contents.setOutAnimation(out);

        bitHandler = new BitHandler();
        new myThread().start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        }, 20000);

        //  next(null);
    }
  /*      handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        }, 3000);
    }*/
    //事件处理函数，控制显示下一个字符串


    //  @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView6:
                startSendEmailIntent();
                break;
        }
    }

    private void startSendEmailIntent() {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:georgesabr.24@hotmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "Feedback 4 Quienscence");
        data.putExtra(Intent.EXTRA_TEXT, "Content");
        startActivity(data);
    }

    class BitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            contents.setText(strs[curStr]);
            curStr++;
            if (curStr == strs.length) {
                curStr = 0;
            }
        }
    }

    private class myThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (curStr < strs.length) {
                try {
                    synchronized (this) {
                        bitHandler.sendEmptyMessage(0);
                        this.sleep(6000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

