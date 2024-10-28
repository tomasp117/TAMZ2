package com.example.barcode_2024;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BarcodeView extends View {

    //UPC-A code

    //http://en.wikipedia.org/wiki/EAN_code
    //http://www.terryburton.co.uk/barcodewriter/generator/


    static final int[] L = {
            0x0D,  //000 1101
            0x19,  //001 1001
            0x13,  //001 0011
            0x3D,  //011 1101
            0x23,  //010 0011
            0x31,  //011 0001
            0x2F,  //010 1111
            0x3B,  //011 1011
            0x37,  //011 0111
            0x0B   //000 1011
    };

    static final int[] R = {
            0x72, //111 0010
            0x66, //110 0110
            0x6C, //110 1100
            0x42, //100 0010
            0x5C, //101 1100
            0x5E, //100 1110
            0x50, //101 0000
            0x44, //100 0100
            0x48, //100 1000
            0x74  //111 0100
    };

    final static int BARCODE_WIDTH =  600;
    final static int BARCODE_HEIGHT = 200;
    final static int BARCODE_LINE_WIDTH = 5;

    // čísla čárového kódu
    int code[] = new int[12];

    public BarcodeView(Context context) {
        super(context);
        setDefaults();
    }

    public BarcodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaults();
    }

    public BarcodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaults();
    }

    public BarcodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setDefaults();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // při změně velikosti view,  w a h obsahují novou velikost
    }

    // nastaví výchozí hodnoty
    void setDefaults() {
        int copyFrom[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2};
        System.arraycopy(copyFrom, 0, code, 0, copyFrom.length);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint twPaint = new Paint();
        twPaint.setColor(Color.WHITE);

        Paint tbPaint = new Paint();
        tbPaint.setColor(Color.BLACK);

        Paint trPaint = new Paint();
        trPaint.setColor(Color.RED);
        trPaint.setStrokeWidth(BARCODE_LINE_WIDTH);

        // vykreslí bílý obdelník do kterého se bude kreslit čárový kód
        canvas.drawRect(new Rect(0, 0, BARCODE_WIDTH, BARCODE_HEIGHT), twPaint);

        // tloušťka čáry
        tbPaint.setStrokeWidth(BARCODE_LINE_WIDTH);

        // velikost písma, antialiasing
        trPaint.setTextSize(30);
        trPaint.setAntiAlias(true);

//        canvas.drawLine(0, 0, BARCODE_WIDTH, BARCODE_HEIGHT, tbPaint);
//        canvas.drawLine(0, BARCODE_HEIGHT, BARCODE_WIDTH, 0, tbPaint);
//
//        canvas.drawText("Zde bude čárový kód", (int)(BARCODE_WIDTH * 0.3), (int)(BARCODE_HEIGHT * 0.95), trPaint);

        //canvas.drawLine(10, 0, 10, BARCODE_HEIGHT, tbPaint);

        canvas.drawLine(10, 0, 10, BARCODE_HEIGHT + 10, trPaint);
        canvas.drawLine(15, 0, 15, BARCODE_HEIGHT, twPaint);
        canvas.drawLine(20, 0, 20, BARCODE_HEIGHT + 10, trPaint);

        int xPos = 25;


        for (int i = 0; i < code.length; i++) {
            int side = (i < 6) ? L[code[i]] : R[code[i]];

            if(i == 6){
                for(int j = 1 ; j <= 5; j ++){
                    if(j%2==0){
                        canvas.drawLine(xPos, 0, xPos, BARCODE_HEIGHT + 10, trPaint);
                    }
                    xPos += BARCODE_LINE_WIDTH;

                }
            }
            for (int bit = 6; bit >= 0; bit--) {
                if ((side & (1 << bit)) != 0) {
                    canvas.drawLine(xPos, 0, xPos, BARCODE_HEIGHT, tbPaint);
                }
                xPos += BARCODE_LINE_WIDTH;
            }
        }
        canvas.drawLine(xPos, 0, xPos, BARCODE_HEIGHT + 10, trPaint);
        canvas.drawLine(xPos + 5, 0, xPos + 5, BARCODE_HEIGHT, twPaint);
        canvas.drawLine(xPos + 10, 0, xPos + 10, BARCODE_HEIGHT + 10, trPaint);

        // Zobrazíme textový popis čárového kódu pod ním
        canvas.drawText("UPC-A: " + formatCode(code), 50, BARCODE_HEIGHT + 40, trPaint);

    }

    private String formatCode(int[] code) {
        StringBuilder sb = new StringBuilder();
        for (int digit : code) {
            sb.append(digit);
        }
        return sb.toString();
    }

    private int calculateCheckDigit(int[] code) {
        int sumOdd = 0;
        int sumEven = 0;

        for (int i = 0; i < 11; i++) {
            if (i % 2 == 0) {
                sumOdd += code[i];
            } else {
                sumEven += code[i];
            }
        }

        // Vypočítáme celkový součet
        int totalSum = (sumOdd * 3) + sumEven;
        int checkDigit = (10 - (totalSum % 10)) % 10; // Kontrolní číslice

        return checkDigit;
    }

    // Upravte metodu setCode
    public void setCode(int[] newCode) {
        if (newCode.length != 12) {
            Toast.makeText(this.getContext(), "Kód musí mít 12 číslic.", Toast.LENGTH_SHORT).show();
            return;
        }



        // Ověření kontrolní číslice
        int calculatedCheckDigit = calculateCheckDigit(newCode);
        if (calculatedCheckDigit != newCode[11]) {
            Toast.makeText(this.getContext(), "Neplatný čárový kód. Kontrolní číslice se neshoduje.", Toast.LENGTH_SHORT).show();
            return;
        }
        System.arraycopy(newCode, 0, code, 0, newCode.length);
    }

    public Bitmap getBarcodeBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(BARCODE_WIDTH , BARCODE_HEIGHT, Bitmap.Config.ARGB_8888);
        //Bitmap bitmap = Bitmap.createBitmap(BARCODE_WIDTH + 20 , BARCODE_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }


}

