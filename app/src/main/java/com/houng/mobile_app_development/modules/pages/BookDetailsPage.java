package com.houng.mobile_app_development.modules.pages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.houng.mobile_app_development.MainButtomNavigation;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.ReadWriteUserDetails;
import com.houng.mobile_app_development.model.Book_model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookDetailsPage extends AppCompatActivity {
    public ImageButton buttonEditText;
    public TextView title;
    public ImageView img;
    public TextView story;
    public TextView rate;
    public Book_model book;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details_page);
        Toolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        int titleTextColor = ContextCompat.getColor(this, R.color.white);
        SpannableString spannableString = new SpannableString(getSupportActionBar().getTitle());
        spannableString.setSpan(new ForegroundColorSpan(titleTextColor), 0, spannableString.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        buttonEditText = findViewById(R.id.buttonEditText);
        title = findViewById(R.id.title);
        img = findViewById(R.id.img);
        story = findViewById(R.id.story);
        rate = findViewById(R.id.rate);
        book = (Book_model) getIntent().getSerializableExtra("EXTRA_DATA");

        buttonEditText.setOnClickListener(
            v -> new UpdateDialog(book.title, book.subtitle, book.category, book.image, book.rate, book.des, book.story).show(BookDetailsPage.this.getSupportFragmentManager(), "GAME_DIALOG")
        );

        if (book != null) {
            title.setText(book.title); if (book.image != null && !book.image.isEmpty()) {
                Glide.with(BookDetailsPage.this)
                    .load(book.image)
                    .into(img);
            }
            story.setText(book.story);
            rate.setText(book.rate + "/5");
        }
        loadUserProfileRole();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfileRole() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(BookDetailsPage.this, "User not logged in", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");
            referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String role = "";
                    ReadWriteUserDetails userDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (userDetails != null && userDetails.imageUrl != null && !userDetails.imageUrl.isEmpty()) {
                        role = userDetails.role;
                        System.out.println("===== %%");
                    }

                    if(role.equals("1")){
                        buttonEditText.setVisibility(View.VISIBLE);
                    }else{
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) rate.getLayoutParams();
                        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 310, getResources().getDisplayMetrics());
                        layoutParams.setMarginEnd((int) pixels);
                        rate.setLayoutParams(layoutParams);
                        buttonEditText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public static class UpdateDialog extends DialogFragment {
        private final String title;
        private final String subtitle;
        private final String category;
        private final String image;
        private final String story;
        private final String des;
        private final String rate;

        EditText editTitle, editSubtitle, editCategory, editRate, editDes, editStory;
        ImageView imageUrl;



        public UpdateDialog(String title, String subtitle, String category, String image, String rate, String des, String story) {
            this.title = title;
            this.subtitle = subtitle;
            this.category = category;
            this.image = image;
            this.story = story;
            this.des = des;
            this.rate = rate;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.update_book, null);
            editTitle = view.findViewById(R.id.input_title);
            editSubtitle = view.findViewById(R.id.input_subtitle);
            imageUrl = view.findViewById(R.id.image_preview);
            editCategory = view.findViewById(R.id.choose_type_book);
            editStory = view.findViewById(R.id.input_story);
            editDes = view.findViewById(R.id.input_des);
            editRate = view.findViewById(R.id.input_rate);
            if (!image.isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(image)) // Glide can handle Uri.parse(image) directly
                        .into(imageUrl);
            } else {
                // Consider setting a default image or placeholder
                Glide.with(this)
                        .load(R.drawable.empty_image) // Assuming you have a default placeholder
                        .into(imageUrl);
            }
            editTitle.setText(title);
            editSubtitle.setText(subtitle);
            editCategory.setText(category);
            editStory.setText(story);
            editDes.setText(des);
            editRate.setText(rate);


            builder.setView(view)
                    .setPositiveButton(R.string.start, null) // Set to null temporarily
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel behavior here
                            }
                        }
                    );

            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Validate input fields if necessary
                            String textTitle = editTitle.getText().toString();
                            String textCategory = editCategory.getText().toString();
                            String imagePreview = imageUrl.getImageMatrix().toString();
                            String textDes = editDes.getText().toString();
                            String textSubtitle = editSubtitle.getText().toString();
                            String textRate = editRate.getText().toString();
                            String textStory = editStory.getText().toString();

                            // Assuming 'bookId' is the ID of the book to update. You need to obtain this ID.
                            String bookId = "the_book_id"; // Placeholder, replace with actual book ID logic
                            DatabaseReference bookReference = FirebaseDatabase.getInstance().getReference("book").child(bookId);

                            // Create a map to update book information
                            Map<String, Object> bookUpdates = new HashMap<>();
                            bookUpdates.put("title", textTitle);
                            bookUpdates.put("subtitle", textSubtitle);
                            bookUpdates.put("category", textCategory);
                            bookUpdates.put("image", imagePreview);
                            bookUpdates.put("description", textDes);
                            bookUpdates.put("rate", textRate);
                            bookUpdates.put("story", textStory);

                            bookReference.updateChildren(bookUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // Dismiss dialog and navigate or show success message
                                        Toast.makeText(getContext(), "Book updated successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        // Navigate or refresh as needed
                                        Intent intent = new Intent(getActivity(), MainButtomNavigation.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        // Handle failure
                                        Toast.makeText(getContext(), "Failed to update book", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }

            });
            return dialog;
        }
    }
}