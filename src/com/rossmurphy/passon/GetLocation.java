package com.rossmurphy.passon;

import java.util.List;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class GetLocation implements LocationListener, SensorEventListener{

	Context con;
	LocationManager mLocationManager;
	private LocationManager locationManager;
	String best;
	String[] args;
	private double lat;
	private double lng;
	public  double getLat() {
		return this.lat;
	}

	public  void setLat(double lat) {
		this.lat = lat;
	}

	public  double getLng() {
		return this.lng;
	}

	public  void setLng(double lng) {
		this.lng = lng;
	}
	
	private float[] matrixR;
	//private float[] matrixI;
	private float[] matrixValues;
	private float[] mOrientation = new float[3];
	private float[] mR;

	private String provider;
	boolean a;
	boolean b;
	private String currentDegree = "0";
	// device sensor manager
	private SensorManager mSensorManager;
	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;
	String callingClass;
	Location location=null;

	public GetLocation(Context c,String calledBy){
		this.con=c;
		this.callingClass=calledBy;
		// initialize your android device sensor capabilities
		mSensorManager = (SensorManager)c.getSystemService(c.SENSOR_SERVICE);
		mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		args = new String[2];

		// Get the location manager
		locationManager = (LocationManager)c.getSystemService(c.LOCATION_SERVICE);

		// Define the criteria how to select the location provider -> use
		// default
		Criteria criteria = new Criteria();
		

		best=locationManager.getBestProvider(criteria,false);
		System.out.println("\nBest provider is: " + best);
		System.out.println("\nLocations (starting with last known):");
		if (best != null) {
			//System.out.println("Type is not null");
			location=locationManager.getLastKnownLocation(best);
			//System.out.println(location);
			if(location==null){
				location = getLastKnownLocation();
			}
		}

		//updateLocation(location);
		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		//matrixI = new float[9];
		mR = new float[9];
		matrixValues = new float[3];
		locationManager.requestLocationUpdates(best,15000,10,this);
		//provider = locationManager.getBestProvider(criteria, false);
		//locationManager.requestLocationUpdates(provider,15000,1,this);
		// Location location = locationManager.getLastKnownLocation(provider);

		//System.out.println(location);
		// Initialize the location fields

		if (location != null) {
			//System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			Toast.makeText(c, "Error retrieving location " + provider,
					Toast.LENGTH_SHORT).show();
		}
		a = false;
		b=false;
	}

	public void resume(){
		locationManager.requestLocationUpdates(best, 1500, 10, this);
		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}
	public void pause(){
		locationManager.removeUpdates(this);
		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// get the angle around the z-axis rotated
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(event.values, 0, valuesMagneticField, 0, event.values.length);
			a=true;
			//System.out.println(Arrays.toString(valuesMagneticField));
			break;
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, valuesAccelerometer, 0, event.values.length);
			b=true;
			//System.out.println(Arrays.toString(valuesAccelerometer));
			break;
		}

		if(a==true&&b==true){
			boolean success = SensorManager.getRotationMatrix(
					matrixR,
					null,
					valuesAccelerometer,
					valuesMagneticField);

			SensorManager.getOrientation(matrixR, mOrientation);

			float azimuthInRadians = mOrientation[0];
			float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
			//System.out.println(azimuthInDegress  + " rotation");
			
			if(!currentDegree.equals(String.format("%.0f", azimuthInDegress))){

				
				if(callingClass.equals("SendActivity")){
					SendActivity.drawText(azimuthInDegress);
				}
				currentDegree=String.format("%.0f", azimuthInDegress);
			}

			
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.out.println("Location Changed");
		lat = location.getLatitude();
		lng = location.getLongitude();
		args[0]= String.valueOf(lat);
		args[1]= String.valueOf(lng);
		new SendLocation().execute(args);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(con, "Problem reaching provider" + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(con, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(con, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	private Location getLastKnownLocation() {
		mLocationManager = (LocationManager)con.getApplicationContext().getSystemService(con.LOCATION_SERVICE);
		List<String> providers = mLocationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocationManager.getLastKnownLocation(provider);
			if (l == null) {
				continue;
			}
			if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
				// Found best last known location: %s", l);
				bestLocation = l;
			}
		}
		return bestLocation;
	}
	class SendLocation extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			//System.out.println("update location "   + lat + " "+ lng);
			return null;
		}

	}
	
	
}
