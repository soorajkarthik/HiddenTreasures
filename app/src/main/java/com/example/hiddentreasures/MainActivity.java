package com.example.hiddentreasures;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.hiddentreasures.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

  // Fields
  private FirebaseDatabase database;
  private DatabaseReference users;
  private Toolbar toolbar;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private PagerAdapter pagerAdapter;
  private String username;
  private User user;
  private ArrayList<User> userList, friendList;
  private boolean hasUpdatedNotificationToken, isTabLayoutSetUpDone;

  /**
   * Get reference to all components of the activity's view Get reference to Firebase Database, and
   * the "Users" node
   *
   * @param savedInstanceState The last saved state of the application
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    database = FirebaseDatabase.getInstance();
    users = database.getReference("Users");

    userList = new ArrayList<>();
    friendList = new ArrayList<>();

    username = Objects.requireNonNull(getIntent().getExtras().getString("username"));

    hasUpdatedNotificationToken = false;
    isTabLayoutSetUpDone = false;
    updateUser();
  }

  //Updates user's last seen in Firebase when they reopen the app
  @Override
  protected void onResume() {
    super.onResume();
    updateLastSeen();
  }

  /**
   * Checks to see if the user has already seen the app instructions. If they haven't, show them the
   * instructions dialog and save that they've seen it in SharedPreferences. If they have, don't
   * show the instructions dialog again.
   */
  private void showInstructions() {
    SharedPreferences pref =
        getApplicationContext().getSharedPreferences("MyPref", 0);

    if (pref.getString("hasShownInstructions", null) == null) {

      new AlertDialog.Builder(this)
          .setTitle("Welcome!")
          .setMessage(
              "- Swipe left and right to navigate"
                  + "\n- Pinch to zoom"
                  + "\n- Tap on a treasure when you're near"
                  + "\n  it to collect it"
                  + "\n- Collect as many treasures as you can!"
                  + "\n- Search for your friends to add them"
                  + "\n  and compete with them!"
                  + "\n- Treasures don't understand the concept"
                  + "\n  of private property. Please stay safe"
                  + "\n  and don't trespass."
                  + "\n- What are you waiting for?"
                  + "\n  GET EXPLORING AND HAVE FUN!"
                  + "\n\n\n  (Pro tip: the closer the star is to red"
                  + "\n   the more points its worth!)")
          .setNegativeButton("Ok",
              (dialog, which) -> {
                dialog.dismiss();
                pref.edit()
                    .putString("hasShownInstructions", "Yes")
                    .apply();
              })
          .create()
          .show();
    }
  }

  /**
   * Updates the local reference to the user, user's friend list, and list of all users. Also sets
   * up the TabView the first time the method is called.
   */
  private void updateUser() {

    /*
     * Ensures that local reference to user, user friend list, and list of all users are
     * updated every time the user is modified in Firebase
     */
    users.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            user = dataSnapshot.child(username).getValue(User.class);

            userList.clear();
            dataSnapshot.getChildren().forEach(child -> userList.add(child.getValue(User.class)));
            userList.remove(user);
            userList.add(user.placeHolderUser());
            userList.sort(User::compareTo);

            friendList.clear();
            user.getFriendList()
                .forEach(
                    friendName ->
                        friendList.add(dataSnapshot.child(friendName).getValue(User.class)));
            friendList.remove(user);
            friendList.add(user.placeHolderUser());
            friendList.sort(User::compareTo);

            /*
             * Updates user's notification token that directs notifications to the last device they
             * were logged into. Ensures that notification token is set up after initial reference
             * to user is received.
             */
            if (!hasUpdatedNotificationToken) {
              updateNotificationToken();
            }

            /*
             * Ensures that tablayout is set up after initial reference to user is received. Ensures
             * tablayout is only set up once. Sets the tab that is seen when the app is opened
             * to the map tab
             */
            if (!isTabLayoutSetUpDone) {
              setUpLayout();
              viewPager.setCurrentItem(1);
              updateLastSeen();
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
          }
        });
  }

  //Updates the user's notification token to their current device's unique token.
  private void updateNotificationToken() {
    FirebaseInstanceId.getInstance()
        .getInstanceId()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                user.setInstanceToken(token);
                users.child(username).setValue(user);
                hasUpdatedNotificationToken = true;
              }
            });
  }

  //Updates the user's last seen time in Firebase
  private void updateLastSeen() {

    if (user != null) {
      user.setLastSeen(System.currentTimeMillis());
      users.child(username).child("lastSeen").setValue(user.getLastSeen());
    }
  }

  /**
   * Sets up Toolbar and TabLayout. Connects ViewPager to TabLayout allowing user too scroll through
   * tabs seamlessly
   */
  private void setUpLayout() {

    toolbar = findViewById(R.id.toolbar);
    tabLayout = findViewById(R.id.tabLayout);
    viewPager = findViewById(R.id.viewPager);
    pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

    viewPager.setAdapter(pagerAdapter);
    setSupportActionBar(toolbar);

    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {

          /**
           * Changes color and text of Toolbar and TabLayout based on the selected tab
           *
           * @param tab The selected tab
           */
          @Override
          public void onTabSelected(TabLayout.Tab tab) {

            viewPager.setCurrentItem(tab.getPosition(), true);

            switch (tab.getPosition()) {
              case 0:
                toolbar.setBackgroundColor(ContextCompat
                    .getColor(getApplicationContext(), android.R.color.holo_blue_dark));

                tabLayout.setBackgroundColor(ContextCompat
                    .getColor(getApplicationContext(), android.R.color.holo_blue_dark));

                getWindow().setStatusBarColor(ContextCompat
                    .getColor(getApplicationContext(), android.R.color.holo_blue_dark));

                toolbar.setTitle("Social");
                break;

              case 1:
                toolbar.setBackgroundColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

                tabLayout.setBackgroundColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

                getWindow().setStatusBarColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

                toolbar.setTitle("Map");
                break;

              case 2:
                toolbar.setBackgroundColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));

                tabLayout.setBackgroundColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));

                getWindow().setStatusBarColor(
                    ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));

                toolbar.setTitle("Profile");
                break;

              default:
                break;
            }
          }

          // Method required by OnTabSelectedListener
          @Override
          public void onTabUnselected(TabLayout.Tab tab) {
          }

          // Method required by OnTabSelectedListener
          @Override
          public void onTabReselected(TabLayout.Tab tab) {
          }
        });

    // Connects ViewPager to TabLayout
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    isTabLayoutSetUpDone = true;
    showInstructions();
  }

  /**
   * Public so method can be accessed by fragments
   *
   * @return Current instance of user
   */
  public User getUser() {
    return user;
  }

  /**
   * Public so method can be accessed by fragments
   *
   * @return Returns current instance of list of all users
   */
  public ArrayList<User> getUserList() {
    return userList;
  }

  /**
   * Public so method can be accessed by fragments
   *
   * @return Returns current instance of user's friend list
   */
  public ArrayList<User> getFriendList() {
    return friendList;
  }

  //Disables back button so user must logout to go back to login screen
  @Override
  public void onBackPressed() {
    return;
  }
}
