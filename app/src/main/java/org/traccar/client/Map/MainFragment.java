package org.traccar.client.Map;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.Manifest;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.traccar.client.Constants;
import org.traccar.client.R;
import org.traccar.client.ReverseGeocodingService;

import java.lang.reflect.Type;
import java.util.Calendar;

public class MainFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    TextView address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize GoogleMap
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        // Initialize FusedLocationClient to get last location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // show alert to get permission for ACCESS_FINE_LOCATION
        checkLocationPermission();

        // show alert when gps settings is off
        setLocationSettingsEnabled();

        //get last known location
        getLastLocation();

        Typeface sansMedium = Typeface.createFromAsset(getContext().getAssets(),"fonts/sansmedium.ttf");
        address = v.findViewById(R.id.fragment_main_address);
        address.setTypeface(sansMedium);

        TextView lastLocationStatus = v.findViewById(R.id.fragment_main_last_location_status);
        lastLocationStatus.setTypeface(sansMedium);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Calendar calendar = Calendar.getInstance();

        long lastUpdate = preferences.getLong("lastUpdate",0);
        if(lastUpdate!=0){
            calendar.setTimeInMillis(preferences.getLong("lastUpdate",0));
            lastLocationStatus.setText("آخرین ارسال موقعیت شما : "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE));
        }else{
            lastLocationStatus.setText("تا کنون موقعیت شما به سرور ارسال نشده است");
        }

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(checkLocationPermission()){
            map.setMyLocationEnabled(true);
            getLastLocation();
        }
    }

    private void goToLocation(double lat, double lng){
        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f);
        map.moveCamera(cameraUpdate);
    }

    void setLocationSettingsEnabled(){
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder settings = new LocationSettingsRequest.Builder().addLocationRequest(request);
        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> responseTask = client.checkLocationSettings(settings.build());

        responseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("LOCATION","SUCCESS");
            }
        });

        responseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if( e instanceof ResolvableApiException){
                    try{
                        ResolvableApiException exception = (ResolvableApiException) e;
                        exception.startResolutionForResult(getActivity(),1);
                    }catch (IntentSender.SendIntentException sendEx){
                        Log.e("ERROR","GET LOCATION SETTINGS");
                    }
                }
            }
        });
    }

    void getLastLocation(){
        // Start get location
        // Check permission
        if (checkLocationPermission())
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    goToLocation(location.getLatitude(),location.getLongitude());
                    lastLocation = location;
                    if(!Geocoder.isPresent()){
                        Toast.makeText(getContext(),"مشکل در دریافت آدرس",Toast.LENGTH_LONG).show();
                    }else startIntentService();
                }else{
                    Log.e("LOCATION","NULL");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("LOCATION","FAILURE");
            }
        });
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Log.e("RESUME","OK");
            getLastLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Log.e("PAUSE","OK");
        }
    }

    private void startIntentService(){
        Intent intent = new Intent(getContext(),ReverseGeocodingService.class);
        AddressResultReceiver addressResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,lastLocation);
        getContext().startService(intent);

    }

    class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                address.setText(mAddressOutput);
            }

        }
    }

}
