package com.example.hiddentreasures;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference users;
    private String username;
    private User user;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_profile, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.optLogOut) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log Out", (dialog, which) -> {

                        SharedPreferences pref = getActivity()
                                .getApplicationContext()
                                .getSharedPreferences("MyPref", 0);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("username");
                        editor.commit();

                        Intent myIntent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                        startActivity(myIntent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }

        return true;

    }

}