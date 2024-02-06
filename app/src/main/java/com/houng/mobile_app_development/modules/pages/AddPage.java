package com.houng.mobile_app_development.modules.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.appcheck.FirebaseAppCheck;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.Toast;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.houng.mobile_app_development.MainActivity;
import com.houng.mobile_app_development.MainButtomNavigation;
import com.houng.mobile_app_development.R;
import com.houng.mobile_app_development.model.Book_model;
import com.houng.mobile_app_development.modules.screens.LoginScreen;
import com.houng.mobile_app_development.modules.screens.SignUpScreen;

import java.util.Objects;

public class AddPage extends AppCompatActivity {
    public TextInputEditText choose_type_book, title, subtitle, rate, des, story;
    public Button save_button;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imagePreview;
    private Uri imageUri;
    private ProgressBar progressBar;

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
        Toolbar toolbar = findViewById(R.id.materialToolbar);
        imagePreview = findViewById(R.id.image_preview);
        setSupportActionBar(toolbar);

        FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Insert New Books");
        TextInputEditText inputImage = findViewById(R.id.input_image);
        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        int titleTextColor = ContextCompat.getColor(this, R.color.white);
        SpannableString spannableString = new SpannableString(getSupportActionBar().getTitle());
        spannableString.setSpan(new ForegroundColorSpan(titleTextColor), 0, spannableString.length(), 0);
        getSupportActionBar().setTitle(spannableString);

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
                progressBar.setVisibility(View.VISIBLE);
                save_button.setVisibility(View.GONE);
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
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads")
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

    private void saveBook(String imageUrl) {

        String editTitle = title.getText().toString();
        String editSubtitle = subtitle.getText().toString();
        String editRate = rate.getText().toString();
        String editDes = des.getText().toString();
        String editStory = story.getText().toString();
        String editCategory = choose_type_book.getText().toString();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("book");
        String bookId = databaseReference.push().getKey();
        Book_model book = new Book_model(editTitle,editCategory, editSubtitle,"1.png",editRate,editDes,editStory);
        databaseReference.child(bookId).setValue(book);
        Book_model books = new Book_model(editTitle, editCategory, editSubtitle, imageUrl, editRate, editDes, editStory);
        databaseReference.child(bookId).setValue(books).addOnCompleteListener(
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(AddPage.this, MainButtomNavigation.class);
                        startActivity(intent);
                    }
                }
            }
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        final CharSequence[] items = {"សៀវភៅប្រលោមលោក", "សៀវភៅទូទៅ", "សៀវភៅអក្សរសិល្ប៌", "សៀវភៅរឿងកំប្លែង"};
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_single_choice, null);
        final int[] selectedItemIndex = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ប្រភេទសៀវភៅ");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectedItemIndex[0] = item;
//                choose_type_book.setText(items[item].toString());
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