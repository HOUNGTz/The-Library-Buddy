package com.houng.mobile_app_development.modules.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.model.Book_model;

import java.util.ArrayList;
import java.util.List;

public class SearchPage extends Fragment {
    private EditText searchField;
    private final List<Book_model> itemList = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private ImageView imageEmpty;
    public LinearLayout loading;
    private DatabaseReference databaseReference;

    private static final int LOADING_DELAY = 5000; // 5 seconds

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View views = inflater.inflate(R.layout.activity_search_page, container, false);
        itemList.clear();
        searchField = views.findViewById(R.id.searchField);

        Button searchButton = views.findViewById(R.id.searchButton);
        imageEmpty = views.findViewById(R.id.image_none);
        RecyclerView resultView = views.findViewById(R.id.resultView);
        resultView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemAdapter = new ItemAdapter(getActivity(), itemList);
        resultView.setAdapter(itemAdapter);
        loading = views.findViewById(R.id.loading);

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("book");

        // Listen for text changes in the search field
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString().trim();
                if (!searchText.isEmpty()) {
                    // Trigger search when text changes
                    firebaseItemSearch(searchText);
                } else {
                    // Clear the list when search text is empty
                    itemList.clear();
                    itemAdapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE); // Hide loading indicator
                    // Show "no data" message
                    imageEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        searchButton.setOnClickListener(view -> {
            String searchText = searchField.getText().toString().trim();
            if (searchText.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in the blank before tapping to search", Toast.LENGTH_LONG).show();
            } else {
                // Clear the list when search button is clicked
                itemList.clear();
                itemAdapter.notifyDataSetChanged();

                // Show loading indicator
                loading.setVisibility(View.VISIBLE);

                // Start the loading delay
                new Handler().postDelayed(() -> {
                    // Perform search after the delay
                    firebaseItemSearch(searchText);

                    // Hide loading indicator
                    loading.setVisibility(View.GONE);
                }, LOADING_DELAY);
            }
        });
        return views;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchField != null) {
            searchField.setText("");
        }
        loading.setVisibility(View.GONE);
    }

    private void firebaseItemSearch(String searchText) {
        Query firebaseSearchQuery = databaseReference
                .orderByChild("title")
                .startAt(searchText).endAt(searchText + "\uf8ff");
        loading.setVisibility(View.VISIBLE);
        imageEmpty.setVisibility(View.GONE);
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loading.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    itemList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book_model item = snapshot.getValue(Book_model.class);
                        itemList.add(item);
                    }
                    imageEmpty.setVisibility(View.GONE);

                    itemAdapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    itemAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "No items found", Toast.LENGTH_SHORT).show();
                    // Set visibility of the empty view
                    imageEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hide loading indicator
                loading.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
