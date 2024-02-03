package com.houng.mobile_app_development.modules.pages;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.houng.mobile_app_development.R;

public class SearchPage extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search_page, container, false);

        // Find the TextInputEditText
        EditText editText = view.findViewById(R.id.search_textfield);
        InputFilter noLineBreakFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (source.charAt(i) == '\n') {
                        return "";
                    }
                }
                return null;
            }
        };

        // Apply the filter to the EditText
        editText.setFilters(new InputFilter[] { noLineBreakFilter });

        return view;
    }
}
