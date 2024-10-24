package com.example.slozenyurok;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.Button;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_COLOR = 1;
    private final String[] settingsOptions;
    private final Context context;


    public SettingsAdapter(String[] settingsOptions, Context context) {
        this.settingsOptions = settingsOptions;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if ("Barvy".equals(settingsOptions[position])) {
            return TYPE_COLOR;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String settingTitle = settingsOptions[position];
        int viewType = getItemViewType(position);

        // Set the title based on the setting option
        holder.settingTitle.setText(settingTitle);

        if (viewType == TYPE_NORMAL) {
            holder.minInputLayout.setVisibility(View.VISIBLE);
            holder.maxInputLayout.setVisibility(View.VISIBLE);
            holder.colorComponents.setVisibility(View.GONE);
            setInputHints(holder, settingTitle);

            SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            String minValue = sharedPreferences.getString("min" + settingTitle, "");
            String maxValue = sharedPreferences.getString("max" + settingTitle, "");

            holder.minInputLayout.getEditText().setText(minValue);
            holder.maxInputLayout.getEditText().setText(maxValue);
        } else if (viewType == TYPE_COLOR) {
            holder.minInputLayout.setVisibility(View.GONE);
            holder.maxInputLayout.setVisibility(View.GONE);
            holder.colorComponents.setVisibility(View.VISIBLE);
//            setupColorPicker(holder.primaryColorButton, "PrimaryColor");
//            setupColorPicker(holder.secondaryColorButton, "SecondaryColor");
//            setupColorPicker(holder.menuColorButton, "MenuColor");
            setupColorPickers(holder);
        }

        // Toggle visibility for expandable content
        holder.itemView.setOnClickListener(v -> {
            if (holder.expandableLayout.getVisibility() == View.VISIBLE) {
                holder.expandableLayout.setVisibility(View.GONE);
            } else {
                holder.expandableLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setInputHints(ViewHolder holder, String settingTitle) {
        switch (settingTitle) {
            case "Vklad":
                holder.minInputLayout.setHint("Minimální vklad");
                holder.maxInputLayout.setHint("Maximální vklad");
                break;
            case "Úrok":
                holder.minInputLayout.setHint("Minimální úrok");
                holder.maxInputLayout.setHint("Maximální úrok");
                break;
            case "Období":
                holder.minInputLayout.setHint("Minimální období");
                holder.maxInputLayout.setHint("Maximální období");
                break;
        }
    }

    private void setupColorPickers(ViewHolder holder) {
        // Load existing colors
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int primaryColor = sharedPreferences.getInt("PrimaryColor", context.getResources().getColor(R.color.main_color));
        int secondaryColor = sharedPreferences.getInt("SecondaryColor", context.getResources().getColor(R.color.secondary_color));
        //int menuColor = sharedPreferences.getInt("MenuColor", context.getResources().getColor(R.color.menu_color));

        holder.primaryColorButton.setBackgroundColor(primaryColor);
        holder.secondaryColorButton.setBackgroundColor(secondaryColor);
        //holder.menuColorButton.setBackgroundColor(menuColor);

        setupColorButton(holder.primaryColorButton, "PrimaryColor");
        setupColorButton(holder.secondaryColorButton, "SecondaryColor");
        //setupColorButton(holder.menuColorButton, "MenuColor");
    }

    private void setupColorButton(Button button, String preferenceKey) {
        button.setOnClickListener(v -> {
            new ColorPickerDialog.Builder(context)
                    .setTitle("Vyber barvu")
                    .setPreferenceName(preferenceKey + "PickerDialog")
                    .setPositiveButton("Vybrat", new ColorEnvelopeListener() {
                        @Override
                        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                            int selectedColor = envelope.getColor();
                            button.setBackgroundColor(selectedColor);
                            saveColorToPreferences(preferenceKey, selectedColor);
                            // Uložení všech barev
                            //saveAllColors();
                        }
                    })
                    .setNegativeButton("Zrušit", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void saveAllColors() {
        // Načtěte barvy ze SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int primaryColor = sharedPreferences.getInt("PrimaryColor", context.getResources().getColor(R.color.main_color));
        int secondaryColor = sharedPreferences.getInt("SecondaryColor", context.getResources().getColor(R.color.secondary_color));
        //int menuColor = sharedPreferences.getInt("MenuColor", context.getResources().getColor(R.color.menu_color));

        // Zavolejte saveSettings v Settings2

    }
    private void saveColorToPreferences(String key, int color) {
        SharedPreferences.Editor editor = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE).edit();
        editor.putInt(key, color);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return settingsOptions.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView settingTitle;
        LinearLayout expandableLayout;
        TextInputLayout minInputLayout;
        TextInputLayout maxInputLayout;
        LinearLayout colorComponents;
        Button primaryColorButton;
        Button secondaryColorButton;
        //Button menuColorButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            settingTitle = itemView.findViewById(R.id.textViewSettingTitle);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            minInputLayout = itemView.findViewById(R.id.minInputLayout);
            maxInputLayout = itemView.findViewById(R.id.maxInputLayout);
            colorComponents = itemView.findViewById(R.id.colorComponents);
            primaryColorButton = itemView.findViewById(R.id.buttonPrimaryColor);
            secondaryColorButton = itemView.findViewById(R.id.buttonSecondaryColor);
            //menuColorButton = itemView.findViewById(R.id.buttonMenuColor);
        }
    }
}
