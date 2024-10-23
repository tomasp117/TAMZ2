package com.example.slozenyurok;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import top.defaults.colorpicker.ColorPickerPopup;

public class Settings extends AppCompatActivity{
    private EditText maxRangeEditText;
    private EditText maxInterestEditText;
    private EditText maxPeriodEditText;
    private View colorPreview; // Změna na View pro náhled barvy
    private Button pickColorButton;
    private int defaultColor = Color.RED; // Výchozí barva
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Inicializace UI komponent
        maxRangeEditText = findViewById(R.id.maxRangeEditText);
        maxInterestEditText = findViewById(R.id.maxInterestEditText);
        maxPeriodEditText = findViewById(R.id.maxPeriodEditText);
        colorPreview = findViewById(R.id.preview_selected_color); // Opraveno na správné ID
        pickColorButton = findViewById(R.id.pickColorButton);

        // Načtení nastavení
        loadSettings();

        // Ovládání události pro výběr barvy
        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new ColorPickerPopup.Builder(Settings.this)
                        .initialColor(defaultColor) // Set the initial color
                        .enableBrightness(true) // Allow adjusting brightness
                        .enableAlpha(true) // Allow adjusting alpha (transparency)
                        .okTitle("Choose") // Confirm button title
                        .cancelTitle("Cancel") // Cancel button title
                        .showIndicator(true) // Show color indicator
                        .showValue(true) // Show hex color value
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                Log.d("Settings", "Color picked: " + color);
                                defaultColor = color; // Update selected color
                                colorPreview.setBackgroundColor(defaultColor); // Update color preview
                            }
                        });
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

    private void loadSettings() {
        String maxRange = sharedPreferences.getString("maxRange", "100");
        String maxInterest = sharedPreferences.getString("maxInterest", "5");
        String maxPeriod = sharedPreferences.getString("maxPeriod", "12");
        defaultColor = sharedPreferences.getInt("chartColor", Color.RED);

        maxRangeEditText.setText(maxRange);
        maxInterestEditText.setText(maxInterest);
        maxPeriodEditText.setText(maxPeriod);
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
