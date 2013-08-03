package com.map.kampus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

public class NavigateActivity extends android.support.v4.app.FragmentActivity implements OnMyLocationChangeListener {

	private GoogleMap map;
	LatLng coordinate = null;
	private String message = null;
	TextView addressText, distanceText;
	Location toLocation, startLocation;
	LatLng from, to;
	DirectionRoute direction;
	
	// For Location	

	private LocationManager myLocation;
	private LocationListener listener;

	private static final int TEN_SECOND = 10000;
	private static final int TEN_METER = 10;
	private static final int TWO_MINUTE = 1000 * 60 * 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		
		addressText = (TextView) findViewById(R.id.address_on_map_bound);
		distanceText = (TextView) findViewById(R.id.distance_on_map_bound);
		
		myLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		
		setupIfNeeded();
		checkDestination();
		setupRoute();
		
		float distances = startLocation.distanceTo(toLocation);
		String distancesValue = String.valueOf(distances);
		distanceText.setText(distancesValue + "Meter");
	}
	
	private void setupRoute() {		
		
		direction = new DirectionRoute();
		Document doc = direction.getDocument(from, to, DirectionRoute.MODE_DRIVING);
		ArrayList<LatLng> directionPoints = direction.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.DKGRAY);

		for (int i = 0; i < directionPoints.size(); i++)
		{
			rectLine.add(directionPoints.get(i));
		}
		
		map.addPolyline(rectLine);
		//float distanceFloat = startLocation.distanceTo(toLocation);
		//String distance = String.valueOf(distanceFloat);
		//distanceText.setText(distance);
	}
	
	private void checkDestination() {
		Intent intent = getIntent();
		message = intent.getStringExtra(UniversityActivity.UNIVERSITY_LOCATION);
		setDestination(message);
	}

	private void setDestination(String destination) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addressesNames = geocoder.getFromLocationName(destination, 3);
			if (addressesNames.size() > 0) {
				coordinate = new LatLng(addressesNames.get(0).getLatitude(),addressesNames.get(0).getLongitude());
				to = new LatLng(coordinate.latitude, coordinate.longitude);				
				map.addMarker(new MarkerOptions().position(coordinate).title(message).snippet("Destination Place")).showInfoWindow();
				CameraPosition camera = new CameraPosition.Builder().target(new LatLng(coordinate.latitude, coordinate.longitude)).zoom(16).build();
				map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
				
				// To get address name of destination location
				 try {
				        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
				        List<Address> addresses = geo.getFromLocation(coordinate.latitude, coordinate.longitude, 1);
				        if (addresses.isEmpty()) {
				            addressText.setText("Waiting for location");
				        }
				        else {
				            if (addresses.size() > 0) {
				                addressText.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
				            }
				        }
				    } catch (IOException e) {
				    	e.printStackTrace();
				    }				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupIfNeeded() {
		// TODO Auto-generated method stub
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (map != null) {
				//setupMap();
				setupMediaLocationProvider();
			}
		}
	}

	/*private void setupMap() {
		// TODO Auto-generated method stub
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		map.setLocationSource(new CurrentLocationProvider(this));

		Location location = new CurrentLocationProvider(this).getCurrentLocation();
		if (location != null) {
			CameraPosition camera = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
			// location.distanceTo(destinationLocation);
		}
	}*/

	/*
	public class CurrentLocationProvider implements LocationListener,
			LocationSource {

		private OnLocationChangedListener listener;
		private LocationManager locationProvider;
		private static final int TWO_SECOND = 2000;
		private static final int TWO_METER = 2;
		public Location currentLocation;

		public CurrentLocationProvider(Context context) {
			locationProvider = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		}

		public Location getCurrentLocation() {
			return currentLocation;
		}

		@Override
		public void activate(OnLocationChangedListener listener) {
			// TODO Auto-generated method stub
			this.listener = listener;
			boolean gpsEnabled = locationProvider.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkEnabled = locationProvider.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// To check the provider available on device, then get location
			// update
			if (gpsEnabled) {
				LocationProvider gpsProvider = locationProvider.getProvider(LocationManager.GPS_PROVIDER);
				if (gpsProvider != null) {
					locationProvider.requestLocationUpdates(LocationManager.GPS_PROVIDER, TWO_SECOND, TWO_METER, this);
				}
			} else if (networkEnabled) {
				LocationProvider networkProvider = locationProvider.getProvider(LocationManager.NETWORK_PROVIDER);
				if (networkProvider != null) {
					locationProvider.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TWO_SECOND, TWO_METER, this);
				}
			}
		}

		@Override
		public void deactivate() {
			// TODO Auto-generated method stub
			locationProvider.removeUpdates(this);
		}

		@Override
		public void onLocationChanged(Location location) {
			this.currentLocation = location;
			from = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			//startLocation.setLongitude(from.longitude);
			//startLocation.setLatitude(from.latitude);
			if (listener != null) {
				listener.onLocationChanged(location);
			}
			setupRoute();
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub
		map.setOnMyLocationChangeListener(null);
	}
	*/
	public void setupMediaLocationProvider() {

		Location GPSLocation = null;
		Location WIFILocation = null;

		boolean isGPSEnabled = myLocation.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!isGPSEnabled) {
			enableGPS();
		}

		myLocation.removeUpdates(listener);
		GPSLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
		WIFILocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

		if ((GPSLocation != null) && (WIFILocation != null)) {
			drawPosisionOnMap(getBetterProvider(GPSLocation, WIFILocation));
		} else if (GPSLocation != null) {
			drawPosisionOnMap(GPSLocation);
		} else if (WIFILocation != null) {
			drawPosisionOnMap(WIFILocation);
		}
	}

	public void enableGPS() {
		Toast.makeText(this, "GPS signal not found, please enable GPS",	Toast.LENGTH_LONG).show();
		Intent intentGPS = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intentGPS);
	}

	private Location requestUpdateFromProvider(final String provider) {
		Location location = null;
		if (myLocation.isProviderEnabled(provider)) {
			myLocation.requestLocationUpdates(provider, TEN_SECOND, TEN_METER, listener);
			location = myLocation.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, "Your current location is temporary unavailable", Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			startLocation = location;
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

	private void drawPosisionOnMap(Location location) {		

		from = new LatLng(location.getLatitude(), location.getLongitude());
		LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());	

		String address = "";
		String nameCoordinate = "";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		try {
			List<Address> addresses = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1);
			if (addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++) {
					address += addresses.get(0).getAddressLine(index) + " ";
					Log.i(address, address);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			List<Address> addressesNames = geocoder.getFromLocationName(
					"unesa", 3);
			if (addressesNames.size() > 0) {
				nameCoordinate = "Lat: "
						+ String.valueOf(addressesNames.get(0).getLatitude())
						+ "\n" + "Long: "
						+ String.valueOf(addressesNames.get(0).getLongitude());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		map.addMarker(new MarkerOptions().position(coordinate).title(nameCoordinate).snippet(address));
		//CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16).tilt(52).build();
		//map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private Location getBetterProvider(Location GPS, Location WIFI) {
		if (WIFI == null) {
			return GPS;
		}

		long timeDelta = GPS.getTime() - WIFI.getTime();
		boolean isSignificantNewer = timeDelta > TWO_MINUTE;
		boolean isSignificantOlder = timeDelta < -TWO_MINUTE;
		boolean isNewer = timeDelta > 0;

		if (isSignificantNewer) {
			return GPS;
		} else if (isSignificantOlder) {
			return WIFI;
		}

		int accuracy = (int) (GPS.getAccuracy() - WIFI.getAccuracy());
		boolean isLessAccurate = accuracy > 0;
		boolean isMoreAccurate = accuracy < 0;
		boolean isSignificantLessAccurate = accuracy > 200;

		boolean isFromSameProvider = isSameProvider(GPS.getProvider(),
				WIFI.getProvider());

		if (isMoreAccurate) {
			return GPS;
		} else if (isNewer && !isLessAccurate) {
			return GPS;
		} else if (isNewer && isSignificantLessAccurate && isFromSameProvider) {
			return GPS;
		}

		return WIFI;
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onMyLocationChange(Location arg0) {
		// TODO Auto-generated method stub
		
	}
}
