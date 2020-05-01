package com.github.beinstag.screentime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends SwipeActivity {
    ArrayList<BarEntry> barEntries;
    static ArrayList<String> barLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        barEntries = loadWeekDataHistory();
        Button bWeek = findViewById(R.id.button);
        Button bMonth = findViewById(R.id.button2);
        Button bYear = findViewById(R.id.button3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barEntries = loadWeekDataHistory();
                    presentChart(barEntries,
                            getResources().getColor(R.color.colorBlue));
                }
            });
            bMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barEntries = loadMonthDataHistory();
                    presentChart(barEntries, getResources().getColor(R.color.colorRed));
                }
            });
            bYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barEntries = loadYearDataHistory();
                    presentChart(barEntries, getResources().getColor(R.color.colorGreen));
                }
            });
        }
        presentChart(barEntries, getResources().getColor(R.color.colorBlue));
    }

    @Override
    protected void onSwipeRight() {
        if (barEntries.size() == 7) {
            barEntries = loadYearDataHistory();
            presentChart(barEntries, getResources().getColor(R.color.colorGreen));
        } else if (barEntries.size() == 4) {
            barEntries = loadWeekDataHistory();
            presentChart(barEntries,
                    getResources().getColor(R.color.colorBlue));
        } else {
            barEntries = loadMonthDataHistory();
            presentChart(barEntries, getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    protected void onSwipeLeft() {
        if (barEntries.size() == 7) {
            barEntries = loadMonthDataHistory();
            presentChart(barEntries, getResources().getColor(R.color.colorRed));

        } else if (barEntries.size() == 4) {
            barEntries = loadYearDataHistory();
            presentChart(barEntries, getResources().getColor(R.color.colorGreen));

        } else {
            barEntries = loadWeekDataHistory();
            presentChart(barEntries,
                    getResources().getColor(R.color.colorBlue));
        }
    }

    public ArrayList<BarEntry> loadYearDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        DataManager dataManager = new DataManager(getApplicationContext());
        for (int i = 0; i < 12; ++i) {
            int out = 0;
            for (int j = 0; j < 30; ++j) {
                out += dataManager.loadPastScreenDuration(7 * i + j);
            }
            entries.add(new BarEntry(i, out));
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -30 * i);
            barLabels.add(cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                    getApplicationContext().getResources().getConfiguration().locale));

        }
        return entries;
    }


    public ArrayList<BarEntry> loadMonthDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        DataManager dataManager = new DataManager(getApplicationContext());
        for (int i = 0; i < 4; ++i) {
            int out = 0;
            for (int j = 0; j < 7; ++j) {
                out += dataManager.loadPastScreenDuration(7 * i + j);
            }
            entries.add(new BarEntry(i, out));
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7 * i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                barLabels.add("" + cal.get(Calendar.WEEK_OF_YEAR));
            }
        }
        return entries;
    }


    public ArrayList<BarEntry> loadWeekDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        DataManager dataManager = new DataManager(getApplicationContext());
        for (int i = 0; i < 7; ++i) {
            int out = dataManager.loadPastScreenDuration(i);
            entries.add(new BarEntry(i, out));
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            barLabels.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
                    getApplicationContext().getResources().getConfiguration().locale));
        }

        return entries;
    }

    public void presentChart(ArrayList<BarEntry> entries, int color) {
        BarChart barChart = findViewById(R.id.chart);
        WindowManager wp = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        assert wp != null;
        Display display = wp.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int s = entries.size();
        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.app_name)); // add entries to dataset
        dataSet.setColors(color);
        BarData lineData = new BarData(dataSet);
        // lineData.setValueTextSize(lineData.getBarWidth());
        lineData.setValueFormatter(new MyValueFormatter(getApplicationContext()));
        barChart.setData(lineData);
        barChart.setDrawGridBackground(true);
        barChart.setGridBackgroundColor(getResources().getColor(R.color.colorPaper));
        barChart.setAutoScaleMinMaxEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        YAxis yLAxis = barChart.getAxisLeft();
        YAxis yRAxis = barChart.getAxisRight();
        xAxis.setEnabled(true);
        yLAxis.setEnabled(false);
        yRAxis.setEnabled(false);
        xAxis.setValueFormatter(new MyXAxisFormatter());
        barChart.setXAxisRenderer(new MyXAxisRenderer(barChart.getViewPortHandler(), xAxis, barChart.getTransformer(null)));
        barChart.setScaleEnabled(false);
        barChart.setClickable(false);
        barChart.setDrawBarShadow(true);
        barChart.getDescription().setText("");
        barChart.invalidate();
    }


    static class MyValueFormatter extends ValueFormatter {
        // override this for e.g. LineChart or ScatterChart
        Context c;

        MyValueFormatter(Context c){
            this.c = c;
        }

        @Override
        public String getPointLabel(Entry e) {
            return new DurationParser(c).parseToTimeFormat((int) e.getY());
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return new DurationParser(c).parseToTimeFormat((int) barEntry.getY());
        }

    }

    static class MyXAxisFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            axis.setLabelCount(barLabels.size());
            String label = barLabels.get((int) value % barLabels.size());
            return "" + label;
        }
    }

    static class MyXAxisRenderer extends XAxisRenderer {

        MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        /**
         * draws the x-labels on the specified y-position
         *
         * @param pos
         */
        @Override
        protected void drawLabels(Canvas c, float pos, MPPointF anchor) {


            final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
            boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

            float[] positions = new float[mXAxis.mEntryCount * 2];

            for (int i = 0; i < positions.length; i += 2) {

                // only fill x values
                if (centeringEnabled) {
                    positions[i] = mXAxis.mCenteredEntries[i / 2];
                } else {
                    positions[i] = mXAxis.mEntries[i / 2];
                }
            }

            mTrans.pointValuesToPixel(positions);

            for (int i = 0; i < positions.length; i += 2) {

                float x = positions[i];
                if (i == 0)
                    mAxisLabelPaint.setColor(Color.RED);
                else
                    mAxisLabelPaint.setColor(mXAxis.getTextColor());
                if (mViewPortHandler.isInBoundsX(x)) {

                    String label = mXAxis.getValueFormatter().getAxisLabel(mXAxis.mEntries[i / 2], mXAxis);

                    if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                        // avoid clipping of the last
                        if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                            float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                            if (width > mViewPortHandler.offsetRight() * 2
                                    && x + width > mViewPortHandler.getChartWidth())
                                x -= width / 2;

                            // avoid clipping of the first
                        } else if (i == 0) {

                            float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                            x += width / 2;
                        }
                    }

                    drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
                }
            }
        }
    }
}
