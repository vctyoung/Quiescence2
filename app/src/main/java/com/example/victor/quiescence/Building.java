package com.example.victor.quiescence;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Building extends AppCompatActivity {

    //private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private dataBaseHelper myDB;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recycleListView;
    private String campus;
    private String building;
    private SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_building);
        myDB=new dataBaseHelper(Building.this);
        sharedPreferenceHelper=new SharedPreferenceHelper(Building.this);
        ArrayList<Room> rooms= new ArrayList<>();
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
      /*  myDB.createRoom("SGW","Hall Building","H-814");
        myDB.createRoom("SGW","Hall Building","H-812");
        myDB.createRoom("SGW","Hall Building","H-813");
        myDB.createRoom("SGW","Hall Building","H-811");
        myDB.createRoom("SGW","Hall Building","H-810");
        myDB.createRoom("SGW","Hall Building","H-915");
        myDB.createRoom("SGW","Hall Building","H-415");
        myDB.createRoom("SGW","Hall Building","H-615");
        myDB.createRoom("SGW","Webster","LIB-515");*/
        //ArrayList<roomLog> roomlog= myDB.getroomLog("H-815");*/
        // myDB.addTime();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                //重新获取数据
                //获取完成
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        rooms= myDB.getRooms(building);
        recycleListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Building.this);


        recycleListView.setLayoutManager(layoutManager);
        recyclerAdapter= new RecyclerAdapter(Building.this,rooms);
        recycleListView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(Building.this, History.class);
                sharedPreferenceHelper.saveRoom(view.getTag().toString(),Building.this);

                startActivity(intent);

            }
        });
    }


}
