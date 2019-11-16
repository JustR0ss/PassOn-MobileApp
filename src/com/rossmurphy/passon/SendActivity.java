package com.rossmurphy.passon;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.Toast;

public class SendActivity extends Activity {

	
	Location myLocation;
	static SendView v;
	static double lng;
	static double lat;
	float current;
	static String pic;
	// record the compass picture angle turned
	
	GetLocation gl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		 pic = intent.getStringExtra("pictureName");
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
	    String mPhoneNumber = tMgr.getLine1Number();
		v = new SendView(this,mPhoneNumber);
		v.setDrawingCacheEnabled(true);
		super.onCreate(savedInstanceState);
		setContentView(v);
		  gl = new GetLocation(this,"SendActivity");
		lng = gl.getLng();
		lat=gl.getLat();
		
	}


	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Resumed");
		gl.resume();
		
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("Paused");
		gl.pause();
		
	}

	public static void drawText(float azimuthInDegress){
		v.drawText("Heading: " + String.format("%.0f", azimuthInDegress) + " degrees", 100,100,azimuthInDegress);

	}
}
