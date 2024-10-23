package com.example.slozenyurok;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartType extends AppCompatActivity {

    private int deposit;
    private double totalInterest;
    BarChart barChart;
    PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        deposit = intent.getIntExtra("deposit", 0);  // 0 je výchozí hodnota, pokud data nepřijdou
        totalInterest = intent.getDoubleExtra("totalInterest", 0.0);
        // Najdi karty podle jejich ID

        View barChartOverlay = findViewById(R.id.barChartOverlay);
        View pieChartOverlay = findViewById(R.id.pieChartOverlay);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        updateBarChart(deposit, totalInterest);
        updatePieChart(deposit, totalInterest);



        barChartOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Odeslání výsledku zpět do MainActivity pro BarChart
                Intent resultIntent = new Intent();
                resultIntent.putExtra("chart_type", "bar");  // Posíláme zpět "bar"
                resultIntent.putExtra("deposit", deposit);  // Předáme zpět i hodnoty
                resultIntent.putExtra("totalInterest", totalInterest);
                setResult(RESULT_OK, resultIntent);
                finish();  // Ukončení aktivity
            }
        });

        // Nastav onClickListener pro PieChart
        pieChartOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Odeslání výsledku zpět do MainActivity pro PieChart
                Intent resultIntent = new Intent();
                resultIntent.putExtra("chart_type", "pie");  // Posíláme zpět "pie"
                resultIntent.putExtra("deposit", deposit);  // Předáme zpět i hodnoty
                resultIntent.putExtra("totalInterest", totalInterest);
                setResult(RESULT_OK, resultIntent);
                finish();  // Ukončení aktivity
            }
        });
    }

    private void updateBarChart(int deposit, double totalInterest) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) deposit));  // Deposit bar
        entries.add(new BarEntry(1, (float) totalInterest)); // Interest bar

        BarDataSet dataSet = new BarDataSet(entries, "Vklad/Úroky");
        dataSet.setColors(new int[] {
                getResources().getColor(R.color.main_color), // Barva pro vklad
                getResources().getColor(R.color.secondary_color) // Barva pro úrok
        });
        BarData data = new BarData(dataSet);

        // Set data for the chart
        barChart.setData(data);
        barChart.invalidate();  // Redraw the chart with new data

        // Customize the chart appearance
        customizeBarChart();
    }

    // New helper method for customizing the chart appearance
    private void customizeBarChart() {
        barChart.getAxisLeft().setAxisMinimum(0);  // Ensure the Y-axis starts from 0
        barChart.getAxisRight().setAxisMinimum(0);  // Ensure the Y-axis starts from 0 (if right axis is enabled)

        // Disable grid lines
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // Set the value text size for the bars
        barChart.getData().getDataSetByIndex(0).setValueTextSize(16f);

        // Set extra spacing for the chart
        barChart.setExtraOffsets(0, 0, 0, 5);

        // Set label and position of values above bars
        BarDataSet dataSet = (BarDataSet) barChart.getData().getDataSetByIndex(0);
        dataSet.setLabel("");
        dataSet.setDrawValues(true);

        // Position the X-axis labels at the bottom
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void updatePieChart(int deposit, double totalInterest) {
        // Pie chart data
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) deposit, "Vklad"));
        entries.add(new PieEntry((float) totalInterest, "Úroky"));

        // Create PieDataSet
        PieDataSet dataSet = new PieDataSet(entries, "Vklad/Úroky");
        dataSet.setColors(new int[] {
                getResources().getColor(R.color.main_color), // Vklad barva
                getResources().getColor(R.color.secondary_color) // Úroky barva
        });

        // Create PieData
        PieData data = new PieData(dataSet);
        data.setValueTextSize(16f);  // Nastavení velikosti textu

        // Set data for the PieChart
        pieChart.setData(data);
        pieChart.invalidate();  // Redraw the PieChart

        // Customize PieChart appearance
        pieChart.setHoleColor(Color.WHITE); // Hole color
        pieChart.setDrawHoleEnabled(true); // Enable hole
        pieChart.setHoleRadius(30f); // Radius of the hole
        pieChart.setTransparentCircleRadius(35f); // Transparency of the hole
        //pieChart.setUsePercentValues(true); // Use percentage values
        pieChart.getDescription().setEnabled(false); // Disable chart description
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Přesměruje zpět na MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Ukončí aktuální aktivitu
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
