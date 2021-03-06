package com.example.hiddentreasures;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.Treasure;
import com.example.hiddentreasures.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfileFragment extends Fragment {

  //Fields
  private FirebaseDatabase database;
  private DatabaseReference users;
  private String username;
  private User user;
  private View view;

  /**
   * Get reference to Firebase Database, the "Users" nodes, the current user from MainActivity and
   * inflates the fragment's view.
   *
   * @param inflater           The LayoutInflater used by the MainActivity
   * @param container          The ViewGroup that this fragment is a part of
   * @param savedInstanceState The last saved state of the application
   * @return The view corresponding to this fragment
   */
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    database = FirebaseDatabase.getInstance();
    users = database.getReference("Users");
    user = ((MainActivity) getActivity()).getUser();
    username = user.getUsername();

    setHasOptionsMenu(true);
    view = inflater.inflate(R.layout.fragment_profile, container, false);
    return view;
  }

  /**
   * Gets reference to all the text views that make up the user's summary and sets them to their
   * corresponding values
   *
   * @param view               The view corresponding to this fragment
   * @param savedInstanceState The last saved state of the application
   */
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
    Date date = new Date(user.getDateJoined());

    ArrayList<User> userList = ((MainActivity) getActivity()).getUserList();
    ArrayList<User> friendList = ((MainActivity) getActivity()).getFriendList();

    ((TextView) view.findViewById(R.id.usernameText))
        .setText(username);

    ((TextView) view.findViewById(R.id.dateJoinedText))
        .setText(simpleDateFormat.format(date));

    ((TextView) view.findViewById(R.id.globalRankText))
        .setText((userList.indexOf(user.placeHolderUser()) + 1) + "");

    ((TextView) view.findViewById(R.id.socialRankText))
        .setText((friendList.indexOf(user.placeHolderUser()) + 1) + "");

    ((TextView) view.findViewById(R.id.scoreText))
        .setText(user.calculateScore() + "");

    ((TextView) view.findViewById(R.id.commonText))
        .setText(user.getFoundTreasures().get(Treasure.COMMON) + "");

    ((TextView) view.findViewById(R.id.uncommonText))
        .setText(user.getFoundTreasures().get(Treasure.UNCOMMON) + "");

    ((TextView) view.findViewById(R.id.rareText))
        .setText(user.getFoundTreasures().get(Treasure.RARE) + "");

    ((TextView) view.findViewById(R.id.ultraRareText))
        .setText(user.getFoundTreasures().get(Treasure.ULTRA_RARE) + "");

    ((TextView) view.findViewById(R.id.legendaryText))
        .setText(user.getFoundTreasures().get(Treasure.LEGENDARY) + "");
  }

  /**
   * Inflates options menu
   *
   * @param menu     Menu used by the current activity
   * @param inflater MenuInflater used by the current activity
   */
  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {

    inflater.inflate(R.menu.menu_profile, menu);
  }

  /**
   * Inflates help dialog to confirm user's logout when user clicks logout button
   *
   * @param item The item selected by the user
   * @return True because there is no need for system processing, all processing necessary
   * processing is done in the method
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.optLogOut) {
      new AlertDialog.Builder(getContext())
          .setTitle("Confirm")
          .setMessage("Are you sure you want to log out?")
          .setPositiveButton("Log Out", (dialog, which) -> {
            SharedPreferences pref =
                getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);

            //Removes stored username from device so user must login again when they reopen the app
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("username");
            editor.apply();

            Intent myIntent = new Intent(getActivity().getApplicationContext(),
                LoginActivity.class);
            startActivity(myIntent);
          })
          .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
          .create()
          .show();
    }

    return true;
  }
}
