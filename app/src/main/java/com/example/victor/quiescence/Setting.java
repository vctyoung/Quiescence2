package com.example.victor.quiescence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
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
        Spinner dispaly=(Spinner) findViewById(R.id.spinner);
        final TextView help =(TextView) findViewById(R.id.textView7);
        sharedPreferenceHelper.setInstallation();

        update.setChecked(sharedPreferenceHelper.getUpdate()==1);
        notice.setChecked(sharedPreferenceHelper.getNotice()==1);

        dispaly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

              switch (pos)
                {
                    case 0:

                        break;
                    case 1:
                        sharedPreferenceHelper.setReturnAll(1);
                        break;
                    case 2:
                        sharedPreferenceHelper.setReturnAll(2);
                        break;
                    case 3:
                        sharedPreferenceHelper.setReturnAll(3);
                        break;
                    default:
                        break;

                }
                //Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], 2000).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        //dispaly.no


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
                       Toast.makeText(Setting.this,"You need to enable update too",Toast.LENGTH_LONG).show();

                   }

                    }
                else
                        sharedPreferenceHelper.setNotification(0);

            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Setting.this,About.class);
                startActivity(intent);
            }
        });


        /*display.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {

                    sharedPreferenceHelper.setReturnAll(1);
                } else {
                    sharedPreferenceHelper.setReturnAll(3);
                }
            }
        });*/

}
    }
