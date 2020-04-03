package com.mudasir.moviesappadmin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mudasir.moviesappadmin.R;
import com.mudasir.moviesappadmin.models.Movie;
import com.mudasir.moviesappadmin.models.MovieTrailor;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UploadTrailor extends AppCompatActivity {


    ImageView imageViewthumbnail;
    FloatingActionButton btnChooseTrailorThumbnail;
    EditText etTrailotMovieTitle,etTrailorMovieStreamingUrl;
    Button btnSaveTrailorData;
    public static final int PICK_IMAGE_TRAILOR=1;
    private Uri SelectedThumbanail;
    private ProgressDialog mProgress;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_trailor);

        getSupportActionBar().setTitle("Upload a Trailor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        init();


    }

    private void init() {

        databaseReference= FirebaseDatabase.getInstance().getReference("MovieTrailors");
        storageReference= FirebaseStorage.getInstance().getReference("TrailorThumbnails");

        imageViewthumbnail=findViewById(R.id.ivTrailorThumbnail);
        btnChooseTrailorThumbnail=findViewById(R.id.btnTrailorChooseThumbnail);
        etTrailotMovieTitle=findViewById(R.id.etTrailorTitle);
        etTrailorMovieStreamingUrl=findViewById(R.id.etTrailorVedioUrl);
        btnSaveTrailorData=findViewById(R.id.btnUploadTrailorData);


        btnChooseTrailorThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Thumbnail For Movie"), PICK_IMAGE_TRAILOR);

            }
        });

        btnSaveTrailorData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadTrailorDataAndImage();

            }
        });



    }

    private void UploadTrailorDataAndImage() {

        final String Trailortitle= etTrailotMovieTitle.getText().toString();
        final String TrailorStreamingLink= etTrailorMovieStreamingUrl.getText().toString();

        if (!TrailorStreamingLink.isEmpty() && !Trailortitle.isEmpty()){

            mProgress=new ProgressDialog(UploadTrailor.this);
            mProgress.setTitle("Uploading Movie Trailor");
            mProgress.setMessage("Please Wait until It Finishes");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            final String key= databaseReference.push().getKey();
            final String fileNameAndExtension= key + "." + getFileExtension(SelectedThumbanail);

            StorageReference fileReference = storageReference.child(key).child(fileNameAndExtension);
            fileReference.putFile(SelectedThumbanail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> Thumb_Url=taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    Thumb_Url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String download_Url= uri.toString();

                            writeNewTrailor(Trailortitle,download_Url,TrailorStreamingLink,key);

                        }
                    });


                }
            });

        }

    }

    private void writeNewTrailor(String trailortitle, String download_url, String trailorStreamingLink, String key) {

        MovieTrailor post = new MovieTrailor(trailortitle,download_url,trailorStreamingLink,key);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);

        databaseReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgress.dismiss();
                Toast.makeText(UploadTrailor.this, "Trailor Has SuccessFully Uploaded Firebase", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.hide();
                Toast.makeText(UploadTrailor.this, "Trailor Did not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_TRAILOR && resultCode==RESULT_OK) {
            //TODO: action

            SelectedThumbanail=data.getData();
            Toast.makeText(this, ""+SelectedThumbanail, Toast.LENGTH_SHORT).show();

            Picasso.get().load(SelectedThumbanail).into(imageViewthumbnail);

        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }





}
