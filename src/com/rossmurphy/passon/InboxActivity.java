package com.rossmurphy.passon;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class InboxActivity extends ListActivity {
	static String path;
	String[] fileNames;
	String[] fileNamesText;
	TextView text;
	int length;
	int length1;
	File f2 = new File(path+"/PassOn/Received");
	File f = new File(path+"/PassOn");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("PassOn Inbox");
		setContentView(R.layout.activity_inbox);
		path = Environment.getExternalStorageDirectory().getAbsolutePath(); 
		text = (TextView) findViewById(R.id.mainText);

		f = new File(path+"/PassOn");
		f2 = new File(path+"/PassOn/Received");

		f.mkdirs();
		f2.mkdirs();

		TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		String[] params = new String[1];
		params[0]= mPhoneNumber;
		new requestUpdate().execute(params);

	}
	public void updateArray(){

		File[] file = f.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".png") && pathname.isFile();
		    }
		});

		length = file.length;
		length1=file.length;

		File[] file2 = f2.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".png") && pathname.isFile();
		    }
		});
		
		length = length+file2.length;
		System.out.println(length);
		System.out.println(length1);

		fileNames= new String[length];
		fileNamesText= new String[length];

		for (int i=0; i < length1; i++)
		{
			System.out.println(i);
			fileNamesText[i]=file[i].getName()+" Sent";
			fileNames[i]=file[i].getName();
		}

		if(file2.length>0){

			for (int i=length1; i < length; i++)
			{
				int num = length1;
				fileNames[i]=file2[i-num].getName();
				fileNamesText[i]=file2[i-num].getName() + " Received";
			}
		}
		System.out.println(Arrays.toString(fileNamesText));
		System.out.println(Arrays.toString(fileNames));
		ArrayAdapter<String> myAdapter = new ArrayAdapter <String>(this,

				R.layout.row_layout, R.id.listText, fileNamesText);



		// assign the list adapter

		setListAdapter(myAdapter);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
	}

	@Override

	protected void onListItemClick(ListView list, View view, int position, long id) {

		super.onListItemClick(list, view, position, id);



		String selectedItem = (String) getListView().getItemAtPosition(position);

		//String selectedItem = (String) getListAdapter().getItem(position);

		Intent i = new Intent(InboxActivity.this, ViewActivity.class);
		i.putExtra("fileName", fileNames[position]);
		startActivity(i);

		text.setText("You clicked " + selectedItem + " at position " + position);

	}

	class requestUpdate extends AsyncTask<String, Void, Void>{
		String[] split;
		@Override
		protected Void doInBackground(String... params) {
			

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://54.76.199.8:8080/PassOn/retrieveImage.jsp?number="+params[0]);
			
			try {
				
				HttpResponse response;

				response = httpclient.execute(httppost);
				HttpEntity rp = response.getEntity();
				String origresponseText =  EntityUtils.toString(rp);
				String responseText = origresponseText.substring(7, origresponseText.length());
				if(!responseText.equals("")){
					split = responseText.split(",");
				}
				
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void unused) {
			split[0] = split[0].replaceAll("\\n", "");
			split[0] = split[0].replaceAll("\\r", "");
			saveNewImage(split[0],split[1]);
			
		}
	}
	public  void saveNewImage(String str,String str2){
		
		//System.out.println("NAme "+ str + " text follows..");
		FileOutputStream imageOutFile;
		
		byte[] imageByteArray = Base64.decode(str2, Base64.DEFAULT);

		try {

			imageOutFile = new FileOutputStream(f2+"/"+str);
			//System.out.println(f2+"/"+str+"/test");
			//System.out.println(imageOutFile.toString());
			imageOutFile.write(imageByteArray);
			imageOutFile.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateArray();
	}
}
