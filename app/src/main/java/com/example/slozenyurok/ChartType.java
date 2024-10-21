package com.example.slozenyurok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ChartType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_selection);

        // Najdi karty podle jejich ID
        CardView cardBarChart = findViewById(R.id.cardBarChart);
        CardView cardPieChart = findViewById(R.id.cardPieChart);

        cardBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Odeslání výsledku zpět do MainActivity pro BarChart
                Intent resultIntent = new Intent();
                resultIntent.putExtra("chart_type", "bar");  // Posíláme zpět "bar"
                setResult(RESULT_OK, resultIntent);
                finish();  // Ukončení aktivity
            }
        });

        // Nastav onClickListener pro PieChart
        cardPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Odeslání výsledku zpět do MainActivity pro PieChart
                Intent resultIntent = new Intent();
                resultIntent.putExtra("chart_type", "pie");  // Posíláme zpět "pie"
                setResult(RESULT_OK, resultIntent);
                finish();  // Ukončení aktivity
            }
        });
    }

}
