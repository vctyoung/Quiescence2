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

    public void setInstallation()
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("FirstInstall", 1);
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
    public int getFirstInstallation()
    {
        return sharedPreferences.getInt("FirstInstall",0);
    }
}
