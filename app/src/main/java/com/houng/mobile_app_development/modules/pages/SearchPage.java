package com.houng.mobile_app_development.modules.pages;

import android.os.Bundle;
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
    private Button searchButton;
    private RecyclerView resultView;
    private final List<Book_model> itemList = new ArrayList<>();
    private ItemAdapter itemAdapter;

    private ImageView imageEmpty;
    public LinearLayout loading;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.activity_search_page, container, false);
        itemList.clear();
        searchField = views.findViewById(R.id.searchField);
        searchButton = views.findViewById(R.id.searchButton);
        imageEmpty = views.findViewById(R.id.image_none);
        resultView = views.findViewById(R.id.resultView);
        resultView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemAdapter = new ItemAdapter(getActivity(), itemList);
        resultView.setAdapter(itemAdapter);
        loading = views.findViewById(R.id.loading);

        databaseReference = FirebaseDatabase.getInstance().getReference("book");

        searchButton.setOnClickListener(view -> {
            String searchText = searchField.getText().toString().trim();
            firebaseItemSearch(searchText);
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
        Query firebaseSearchQuery = databaseReference.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");
        loading.setVisibility(View.VISIBLE);
        imageEmpty.setVisibility(View.GONE);
        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
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
