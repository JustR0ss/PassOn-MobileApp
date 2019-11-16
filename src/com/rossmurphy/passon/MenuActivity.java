package com.rossmurphy.passon;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MenuActivity extends Activity{
	GetLocation gl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		gl = new GetLocation(this,"MenuActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();

		String[] args = new String[1];
		args[0] = "http://54.76.199.8:8080/PassOn/services/Main/updateLocation?longi="+String.valueOf(gl.getLng())+"&lat="+String.valueOf(gl.getLat())+"&number="+mPhoneNumber;
		System.out.println(args[0]);
		if(gl.getLng()!=0.0&&gl.getLat()!=0.00){
			new updateLocationClass().execute(args[0]);
		}


		findViewById(R.id.compose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MenuActivity.this, MainActivity.class);
				startActivity(i);
				//Toast.makeText(getApplicationContext(), "multiplayer", Toast.LENGTH_SHORT).show();
			}

		});
		findViewById(R.id.inbox).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				//Toast.makeText(getApplicationContext(), "inbox", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(MenuActivity.this, InboxActivity.class);
				startActivity(i);
			}

		});
		findViewById(R.id.pref).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MenuActivity.this, PrefActivity.class);
				startActivity(i);
			}

		});
		findViewById(R.id.exit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//Intent i = new Intent(MainActivity.this, Instructions.class);
				//startActivity(i);
				//Toast.makeText(getApplicationContext(), "instructions", Toast.LENGTH_SHORT).show();
				MenuActivity.this.finish();
			}

		});
	}

	class updateLocationClass extends AsyncTask<String, Void, Void>{
		URL url;
		HttpURLConnection connection;
		String SetServerString;
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			//System.out.println(params[0]);
			HttpClient Client = new DefaultHttpClient();

			HttpGet httpget = new HttpGet(params[0]);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			try {
				SetServerString = Client.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}
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
}
