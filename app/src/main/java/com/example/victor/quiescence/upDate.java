package com.example.victor.quiescence;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class upDate extends Service {
    private  final int PERIOD=30000;
    private dataBaseHelper myDB;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private Notification mNotification;
    private NotificationManager mNotificationManager;


    //   public upDate() {
   // }
    @Override
    public void onCreate()
    {
        super.onCreate();
        myDB= new dataBaseHelper(getApplicationContext());
        sharedPreferenceHelper= new SharedPreferenceHelper(upDate.this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mNotification = new NotificationCompat.Builder(this).
        setSmallIcon(R.drawable.concordia_coa).setContentTitle("The room you are looking for is quiet now!")
                .setContentText("Sent by Quiescence").setDefaults(NotificationCompat.DEFAULT_SOUND);


    }

    public void  onDestroy()
    {
        super.onDestroy();

    }
    public  int onStartCommand (Intent intent, int flags, int startId)
    {
        AlarmManager manager=(AlarmManager) getSystemService(Service.ALARM_SERVICE);
        long period= 5*1000*60;
        long nextTime= SystemClock.elapsedRealtime()+ period;
        Intent refresh= new Intent( this, MyReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,0,refresh,0);
        sendRequestWithHttpURLConnection();
        manager.set(manager.ELAPSED_REALTIME_WAKEUP, nextTime,pendingIntent);

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    //用于处理和发送消息的Hander
    private Handler mhandler=new Handler(){
        public void handleMessage(Message msg) {
            //如果返现msg.what=SHOW_RESPONSE，则进行制定操作，如想进行其他操作，则在子线程里将SHOW_RESPONSE改变
            String response = (String) msg.obj;
            String[] data=response.split(" ");

            for (int i=2; i<data.length;)

            {
                float noise = Float.parseFloat(data[i]);
                String building= checkBuilding(data[i+1]);
                String room=data[i+2];
                if (sharedPreferenceHelper.getNotice()==1 && sharedPreferenceHelper.getPreferRoom().equals(new String( room)) && noise<=1)
                {
                    mNotificationManager.notify(0, mNotification);
                }

               // myDB.createRoom("SGW",building,room,noise);
                myDB.updateRoom(building,room,noise);
                //myDB.

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
