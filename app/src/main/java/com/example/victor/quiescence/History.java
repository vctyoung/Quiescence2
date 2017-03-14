package com.example.victor.quiescence;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

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

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private dataBaseHelper myDB;
    private LineChart chart;
   private String campus;
    private String building;
   private String room=null;
    private ArrayList<roomLog> logs;
    SharedPreferenceHelper sharedPreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedPreferenceHelper=new SharedPreferenceHelper(History.this);

        campus= sharedPreferenceHelper.getCampus();
        building =sharedPreferenceHelper.getBuilding();
        // building = intent.getStringExtra("building");
        room = sharedPreferenceHelper.getRoom();
        if (room=="---")
            return;

        getSupportActionBar().setTitle(room + " " + campus);
        myDB= new dataBaseHelper(getApplicationContext());
        logs=new ArrayList<roomLog>();
        logs=myDB.getroomLog(room);

      // logs = new ArrayList<roomLog>();
      /*  logs.add(new roomLog((float) 0, "10"));
        logs.add(new roomLog((float) 0, "16"));
        logs.add(new roomLog((float) 0, "20"));
        logs.add(new roomLog((float) 0, "30"));*/
       // logs.add(new roomLog( 0, room));

        chart = (LineChart) findViewById(R.id.chart);

        // chart.setOnChartGestureListener((OnChartGestureListener) this);
        //  chart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);
       chart.setDrawGridBackground(false);
       setData();
        Legend lgd = chart.getLegend();
        lgd.setForm(Legend.LegendForm.LINE);
        Description dec=new Description();
        dec.setText("History of"+ room);
        chart.setDescription(dec);
       // chart.setNoDataTextDescription("You need to provide data for the chart.");
        chart.setTouchEnabled(true);

        LimitLine upper_limit = new LimitLine(130f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);
        LimitLine lower_limit = new LimitLine(-1f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(4f);
        leftAxis.setAxisMinValue(-4f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);

        chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        //  dont forget to refresh the drawing
        chart.invalidate();
    }

       private ArrayList<String> setXAxisValues() {
            ArrayList<String> xVals = new ArrayList<String>();
            for (int i=0;i<logs.size();i++)
            {
                xVals.add(logs.get(i).hours);
            }
            if (logs.size()==0)
                xVals.add("10");

            return xVals;
        }

        private ArrayList<Entry> setYAxisValues(){
            ArrayList<Entry> yVals = new ArrayList<Entry>();
            for (int i=0;i<logs.size();i++)
                yVals.add(new Entry(logs.get(i).level, i));
            if (yVals.size()==0)
                yVals.add(new Entry(10,0));


            return yVals;
        }
        private void setData() {
            ArrayList<String> xVals = setXAxisValues();

            ArrayList<Entry> yVals = setYAxisValues();

            LineDataSet set1;

            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");


            set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            //   set1.enableDashedLine(10f, 5f, 0f);
            // set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
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






}
