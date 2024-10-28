package com.example.barcode_2024;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private BarcodeView barcodeView;

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

        // Inicializace komponent
        EditText barcodeInput = findViewById(R.id.barcodeInput);
        Button generateButton = findViewById(R.id.generateBarcode);
        barcodeView = findViewById(R.id.barcodeView);

        // Generování čárového kódu
        generateButton.setOnClickListener(v -> {
            String input = barcodeInput.getText().toString();
            if (input.length() == 12) {  // UPC-A kódy mají 12 číslic
                int[] code = new int[12];
                for (int i = 0; i < 12; i++) {
                    code[i] = Character.getNumericValue(input.charAt(i));
                }
                barcodeView.setCode(code);
                barcodeView.invalidate(); // Překreslení čárového kódu
            } else {
                barcodeInput.setError("UPC-A kód musí mít 12 číslic");
            }
        });
    }

    public void saveBarcodeToFile(View view) {
        Bitmap barcodeBitmap = barcodeView.getBarcodeBitmap();
        if (barcodeBitmap != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "barcode.png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    barcodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    Toast.makeText(this, "Čárový kód uložen!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Chyba při ukládání: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Nepodařilo se vytvořit URI pro obrázek.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Nepodařilo se vytvořit bitmapu.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can perform your save operation
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to write to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}