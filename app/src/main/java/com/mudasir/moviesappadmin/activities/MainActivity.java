package com.mudasir.moviesappadmin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mudasir.moviesappadmin.R;
import com.mudasir.moviesappadmin.models.Movie;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    DatabaseReference mDatabaseRef;
    String SelectedCategory;
    String SelectedType;

    ImageView btnThumbanail, ImageThumbnail;
    EditText etTitle,etDes,etVedioUrl;
    Spinner categories;

    Uri SelectedThumbanail;
    public static final int PICK_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;

    Bitmap bitmap;
    StorageReference mStorageRef;
    Button btnUpload;

    String Thumb_Uri;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setTitle("Upload a Movie");

        btnThumbanail=findViewById(R.id.btnChooseThumbnail);
        ImageThumbnail=findViewById(R.id.thumbnail);
        etTitle=findViewById(R.id.etTitle);
        etDes=findViewById(R.id.etDes);
        etVedioUrl=findViewById(R.id.etVedioUrl);
        btnUpload=findViewById(R.id.btnUploadWholeData);


        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Movies");
        mStorageRef= FirebaseStorage.getInstance().getReference("MoviesThumbnail");

        Spinner spinner = findViewById(R.id.categories);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);





        btnThumbanail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Thumbnail For Movie"), PICK_IMAGE);


            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadImageAndAllData();

            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode==RESULT_OK) {
            //TODO: action

               SelectedThumbanail=data.getData();
                Toast.makeText(this, ""+SelectedThumbanail, Toast.LENGTH_SHORT).show();

                Picasso.get().load(SelectedThumbanail).into(ImageThumbnail);

        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UploadImageAndAllData(){

        final String title= etTitle.getText().toString();
        final String description= etDes.getText().toString();
        final String vedioUrl= etVedioUrl.getText().toString();

        if (ImageThumbnail!=null && !title.isEmpty() && !description.isEmpty() && !vedioUrl.isEmpty()){

            mProgress=new ProgressDialog(MainActivity.this);
            mProgress.setTitle("Uploading Movie Data");
            mProgress.setMessage("Please Wait until It Finishes");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            final String key= mDatabaseRef.push().getKey();
            final String fileNameAndExtension= key + "." + getFileExtension(SelectedThumbanail);
            StorageReference fileReference = mStorageRef.child(SelectedCategory).child(key).child(fileNameAndExtension);

            fileReference.putFile(SelectedThumbanail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> Thumb_Url=taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    Thumb_Url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String download_Url= uri.toString();

                            writeNewMovie(title,description,download_Url,SelectedCategory,vedioUrl,key);

                        }
                    });


                }
            });


        }
        else{
            Toast.makeText(this, "Please Select Movie Thumbnail", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeNewMovie(String title, String descripion, String thumbnail, String category,String videoUrl,String key) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Movie post = new Movie(title,descripion,thumbnail,category,videoUrl,key);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+category+"/" + key, postValues);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgress.dismiss();
                Toast.makeText(MainActivity.this, "Movie Has SuccessFully Uploaded Firebase", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.hide();
                Toast.makeText(MainActivity.this, "Movie Did not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        SelectedCategory=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onStart() {
        super.onStart();

        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.showAll:
                Intent intent=new Intent(MainActivity.this,ShowAllMovies.class);
                startActivity(intent);
                break;
            case R.id.uploadTrailor:
                Intent sendToUploadTrailor=new Intent(MainActivity.this,UploadTrailor.class);
                startActivity(sendToUploadTrailor);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
