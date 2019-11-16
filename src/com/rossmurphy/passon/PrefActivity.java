package com.rossmurphy.passon;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;






import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PrefActivity extends Activity{

	//Button b;
	EditText t;
	String txt;
	String mPhoneNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pref);
		setTitle("PassOn Preferences");
		//b = (Button)findViewById(R.id.button1);
		t = (EditText) findViewById(R.id.editText1);
		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneNumber = tMgr.getLine1Number();
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				txt = t.getText().toString();
				;
				if(!txt.equals("")){
					String[] params = new String[2];
					params[0] = txt;
					params[1] = mPhoneNumber;
					new updatePref().execute(params);
				}
			}

		});
	}

	class getPref extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	class updatePref extends AsyncTask<String, Void, Void>{
		HttpURLConnection connection;
		URL url;
		HttpResponse response;
		StringEntity se;
		 String SetServerString = "";
		@Override
		protected Void doInBackground(String... params) {
			
			// TODO Auto-generated method stub
			 HttpClient Client = new DefaultHttpClient();
			
			String str = "http://54.76.199.8:8080/PassOn/services/Main/updateName?name="+params[0]+"&number="+params[1];
			HttpGet httpget = new HttpGet(str);
			
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
		@Override
		protected void onPostExecute(Void unused) {
			makeNotice();
		}
	}

	public void makeNotice(){

		Toast.makeText(getApplicationContext(), "Update Preference", Toast.LENGTH_SHORT).show();
	}
}
