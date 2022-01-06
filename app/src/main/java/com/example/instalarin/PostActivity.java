 package com.example.instalarin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

 public class  PostActivity extends AppCompatActivity {

    private ImageView close;
    private ImageView imageAdded;
    private TextView post;
    SocialAutoCompleteTextView description;

    private Uri imageUri;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.closeIcon);
        imageAdded = findViewById(R.id.imageAdded);
        post = findViewById(R.id.postText);

        description = findViewById(R.id.imageDescription);

        CropImage.activity().start(PostActivity.this);

    }

     public void setCloseButton(View v){
        startActivity(new Intent(this, HomeActivity.class));
        finish();

     }

     public void setPostButton(View v)
     {
        upload();
     }

     private void upload() {
         ProgressDialog pd = new ProgressDialog(this);
         pd.setMessage("Uploading");
         pd.show();

         if(imageUri != null){
             StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
             StorageTask uploadTask = filePath.putFile(imageUri);
             uploadTask.continueWithTask(new Continuation() {
                 @Override
                 public Object then(@NonNull Task task) throws Exception {
                     if (!task.isSuccessful()) {
                         Log.d("fail", "not successful");
                         throw task.getException();

                     }
                     return filePath.getDownloadUrl();
                 }
             }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                     DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                     String postID = ref.push().getKey();
                     HashMap<String, Object> map = new HashMap<>();
                     map.put("postid", postID);
                     map.put("imageurl", imageUrl);
                     map.put("description", description.getText().toString());
                     map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                     ref.child(postID).setValue(map);

                     DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                     List<String> hashTags = description.getHashtags();
                     if(!hashTags.isEmpty()) {
                         for(String tag: hashTags){
                             map.clear();
                             map.put("tag", tag.toLowerCase());
                             map.put("postid", postID);

                             mHashTagRef.child(tag.toLowerCase()).child(postID).setValue(map);
                         }
                     }
                     pd.dismiss();
                     startActivity(new Intent(PostActivity.this, HomeActivity.class));
                     finish();
                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.d("fail23", "onfailure");
                     Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             });
         }
         else {
             Toast.makeText(PostActivity.this, "No image was selected!", Toast.LENGTH_SHORT).show();
         }


     }

     private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
             CropImage.ActivityResult result = CropImage.getActivityResult(data);
             imageUri = result.getUri();
             imageAdded.setImageURI(imageUri);
         }
         else {
             Toast.makeText(this, "Try again!", Toast.LENGTH_LONG).show();
             startActivity(new Intent(this, HomeActivity.class));
             finish();
         }
     }

     //in part 24 you can add hashtag suggestions when you user writes post description

 }