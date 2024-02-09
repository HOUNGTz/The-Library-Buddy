package com.houng.mobile_app_development.modules.pages;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.model.Book_model;

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
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Registered users");
    public void searchByName(String name) {
        Query query = myRef.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Assuming you have a User class that matches the data structure
                    Book_model book = snapshot.getValue(Book_model.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
