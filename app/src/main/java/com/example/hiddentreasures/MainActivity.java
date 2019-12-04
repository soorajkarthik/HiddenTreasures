package com.example.hiddentreasures;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.hiddentreasures.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    //Fields
    private FirebaseDatabase database;
    private DatabaseReference users;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private String username;
    private User user;
    private boolean isTabLayoutSetUpDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        username = Objects.requireNonNull(getIntent()
                .getExtras()
                .getString("username"));


        isTabLayoutSetUpDone = false;
        updateUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLastSeen();
    }

    public void updateUser() {

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);
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

    private void updateLastSeen() {

        if (user != null) {
            user.setLastSeen(System.currentTimeMillis());
            users.child(username).child("lastSeen").setValue(user.getLastSeen());
        }

    }

    private void setUpLayout() {

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition(), true);

                switch (tab.getPosition()) {

                    case 0:
                        setTitle("Social");
                        break;

                    case 1:
                        setTitle("Map");
                        break;

                    case 2:
                        setTitle("Profile");
                        break;

                    default:
                        break;
                }
            }

            //Required method
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            //Required method
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Connects ViewPager to TabLayout
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        isTabLayoutSetUpDone = true;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
