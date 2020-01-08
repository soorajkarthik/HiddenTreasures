package com.example.hiddentreasures;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

  //fields
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

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.menu_map, menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.optHelp) {
      new AlertDialog.Builder(getContext())
          .setTitle("Help")
          .setMessage(
              "\n- Pinch to zoom"
                  + "\n- Move within a treasure's circle to collect it"
                  + "\n- Color Cheat-Sheet:"
                  + "\n\t\t- Blue - Common - 100 Points"
                  + "\n\t\t- Green - Uncommon - 250 Points"
                  + "\n\t\t- Yellow - Rare - 500 Points"
                  + "\n\t\t- Orange - Ultra-Rare - 1000 Points"
                  + "\n\t\t- Red - Legendary - 5000 Points")
          .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
          .create()
          .show();
    }

    return true;
  }

  private void loadMap() {

    mapView.getMapAsync(
        googleMap -> {
          MapsInitializer.initialize(getContext());

          mGoogleMap = googleMap;
          mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
          mGoogleMap.setMinZoomPreference(11.5f);

          clusterManager = new ClusterManager<>(getContext(), mGoogleMap);
          clusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<>());

          clusterManager.setRenderer(
              new DefaultClusterRenderer<Treasure>(getContext(), mGoogleMap, clusterManager) {
                private final IconGenerator iconGenerator = new IconGenerator(getContext());

                @Override
                protected void onBeforeClusterItemRendered(Treasure item,
                    MarkerOptions markerOptions) {
                  super.onBeforeClusterItemRendered(item, markerOptions);

                  if (user.getTreasuresFoundTodayIDs().contains(item.getId())) {
                    iconGenerator
                        .setBackground(getContext().getDrawable(R.drawable.ic_treasure_found));
                  } else {
                    switch (item.getRarity()) {
                      case Treasure.COMMON:
                        iconGenerator
                            .setBackground(getContext().getDrawable(R.drawable.ic_treasure_common));
                        break;
                      case Treasure.UNCOMMON:
                        iconGenerator.setBackground(
                            getContext().getDrawable(R.drawable.ic_treasure_uncommon));
                        break;
                      case Treasure.RARE:
                        iconGenerator
                            .setBackground(getContext().getDrawable(R.drawable.ic_treasure_rare));
                        break;
                      case Treasure.ULTRA_RARE:
                        iconGenerator.setBackground(
                            getContext().getDrawable(R.drawable.ic_treasure_ultrarare));
                        break;
                      case Treasure.LEGENDARY:
                        iconGenerator.setBackground(
                            getContext().getDrawable(R.drawable.ic_treasure_legendary));
                        break;
                    }
                  }

                  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
                  markerOptions.flat(false);
                }
              });

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
                  clusterManager.cluster();

                  new AlertDialog.Builder(getContext())
                      .setTitle(
                          "Congratulations!\nYou found a " + treasure.getRarity() + " treasure!")
                      .setNegativeButton("Ok",
                          ((dialog, which) ->
                              Toast.makeText(getContext(), "Keep Exploring!",
                                  Toast.LENGTH_LONG)
                                  .show()))
                      .create()
                      .show();
                } else if (!user.getTreasuresFoundTodayIDs().contains(treasure.getId())) {

                  currentDrawnCircle = mGoogleMap.addCircle(
                      new CircleOptions()
                          .radius(250)
                          .center(treasure.getPosition())
                          .clickable(false)
                          .fillColor(Color.argb(0.5f, 0, 89, 117)));

                  mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
                }

                return false;
              });

          treasureList.forEach(treasure -> clusterManager.addItem(treasure));

          mGoogleMap.setOnCameraIdleListener(clusterManager);
          mGoogleMap.setOnMarkerClickListener(clusterManager);

          mGoogleMap.setOnMapClickListener(
              latLng -> {
                if (currentDrawnCircle != null) {
                  currentDrawnCircle.remove();
                }
              });

          mGoogleMap.setOnCameraMoveListener(() -> {
            if (mGoogleMap.getCameraPosition().zoom < 14f && currentDrawnCircle != null) {
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

      // Check if we need to show explanation for location services
      if (ActivityCompat.shouldShowRequestPermissionRationale(
          getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

        /*
         * Show an explanation to the user asynchronously. After the user sees the explanation,
         * try again to request the permission.
         */
        new AlertDialog.Builder(getContext())
            .setTitle("Location Permission Needed")
            .setMessage("This app needs location permissions to be used to its fullest. "
                + "\nPlease accept to use location functionality")
            .setPositiveButton("OK", (dialog, which) -> {
              // Prompt the user once explanation has been shown
              ActivityCompat.requestPermissions(
                  getActivity(),
                  new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                  MY_PERMISSIONS_REQUEST_LOCATION);
            })
            .create()
            .show();
      } else {
        // No explanation needed\
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
    if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {

      // If request is cancelled, the result arrays are empty.
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        // Permission was granted. Access user's location.
        if (ContextCompat.checkSelfPermission(
            getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

          fusedLocationClient.requestLocationUpdates(
              locationRequest, locationCallback, Looper.myLooper());
          mGoogleMap.setMyLocationEnabled(true);
        }
      } else {

        // Permission denied. Don't access user's location.
        Toast.makeText(
            getContext(),
            "permission denied",
            Toast.LENGTH_LONG)
            .show();
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
