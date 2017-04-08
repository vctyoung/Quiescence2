package com.example.victor.quiescence;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] names =new String[] { "Loyola", "SGW"};
    private int[] pics= new int[]{R.drawable.loyola,R.drawable.concordia_ev_complex };
    private Picture campus;
    static private String building;
    private AlertDialog alertDialog2;
    private AlertDialog warning;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private RadioButton setting, help, about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferenceHelper=new SharedPreferenceHelper(MainActivity.this);

        getSupportActionBar().setTitle("Choose Your Campus") ;

        //getSupportActionBar().setLogo(R.drawable.concordia_coa);
        gridView = (GridView) findViewById(R.id.gridview1);

        SimpleAdper adapter = new SimpleAdper(names, pics, this);
        gridView.setAdapter(adapter);

        setting = (RadioButton) findViewById(R.id.rbg);
        help= (RadioButton) findViewById(R.id.rbAddress);
        about=(RadioButton) findViewById(R.id.rbChat);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,Setting.class);

                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showNormalDialog();            }
        });

       about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, About.class);
                startActivity(intent);

            }
        });

       // Toast.makeText(MainActivity.this, data,Toast.LENGTH_SHORT).show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                campus = (Picture) parent.getItemAtPosition(position); //(position);
                showSingleAlertDialog(v);

                //  startActivity(intent);
            }
        });

        if (sharedPreferenceHelper.getFirstInstallation()) {
            sharedPreferenceHelper.setInstallation();
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);}

        if (!isWifi(this) || sharedPreferenceHelper.getUpdate()==0)
        {
            dialog2_give_content();
            delay_operation(5000);
        }


    }


    public void showSingleAlertDialog(View view){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Choose a building");
        final String[] items;

        if (campus.getTitle()=="SGW")
            items=new String[] {"Webster","JMSB","Hall Building","EV Buidling"};
        else  items = new String[] {"Vanier","Chapel","CJ Building","FC Smith Buidling"};


        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                building=items[index];//Toast.makeText(MainActivity.this, items[index], Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Intent intent =new Intent(MainActivity.this,Building.class);
                Intent intent =new Intent(MainActivity.this,Building.class);
                sharedPreferenceHelper.saveCapums(campus.getTitle(),building,MainActivity.this);

                startActivity(intent);
                alertDialog2.dismiss();
            }
        });
        alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {


                alertDialog2.dismiss();
            }
        });
        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }
    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public void dialog2_give_content( )
    {

        if (sharedPreferenceHelper.getUpdate()==0)
        {
            warning= new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Oops").setMessage(" The information is not updated because you disable the autoupdate!")
                    .setPositiveButton("Enable Update！",new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {//进入设置页面
                            Intent intent2=new Intent();
                            intent2.setClass(MainActivity.this, Setting.class);
                            startActivity(intent2);
                        }
                    })
                    .setNegativeButton("",null).create();
        }
        else
        {
            warning= new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Oops").setMessage(" The information is not updated because no WIFI connection!")
                    .setNegativeButton("",null).create();
        }
        warning.show();

    }
    public void delay_operation(long time)
    {
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                warning.dismiss();//隐藏对话框
            }
        }, time);
    }
    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(android.R.drawable.ic_dialog_info);
        normalDialog.setTitle("Quick Guide");
        normalDialog.setMessage("1. Choose a campus you are in by clicking its image. Then choose a building you want to go"+"\n"
        +  "2. Click the name of room for viewing its past recsord. Long click for more options.Refresh for getting updated information." +"\n"
        + "3. Click 'calender' to see previous records, click 'revert' to go back.");

        normalDialog.setPositiveButton("Got it!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  normalDialog.set;
                    }
                });
        normalDialog.setNegativeButton("",null);
        // 显示
        normalDialog.show();
    }
}



