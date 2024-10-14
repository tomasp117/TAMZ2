package com.example.slozenyurok;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SeekBar depositSlider, interestSlider, periodSlider;
    private TextView depositTextView, interestTextView, periodTextView;

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        depositSlider = findViewById(R.id.depositSlider);
        interestSlider = findViewById(R.id.interestSlider);
        periodSlider = findViewById(R.id.periodSlider);

        depositTextView = findViewById(R.id.depositText); // TextView pro Vklad
        interestTextView = findViewById(R.id.interestText); // TextView pro Úrok
        periodTextView = findViewById(R.id.periodText); // TextView pro Období

        TextView savingsResult = findViewById(R.id.savingsResult);
        TextView interestResult = findViewById(R.id.interestResult);

        barChart = findViewById(R.id.barChart);

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Volá se, když uživatel začne interakci s posuvníkem
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Volá se, když uživatel přestane interagovat s posuvníkem
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
                // Do something here if needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something here if needed
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
                // Do something here if needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something here if needed
            }
        });


    }

    private void updateResults(int deposit, int interestProgress, int period, TextView savingsResult, TextView interestResult) {
        // Pokud jsou všechny hodnoty nula, nezobrazovat nic


        if (deposit == 0 || interestProgress == 0 || period == 0) {
            savingsResult.setText("Naspořená částka: 0");
            interestResult.setText("Z toho úroky: 0");// Interest bar
            return;
        }

        // Úroková sazba jako procento (z posuvníku 0-50, převádíme na hodnotu 0.0-0.5)
        double interestRate = (float) interestProgress / 10;

        // Vzorec pro výpočet naspořené částky
        double finalAmount = deposit * Math.pow(1 + (interestRate / 100), period);

        // Výpočet celkového úroku
        double totalInterest = finalAmount - deposit;

        // Zobrazení výsledků
        savingsResult.setText(String.format("Naspořená částka: %.0f", finalAmount));
        interestResult.setText(String.format("Z toho úroky: %.0f", totalInterest));

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) deposit));  // Deposit bar
        entries.add(new BarEntry(1, (float) totalInterest)); // Interest bar

        BarDataSet dataSet = new BarDataSet(entries, "Vklad/Úroky");
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

        // Nastavení pozice hodnot nad sloupci
        dataSet.setDrawValues(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }



}