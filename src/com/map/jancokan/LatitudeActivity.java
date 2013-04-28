package com.map.jancokan;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LatitudeActivity extends android.support.v4.app.FragmentActivity {

	private GoogleMap map;
	private LocationManager myLocation;
	private LocationListener listener;
	private Marker marker;
	
	private static final int TEN_SECOND = 10000;
	private static final int TEN_METER = 10;
	private static final int TWO_MINUTE = 1000 * 60 * 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		myLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		initializeMap();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// setupMediaLocationProvider();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		myLocation.removeUpdates(listener);
	}
	
	public void initializeMap() {
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			map.getUiSettings().setZoomGesturesEnabled(true);
			marker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
			marker.setVisible(false);
			if (map != null) {
				setupMediaLocationProvider();
			}
		}
	}
	
	public void setupMediaLocationProvider() {
		
		Location GPSLocation = null;
		Location WIFILocation = null;
		
		boolean isGPSEnabled = myLocation.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		if(!isGPSEnabled) {
			enableGPS();
		}
		
		myLocation.removeUpdates(listener);
		GPSLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
		WIFILocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);
		
		if((GPSLocation != null) && (WIFILocation != null)) {
			drawPosisionOnMap(getBetterProvider(GPSLocation, WIFILocation));			
		} else if(GPSLocation != null) {
			drawPosisionOnMap(GPSLocation);
		} else if(WIFILocation != null) {
			drawPosisionOnMap(WIFILocation);
		}
	}
	
	public void enableGPS() {
		Toast.makeText(this, "GPS signal not found, please enable GPS", Toast.LENGTH_LONG).show();
		Intent intentGPS = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intentGPS);
	}
	
	private Location requestUpdateFromProvider(final String provider) {
		Location location = null;
		if(myLocation.isProviderEnabled(provider)) {
			myLocation.requestLocationUpdates(provider, TEN_SECOND, TEN_METER, listener);
			location = myLocation.getLastKnownLocation(provider);			;
		} else {
			Toast.makeText(this, "Your current location is temporary unavailable", Toast.LENGTH_LONG).show();
		}
		return location;
	}
	
	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			drawPosisionOnMap(location);	
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	}
	
	private void drawPosisionOnMap(Location location){
		
		LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
		
		String address = ""; 
		String nameCoordinate = "";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		
		try {
			List<Address> addresses = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1);
			if(addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++) {
					address += addresses.get(0).getAddressLine(index) + " ";
					Log.i(address, address);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			List<Address> addressesNames = geocoder.getFromLocationName("unesa", 3);
			if(addressesNames.size() > 0) {
				nameCoordinate = "Lat: " + String.valueOf(addressesNames.get(0).getLatitude()) + "\n" + "Long: " + String.valueOf(addressesNames.get(0).getLongitude());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		marker.remove();
		marker = map.addMarker(new MarkerOptions().position(coordinate).title(nameCoordinate).snippet(address));
		marker.setVisible(true);
		marker.showInfoWindow();
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16).tilt(52).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private Location getBetterProvider(Location GPS, Location WIFI) {
		if(WIFI == null) {
			return GPS;
		}
		
		long timeDelta = GPS.getTime() - WIFI.getTime();
		boolean isSignificantNewer = timeDelta > TWO_MINUTE;
		boolean isSignificantOlder = timeDelta < -TWO_MINUTE;
		boolean isNewer = timeDelta > 0;
		
		if(isSignificantNewer) {
			return GPS;
		} else if(isSignificantOlder) {
			return WIFI;
		}
		
		int accuracy = (int) (GPS.getAccuracy() - WIFI.getAccuracy());
		boolean isLessAccurate = accuracy > 0;
		boolean isMoreAccurate = accuracy < 0;
		boolean isSignificantLessAccurate = accuracy > 200;
		
		boolean isFromSameProvider = isSameProvider(GPS.getProvider(), WIFI.getProvider());
		
		if(isMoreAccurate) {
			return GPS;
		} else if(isNewer && !isLessAccurate) {
			return GPS;
		} else if(isNewer && isSignificantLessAccurate && isFromSameProvider) {
			return GPS;
		}
		
		return WIFI;
	}
	
	private boolean isSameProvider(String provider1, String provider2) {
		if(provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
