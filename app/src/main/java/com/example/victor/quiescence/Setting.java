package com.example.victor.quiescence;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Setting");
        sharedPreferenceHelper=new SharedPreferenceHelper(Setting.this);
        final Switch update =(Switch) findViewById(R.id.switch2);
        Switch notice =(Switch) findViewById(R.id.switch1);
        Switch display =(Switch) findViewById(R.id.switch3);
        TextView help =(TextView) findViewById(R.id.textView7);
        sharedPreferenceHelper.setInstallation();

        update.setChecked(sharedPreferenceHelper.getUpdate()==1);
        notice.setChecked(sharedPreferenceHelper.getNotice()==1);
        display.setChecked(sharedPreferenceHelper.getAllRoom()==0);

        update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                Intent intent =new Intent(Setting.this,upDate.class);
                if (isChecked) {
                    startService(intent);
                    sharedPreferenceHelper.setAutoUpdate(1);
                } else {
                    stopService(intent);
                    sharedPreferenceHelper.setAutoUpdate(0);
                    sharedPreferenceHelper.setNotification(0);
                }
            }
        });

        notice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {

                    sharedPreferenceHelper.setNotification(1);
                   if (sharedPreferenceHelper.getUpdate() == 0) {
                       Toast.makeText(Setting.this,"You Nedd to enable update too",Toast.LENGTH_LONG).show();

                   }

                    }
                else
                        sharedPreferenceHelper.setNotification(0);

            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });


        display.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {

                    sharedPreferenceHelper.setReturnAll(0);
                } else {
                    sharedPreferenceHelper.setReturnAll(1);
                }
            }
        });

}
    }
