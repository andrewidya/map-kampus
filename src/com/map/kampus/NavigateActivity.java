package com.map.kampus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class NavigateActivity extends android.support.v4.app.FragmentActivity
		implements OnMyLocationChangeListener {

	private GoogleMap map;
	LatLng coordinate = null;
	private String message = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		setupIfNeeded();
		checkDestination();
	}

	private void checkDestination() {
		Intent intent = getIntent();
		message = intent.getStringExtra(UniversityActivity.UNIVERSITY_LOCATION);
		setDestination(message);
	}

	private void setDestination(String destination) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addressesNames = geocoder.getFromLocationName(
					destination, 3);
			if (addressesNames.size() > 0) {
				coordinate = new LatLng(addressesNames.get(0).getLatitude(),
						addressesNames.get(0).getLongitude());
				map.addMarker(
						new MarkerOptions().position(coordinate).title(message)
								.snippet("Destination Place")).showInfoWindow();
				CameraPosition camera = new CameraPosition.Builder().target(new LatLng(coordinate.latitude, coordinate.longitude)).zoom(16).build();
				map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupIfNeeded() {
		// TODO Auto-generated method stub
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (map != null) {
				setupMap();
			}
		}
	}

	private void setupMap() {
		// TODO Auto-generated method stub
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		map.setLocationSource(new CurrentLocationProvider(this));

		Location location = new CurrentLocationProvider(this)
				.getCurrentLocation();
		if (location != null) {
			CameraPosition camera = new CameraPosition.Builder()
					.target(new LatLng(location.getLatitude(), location
							.getLongitude())).zoom(16).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
		}
	}

	public class CurrentLocationProvider implements LocationListener,
			LocationSource {

		private OnLocationChangedListener listener;
		private LocationManager locationProvider;
		private static final int TWO_SECOND = 2000;
		private static final int TWO_METER = 2;
		public Location currentLocation;

		public CurrentLocationProvider(Context context) {
			locationProvider = (LocationManager) context
					.getSystemService(LOCATION_SERVICE);
		}

		public Location getCurrentLocation() {
			return currentLocation;
		}

		@Override
		public void activate(OnLocationChangedListener listener) {
			// TODO Auto-generated method stub
			this.listener = listener;
			boolean gpsEnabled = locationProvider
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkEnabled = locationProvider
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// To check the provider available on device, then get location
			// update
			if (gpsEnabled) {
				LocationProvider gpsProvider = locationProvider
						.getProvider(LocationManager.GPS_PROVIDER);
				if (gpsProvider != null) {
					locationProvider.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, TWO_SECOND,
							TWO_METER, this);
				}
			} else if (networkEnabled) {
				LocationProvider networkProvider = locationProvider
						.getProvider(LocationManager.NETWORK_PROVIDER);
				if (networkProvider != null) {
					locationProvider.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, TWO_SECOND,
							TWO_METER, this);
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
			// TODO Auto-generated method stub
			String url = null;
			this.currentLocation = location;
			if (listener != null) {
				listener.onLocationChanged(location);
				if (location != null) {
					url = makeURL(location.getLatitude(),
							location.getLongitude(), coordinate.latitude,
							coordinate.longitude);
					new connectAsyncTask(url, getApplicationContext());
				}
			}
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

	public String makeURL(double sourcelat, double sourcelog, double destlat,
			double destlog) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		return urlString.toString();
	}

	public void drawPath(String result) {

		try {
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes
					.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);

			for (int z = 0; z < list.size() - 1; z++) {
				LatLng src = list.get(z);
				LatLng dest = list.get(z + 1);
				PolylineOptions lineOptions = new PolylineOptions();
				lineOptions
						.add(new LatLng(src.latitude, src.longitude),
								new LatLng(dest.latitude, dest.longitude))
						.width(2).color(Color.BLUE).geodesic(true);
				Polyline line = map.addPolyline(lineOptions);// NavigateActivity.this.map.addPolyline(new
																// PolylineOptions().add(new
																// LatLng(src.latitude,
																// src.longitude),
																// new
																// LatLng(dest.latitude,
																// dest.longitude)).width(2).color(Color.BLUE).geodesic(true));
				line.setPoints(list);
			}

		} catch (JSONException e) {

		}
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	private class connectAsyncTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		String url;
		Context context;

		connectAsyncTask(String urlPass, Context contextPass) {
			url = urlPass;
			context = contextPass;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONFromUrl(url);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.hide();
			if (result != null) {
				drawPath(result);
			}
		}
	}
}
