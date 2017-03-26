package com.example.victor.quiescence;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class History extends AppCompatActivity {

    private dataBaseHelper myDB;
    private LineChart chart;
   private String campus;
    private String building;
   private String room=null;
    private ArrayList<roomLog> logs;
    SharedPreferenceHelper sharedPreferenceHelper;
    private Calendar todayStart;
    int mYear, mMonth, mDay;
    FloatingActionButton fab;
     final int DATE_DIALOG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedPreferenceHelper = new SharedPreferenceHelper(History.this);
        todayStart = Calendar.getInstance();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
        mYear = todayStart.get(Calendar.YEAR);
        mMonth = todayStart.get(Calendar.MONTH);
        mDay = todayStart.get(Calendar.DAY_OF_MONTH);

        campus = sharedPreferenceHelper.getCampus();
        building = sharedPreferenceHelper.getBuilding();
        // building = intent.getStringExtra("building");
        room = sharedPreferenceHelper.getRoom();
        if (room.equals(new String("---")))
            return;

        getSupportActionBar().setTitle(room + " " + campus);
        myDB = new dataBaseHelper(getApplicationContext());
        logs = new ArrayList<roomLog>();
        // logs=myDB.getroomLog(room);
        logs = myDB.getroomLog(room);

        // logs = new ArrayList<roomLog>();
      /*  logs.add(new roomLog((float) 0, "10"));
        logs.add(new roomLog((float) 0, "16"));
        logs.add(new roomLog((float) 0, "20"));
        logs.add(new roomLog((float) 0, "30"));*/
        // logs.add(new roomLog( 0, room));

        chart = (LineChart) findViewById(R.id.chart);

        drawChart();
    }

        // chart.setOnChartGestureListener((OnChartGestureListener) this);
        //  chart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                DatePickerDialog dialog = new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
                DatePicker datePicker = dialog.getDatePicker();
                Calendar minCalendar = Calendar.getInstance();

                minCalendar.set(Calendar.DAY_OF_MONTH, minCalendar.get(Calendar.DAY_OF_MONTH)-7);
                datePicker.setMinDate(minCalendar.getTimeInMillis());//添加范围的最小值

                Calendar maxCalendar = Calendar.getInstance();
               // maxCalendar.add(Calendar.YEAR, 2);//设置年的范围（今年是2016，第二个参数是2则，datepicker范围为2016-2018）
                datePicker.setMaxDate(maxCalendar.getTimeInMillis());//添加范围的最小值


                return dialog;
        }
        return null;
    }


        /**
         * 设置日期 利用StringBuffer追加
         */


        private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear+1;
                mDay = dayOfMonth;
                logs=myDB.getRoomLog(room,mYear,mMonth,mDay);
                drawChart();

            }
        };
       private ArrayList<String> setXAxisValues() {
            ArrayList<String> xVals = new ArrayList<String>();



           //Date date = null; //初始化date

            for (int i=0;i<logs.size();i++)
            {
                xVals.add(logs.get(i).hours);
            }
            if (logs.size()==0)
                xVals.add("10");

            return xVals;
        }

    private void drawChart()
    {
        chart.setDrawGridBackground(false);
        setData();
        Legend lgd = chart.getLegend();
        lgd.setForm(Legend.LegendForm.LINE);
        Description dec=new Description();
        dec.setText("History of"+ room);
        chart.setDescription(dec);
        // chart.setNoDataTextDescription("You need to provide data for the chart.");
        chart.setTouchEnabled(true);


        LimitLine upper_limit = new LimitLine(2f, "Upper Limit");
        upper_limit.setLineWidth(1f);
        //  upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);
        LimitLine lower_limit = new LimitLine(0.5f, "Above is noisy.");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        lower_limit.setTextSize(12f);

        YAxis leftAxis = chart.getAxisLeft();
        XAxis axis=chart.getXAxis();
        axis.setAvoidFirstLastClipping(true);
        axis.setDrawLabels(true);
        axis.setDrawAxisLine(true);
        axis.setEnabled(true);

        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(4f);
        leftAxis.setAxisMinValue(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(true);

        chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        chart.notifyDataSetChanged();

        //  dont forget to refresh the drawing
        chart.invalidate();
    }

        private ArrayList<Entry> setYAxisValues(){
            ArrayList<Entry> yVals = new ArrayList<Entry>();
            for (int i=0;i<logs.size();i++)
                yVals.add(new Entry(i, logs.get(i).level));
            if (yVals.size()==0)
                yVals.add(new Entry(0,0));


            return yVals;
        }
        private void setData() {
            ArrayList<String> xVals = setXAxisValues();

            ArrayList<Entry> yVals = setYAxisValues();

            LineDataSet set1;

            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, " Data of "+ mYear+"/"+mMonth+"/"+mDay);


            set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            //   set1.enableDashedLine(10f, 5f, 0f);
            // set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLUE);
            set1.setCircleColor(Color.BLUE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(4f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(12f);
            set1.setDrawFilled(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData( dataSets);


            // set data
            chart.setData(data);

        }

    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    //@Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            chart.highlightValues(null);
    }

   // @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

   // @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

   // @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

   // @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

   // @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

   // @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }


    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
     /*  Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + chart.getLowestVisibleXIndex()
                + ", high: " + chart.getHighestVisibleXIndex());

   /*     Log.i("MIN MAX", "xmin: " + chart.getXChartMin()
                + ", xmax: " + chart.getXChartMax()
                + ", ymin: " + chart.getYChartMin()
                + ", ymax: " + chart.getYChartMax());*/
   }


    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
    private Long getTodayTime(){
        todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }






}
