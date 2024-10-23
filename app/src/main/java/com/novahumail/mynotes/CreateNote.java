package com.novahumail.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateNote extends AppCompatActivity {
    TextView image_url_in_string_tv;
    String image_url_in_string;
    ImageView image_of_create_note_iv;
    public String comeFromLayout;
    EditText mCreateTitleOfNoteET, mCreateContentOfNoteET;
    FloatingActionButton mSaveNoteFAB, backFAB;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Button addImageBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background));


//        initialization of variables
        {
            mCreateTitleOfNoteET = findViewById(R.id.createTitleOfNote);
            mCreateContentOfNoteET = findViewById(R.id.createContentOfNote);
            mSaveNoteFAB = findViewById(R.id.FABsaveNote);
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            backFAB = findViewById(R.id.BTNbackInCreateNote);
            addImageBTN = findViewById(R.id.BTNaddImage);
            image_of_create_note_iv = findViewById(R.id.imageOfCreateNote);
            image_url_in_string_tv = findViewById(R.id.tv_image_url_in_string_create_note);
        }


        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                finish();
            }
        });


        mSaveNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mCreateTitleOfNoteET.getText().toString();
                String content = mCreateContentOfNoteET.getText().toString();
                String url = image_url_in_string_tv.getText().toString();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(CreateNote.this, "Both fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);
                    note.put("url", url);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateNote.this, "Note created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateNote.this, "Failed to create note", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        addImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comeFromLayout = "c";
                Intent h = new Intent(getApplicationContext(), AddImage.class);
//        sending title and content texts to the AddImage Activity
                h.putExtra("comeFromLayout", comeFromLayout);
                h.putExtra("title", mCreateTitleOfNoteET.getText().toString());
                h.putExtra("content", mCreateContentOfNoteET.getText().toString());
                startActivity(h);
            }
        });


//    receive image url in string
        Intent intent = getIntent();
        if (intent != null) {
            String title_title = intent.getStringExtra("title_title");
            String content_content = intent.getStringExtra("content_content");
            image_url_in_string = intent.getStringExtra("image_url_in_string");
            if (image_url_in_string != null) {
                // You have received the string, do something with it
                // For example, set it to a TextView
                image_url_in_string_tv.setText(image_url_in_string);
                mCreateContentOfNoteET.setText(content_content);
                mCreateTitleOfNoteET.setText(title_title);
                if (image_url_in_string != null) {
                    // Load and display the image using Picasso
                    Picasso.get().load(image_url_in_string).into(image_of_create_note_iv);
                }
            }
        }


    }
}