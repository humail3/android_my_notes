package com.novahumail.mynotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {

    FloatingActionButton mCreateNotesFAB;
    private FirebaseAuth firebaseAuth;
    RecyclerView mRecyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background));
        Objects.requireNonNull(getSupportActionBar()).setTitle("All Notes");
        int customColor = Color.parseColor("#FF5733"); // Replace with your desired color
        setActionBarColor(customColor);


        mCreateNotesFAB = findViewById(R.id.FABcreateNote);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mCreateNotesFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(getApplicationContext(), CreateNote.class);
                startActivity(h);
            }
        });


//loading the data for the user / fetch /get data in the allsernotes variable
        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebasemodel> allusernotes = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();


        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
                @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position, @NonNull firebasemodel firebasemodel) {

                ImageView popupBTN = noteViewHolder.itemView.findViewById(R.id.menupopBTN);

                int colourcode = getRandomColor();
                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourcode, null));

                noteViewHolder.notecontent.setText(firebasemodel.getContent());
                noteViewHolder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolder.noteurl.setText(firebasemodel.getUrl());

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();


                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//        we have to open a note detail activity
                        Intent h = new Intent(v.getContext(), NoteDetails.class);
                        h.putExtra("title", firebasemodel.getTitle());
                        h.putExtra("content", firebasemodel.getContent());
                        h.putExtra("url", firebasemodel.getUrl());
                        h.putExtra("noteId", docId);
                        v.getContext().startActivity(h);
                    }
                });


                popupBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);


                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {

                                Intent h = new Intent(getApplicationContext(), EditNoteActivity.class);
                                h.putExtra("content", firebasemodel.getContent());
                                h.putExtra("title", firebasemodel.getTitle());
                                h.putExtra("url", firebasemodel.getUrl());
                                h.putExtra("noteId", docId);
                                v.getContext().startActivity(h);
                                return false;
                            }
                        });


                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(NotesActivity.this, "This note is deleted", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NotesActivity.this, "Failed to deleted", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                return false;
                            }
                        });


                        popupMenu.show();
                    }
                });
            }


            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };


//        setting recycler view
        {
            mRecyclerView = findViewById(R.id.recyclerview);
            mRecyclerView.setHasFixedSize(true);
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            mRecyclerView.setAdapter(noteAdapter);
        }


    }


    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView notetitle;
        private final TextView notecontent;
        private final TextView noteurl;
        LinearLayout mnote;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notecontent = itemView.findViewById(R.id.notecontent);
            notetitle = itemView.findViewById(R.id.notetitle);
            noteurl = itemView.findViewById(R.id.tv_image_url_in_String_in_notes_layout);
            mnote = itemView.findViewById(R.id.note);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if (id == R.id.home) {
            finish();
            startActivity(new Intent(getApplicationContext(), NotesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void setActionBarColor(int color) {
        // Set the background color of the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_bg));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.green);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);

    }


}