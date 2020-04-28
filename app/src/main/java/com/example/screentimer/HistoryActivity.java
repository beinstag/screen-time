package com.example.screentimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Map;

public class HistoryActivity extends Activity {
    static ArrayList<String> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        loadDataHistory();
    }

    public static String parseDuration(Integer duration) {
        double seconds = duration;
        double minutes = Math.floor(seconds / 60);
        double hours = Math.floor(minutes / 60);
        String sDuration = "";

        if (hours > 0) sDuration += (int) hours % 24 + "h";
        if (minutes > 0) sDuration += (int) minutes % 60 + "m";
        sDuration += (int) seconds % 60 + "s";
        return sDuration;
    }


    public void loadDataHistory() {

        String SHARED_PREFS = "sharePrefs";
        BarChart barChart = findViewById(R.id.chart);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Map<String, ?> data = sharedPreferences.getAll();

        ArrayList<BarEntry> entries = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, ?> entry : data.entrySet()) {

            String key = entry.getKey();
            if (key.equals("date"))
                dates.add(entry.getValue().toString());
            else if (key.equals("time")) {
                entries.add(new BarEntry(data.size() - i, (Integer) entry.getValue()));
            } else {
                dates.add(entry.getKey());
                entries.add(new BarEntry(data.size() - i, (Integer) entry.getValue()));
            }
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.notificationTitle)); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.DKGRAY);
        BarData lineData = new BarData(dataSet);
        lineData.setValueTextSize(25);
        lineData.setValueFormatter(new MyValueFormatter());
        XAxis xAxis = barChart.getXAxis();
        YAxis yLAxis = barChart.getAxisLeft();
        YAxis yRAxis = barChart.getAxisRight();
        xAxis.setEnabled(false);
        yLAxis.setEnabled(false);
        yRAxis.setEnabled(false);
        barChart.setData(lineData);
        barChart.setDrawGridBackground(true);
        barChart.setGridBackgroundColor(Color.TRANSPARENT);
        barChart.setAutoScaleMinMaxEnabled(false);
        barChart.invalidate();
    }


    static class MyValueFormatter extends ValueFormatter {
        private StringBuilder format = new StringBuilder();

        // override this for e.g. LineChart or ScatterChart
        @Override
        public String getPointLabel(Entry e) {
            return parseDuration((int)e.getY());
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return parseDuration((int)barEntry.getY());
        }

        // override this for custom formatting of XAxis or YAxis labels
        /*public String getAxisLabel(Float value, AxisBase axis) {
            return format.format(value);
        }*/
    }

    static class MyXAxisFormatter extends ValueFormatter {
        @SuppressLint("DefaultLocale")
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return dates.get((int)value % dates.size());
        }
    }
}
