package com.mudasir.moviesappadmin.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mudasir.moviesappadmin.R;
import com.mudasir.moviesappadmin.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {


    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private Context mContext;
    private List<Movie> movies;

    private ProgressDialog mProgress;



    public MoviesAdapter(Context context, List<Movie> movieList) {
        mContext=context;
        movies=movieList;
        mDatabaseRef= FirebaseDatabase.getInstance().getReference( "Movies");
        mStorageRef= FirebaseStorage.getInstance().getReference("MoviesThumbnail");
    }

    @NonNull
    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.single_item_movies,parent,false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MoviesViewHolder holder, int position) {

        holder.tvMovieTitle.setText(movies.get(position).getTitle());
        holder.tvMovieDes.setText(movies.get(position).getDescription());
        holder.tvMovieKey.setText("Key : "+movies.get(position).getKey());

        Picasso.get().load(movies.get(position).getThumbnail()).into(holder.ivMovieThumbnail);


    }

    @Override
    public int getItemCount() {
        return movies.size();
    }



    public class MoviesViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivMovieThumbnail;
        public TextView tvMovieTitle,tvMovieDes,tvMovieKey;
        public Button btnDel,btnEdit;

        public MoviesViewHolder(@NonNull final View itemView) {
            super(itemView);
            ivMovieThumbnail=itemView.findViewById(R.id.ivMovieThumbnail);
            tvMovieTitle=itemView.findViewById(R.id.tvmovieTitle);
            tvMovieDes=itemView.findViewById(R.id.tvMovieDes);
            tvMovieKey=itemView.findViewById(R.id.tvkey);
            btnDel=itemView.findViewById(R.id.btnDeleteMovie);
            btnEdit=itemView.findViewById(R.id.btnEditMovie);


            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos=getAdapterPosition();
                    String key = movies.get(pos).getKey();

                    Toast.makeText(mContext, "Edit Clicked", Toast.LENGTH_SHORT).show();



                }
            });

            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // btndelete Code Here
                    final int pos=getAdapterPosition();
                    final String key = movies.get(pos).getKey();
                    final String category=movies.get(pos).getCategory();

                    AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                    builder.setTitle("Are You Sure You Want to Delete This Movie");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            mProgress=new ProgressDialog(mContext);
                            mProgress.setTitle("Deleting Movie...");
                            mProgress.setMessage("Please Wait!");
                            mProgress.setCanceledOnTouchOutside(false);
                            mProgress.show();

                            mStorageRef.child(category+"/"+key+"/"+key+".jpg").delete().
                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mDatabaseRef.child(category+"/"+key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                movies.remove(pos);
                                                mProgress.dismiss();
                                                Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();
                                            }


                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    mProgress.hide();
                                    Toast.makeText(mContext, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    builder.create();
                    builder.show();




                    // btn Code End Here

                }



            });


        }


    }


}
