package com.rossmurphy.passon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

public class ViewActivity extends Activity{
	
	 String fName;
	 
	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	     //No call for super(). Bug on API Level > 11.
	 }
	 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		Intent intent = getIntent();
		fName = InboxActivity.path+"/PassOn/"+intent.getStringExtra("fileName");
		
		ImageView imageView = new ImageView(getApplicationContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		String path = fName;
		Bitmap image = BitmapFactory.decodeFile(path);
		imageView.setImageBitmap(image);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout1);
		rl.addView(imageView, lp);
	}
	
}
