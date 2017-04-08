package com.example.victor.quiescence;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Startup extends AppCompatActivity {

    private Handler handler = new Handler();
    dataBaseHelper myDB= new dataBaseHelper(Startup.this);
    private Intent serviceIntent;
    SharedPreferenceHelper sharedPreferenceHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        NO Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_startup);
        serviceIntent = new Intent(Startup.this, upDate.class);
        sharedPreferenceHelper= new SharedPreferenceHelper(Startup.this);

       if (sharedPreferenceHelper.getUpdate()==1)
           startService(serviceIntent);

       // sendRequestWithHttpURLConnection();
       /* try {
            String data=connectServor.readParse();

        } catch ( Exception e)
        {
            e.printStackTrace();
        } ;*/

         handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent intent = new Intent(Startup.this, MainActivity.class);
                    startActivity(intent);



                finish();
            }
        }, 3000);



    }


 //   public static final int SHOW_RESPONSE=0;//用于更新操作
    //用于处理和发送消息的Hander
    private Handler mhandler=new Handler(){
        public void handleMessage(Message msg) {
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            String response = (String) msg.obj;
            String[] data=response.split(" ");
            //进行UI操作，将结果显示到界面上
            for (int i=2; i<data.length;)

                {
                    float noise = Float.parseFloat(data[i]);
                    String building= checkBuilding(data[i+1]);
                    String room=data[i+2];
                   myDB.createRoom("SGW",building,room,noise);
                  //  myDB.updateRoom(building,room,noise);
                    myDB.addItem(building,room,noise);


                    i=i+3;
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
                    URL url=new URL("http://elec390.encs.concordia.ca/data_file");
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
                    mhandler.sendMessage(message);
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
    public String checkBuilding(String id)
    {
        String buidling ;
        switch (Integer.parseInt(id))
        {
            case 2:
                buidling = "EV Buidling";
                break;
            case 1 :
                buidling = "Hall Building";
                break;
            case 3:
                buidling="Webster";
                break;
            default:
                buidling="JMSB";
        }

        return buidling;
    }
}