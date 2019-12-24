package com.example.hiddentreasures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FirebaseDatabase database;
    private DatabaseReference treasures;
    private String username;
    private User user;
    private View view;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        treasures = database.getReference("Treasures");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_map, container, false);
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

        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(33.181128, -96.747210)).title("hi"));
        //CameraPosition pos = CameraPosition.builder().target(new LatLng(33.181128, -96.747210)).build();
        //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }
}