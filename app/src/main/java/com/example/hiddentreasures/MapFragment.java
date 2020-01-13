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

  //Fields
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

  private final LocationCallback locationCallback = new LocationCallback() {
    /**
     * Sets user's current location based on the location result and moves map camera to initially
     * be centered on the user
     *
     * @param locationResult Object containing all of the user's locations since opening the app
     */
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

  /**
   * Get reference to Firebase Database, the "Treasures" and "Users" nodes, the current user from
   * MainActivity and inflates the fragments view
   *
   * @param inflater           The LayoutInflater used by the MainActivity
   * @param container          The ViewGroup that this fragment is a part of
   * @param savedInstanceState The last saved state of the application
   * @return The view corresponding to this fragment
   */
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

  /**
   * Creates components of mapView once the fragment's view has been inflated
   *
   * @param view               The view corresponding to this fragment
   * @param savedInstanceState The last saved state of the application
   */
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mapView = view.findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
  }

  /**
   * Inflates options menu
   *
   * @param menu     Menu used by the current activity
   * @param inflater MenuInflater used by the current activity
   */
  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.menu_map, menu);
  }

  /**
   * Inflates help dialog when user clicks help button
   *
   * @param item The item selected by the user
   * @return True because there is no need for system processing, all processing necessary
   * processing is done in the method
   */
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.optHelp) {
      new AlertDialog.Builder(getContext())
          .setTitle("Help")
          .setMessage(
              "\n- Pinch to zoom"
                  + "\n- Move within a treasure's circle"
                  + "\n  to collect it"
                  + "\n- Color Cheat-Sheet:"
                  + "\n\t\t- Blue - Common - 100 Points"
                  + "\n\t\t- Green - Uncommon - 250 Points"
                  + "\n\t\t- Yellow - Rare - 500 Points"
                  + "\n\t\t- Orange - Ultra-Rare - 1500 Points"
                  + "\n\t\t- Red - Legendary - 5000 Points")
          .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
          .create()
          .show();
    }

    return true;
  }

  /**
   * Loads Google Map asynchronously. Requests for permission to access user's location if
   * permission has not been granted yet. Sets up ClusterManager which allows for clustering of
   * treasure markers. Sets up treasures behavior when clicked.
   */
  private void loadMap() {

    mapView.getMapAsync(
        googleMap -> {
          MapsInitializer.initialize(getContext());

          /*
           * Sets map to regular styled Google Map and restricts zooming out beyond a certain point
           * as ClusterManager render speeds suffer if zoomed out too far
           */
          mGoogleMap = googleMap;
          mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
          mGoogleMap.setMinZoomPreference(11.5f);

          //This algorithm allows for a more natural spread of clusters on the map rather than a grid
          clusterManager = new ClusterManager<>(getContext(), mGoogleMap);
          clusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<>());

          clusterManager.setRenderer(
              new DefaultClusterRenderer<Treasure>(getContext(), mGoogleMap, clusterManager) {

                private final IconGenerator iconGenerator = new IconGenerator(getContext());

                /**
                 * Determines what icon each marker should have based on the treasure's rarity and
                 * whether or not the user has already found that treasure and generates that icon.
                 *
                 * @param item The treasure item that is being rendered
                 * @param markerOptions The options that determine how the marker looks
                 */
                @Override
                protected void onBeforeClusterItemRendered(Treasure item,
                    MarkerOptions markerOptions) {

                  if (user.getTreasuresFoundTodayIDs().contains(item.getId())) {
                    iconGenerator.setBackground(
                        getContext().getDrawable(R.drawable.ic_treasure_found));
                  } else {
                    switch (item.getRarity()) {
                      case Treasure.COMMON:
                        iconGenerator.setBackground(
                            getContext().getDrawable(R.drawable.ic_treasure_common));
                        break;
                      case Treasure.UNCOMMON:
                        iconGenerator.setBackground(
                            getContext().getDrawable(R.drawable.ic_treasure_uncommon));
                        break;
                      case Treasure.RARE:
                        iconGenerator.setBackground(
                            getContext().getDrawable(R.drawable.ic_treasure_rare));
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

          //Processing done when a treasure marker is clicked on the map
          clusterManager.setOnClusterItemClickListener(
              treasure -> {
                Location temp = new Location("");
                temp.setLatitude(treasure.getLatitude());
                temp.setLongitude(treasure.getLongitude());

                //Calculates distance in meters
                float distance = currentLocation.distanceTo(temp);

                if (currentDrawnCircle != null) {
                  currentDrawnCircle.remove();
                }

                /*
                 * If distance is less than 250 meters, shows dialog congratulating user on finding
                 * a treasure and re-renders map so marker of treasure changes to its "found" icon
                 */
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
                }

                /*
                 * If treasure is greater than 250 meters away and still hasn't been found by user,
                 * then show a circle with radius 250 meters so user knows how close they must
                 * get to the treasure. Zooms in on location.
                 */
                else if (!user.getTreasuresFoundTodayIDs().contains(treasure.getId())) {

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

          //Allows map idling and marker clicks to be handled by the ClusterManager
          mGoogleMap.setOnCameraIdleListener(clusterManager);
          mGoogleMap.setOnMarkerClickListener(clusterManager);

          //Removes circle around treasure if empty spot on map is clicked
          mGoogleMap.setOnMapClickListener(
              latLng -> {
                if (currentDrawnCircle != null) {
                  currentDrawnCircle.remove();
                }
              });

          //Removes circle around treasure if user zooms out past a set distance
          mGoogleMap.setOnCameraMoveListener(() -> {
            if (mGoogleMap.getCameraPosition().zoom < 14f && currentDrawnCircle != null) {
              currentDrawnCircle.remove();
            }
          });

          //Initializes location request to retrieves user's location every 2.5 seconds
          locationRequest = new LocationRequest();
          locationRequest.setInterval(2500); // five second interval
          locationRequest.setFastestInterval(2500);
          locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

          /*
           * If location permission has been granted, start looping location request client
           * If not, request permission to access user's location
           */

          if (ContextCompat.checkSelfPermission(getContext(),
              Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
          } else {
            requestLocationPermission();
          }
        });
  }

  private void requestLocationPermission() {
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
            .setMessage(
                "This app needs location permissions in order to allow users to collect treasures "
                    + "they are standing near. Please grant location permissions in order to use "
                    + "all features of this app.")
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
        // No explanation needed
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

        /*
         * If permission was granted, access user's location. If not, don't access user's
         * location and don't set their current location on the Google Map
         */
        if (ContextCompat.checkSelfPermission(
            getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

          fusedLocationClient.requestLocationUpdates(
              locationRequest, locationCallback, Looper.myLooper());
          mGoogleMap.setMyLocationEnabled(true);
        }
      } else {

        Toast.makeText(
            getContext(),
            "permission denied",
            Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  //Lifecycle methods that help improve performance of Google Map
  @Override
  public void onStart() {
    super.onStart();
    mapView.onStart();
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
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
}
