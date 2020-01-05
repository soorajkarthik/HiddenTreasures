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

  private FirebaseDatabase database;
  private DatabaseReference users;
  private String username;
  private User user;
  private View view;

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

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
    Date date = new Date(user.getDateJoined());
    ArrayList<User> userList = ((MainActivity) getActivity()).getUserList();
    ArrayList<User> friendList = ((MainActivity) getActivity()).getFriendList();
    friendList.add(user);
    friendList.sort(User::compareTo);

    ((TextView) view.findViewById(R.id.usernameText)).setText(username);
    ((TextView) view.findViewById(R.id.dateJoinedText)).setText(simpleDateFormat.format(date));
    ((TextView) view.findViewById(R.id.globalRankText)).setText((userList.indexOf(user) + 1) + "");
    ((TextView) view.findViewById(R.id.socialRankText))
        .setText((friendList.indexOf(user) + 1) + "");
    ((TextView) view.findViewById(R.id.scoreText)).setText(user.calculateScore() + "");
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

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {

    inflater.inflate(R.menu.menu_profile, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.optLogOut) {
      new AlertDialog.Builder(getContext())
          .setTitle("Confirm")
          .setMessage("Are you sure you want to log out?")
          .setPositiveButton("Log Out", (dialog, which) -> {
            SharedPreferences pref =
                getActivity().getApplicationContext().getSharedPreferences("MyPref", 0);

            SharedPreferences.Editor editor = pref.edit();
            editor.remove("username");
            editor.commit();

            Intent myIntent =
                new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(myIntent);
          })
          .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
          .create()
          .show();
    }

    return true;
  }
}
