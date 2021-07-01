package com.kdkvit.wherewasi.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.kdkvit.wherewasi.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import models.LocationsGroup;
import models.MyLocation;

import static com.kdkvit.wherewasi.fragments.ActivityFragment.locations;

public class MapsFragment extends Fragment {

    GoogleMap googleMap;
    private ClusterManager<MyLocation> mClusterManager;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap gMap) {
            googleMap = gMap;
            
            setMapPointers();
        }
    };
    private View rootView;
    private LinearLayout infoContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        infoContainer = rootView.findViewById(R.id.info_container);
        infoContainer.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void setMapPointers() {
        if (googleMap != null && getContext()!=null) {
            googleMap.clear();
            mClusterManager = new ClusterManager<MyLocation>(getContext(), googleMap);
            googleMap.setOnCameraIdleListener(mClusterManager);
            googleMap.setOnMarkerClickListener(mClusterManager);
            googleMap.setOnInfoWindowClickListener(mClusterManager);

            mClusterManager.getMarkerCollection().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    final LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.custom_info_window, null);
                    final TextView textView = view.findViewById(R.id.textViewTitle);
                    String text = (marker.getTitle() != null) ? marker.getTitle() : "Cluster Item";
                    textView.setText(text);
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyLocation>() {
                @Override
                public boolean onClusterClick(Cluster<MyLocation> cluster) {
                    List<MyLocation> cLocations = new ArrayList<>(cluster.getItems());

                    if(infoContainer.getVisibility() == View.VISIBLE){
                        rootView.findViewById(R.id.info_container).setVisibility(View.GONE);
                        TextView locationNumTV = (TextView) rootView.findViewById(R.id.location_num_tv);
                        locationNumTV.setText("Locations: " + cLocations.size());
                    }else {
                        rootView.findViewById(R.id.info_container).setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });

            initCluster();
            if (locations.size() > 0) {
                focus(locations.get(0).getLastLocation(),true);
            }
        }
    }

    private void initCluster() {
        mClusterManager.clearItems();
        for(LocationsGroup group:locations)
        mClusterManager.addItems(group.getLocations());
        mClusterManager.cluster();
    }

    public void focus(MyLocation myLocation,boolean init) {
        LatLng mapPoint = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        float zoom = init ?  12.0f : 20.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPoint,zoom));
        if(!init){

        }
    }
}