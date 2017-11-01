package com.immymemine.kevin.firebase_basic2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.immymemine.kevin.firebase_basic2.R;
import com.immymemine.kevin.firebase_basic2.User;
import com.immymemine.kevin.firebase_basic2.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class StorageActivity extends AppCompatActivity implements UserAdapter.Callback {
    // view
    private EditText editMsg;
    private TextView textId;
    private TextView textToken;
    private RecyclerView recyclerView;

    // adapter
    private UserAdapter adapter;

    // data
    List<User> data;

    // storage
    private StorageReference mStorageRef;

    // real time db
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_storage);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        initiateView();
    }

    // 파일 탐색기
    public void openFileBrowser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("/image/*"); // 갤러리 image/* , 동영상 video/*
        startActivityForResult(intent.createChooser(intent, "Select App"), 999);
    }

    // 파일이 선택되면 호출
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode == RESULT_OK) {
            Uri file = data.getData();
            upload(file);
        }
    }

    public void upload(Uri file) {
//        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
//        String[] dirs = file.getPath().split("/");
//        String path = dirs[dirs.length-1];
//        Log.d("upload", path);

        StorageReference storageRef = mStorageRef.child("files/" + file.getLastPathSegment());

        storageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void initiateView() {
        editMsg = findViewById(R.id.editMsg);
        textId = findViewById(R.id.textId);
        textToken = findViewById(R.id.textToken);
        recyclerView = findViewById(R.id.recyclerView);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = new ArrayList<>();
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    String id = item.getKey();
                    String token = (String) item.getValue();
                    data.add(new User(id, token));
                }

                adapter.setDataAndRefresh(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new UserAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void setIdAndToken(String id, String token) {
        textId.setText(id);
        textToken.setText(token);
    }

    public void send(View view) {

    }
}
