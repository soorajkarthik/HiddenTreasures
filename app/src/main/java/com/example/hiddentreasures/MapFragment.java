package com.example.hiddentreasures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.Treasure;
import com.example.hiddentreasures.Model.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.database.*;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference treasures;
    private String username;
    private User user;
    private View view;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private List<Treasure> treasureList;
    private ClusterManager<Treasure> clusterManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        treasures = database.getReference("Treasures");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        treasureList = new ArrayList<>();

        treasures.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getChildren().forEach(child -> treasureList.add(child.getValue(Treasure.class)));
                loadMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_map, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
    }

    private void loadMap() {

        mapView.getMapAsync(googleMap -> {

            MapsInitializer.initialize(getContext());

            mGoogleMap = googleMap;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.setMyLocationEnabled(true);

            clusterManager = new ClusterManager<>(getContext(), mGoogleMap);
            clusterManager.setAnimation(false);
            treasureList.forEach(treasure -> clusterManager.addItem(treasure));
            mGoogleMap.setOnCameraIdleListener(clusterManager);
            mGoogleMap.setMinZoomPreference(1f);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}