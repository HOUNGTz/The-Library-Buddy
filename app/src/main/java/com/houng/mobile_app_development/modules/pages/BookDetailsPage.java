package com.houng.mobile_app_development.modules.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private ImageView deleteIcon;


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
        deleteIcon = findViewById(R.id.delete_button);
        book = (Book_model) getIntent().getSerializableExtra("EXTRA_DATA");

        buttonEditText.setOnClickListener(
            v -> new UpdateDialog(book.id, book.title, book.subtitle, book.category, book.image, book.rate, book.des, book.story).show(BookDetailsPage.this.getSupportFragmentManager(), "GAME_DIALOG")
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
        deleteIcon.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book").child(book.id);
                                              databaseReference.removeValue();
                                              Intent intent = new Intent(BookDetailsPage.this, MainButtomNavigation.class);
                                              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                              startActivity(intent);

                                          }
                                      }


        );


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
        private final String title, id;
        private final String subtitle;
        private final String category;
        private final String image;
        private final String story;
        private final String des;
        private final String rate;
        Uri imageResult;

        EditText editTitle, editSubtitle, editCategory, editRate, editDes, editStory, imagePut;
        ImageView imageUrl;
        private static final int PICK_IMAGE_REQUEST = 1;

        private void openFileChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri imageUri = data.getData();

                imageResult = imageUri;
                // Call upload method
                uploadImageToFirebaseStorage(imageUri);

                // Use Glide to load the selected image into the ImageView
                Glide.with(this).load(imageUri).into(imageUrl);
            }
        }
        private String getFileExtension(Uri uri) {
            ContentResolver contentResolver = getContext().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        }
        private void uploadImageToFirebaseStorage(Uri imageUri) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads/" + System.currentTimeMillis() + ".jpg");
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get download URL and update Realtime Database
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String uploadId = FirebaseDatabase.getInstance().getReference("books").push().getKey();
                            FirebaseDatabase.getInstance().getReference("books").child(uploadId).setValue(uri.toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Image upload successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        public UpdateDialog(String id, String title, String subtitle, String category, String image, String rate, String des, String story) {
            this.title = title;
            this.subtitle = subtitle;
            this.category = category;
            this.image = image;
            this.story = story;
            this.des = des;
            this.rate = rate;
            this.id = id;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.update_book, null);
            imagePut = view.findViewById(R.id.input_image);
            editTitle = view.findViewById(R.id.input_title);
            ProgressBar progressBar = view.findViewById(R.id.progressBar3);
            editSubtitle = view.findViewById(R.id.input_subtitle);
            imageUrl = view.findViewById(R.id.image_preview);
            editCategory = view.findViewById(R.id.choose_type_book);
            editStory = view.findViewById(R.id.input_story);
            editDes = view.findViewById(R.id.input_des);
            editRate = view.findViewById(R.id.input_rate);
            imageUrl.setOnClickListener(v -> openFileChooser());
            imagePut.setOnClickListener(v -> openFileChooser());

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
                    Button back = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Collect all field values
                            progressBar.setVisibility(View.VISIBLE);
                            positiveButton.setVisibility(View.GONE);
                            back.setVisibility(View.GONE);

                            String textTitle = editTitle.getText().toString().trim();
                            String textCategory = editCategory.getText().toString().trim();
                            String textDes = editDes.getText().toString().trim();
                            String textSubtitle = editSubtitle.getText().toString().trim();
                            String textRate = editRate.getText().toString().trim();
                            String textStory = editStory.getText().toString().trim();

                            // Assume imageResult is not null and contains the selected image URI
                            if (imageResult != null) {
                                StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis() + "." + getFileExtension(imageResult));
                                fileReference.putFile(imageResult).addOnSuccessListener(taskSnapshot -> {
                                    // After upload, get the URL of the uploaded file
                                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        // Update book details with the new image URL and other details
                                        String imageUrl = uri.toString(); // URL of the uploaded image
                                        DatabaseReference bookReference = FirebaseDatabase.getInstance().getReference("book").child(id);

                                        Map<String, Object> bookUpdates = new HashMap<>();
                                        bookUpdates.put("title", textTitle);
                                        bookUpdates.put("subtitle", textSubtitle);
                                        bookUpdates.put("category", textCategory);
                                        bookUpdates.put("image", imageUrl); // Use the new image URL
                                        bookUpdates.put("description", textDes);
                                        bookUpdates.put("rate", textRate);
                                        bookUpdates.put("story", textStory);

                                        bookReference.updateChildren(bookUpdates).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Book updated successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                Intent intent = new Intent(getActivity(), MainButtomNavigation.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                // Refresh or navigate as needed
                                            } else {
                                                Toast.makeText(getContext(), "Failed to update book", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    });
                                }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                // Handle case where no new image was selected but other details need to be updated
                                DatabaseReference bookReference = FirebaseDatabase.getInstance().getReference("book").child(id);
                                Map<String, Object> bookUpdates = new HashMap<>();
                                bookUpdates.put("title", textTitle);
                                bookUpdates.put("subtitle", textSubtitle);
                                bookUpdates.put("category", textCategory);
                                // Preserve the old image if no new image was selected
                                bookUpdates.put("description", textDes);
                                bookUpdates.put("rate", textRate);
                                bookUpdates.put("story", textStory);

                                bookReference.updateChildren(bookUpdates).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Book updated successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                        // Refresh or navigate as needed
                                    } else {
                                        Toast.makeText(getContext(), "Failed to update book", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                }

            });
            return dialog;
        }
    }
}