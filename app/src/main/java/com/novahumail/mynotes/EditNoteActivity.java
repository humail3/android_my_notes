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

public class EditNoteActivity extends AppCompatActivity {
    public String comeFromLayout;
    String image_url_in_string;

    Intent data;
    EditText mEditTitleOfNote, mEditContentOfNote;
    FloatingActionButton mSaveEditNote, backFAB;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Button addImageBTN;
    ImageView image_of_edit_note_activity_iv;
    TextView image_url_in_string_edit_note_activity_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background));


//        initialization of variables
        mEditTitleOfNote = findViewById(R.id.editTitleOfNote);
        mEditContentOfNote = findViewById(R.id.editContentOfNote);
        image_of_edit_note_activity_iv = findViewById(R.id.iv_image_of_edit_note_activity);
        image_url_in_string_edit_note_activity_tv = findViewById(R.id.tv_image_url_in_string_edit_note_activity);
        mSaveEditNote = findViewById(R.id.FABsaveEditNote);
        data = getIntent();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        addImageBTN = findViewById(R.id.BTNaddImage);
        backFAB = findViewById(R.id.BTNbackInEditNote);

        backFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NoteDetails.class));
                finish();
            }
        });


//        initialize intent for getting data
        data = getIntent();


        mSaveEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newtitle = mEditTitleOfNote.getText().toString();
                String newcontent = mEditContentOfNote.getText().toString();
                String newurl = image_url_in_string_edit_note_activity_tv.getText().toString();

                if (newtitle.isEmpty() || newcontent.isEmpty()) {
                    Toast.makeText(EditNoteActivity.this, "Something is empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
//                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("docId"));
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newtitle);
                    note.put("content", newcontent);
                    note.put("url", newurl);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditNoteActivity.this, "Note is updated", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditNoteActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        data = getIntent();

        String notetitle = data.getStringExtra("title");
        String notecontent = data.getStringExtra("content");
        String noteurl = data.getStringExtra("url");
        mEditTitleOfNote.setText(notetitle);
        mEditContentOfNote.setText(notecontent);
        image_url_in_string_edit_note_activity_tv.setText(noteurl);


        image_url_in_string = data.getStringExtra("image_url_in_string");
        if (image_url_in_string != null) {
            image_url_in_string_edit_note_activity_tv.setText(image_url_in_string);
        }


        addImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comeFromLayout = "e";
                Intent h = new Intent(getApplicationContext(), AddImage.class);
                h.putExtra("comeFromLayout", comeFromLayout);
                startActivity(h);
            }
        });


        if (image_url_in_string_edit_note_activity_tv != null) {
            // Load and display the image using Picasso
            Picasso.get().load(image_url_in_string_edit_note_activity_tv.getText().toString()).into(image_of_edit_note_activity_iv);
        }
//        if ( noteurl!= null) {
//            // Load and display the image using Picasso
//            Picasso.get().load(noteurl).into(image_of_edit_note_activity_iv);
//        }


    }
}