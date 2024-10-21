package com.example.slozenyurok;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SeekBar depositSlider, interestSlider, periodSlider;
    private TextView depositTextView, interestTextView, periodTextView;

    private BarChart barChart;
    private PieChart pieChart;

    ActivityResultLauncher<Intent> chartTypeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        chartTypeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Získej vybraný typ grafu z Intentu
                            String chartType = data.getStringExtra("chart_type");

                            // Podle typu grafu aktualizuj graf
                            if ("bar".equals(chartType)) {
                                showBarChart();
                            } else if ("pie".equals(chartType)) {
                                showPieChart();
                            }
                        }
                    }
                }
        );


        depositSlider = findViewById(R.id.depositSlider);
        interestSlider = findViewById(R.id.interestSlider);
        periodSlider = findViewById(R.id.periodSlider);

        depositTextView = findViewById(R.id.depositText); // TextView pro Vklad
        interestTextView = findViewById(R.id.interestText); // TextView pro Úrok
        periodTextView = findViewById(R.id.periodText); // TextView pro Období

        TextView savingsResult = findViewById(R.id.savingsResult);
        TextView interestResult = findViewById(R.id.interestResult);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        depositSlider.setProgress(1); // Výchozí vklad
        interestSlider.setProgress(1); // Výchozí úrok
        periodSlider.setProgress(1); // Výchozí období

        // Nastavení textu podle výchozích hodnot
        depositTextView.setText("Vklad: 1000");
        interestTextView.setText("Úrok: 0.1%");
        periodTextView.setText("Období: 1");
        updateResults(1000, 1, 1, savingsResult, interestResult);
        depositSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int depositValue = progress * 1000; // např. pokud chcete tisícové hodnoty
                depositTextView.setText("Vklad: " + depositValue); // Změní hodnotu textu
                updateResults(depositValue, interestSlider.getProgress(), periodSlider.getProgress(), savingsResult, interestResult);
                TooltipCompat.setTooltipText(seekBar, String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        interestSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interestTextView.setText("Úrok: " + (float)progress/10 + "%");
                updateResults(depositSlider.getProgress() * 1000, progress, periodSlider.getProgress(), savingsResult, interestResult);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
        });

        // Posuvník pro Období
        periodSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                periodTextView.setText("Období: " + progress);
                updateResults(depositSlider.getProgress() * 1000, interestSlider.getProgress(), progress, savingsResult, interestResult);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                
            }
        });

    showBarChart();
    }

    /*private void updateResults(int deposit, int interestProgress, int period, TextView savingsResult, TextView interestResult) {
       /* if (deposit == 0 || interestProgress == 0 || period == 0) {
            savingsResult.setText("Naspořená částka: 0");
            interestResult.setText("Z toho úroky: 0");// Interest bar
            return;
        //}
        
        double interestRate = (float) interestProgress / 10;
        
        double finalAmount = deposit * Math.pow(1 + (interestRate / 100), period);
        
        double totalInterest = finalAmount - deposit;
        
        savingsResult.setText(String.format("Naspořená částka: %.0f", finalAmount));
        interestResult.setText(String.format("Z toho úroky: %.0f", totalInterest));

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) deposit));  // Deposit bar
        entries.add(new BarEntry(1, (float) totalInterest)); // Interest bar

        BarDataSet dataSet = new BarDataSet(entries, "Vklad/Úroky");
        dataSet.setColors(new int[] {
                getResources().getColor(R.color.main_color), // Barva pro vklad
                getResources().getColor(R.color.secondary_color) // Barva pro úrok
        });
        BarData data = new BarData(dataSet);

        barChart.setData(data);
        barChart.invalidate();
        barChart.getAxisLeft().setAxisMinimum(0); // Nastavení minimální hodnoty Y osy na 0
        barChart.getAxisRight().setAxisMinimum(0); // Nastavení minimální hodnoty Y osy na 0 (pokud je pravá osa aktivní)



        // Vypnutí mřížky
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        dataSet.setValueTextSize(16f);

        // Zajištění, že Y osa začíná na 0 a žádné mřížky
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);
        barChart.setExtraOffsets(0, 0, 0, 5);

        dataSet.setLabel("");
        // Nastavení pozice hodnot nad sloupci
        dataSet.setDrawValues(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }*/

    private void updateResults(int deposit, int interestProgress, int period, TextView savingsResult, TextView interestResult) {
        // Calculate final amount and interest
        double interestRate = (float) interestProgress / 10;
        double finalAmount = calculateFinalAmount(deposit, interestRate, period);
        double totalInterest = finalAmount - deposit;

        // Update the result text views
        savingsResult.setText(String.format("Naspořená částka: %.0f", finalAmount));
        interestResult.setText(String.format("Z toho úroky: %.0f", totalInterest));

        // Update the chart
        if (barChart.getVisibility() == View.VISIBLE) {
            updateBarChart(deposit, totalInterest); // Update BarChart if it is visible
        } else {
            updatePieChart(deposit, totalInterest); // Update PieChart if it is visible
        }
    }

    private double calculateFinalAmount(int deposit, double interestRate, int period) {
        return deposit * Math.pow(1 + (interestRate / 100), period);
    }

    // New helper method for updating the bar chart
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
        pieChart.setUsePercentValues(true); // Use percentage values
        pieChart.getDescription().setEnabled(false); // Disable chart description
    }

    private void showBarChart() {
        // Zobrazí BarChart a skryje PieChart
        barChart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);
    }

    private void showPieChart() {
        // Zobrazí PieChart a skryje BarChart
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.chart_type) {
            Intent intent = new Intent(MainActivity.this, ChartType.class);
            intent.putExtra("deposit", depositSlider.getProgress() * 1000);
            intent.putExtra("totalInterest", totalInterest);
            chartTypeLauncher.launch(intent);
            return true;
        }
        return false;
    }





}