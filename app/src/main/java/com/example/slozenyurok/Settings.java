package com.example.slozenyurok;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class Settings extends AppCompatActivity{
    private EditText minRangeEditText;
    private EditText maxRangeEditText;
    private EditText minInterestEditText;
    private EditText maxInterestEditText;
    private EditText minPeriodEditText;
    private EditText maxPeriodEditText;
    private TextView colorPreview; // Změna na View pro náhled barvy
    private Button pickColorButton;
    private int defaultColor = R.color.main_color; // Výchozí barva
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Inicializace UI komponent
        minRangeEditText = findViewById(R.id.minRangeEditText);
        maxRangeEditText = findViewById(R.id.maxRangeEditText);
        minInterestEditText = findViewById(R.id.minInterestEditText);
        maxInterestEditText = findViewById(R.id.maxInterestEditText);
        minPeriodEditText = findViewById(R.id.minPeriodEditText);
        maxPeriodEditText = findViewById(R.id.maxPeriodEditText);
        colorPreview = findViewById(R.id.colorPreview);
        pickColorButton = findViewById(R.id.pickColorButton);

        // Načtení nastavení
        loadSettings();

        // Ovládání události pro výběr barvy
        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(Settings.this)
                        .setTitle("Vyber barvu")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton("Vybrat", new ColorEnvelopeListener() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                defaultColor = envelope.getColor(); // Uložení vybrané barvy
                                colorPreview.setBackgroundColor(defaultColor); // Aktualizace náhledu barvy
                            }
                        })
                        .setNegativeButton("Zrušit", (dialogInterface, i) -> dialogInterface.dismiss());

                builder.show(); // Zobrazení dialogu
            }
        });

        // Uložení nastavení po kliknutí na tlačítko
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(Settings.this, "Nastavení uložena!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void loadSettings() {
        defaultColor = sharedPreferences.getInt("chartColor", R.color.main_color);
        colorPreview.setBackgroundColor(defaultColor); // Aktualizuj náhled barvy
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maxRange", maxRangeEditText.getText().toString());
        editor.putString("maxInterest", maxInterestEditText.getText().toString());
        editor.putString("maxPeriod", maxPeriodEditText.getText().toString());
        editor.putInt("chartColor", defaultColor);
        editor.apply();
    }
}
