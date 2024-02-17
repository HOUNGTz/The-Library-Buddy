package com.houng.mobile_app_development.modules.pages;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.houng.mobile_app_development.MainButtomNavigation;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.modules.model.Book_model;
import java.util.Objects;
import java.util.Optional;

public class AddPage extends AppCompatActivity {
    public TextInputEditText choose_type_book, title, subtitle, rate, des, story;
    public Button save_button;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private Uri imageUri;
    private ProgressBar progressBar;
    int titleTextColor;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page);

        save_button = findViewById(R.id.save_book_button);
        title = findViewById(R.id.input_title);
        subtitle = findViewById(R.id.input_subtitle);
        rate = findViewById(R.id.input_rate);
        progressBar = findViewById(R.id.progressBar3);
        des = findViewById(R.id.input_des);
        story = findViewById(R.id.input_story);
        imagePreview = findViewById(R.id.image_preview);

        Toolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Insert New Books");

        titleTextColor = ContextCompat.getColor(this, R.color.white);
        SpannableString spannableString = new SpannableString(getSupportActionBar().getTitle());
        spannableString.setSpan(new ForegroundColorSpan(titleTextColor), 0, spannableString.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        story.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.input_story) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });

        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
        TextInputEditText inputImage = findViewById(R.id.input_image);
        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        choose_type_book = findViewById(R.id.choose_type_book);
        choose_type_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_button.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if (imageUri != null) {
                    uploadFile();
                } else {
                    // Save book without image or show an error message
                    saveBook(null);
                }
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Display the selected image in the ImageView
            imagePreview.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage
                .getInstance()
                .getReference("uploads")
                .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                saveBook(downloadUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }
    }

//    private void saveBook(String imageUrl) {
//        String editTitle = Objects.requireNonNull(title.getText()).toString();
//        String editSubtitle = Objects.requireNonNull(subtitle.getText()).toString();
//        String editRate = Objects.requireNonNull(rate.getText()).toString();
//        String editDes = Objects.requireNonNull(des.getText()).toString();
//        String editStory = Objects.requireNonNull(story.getText()).toString();
//        String editCategory = Objects.requireNonNull(choose_type_book.getText()).toString();
//
//        if(TextUtils.isEmpty(editTitle)) {
//            title.setError("Title Is required");
//            title.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        } else if (TextUtils.isEmpty(editSubtitle)) {
//            subtitle.setError("Subtitle Is required");
//            subtitle.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        }else if (TextUtils.isEmpty(editRate)) {
//            rate.setError("Rate Is required");
//            rate.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        }else if (TextUtils.isEmpty(editDes)) {
//            des.setError("Des Is required");
//            des.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        }else if (TextUtils.isEmpty(editStory)) {
//            story.setError("Story Is required");
//            story.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        } else if (TextUtils.isEmpty(editCategory)) {
//            choose_type_book.setError("Category Is required");
//            choose_type_book.requestFocus();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        } else if (TextUtils.isEmpty(imageUrl)) {
//            Toast.makeText(AddPage.this, "Please input image cover", Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            save_button.setVisibility(View.VISIBLE);
//        }else {
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book");
//            String bookId = databaseReference.push().getKey();
//            assert bookId != null;
//            Book_model books = new Book_model(
//                bookId,
//                editTitle,
//                editCategory,
//                editSubtitle,
//                imageUrl,
//                editRate,
//                editDes,
//                editStory
//            );
//            databaseReference.child(bookId).setValue(books).addOnCompleteListener(
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            progressBar.setVisibility(View.GONE);
//                            Intent intent = new Intent(AddPage.this, MainButtomNavigation.class);
//                            startActivity(intent);
//                        }
//                    }
//                }
//            );
//        }
//    }

    private void saveBook(String imageUrl) {
        String editTitle = Optional.ofNullable(title.getText()).map(CharSequence::toString).orElse("");
        String editSubtitle = Optional.ofNullable(subtitle.getText()).map(CharSequence::toString).orElse("");
        String editRate = Optional.ofNullable(rate.getText()).map(CharSequence::toString).orElse("");
        String editDes = Optional.ofNullable(des.getText()).map(CharSequence::toString).orElse("");
        String editStory = Optional.ofNullable(story.getText()).map(CharSequence::toString).orElse("");
        String editCategory = Optional.ofNullable(choose_type_book.getText()).map(CharSequence::toString).orElse("");

        if (validateInputs(new String[]{editTitle, editSubtitle, editRate, editDes, editStory, editCategory, imageUrl})) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book");
            String bookId = databaseReference.push().getKey();
            if (bookId != null) {
                Book_model book = new Book_model(bookId, editTitle, editCategory, editSubtitle, imageUrl, editRate, editDes, editStory);
                saveBookToDatabase(databaseReference, book);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            save_button.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInputs(String[] inputs) {
        String[] fieldNames = {"Title", "Subtitle", "Rate", "Des", "Story", "Category", "Image URL"};
        TextInputEditText[] fields = {title, subtitle, rate, des, story, choose_type_book, null};
        boolean isValid = true;

        for (int i = 0; i < inputs.length; i++) {
            if (TextUtils.isEmpty(inputs[i])) {
                if (i < fields.length - 1) {
                    fields[i].setError(fieldNames[i] + " is required");
                    fields[i].requestFocus();
                } else {
                    Toast.makeText(AddPage.this, "Please input image cover", Toast.LENGTH_LONG).show();
                }
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private void saveBookToDatabase(DatabaseReference databaseReference, Book_model book) {
        databaseReference.child(book.id).setValue(book).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(AddPage.this, MainButtomNavigation.class));
            } else {
                // Handle failure
                Toast.makeText(AddPage.this, "Failed to save book. Try again.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                save_button.setVisibility(View.VISIBLE);
            }
        });
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
        final CharSequence[] items = {"សៀវភៅប្រលោមលោក", "សៀវភៅទូទៅ", "សៀវភៅអក្សរសិល្ប៌", "សៀវភៅរឿងកំប្លែង"};
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_single_choice, null);
        final int[] selectedItemIndex = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ប្រភេទសៀវភៅ");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectedItemIndex[0] = item;
            }
        });

        builder.setPositiveButton("យល់ព្រម", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (selectedItemIndex[0] != -1) {
                    choose_type_book.setText(items[selectedItemIndex[0]].toString()); // Set the text of EditText when "Okay" is clicked
                }
            }
        });

        builder.setNegativeButton("បោះបង់", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Handle the negative button action here if needed
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}