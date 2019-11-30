package com.example.hiddentreasures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HistoryFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference users;
    private String username;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

}