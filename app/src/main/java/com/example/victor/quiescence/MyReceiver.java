package com.example.victor.quiescence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent intent1 =new Intent(context, upDate.class);
        context.startService(intent1);
       // throw new UnsupportedOperationException("Not yet implemented");
    }
}
