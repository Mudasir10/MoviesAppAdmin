package com.mudasir.moviesappadmin.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mudasir.moviesappadmin.R;
import com.mudasir.moviesappadmin.adapters.MoviesAdapter;
import com.mudasir.moviesappadmin.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class ActionMoviesFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mlayoutManager;
    private MoviesAdapter mAdapter;

     DatabaseReference mDataBaseRef;
     StorageReference mStorageRef;

    private List<Movie> movieList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_action_movies, container, false);

        init(view);



        return view;
    }

    private void init(View view) {


        mRecyclerView=view.findViewById(R.id.rvAction_movies);
        mlayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setHasFixedSize(true);


        mDataBaseRef= FirebaseDatabase.getInstance().getReference("Movies").child("Action");
        mStorageRef= FirebaseStorage.getInstance().getReference("MoviesThumbnail").child("Action");

        movieList=new ArrayList<>();
        mDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    movieList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Movie movie=snapshot.getValue(Movie.class);
                        movieList.add(movie);
                    }

                    mAdapter=new MoviesAdapter(getContext(),movieList);
                    mRecyclerView.setAdapter(mAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    @Override
    public void onStart() {
        super.onStart();



    }

}
