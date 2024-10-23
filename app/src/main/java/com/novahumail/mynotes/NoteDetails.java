package com.novahumail.mynotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

public class NoteDetails extends AppCompatActivity {
    TextView mTitleOfNoteDetail, mContentOfNoteDetail, image_url_in_string_tv;
    ImageView mImageUrlNoteDetail;
    Intent data;
    FloatingActionButton mGoToEditNote, backBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background));


//        initialization of variables
        {
            mTitleOfNoteDetail = findViewById(R.id.titleOfNoteDetail);
            mContentOfNoteDetail = findViewById(R.id.contentOfNoteDetail);
            image_url_in_string_tv = findViewById(R.id.noteurl);
            mImageUrlNoteDetail = findViewById(R.id.imageUrlOfNoteDetail);
            mGoToEditNote = findViewById(R.id.FABgoToEditNote);
            backBTN = findViewById(R.id.BTNback);
        }


        // Retrieve data from the Intent
        data = getIntent();
        String title = data.getStringExtra("title");
        String content = data.getStringExtra("content");
        String url = data.getStringExtra("url");
        String noteId = data.getStringExtra("noteId");


        // Set the retrieved data to your TextViews
        mTitleOfNoteDetail.setText(title);
        mContentOfNoteDetail.setText(content);
        image_url_in_string_tv.setText(url);


        if (image_url_in_string_tv != null) {
            // Load and display the image using Picasso
            Picasso.get().load(url).into(mImageUrlNoteDetail);
        }


//        initialize intent for getting data
        data = getIntent();

//      setting the values to all attributes + getting values from previous activity
            Picasso.get().load(String.valueOf(image_url_in_string_tv)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // Bitmap loaded successfully, set it to the ImageView
                    mImageUrlNoteDetail.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    // Handle errors
//                    Toast.makeText(NoteDetails.this, "Failed to attaching Image", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    // This method is optional and can be used for placeholder setup
                }
            });


        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                finish();
            }
        });


        mGoToEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(v.getContext(), EditNoteActivity.class);
                h.putExtra("title", data.getStringExtra("title"));
                h.putExtra("content", data.getStringExtra("content"));
                h.putExtra("url", data.getStringExtra("url"));
                h.putExtra("noteId", data.getStringExtra("noteId"));
                v.getContext().startActivity(h);
            }
        });


        mTitleOfNoteDetail.setText(data.getStringExtra("title"));
        mContentOfNoteDetail.setText(data.getStringExtra("content"));





    }
}