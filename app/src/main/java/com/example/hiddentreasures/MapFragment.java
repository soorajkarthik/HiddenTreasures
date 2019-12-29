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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.*;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

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

        database = FirebaseDatabase.getInstance();
        treasures = database.getReference("Treasures");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        treasureList = new ArrayList<>();

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_map, container, false);

        treasures.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getChildren().forEach(child -> treasureList.add(child.getValue(Treasure.class)));
                clusterManager = new ClusterManager<>(getContext(), mGoogleMap);
                treasureList.forEach(treasure -> clusterManager.addItem(treasure));
                mGoogleMap.setOnCameraIdleListener(clusterManager);
                mGoogleMap.setOnMarkerClickListener(clusterManager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMyLocationEnabled(true);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}