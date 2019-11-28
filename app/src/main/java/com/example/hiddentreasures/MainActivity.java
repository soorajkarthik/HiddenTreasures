package com.example.hiddentreasures;


import android.os.Bundle;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.hiddentreasures.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference users;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private String username;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        username = Objects.requireNonNull(getIntent()
                .getExtras()
                .getString("username"));

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(username).getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setUpLayout();
    }

    private void setUpLayout() {

        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition(), true);

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
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
