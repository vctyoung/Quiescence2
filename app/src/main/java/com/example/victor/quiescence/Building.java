package com.example.victor.quiescence;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
                //TODO show help
                rooms= myDB.getRooms(building,sharedPreferenceHelper.getAllRoom());
                showDescription();
                recyclerAdapter.notifyDataSetChanged(); //获取完成
                swipeRefreshLayout.setRefreshing(false);
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
                        showDescription();
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

    public  void getNotice (String room)
    {
         sharedPreferenceHelper.setPreferRoom(room);
        sharedPreferenceHelper.setNotification(1);
    }



   /* public void delay_operation(long time)
    {
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                scanningDialog.dismiss();//隐藏对话框
            }
        }, 3000);
    }*/

    public void showDescription()
    {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(3000);
                    // cancel和dismiss方法本质都是一样的，都是从屏幕中删除Dialog,唯一的区别是
                    // 调用cancel方法会回调DialogInterface.OnCancelListener如果注册的话,dismiss方法不会回掉
                    scanningDialog.cancel();
                    // dialog.dismiss();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

    }


    public void decodehttp(String theRoom) {

        char[] charArray = response.toCharArray();
        //Vector<String> allStrings;
        String stringArray []={"","","","","","","",""};  // 2line *4 info per line
        int GenaralCounter=1;//  keep up where we are in the string
        int numberOfCharactersCounter=0;// how much charcters is each peice of information
        //String s1="";
        int arrayFillCounter=0;


        for (int i = 0; i < 2; i++)// 2 is the number of lines in the file
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



        scanningDialog.dismiss();
        // all this is for the pop up dialog
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Lab information");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);




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
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            switch (msg.what) {
                case 0:
                    response = (String) msg.obj;
                    //进行UI操作，将结果显示到界面上

                    decodehttp("h-8xx");

                    //responseText.setText(decodehttp());
                    // responseText.setText(response);
            }
        }
    };
    private void sendRequestWithHttpURLConnection(){
        String line=null;//开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try{
                    URL url=new URL("https://users.encs.concordia.ca/~g_abourj/RoomInformation.txt");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    InputStream in=connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    BufferedReader bufr=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line=null;
                    while((line=bufr.readLine())!=null){
                        response.append(line+" ");
                    }

                    Message message=new Message();
                    message.what=0;
                    //将服务器返回的数据存放到Message中
                    message.obj=response.toString();
                    handler.sendMessage(message);
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }




}
