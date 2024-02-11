package com.houng.mobile_app_development.modules.pages;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.houng.mobile_app_development.R;

import java.util.Objects;

public class UpdatePage extends AppCompatActivity {
    int titleTextColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        Toolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        Objects
                .requireNonNull(getSupportActionBar())
                .setTitle("Update Book");
        titleTextColor = ContextCompat.getColor(this, R.color.white);
        SpannableString spannableString = new SpannableString(getSupportActionBar().getTitle());
        spannableString.setSpan(new ForegroundColorSpan(titleTextColor), 0, spannableString.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        TextInputEditText update_type_book = findViewById(R.id.update_type_book);
        update_type_book.setOnClickListener(v -> showDialog());
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {
        final CharSequence[] items = {"សៀវភៅប្រលោមលោក", "សៀវភៅទូទៅ", "សៀវភៅអក្សរសិល្ប៌", "គម្ពីរធម៌"};
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_single_choice, null);
        CheckBox checkBox = dialogView.findViewById(R.id.checkbox_none);
        final int[] selectedItemIndex = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ប្រភេទសៀវភៅ");
        builder.setSingleChoiceItems(items, -1, (dialog, item) -> {
            selectedItemIndex[0] = item;
            checkBox.setChecked(false);
        });
        builder.setView(dialogView);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ListView listView = ((AlertDialog) dialogView.getParent()).getListView();
                listView.setItemChecked(selectedItemIndex[0], false);
                selectedItemIndex[0] = -1;
            }
        });
        builder.setPositiveButton("យល់ព្រម", (dialog, id) -> {
            if (selectedItemIndex[0] != -1) {
            } else {
                checkBox.isChecked();
            }
        });
        builder.setNegativeButton("បោះបង់", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}