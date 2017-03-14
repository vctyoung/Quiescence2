package com.example.victor.quiescence;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by victor on 2017/3/10.
 */

public class connectServor {


    public static final String urlPath="http://elec390.encs.concordia.ca/data_file";
    public static final  int SHOW_RESPONSE=0;


        public static String readParse() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据
    }

    private static Handler handler=new Handler(){
        public void handleMessage(Message msg){
                     String response=(String)msg.obj;
            Log.d("connect", response);
        }
    };

    public static void sendRequestWithHttpURLConnection(){
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
                        response.append(line);
                    }

                    Message message=new Message();
                    message.what=SHOW_RESPONSE;
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