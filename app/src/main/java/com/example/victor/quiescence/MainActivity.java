package com.example.victor.quiescence;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] names =new String[] { "Loyola", "SGW"};
    private int[] pics= new int[]{R.drawable.loyola,R.drawable.concordia_ev_complex };
    private Picture campus;
    static private String building;
    private AlertDialog alertDialog2;
    private SharedPreferenceHelper sharedPreferenceHelper;
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
        try {
            ///String data=connectServor.readParse();
             connectServor.sendRequestWithHttpURLConnection();
         //   Toast.makeText(MainActivity.this, data,Toast.LENGTH_SHORT).show();

        } catch ( Exception e)
        {
            Log.e("MainActivity", Log.getStackTraceString(e));
            Toast.makeText(MainActivity.this, "fff",Toast.LENGTH_SHORT).show();
        } ;
        connectServor.sendRequestWithHttpURLConnection();

       // Toast.makeText(MainActivity.this, data,Toast.LENGTH_SHORT).show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                campus = (Picture) parent.getItemAtPosition(position); //(position);
                showSingleAlertDialog(v);

                //  startActivity(intent);
            }
        });
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





}