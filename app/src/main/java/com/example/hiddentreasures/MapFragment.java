package com.example.hiddentreasures;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.Treasure;
import com.example.hiddentreasures.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

  private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
  private FirebaseDatabase database;
  private DatabaseReference users;
  private DatabaseReference treasures;
  private String username;
  private User user;
  private View view;
  private MapView mapView;
  private GoogleMap mGoogleMap;
  private ArrayList<Treasure> treasureList;
  private ClusterManager<Treasure> clusterManager;
  private boolean initialCameraLocationSet;
  private LocationRequest locationRequest;
  private Location currentLocation;
  private FusedLocationProviderClient fusedLocationClient;
  private Circle currentDrawnCircle;

  private LocationCallback locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult locationResult) {
      List<Location> locationList = locationResult.getLocations();
      if (locationList.size() > 0) {
        // The last location in the list is the newest
        currentLocation = locationList.get(locationList.size() - 1);
        if (!initialCameraLocationSet) {
          mGoogleMap.moveCamera(
              CameraUpdateFactory.newLatLng(
                  new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
          initialCameraLocationSet = true;
        }
      }
    }
  };

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    super.onCreateView(inflater, container, savedInstanceState);
    database = FirebaseDatabase.getInstance();
    treasures = database.getReference("Treasures");
    users = database.getReference("Users");
    user = ((MainActivity) getActivity()).getUser();
    username = user.getUsername();
    treasureList = new ArrayList<>();

    treasures.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            dataSnapshot
                .getChildren()
                .forEach(child -> treasureList.add(child.getValue(Treasure.class)));
            loadMap();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
          }
        });

    setHasOptionsMenu(true);
    view = inflater.inflate(R.layout.fragment_map, container, false);
    initialCameraLocationSet = false;
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mapView = view.findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
  }

  @SuppressLint("NewApi")
  private void loadMap() {

    mapView.getMapAsync(
        googleMap -> {
          MapsInitializer.initialize(getContext());

          mGoogleMap = googleMap;
          mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

          clusterManager = new ClusterManager<>(getContext(), mGoogleMap);
          clusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<>());

          clusterManager.setRenderer(
              new DefaultClusterRenderer<Treasure>(getContext(), mGoogleMap, clusterManager) {
                private final IconGenerator iconGenerator = new IconGenerator(getContext());

                @Override
                protected void onBeforeClusterItemRendered(Treasure item, MarkerOptions markerOptions) {
                  super.onBeforeClusterItemRendered(item, markerOptions);

                  if (user.getTreasuresFoundTodayIDs().contains(item.getId())) {
                    iconGenerator.setBackground(getContext().getDrawable(R.drawable.ic_treasure_found));
                  } else {
                    switch (item.getRarity()) {
                      case Treasure.COMMON:
                        iconGenerator.setBackground(getContext().getDrawable(R.drawable.ic_treasure_common));
                        break;
                      case Treasure.UNCOMMON:
                        iconGenerator.setBackground(getContext().getDrawable(R.drawable.ic_treasure_uncommon));
                        break;
                      case Treasure.RARE:
                        iconGenerator.setBackground(getContext().getDrawable(R.drawable.ic_treasure_rare));
                        break;
                      case Treasure.ULTRA_RARE:
                        iconGenerator.setBackground(getContext().getDrawable(R.drawable.ic_treasure_ultrarare));
                        break;
                      case Treasure.LEGENDARY:
                        iconGenerator.setBackground(getContext().getDrawable(R.drawable.ic_treasure_legendary));
                        break;
                    }
                  }

                  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
                  markerOptions.flat(true);
                }
              });

          clusterManager.setAnimation(false);

          clusterManager.setOnClusterItemClickListener(
              treasure -> {
                Location temp = new Location("");
                temp.setLatitude(treasure.getLatitude());
                temp.setLongitude(treasure.getLongitude());

                float distance = currentLocation.distanceTo(temp);

                if (currentDrawnCircle != null) {
                  currentDrawnCircle.remove();
                }

                if (distance <= 250
                    && !user.getTreasuresFoundTodayIDs().contains(treasure.getId())) {
                  user.addFoundTreasure(treasure);
                  users.child(username).setValue(user);

                  mGoogleMap.clear();
                  mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(11.5f));
                  clusterManager.cluster();

                  new AlertDialog.Builder(getContext())
                      .setTitle(
                          "Congratulations!\nYou found a " + treasure.getRarity()
                              + " treasure!")
                      .setNegativeButton(
                          "OK",
                          ((dialog, which) ->
                              Toast.makeText(getContext(), "Keep Exploring!",
                                  Toast.LENGTH_LONG)
                                  .show()))
                      .create()
                      .show();
                } else if (!user.getTreasuresFoundTodayIDs().contains(treasure.getId())) {

                  currentDrawnCircle =
                      mGoogleMap.addCircle(
                          new CircleOptions()
                              .radius(250)
                              .center(treasure.getPosition())
                              .clickable(false)
                              .fillColor(Color.argb(0.5f, 0, 89, 117)));
                }

                return false;
              });

          treasureList.forEach(treasure -> clusterManager.addItem(treasure));

          mGoogleMap.setOnCameraIdleListener(clusterManager);
          mGoogleMap.setOnMarkerClickListener(clusterManager);
          mGoogleMap.setMinZoomPreference(11.5f);

          mGoogleMap.setOnMapClickListener(
              latLng -> {
                if (currentDrawnCircle != null) {
                  currentDrawnCircle.remove();
                }
              });

          locationRequest = new LocationRequest();
          locationRequest.setInterval(5000); // five second interval
          locationRequest.setFastestInterval(1000);
          locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

          if (ContextCompat.checkSelfPermission(
              getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED) {
            // Location Permission already granted
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
          } else {
            // Request Location Permission
            checkLocationPermission();
          }
        });
  }

  private void checkLocationPermission() {
    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(
          getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        new AlertDialog.Builder(getContext())
            .setTitle("Location Permission Needed")
            .setMessage(
                "This app needs the Location permission, please accept to use location functionality")
            .setPositiveButton(
                "OK",
                (dialogInterface, which) -> {
                  // Prompt the user once explanation has been shown
                  ActivityCompat.requestPermissions(
                      getActivity(),
                      new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                      MY_PERMISSIONS_REQUEST_LOCATION);
                })
            .create()
            .show();
      } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(
            getActivity(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode
        == MY_PERMISSIONS_REQUEST_LOCATION) { // If request is cancelled, the result arrays are
      // empty.
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(
            getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

          fusedLocationClient.requestLocationUpdates(
              locationRequest, locationCallback, Looper.myLooper());
          mGoogleMap.setMyLocationEnabled(true);
        }
      } else {

        // permission denied, boo! Disable the
        // functionality that depends on this permission.
        Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
      }
    }
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
