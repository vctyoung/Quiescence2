package com.example.victor.quiescence;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private FloatingActionButton back;
   // private static  String[] ylabel= {"Quite","Noise"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_history);
        sharedPreferenceHelper = new SharedPreferenceHelper(History.this);
        todayStart = Calendar.getInstance();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        back = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mYear = todayStart.get(Calendar.YEAR);
        mMonth = todayStart.get(Calendar.MONTH)+1;
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
        logs=myDB.getRoomLog(room,mYear,mMonth,mDay);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setVisibleXRangeMaximum(15);
       // chart.On

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

                datePicker.setMaxDate(maxCalendar.getTimeInMillis());//添加范围的最小值


                return dialog;
        }
        return null;
    }

        private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear+1;
                mDay = dayOfMonth;
                logs=myDB.getRoomLog(room,mYear,mMonth,mDay);
                Log.i("dar", "size="+ logs.size());
                drawChart();

            }
        };
       private ArrayList<String> setXAxisValues() {
            ArrayList<String> xVals = new ArrayList<String>();



           //Date date = null; //初始化date
           String temp;

            for (int i=0;i<logs.size();i++)
            {

                xVals.add(logs.get(i).hours);
            }
            if (logs.size()==0)
                xVals.add("0");

            return xVals;
        }

    private void drawChart()
    {
        chart.setDrawGridBackground(false);
        setData();
        Legend lgd = chart.getLegend();
        lgd.setForm(Legend.LegendForm.LINE);
        Description dec=new Description();
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        dec.setText("History of "+ room);
        chart.setDescription(dec);
        // chart.setNoDataTextDescription("You need to provide data for the chart.");
        chart.setTouchEnabled(true);


        LimitLine upper_limit = new LimitLine(3f, "Upper Limit");
        upper_limit.setLineWidth(1f);
        //  upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);
        LimitLine lower_limit = new LimitLine(1f, "Above is noisy.");
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
        axis.setGranularity(1f);
        Toast.makeText(History.this,"Start from:"+logs.get(0).hours,Toast.LENGTH_LONG).show();
        axis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");
            long start = stringToLong(logs.get(0).hours);

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                //Log.i("value", "is:"+value);
                long start = stringToLong(logs.get(0).hours);
              //  long millis = TimeUnit.DAYS.toMillis((long) value);
              //  Log.i("value", "is:"+value+"VVV"+millis);
                return mFormat.format(new Date(((long) value)+start));
            }
        });

        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(2.5f);
        leftAxis.setAxisMinValue(-0.5f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLabels(true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(true);


        chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        //chart.notifyDataSetChanged();

        //  dont forget to refresh the drawing
        chart.invalidate();
    }

        private ArrayList<Entry> setYAxisValues(){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

              // long start= stringToLong(mYear+"-"+mMonth+"-"+mDay+" 08:00:00");
            long start = stringToLong(logs.get(0).hours);

            ArrayList<Entry> yVals = new ArrayList<Entry>();
            for (int i=0;i<logs.size();i++)
            {

                yVals.add(new Entry(stringToLong(logs.get(i).hours)-start, logs.get(i).level));
            }
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
            set1.setCircleRadius(2f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(12f);
            set1.setDrawFilled(false);
            set1.setDrawValues(false);
          //  set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

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
    //    if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
     //       // or highlightTouch(null) for callback to onNothingSelected(...)
       //     chart.highlightValues(null);
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
      //  drawChart();Log.i("SingleTap", "Chart single-tapped.");
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

    public  long stringToLong(String strTime)
          //  throws ParseException
    {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(strTime);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

}
