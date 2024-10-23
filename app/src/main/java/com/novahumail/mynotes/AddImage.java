package com.novahumail.mynotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AddImage extends AppCompatActivity {
    //    for adding image to the location using reference of the document location
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    // views for button
    private Button btnSelect, btnUpload;
    // view for image view
    private ImageView imageView;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    String image_url_in_string;
    String comeFromLayout;
    String content_content, title_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0F9D58"));
        actionBar.setBackgroundDrawable(colorDrawable);


        {
            // initialise views and variables
            btnSelect = findViewById(R.id.btnChoose);
            btnUpload = findViewById(R.id.btnUpload);
            imageView = findViewById(R.id.imgView);
            // get the Firebase storage reference
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            Intent intent = getIntent();
            comeFromLayout = intent.getStringExtra("comeFromLayout");
            //        receive ET of title and content
            title_title = intent.getStringExtra("title");
            content_content = intent.getStringExtra("content");
        }


        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


    }


    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }


    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code if request code is PICK_IMAGE_REQUEST and resultCode is RESULT_OK then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get the Url of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    // Upload Image method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
//            StorageReference ref = storageReference.child("images/" + docId);

            // adding listeners on upload or failure of image
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Storing Image URL in Firestore:

//                    firebaseFirestore = FirebaseFirestore.getInstance();
//                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
//
//// Replace "your_image_url" with the actual URL of the image String image_url
//                    Map<String, Object> note = new HashMap<>();
//                    note.put("url", filePath);
//
//// Update the Firestore document with the image URL
//                    documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Image URL updated successfully
//                            Toast.makeText(AddImage.this, "Image URL updated successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Handle errors
//                            Toast.makeText(AddImage.this, "Handle errors", Toast.LENGTH_SHORT).show();
//                        }
//                    });


                    // Image uploaded successfully Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(AddImage.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                    // checking from where we come to this screen ,,, Inside the receiving activity's onCreate method or any suitable place
                    if (comeFromLayout.equals("c")) {

                        Intent h = new Intent(getApplicationContext(), CreateNote.class);
                        image_url_in_string = filePath.toString();
                        h.putExtra("image_url_in_string", image_url_in_string);
                        h.putExtra("title_title",title_title);
                        h.putExtra("content_content",content_content);
                        setResult(RESULT_OK);
                        startActivity(h);
                        finish();

                    } else if (comeFromLayout.equals("e")) {
                        Intent h = new Intent(getApplicationContext(), EditNoteActivity.class);
                        image_url_in_string = filePath.toString();
                        h.putExtra("image_url_in_string", image_url_in_string);
                        h.putExtra("title_title",title_title);
                        h.putExtra("content_content",content_content);
                        setResult(RESULT_OK);
                        startActivity(h);
                        finish();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(AddImage.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (comeFromLayout.equals("c")) {
                        Intent h = new Intent(getApplicationContext(), CreateNote.class);
                       h.putExtra("title_title",title_title);
                        h.putExtra("content_content",content_content);
                        startActivity(h);
                        finish();

                    } else if (comeFromLayout.equals("e")) {
                        Intent h = new Intent(getApplicationContext(), EditNoteActivity.class);
                       h.putExtra("title_title",title_title);
                        h.putExtra("content_content",content_content);
                        startActivity(h);
                        finish();

                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }








    }
}
