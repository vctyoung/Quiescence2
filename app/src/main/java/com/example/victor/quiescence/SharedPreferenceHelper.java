package com.example.victor.quiescence;

/**
 * Created by victor on 2017/3/4.
 */
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceHelper {
    private SharedPreferences sharedPreferences;

    // Constructor
    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ProfilePreference",
                Context.MODE_PRIVATE);
    }
    // TODO: save a profile if the input are legal, or shows the warning
    public void saveCapums(String campus,String building, Context context) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("campus", campus);
        editor.putString("building",building);
       // editor.putString("room",room);
        editor.apply();

    }
    public void saveRoom( String room,Context context) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("room",room);
        editor.apply();

    }

    public void resetDataBase(int version)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("DBVersion", version);
        editor.apply();
    }

    public int getDataDase ()
    {
        int temp ;
        temp =sharedPreferences.getInt("DBVersion",1);
        return temp;
    }
    public void setAutoUpdate(int i)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("update",i);
        editor.apply();
    }
    public int getUpdate ( )
    {
        int temp ;
        temp =sharedPreferences.getInt("update",1);
        return temp;
    }

    public void setReturnAll(int i)
    {
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putInt("allrooms",i);
        editor.commit();
    }

    public int getAllRoom( )
    {
        int temp ;
        temp =sharedPreferences.getInt("allrooms",1);
        return temp;
    }

    public void setNotification(int i)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notice",i);
        editor.apply();
    }

    public void setPreferRoom(String room)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("preroom",room);
        editor.apply();
    }

    public String getPreferRoom()
    {
        String temp ;
        temp =sharedPreferences.getString("preroom","h811");
        return temp;
    }

    public int getNotice ()
    {
        int temp ;
        temp =sharedPreferences.getInt("notice",0);
        return temp;
    }



    public void setInstallation()
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("FirstInstall",false);
        editor.apply();
    }
    public boolean getFirstInstallation()
    {
        return sharedPreferences.getBoolean("FirstInstall",true);
    }

    public void setVersion(long version)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putLong("Version",version);
        editor.apply();
    }

    public long getVersion()
    {
        return sharedPreferences.getLong("Version",0);
    }


    //TODO: return a profile to viewer
    public String getCampus ()
    {
        String temp ;
        temp =sharedPreferences.getString("campus",null);
        return temp;
    }
    public String getBuilding()
    {
        String temp ;
        temp =sharedPreferences.getString("building",null);
        return temp;
    }

    public String getRoom()
    {
        String temp ;
        temp =sharedPreferences.getString("room",null);
        return temp;
    }

}
