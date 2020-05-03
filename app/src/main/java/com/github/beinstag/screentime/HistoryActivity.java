package com.github.beinstag.screentime;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDelegate;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryActivity extends SwipeActivity {

    private static final int HOURS_IN_DAY = 14;
    private static final int DAYS_IN_WEEK = 7;
    private static final int WEEKS_IN_MONTH = 8;
    private static final int DAYS_IN_MONTH = 30;
    private static final int MONTH_IN_YEAR = 12;
    ArrayList<BarEntry> barEntries;
    static ArrayList<String> barLabels;

    Configuration configuration;
    DataManager dataManager;
    int green, darkGreen;
    int blue, darkBlue;
    int red, darkRed;
    int yellow, darkYellow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        green = getResources().getColor(R.color.colorGreen);
        darkGreen = getResources().getColor(R.color.colorDarkGreen);
        blue = getResources().getColor(R.color.colorBlue);
        darkBlue = getResources().getColor(R.color.colorDarkBlue);
        red = getResources().getColor(R.color.colorRed);
        darkRed = getResources().getColor(R.color.colorDarkRed);
        yellow = getResources().getColor(R.color.colorYellow);
        darkYellow = getResources().getColor(R.color.colorDarkYellow);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        dataManager = new DataManager(getApplicationContext());
        configuration = getApplicationContext().getResources().getConfiguration();

        barEntries = loadDayDataHistory();

        Button bWeek = findViewById(R.id.button);
        Button bMonth = findViewById(R.id.button2);
        Button bYear = findViewById(R.id.button3);
        Button bDay = findViewById(R.id.button4);


        int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barEntries = loadDayDataHistory();
                    presentChart(barEntries,
                            getResources().getColor(R.color.colorYellow));
                }
            });
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
                    presentChart(barEntries,
                            getResources().getColor(R.color.colorRed));
                }
            });
            bYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    barEntries = loadYearDataHistory();
                    presentChart(barEntries,
                            getResources().getColor(R.color.colorGreen));
                }
            });
        }
        presentChart(barEntries,
                getResources().getColor(R.color.colorYellow));
    }

    @Override
    protected void onSwipeRight() {
        if (barEntries.size() == HOURS_IN_DAY) {
            barEntries = loadYearDataHistory();
            presentChart(barEntries, green);
        } else if (barEntries.size() == DAYS_IN_WEEK) {
            barEntries = loadDayDataHistory();
            presentChart(barEntries, yellow);
        } else if (barEntries.size() == WEEKS_IN_MONTH) {
            barEntries = loadWeekDataHistory();
            presentChart(barEntries, blue);
        } else {
            barEntries = loadMonthDataHistory();
            presentChart(barEntries, red);
        }
    }

    @Override
    protected void onSwipeLeft() {

        if (barEntries.size() == HOURS_IN_DAY) {
            barEntries = loadWeekDataHistory();
            presentChart(barEntries, blue);
        } else if (barEntries.size() == DAYS_IN_WEEK) {
            barEntries = loadMonthDataHistory();
            presentChart(barEntries, red);
        } else if (barEntries.size() == WEEKS_IN_MONTH) {
            barEntries = loadYearDataHistory();
            presentChart(barEntries, green);
        } else {
            barEntries = loadDayDataHistory();
            presentChart(barEntries, yellow);
        }
    }

    public ArrayList<BarEntry> loadYearDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < MONTH_IN_YEAR; ++i) {
            int out = 0;
            for (int j = 0; j < DAYS_IN_MONTH; ++j) {
                out += dataManager.loadPastScreenDuration(DAYS_IN_MONTH * i + j);
            }
            entries.add(new BarEntry(i, out));

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -DAYS_IN_MONTH * i);
            barLabels.add(cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                    configuration.locale));
        }
        return entries;
    }


    public ArrayList<BarEntry> loadMonthDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < WEEKS_IN_MONTH; ++i) {
            int out = 0;
            for (int j = 0; j < DAYS_IN_WEEK; ++j) {
                out += dataManager.loadPastScreenDuration(DAYS_IN_WEEK * i + j);
            }
            entries.add(new BarEntry(i, out));

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -DAYS_IN_WEEK * i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                barLabels.add("" + cal.get(Calendar.WEEK_OF_YEAR));
            }
        }
        return entries;
    }


    public ArrayList<BarEntry> loadWeekDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < DAYS_IN_WEEK; ++i) {
            int out = dataManager.loadPastScreenDuration(i);
            entries.add(new BarEntry(i, out));
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -i);
            barLabels.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
                    configuration.locale));
        }

        return entries;
    }


    public ArrayList<BarEntry> loadDayDataHistory() {
        barLabels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < HOURS_IN_DAY; ++i) {
            int out = dataManager.loadPastHoursScreenDuration(i);
            entries.add(new BarEntry(i, out));
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, -i);
            barLabels.add("" + cal.get(Calendar.HOUR_OF_DAY));
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
        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.app_name)); // add entries to dataset
        dataSet.setColors(color);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorText));
        dataSet.setBarShadowColor(getResources().getColor(R.color.colorBackground));
        dataSet.setHighlightEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        YAxis yLAxis = barChart.getAxisLeft();
        YAxis yRAxis = barChart.getAxisRight();
        xAxis.setEnabled(true);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorText));
        yLAxis.setEnabled(false);
        yRAxis.setEnabled(false);
        xAxis.setValueFormatter(new MyXAxisFormatter());
        Legend legend = barChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.colorText));
        BarData lineData = new BarData(dataSet);
        lineData.setValueTextSize(lineData.getBarWidth() * 15);
        lineData.setDrawValues(true);
        if (lineData.getEntryCount() == HOURS_IN_DAY) {
            lineData.setValueFormatter(new MyHourlyValueFormatter(getApplicationContext()));
            ArrayList<LegendEntry> LE = new ArrayList<>();
            LE.add(new LegendEntry("Temps d'écran par heure (en minutes)", Legend.LegendForm.CIRCLE, 12f,1f, null, yellow));
            legend.setCustom(LE);
        } else if (lineData.getEntryCount() == DAYS_IN_WEEK) {
            lineData.setValueFormatter(new MyDailyValueFormatter(getApplicationContext()));
            ArrayList<LegendEntry> LE = new ArrayList<>();
            LE.add(new LegendEntry("Temps d'écran par jour (hh:mm)", Legend.LegendForm.CIRCLE, 12f,1f, null, blue));
            legend.setCustom(LE);
        } else if (lineData.getEntryCount() == WEEKS_IN_MONTH) {
            lineData.setValueFormatter(new MyWeeklyValueFormatter(getApplicationContext()));
            ArrayList<LegendEntry> LE = new ArrayList<>();
            LE.add(new LegendEntry("Temps d'écran par semaine (hh:mm)", Legend.LegendForm.CIRCLE, 12f,1f, null, red));
            legend.setCustom(LE);
        } else {
            lineData.setValueFormatter(new MyMonthlyValueFormatter(getApplicationContext()));
            ArrayList<LegendEntry> LE = new ArrayList<>();
            LE.add(new LegendEntry("Temps d'écran par mois", Legend.LegendForm.CIRCLE, 12f,1f, null, green));
            legend.setCustom(LE);
        }

        barChart.setData(lineData);
        barChart.setDrawGridBackground(true);
        barChart.setGridBackgroundColor(Color.TRANSPARENT);
        barChart.setAutoScaleMinMaxEnabled(false);
        ArrayList<Integer> colorsLabel = new ArrayList<>();
        for (int i = 0; i < barLabels.size(); ++i)
            colorsLabel.add(getResources().getColor(R.color.colorText));
        barChart.setXAxisRenderer(new MyXAxisRenderer(barChart.getViewPortHandler(), xAxis, barChart.getTransformer(null), colorsLabel));
        barChart.setScaleEnabled(false);
        barChart.setClickable(false);
        barChart.setDrawBarShadow(true);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setText("");

        barChart.invalidate();
    }


    static class MyMonthlyValueFormatter extends ValueFormatter {
        // override this for e.g. LineChart or ScatterChart
        Context c;

        MyMonthlyValueFormatter(Context c) {
            this.c = c;
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return new DurationParser(c).parseToDayHourFormat((int) barEntry.getY());
        }
    }

    static class MyWeeklyValueFormatter extends ValueFormatter {
        // override this for e.g. LineChart or ScatterChart
        Context c;

        MyWeeklyValueFormatter(Context c) {
            this.c = c;
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return new DurationParser(c).parseToHourMinuteFormat((int) barEntry.getY());
        }
    }

    static class MyDailyValueFormatter extends ValueFormatter {
        // override this for e.g. LineChart or ScatterChart
        Context c;

        MyDailyValueFormatter(Context c) {
            this.c = c;
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return new DurationParser(c).parseToHourMinuteFormat((int) barEntry.getY());
        }
    }

    static class MyHourlyValueFormatter extends ValueFormatter {
        // override this for e.g. LineChart or ScatterChart
        Context c;

        MyHourlyValueFormatter(Context c) {
            this.c = c;
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            String label = new DurationParser(c).parseToMinuteSecondsFormat((int) barEntry.getY());
            if((int) barEntry.getY() == 0)
                return "0";
            if (label.length() > 2)
                if (label.charAt(2) == 's')
                    return "<1";
                else
                    return new DurationParser(c).parseToMinuteSecondsFormat((int) barEntry.getY()).substring(0, 2);
            return label;
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

        ArrayList<Integer> labelColors;

        MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, ArrayList<Integer> colors) {
            super(viewPortHandler, xAxis, trans);
            this.labelColors = colors;
        }


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
                if (mViewPortHandler.isInBoundsX(x)) {

                    String label = mXAxis.getValueFormatter().getAxisLabel(mXAxis.mEntries[i / 2], mXAxis);
                    int color = getColorForXValue((int) mXAxis.mEntries[i / 2]); //added

                    mAxisLabelPaint.setColor(color);

                    if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                        // avoid clipping of the last
                        if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
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


        private int getColorForXValue(int index) {
            if (index >= labelColors.size()) return mXAxis.getTextColor();

            if (index < 0) return mXAxis.getTextColor();
            if (index == 0) return Color.RED;
            return labelColors.get(index);
        }
    }
}










