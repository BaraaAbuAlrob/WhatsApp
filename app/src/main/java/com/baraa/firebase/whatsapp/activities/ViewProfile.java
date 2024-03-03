package com.baraa.firebase.whatsapp.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.modelClass.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/** @noinspection ALL*/
public class ViewProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView username, status, email;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private CircleImageView profile_img;
    private ProgressBar mProgress;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private final String USER_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        setState("online");

        toolbar = (Toolbar) findViewById(R.id.viewProfileToolbar);
        CircleImageView back = findViewById(R.id.profile_arrowBack);

        profile_img = findViewById(R.id.imgProfile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        username = (TextView) findViewById(R.id.username);
        status = (TextView) findViewById(R.id.status);
        email = (TextView) findViewById(R.id.email);

        mProgress = findViewById(R.id.progress_horizontal);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            toolbar.setTitleTextColor(Color.WHITE);
        }

        back.setOnClickListener(view -> {
            Intent intent = new Intent(ViewProfile.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        String UserID = FirebaseAuth.getInstance().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.child(UserID).getValue(User.class);
                username.setText(user.getUsername());
                status.setText(user.getStatus());
                email.setText(user.getEmail());

                if(user.getImgUrl().equals("default"))
                    profile_img.setImageResource(R.drawable.user);
                else {
                    if(!isDestroyed())
                        Glide.with(ViewProfile.this).load(user.getImgUrl()).into(profile_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ViewProfile.this, error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });

        profile_img.setOnClickListener(view -> updateImage());
    }//End onCreate

    private void updateImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = ViewProfile.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(0);
                        }
                    }, 1000);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    mProgress.setProgress((int) progress);
                }
            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        throw task.getException();

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users")
                                .child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imgUrl", mUri);
                        reference.updateChildren(map);
                    } else
                        Toast.makeText(ViewProfile.this, "Failed!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else
            Toast.makeText(this, "No image selected!", Toast.LENGTH_LONG).show();

    }// End of uploadImage() method.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress())
                Toast.makeText(this, "Upload in progress!", Toast.LENGTH_SHORT).show();
            else
                uploadImage();
        }
    }// End of onActivityResult(...) method.

    private void setState(String state){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("users").child(USER_ID);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("state",state);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setState("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setState("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setState("offline");
    }
}