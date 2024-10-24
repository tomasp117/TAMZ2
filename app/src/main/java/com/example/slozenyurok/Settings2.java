package com.example.slozenyurok;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
public class Settings2 extends AppCompatActivity {
    //private TextView colorPreview;
    //private Button pickColorButton;
    private int defaultColor = R.color.main_color;
    private SharedPreferences sharedPreferences;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);


        Button buttonSave = findViewById(R.id.saveButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int primaryColor = sharedPreferences.getInt("PrimaryColor", getResources().getColor(R.color.main_color));
                int secondaryColor = sharedPreferences.getInt("SecondaryColor", getResources().getColor(R.color.secondary_color));
                //int menuColor = sharedPreferences.getInt("MenuColor", getResources().getColor(R.color.menu_color));

                //saveSettings(primaryColor, secondaryColor);
                saveMinMaxValues();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("primary_color", primaryColor);
                resultIntent.putExtra("secondary_color", secondaryColor);
                //resultIntent.putExtra("menu_color", menuColor);
                setResult(RESULT_OK, resultIntent);
                finish();// Ukončete aktivitu a odešlete výsledek
            }
        });

        RecyclerView settingsRecyclerView = findViewById(R.id.settingsRecyclerView);
        String[] settingsOptions = {
                "Vklad",
                "Úrok",
                "Období",
                "Barvy"
        };

        SettingsAdapter adapter = new SettingsAdapter(settingsOptions, this);
        settingsRecyclerView.setAdapter(adapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void saveMinMaxValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Uložení hodnot pro "Vklad"
        String minDeposit = ((EditText) findViewById(R.id.minRangeEditText)).getText().toString();
        String maxDeposit = ((EditText) findViewById(R.id.maxRangeEditText)).getText().toString();
        editor.putString("minDeposit", minDeposit);
        editor.putString("maxDeposit", maxDeposit);

        // Uložení hodnot pro "Úrok"
        String minInterest = ((EditText) findViewById(R.id.minInterestEditText)).getText().toString();
        String maxInterest = ((EditText) findViewById(R.id.maxInterestEditText)).getText().toString();
        editor.putString("minInterest", minInterest);
        editor.putString("maxInterest", maxInterest);

        // Uložení hodnot pro "Období"
        String minPeriod = ((EditText) findViewById(R.id.minPeriodEditText)).getText().toString();
        String maxPeriod = ((EditText) findViewById(R.id.maxPeriodEditText)).getText().toString();
        editor.putString("minPeriod", minPeriod);
        editor.putString("maxPeriod", maxPeriod);

        editor.apply();
    }
    public void saveSettings(int primaryColor, int secondaryColor) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("primary_color", primaryColor);
        editor.putInt("secondary_color", secondaryColor);
        //editor.putInt("menu_color", menuColor);
        editor.apply();

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                // Zkontroluj, zda je kliknutí mimo aktuálně aktivní prvek
                int[] location = new int[2];
                currentFocus.getLocationOnScreen(location);
                float x = event.getRawX() + currentFocus.getLeft() - location[0];
                float y = event.getRawY() + currentFocus.getTop() - location[1];
                if (x < 0 || x >= currentFocus.getWidth() || y < 0 || y >= currentFocus.getHeight()) {
                    currentFocus.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
