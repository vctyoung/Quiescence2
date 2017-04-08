package com.example.victor.quiescence;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class Building extends AppCompatActivity {

    //private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private dataBaseHelper myDB;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recycleListView;
    private String campus;
    private String building;
    static private int option;
    private ProgressDialog scanningDialog;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private AlertDialog alertDialog2;
    ArrayList<Room> rooms= new ArrayList<>();
    String response;
    String currentRoom;
    private AlertDialog information;
    protected TextView counter;
    protected CountDownTimer alertCounter;
    private int countSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_building);
        myDB=new dataBaseHelper(Building.this);
        sharedPreferenceHelper=new SharedPreferenceHelper(Building.this);
       // ArrayList<Room> rooms= new ArrayList<>();
        recycleListView= (RecyclerView) findViewById(R.id.recycler_view);
        campus= sharedPreferenceHelper.getCampus();
        building =sharedPreferenceHelper.getBuilding();
        countSwipe=0;
        // if(campus=="Loyola" || campus=="SGW")
        //toolbar= new Toolbar(Building.this);
        getSupportActionBar().setTitle(campus+"--"+building) ;

    /*  if ( sharedPreferenceHelper.getFirstInstallation()==0  )
      {
         // myDB.createRoom("SGW","Hall Building","H-815");
          sharedPreferenceHelper.setInstallation();
      }*/

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        rooms= myDB.getRooms(building,sharedPreferenceHelper.getAllRoom());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                String old=rooms.get(0).getTime();
       /*         if (!isWifi(Building.this))
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(Building.this,"Check your connection.", Toast.LENGTH_SHORT).show();
                    return;
                }*/


                rooms.clear();
                rooms.addAll(myDB.getRooms(building,sharedPreferenceHelper.getAllRoom()));
                recyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                if (rooms.get(0).getTime().equals(old) && countSwipe<5)

                {
                    countSwipe++;
                    Toast.makeText(Building.this,"No update.",Toast.LENGTH_SHORT).show();
                }
                if (countSwipe>=5)
                {
                    setOnfresh(recycleListView);
                    countSwipe=0;
                }
            }
        });

        recycleListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Building.this);


        recycleListView.setLayoutManager(layoutManager);
        recyclerAdapter= new RecyclerAdapter(Building.this,rooms);
        recycleListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recycleListView.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(view.getTag().toString().equals("---"))
                    return;
                Intent intent = new Intent(Building.this, History.class);
                sharedPreferenceHelper.saveRoom(view.getTag().toString(),Building.this);

                startActivity(intent);

            }
        });
        recyclerAdapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if(view.getTag().toString().equals("---"))
                    return;
                currentRoom=view.getTag().toString();
                showSingleAlertDialog(view, view.getTag().toString());

            }
        });


    }
    public void showSingleAlertDialog(final View view, String room){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Choose one option");
        final String[] items;
        final String currentRoom =room;

       items = new String[] {" Get further notification ","Show more Information"};


        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
              option =index;//Toast.makeText(MainActivity.this, items[index], Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Intent intent =new Intent(MainActivity.this,Building.class);

                alertDialog2.dismiss();
                switch (option) {
                    case 0:
                        getNotice(currentRoom);

                        Snackbar.make(view, "Get the further notice of  " + currentRoom, Snackbar.LENGTH_LONG).show();
                        break;
                    case 1:
                        scanningDialog = new ProgressDialog(Building.this);
                        scanningDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        scanningDialog.setMessage("Waiting for More details of " + currentRoom);
                        scanningDialog.setIndeterminate(true);
                        scanningDialog.setCanceledOnTouchOutside(false);
                        scanningDialog.show();
                        delay_operation();
                        sendRequestWithHttpURLConnection();

                      //  Snackbar.make(view, "More details of " + currentRoom, Snackbar.LENGTH_INDEFINITE).show();

                }

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

    public void setOnfresh(View v) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        counter=new TextView(this);
        alertCounter = new CountDownTimer(6000, 1000)
        {

                @Override
                public void onTick(long millisUntilFinished) {
                counter.setText("Calm down for: " + (millisUntilFinished / 1000) + " seconds");
            }

                @Override
                public void onFinish() {
               // alertContacts(danger_message);
            }



        };
        alertCounter.start();
        dialog.setView(counter);
        // 正在加载
        dialog.setTitle("Refresh too much!");
        dialog.setMessage("Take a break.");

        dialog.setIcon(android.R.drawable.ic_dialog_alert);
                // 关闭'按钮点界面的自动取消'功能
        dialog.setCancelable(false);

        // 进度条效果
        new Handler().postDelayed(new Runnable()
        {
            AlertDialog dialog1=dialog.show();
            public void run()
            {
                dialog1.dismiss();//隐藏对话框
            }
        }, 6000);
    }

    public  void getNotice (String room)
    {
         sharedPreferenceHelper.setPreferRoom(room);
        sharedPreferenceHelper.setNotification(1);
    }



   public void delay_operation( )
    {
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                scanningDialog.dismiss();//隐藏对话框
            }
        }, 3000);
    }


    public void decodehttp(String theRoom) {



        char[] charArray = response.toCharArray();
       // if(charArray.length==0)
       // {
                //   return;
        //}
        //Vector<String> allStrings;
        String stringArray []={"","","","","","","","","","","","","","","",""};  // 4line *4 info per line
        int GenaralCounter=1;//  keep up where we are in the string
        int numberOfCharactersCounter=0;// how much charcters is each peice of information
        //String s1="";
        int arrayFillCounter=0;

        for (int i = 0; i < 4; i++)// 4 is the number of lines in the file
        {
            if( charArray[GenaralCounter-1]=='(' || charArray[GenaralCounter]=='(')// genral counter -1 is when it is =1 so 0 and the second is for the next line
            {
                // GenaralCounter = 1;

                while (charArray[GenaralCounter] != ')')
                {

                    while (charArray[GenaralCounter] != '+' && charArray[ GenaralCounter]!=')')
                    {
                        GenaralCounter++;
                        numberOfCharactersCounter++;
                    }

                    //s1="";
                    //s1 = new String(charArray, (GenaralCounter- numberOfCharactersCounter), (numberOfCharactersCounter));
                    stringArray[arrayFillCounter]=new String(charArray, (GenaralCounter- numberOfCharactersCounter), (numberOfCharactersCounter));
                    arrayFillCounter++;

                    numberOfCharactersCounter = 0;
                    GenaralCounter++;// to skip the +
                }
            }

            GenaralCounter++;//to skip the )  of the first line
            GenaralCounter++;//to skip the (   of the second line
        }



        // all this is for the pop up dialog
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Lab information");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);




        // this is for the search shit
        for(int i=0;i< stringArray.length;i=i+4)
        {
            if(theRoom.equals(stringArray[i])==true)
            {
                for(int j=i; j<(i+4) ;j++)
                {
                    arrayAdapter.add(stringArray[j]);
                }
            }
        }

        //arrayAdapter.add("why");

        builderSingle.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderSingle.show();



    }




    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //濡傛灉杩旂幇msg.what=SHOW_RESPONSE锛屽垯杩涜鍒跺畾鎿嶄綔锛屽鎯宠繘琛屽叾浠栨搷浣滐紝鍒欏湪瀛愮嚎绋嬮噷灏哠HOW_RESPONSE鏀瑰彉
            switch (msg.what) {
                case 0:
                    response = (String) msg.obj;
                    //杩涜UI鎿嶄綔锛屽皢缁撴灉鏄剧ず鍒扮晫闈笂
                    decodehttp(currentRoom);

                    //responseText.setText(decodehttp());
                    // responseText.setText(response);
            }
        }
    };

    private void sendRequestWithHttpURLConnection() {
        String line = null;//寮€鍚嚎绋嬫潵鍙戣捣缃戠粶璇锋眰

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://users.encs.concordia.ca/~g_abourj/RoomInformation.txt");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //涓嬮潰瀵硅幏鍙栧埌鐨勮緭鍏ユ祦杩涜璇诲彇
                    BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line = null;
                    while ((line = bufr.readLine()) != null) {
                        response.append(line);
                    }

                    Message message = new Message();
                    message.what = 0;
                    //灏嗘湇鍔″櫒杩斿洖鐨勬暟鎹瓨鏀惧埌Message涓?
                    message.obj = response.toString();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();


                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

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



}
