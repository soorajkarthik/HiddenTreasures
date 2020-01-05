package com.example.hiddentreasures;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

  @Override
  protected void onResume() {
    super.onResume();
    updateLastSeen();
  }

  public void updateUser() {

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

            if (!hasUpdatedNotificationToken) {
              updateNotificationToken();
            }

            /*
             * Ensures that tablayout is set up after initial reference to user is received
             * Ensures tablayout is only set up once
             * Sets the tab that is seen when the app is opened to the step counter tab
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

  private void updateLastSeen() {

    if (user != null) {
      user.setLastSeen(System.currentTimeMillis());
      users.child(username).child("lastSeen").setValue(user.getLastSeen());
    }
  }

  private void setUpLayout() {

    toolbar = findViewById(R.id.toolbar);
    tabLayout = findViewById(R.id.tabLayout);
    viewPager = findViewById(R.id.viewPager);
    pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

    viewPager.setAdapter(pagerAdapter);
    setSupportActionBar(toolbar);

    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {

            viewPager.setCurrentItem(tab.getPosition(), true);

            switch (tab.getPosition()) {
              case 0:
                getSupportActionBar().setTitle("Social");
                break;

              case 1:
                getSupportActionBar().setTitle("Map");
                break;

              case 2:
                getSupportActionBar().setTitle("Profile");
                break;

              default:
                break;
            }
          }

          // Required method
          @Override
          public void onTabUnselected(TabLayout.Tab tab) {
          }

          // Required method
          @Override
          public void onTabReselected(TabLayout.Tab tab) {
          }
        });

    // Connects ViewPager to TabLayout
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    isTabLayoutSetUpDone = true;
  }

  public User getUser() {
    return user;
  }

  public ArrayList<User> getUserList() {
    return userList;
  }

  public ArrayList<User> getFriendList() {
    return friendList;
  }

  // effectively disables back button so user cannot go back without logging out
  @Override
  public void onBackPressed() {
    return;
  }
}
