package com.rossmurphy.passon;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity  implements ColorPickerDialog.OnColorChangedListener{

	String name;
	View1 v;
	Canvas canvas;
	Paint paint;
	static boolean textFlag;
	public static Paint       mPaint;
	private MaskFilter  mEmboss;
	private MaskFilter  mBlur;
	AlertDialog dialog;
	static String textField; 
	static String path;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		name=null;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);
		path = Environment.getExternalStorageDirectory().getAbsolutePath(); 
		v = new View1(this,0);
		v.setDrawingCacheEnabled(true);
		setContentView(v);
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
				0.4f, 6, 3.5f);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}

	//Create Menu
	private static final int SEND_MENU_ID = Menu.FIRST;
	private static final int TEXT_MENU_ID = Menu.FIRST+1;
	private static final int COLOR_MENU_ID = Menu.FIRST+2;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 3;
	private static final int BLUR_MENU_ID = Menu.FIRST + 4;
	private static final int ERASE_MENU_ID = Menu.FIRST + 5;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 6;
	private static final int Save = Menu.FIRST + 7;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, SEND_MENU_ID, 0, "Send").setShortcut('1', 'e');
		menu.add(0, TEXT_MENU_ID, 0, "Text").setShortcut('1', 't');
		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');
		menu.add(0, Save, 0, "Save").setShortcut('5', 'z');

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case SEND_MENU_ID:
			if(name==null){
				AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
				TextView myMsg = new TextView(this);
				myMsg.setText("Please save picture first.");
				myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
				popupBuilder.setView(myMsg);
				popupBuilder.show();
			}else{
				Intent i = new Intent(this,SendActivity.class);
				i.putExtra("pictureName", name);
				startActivity(i);
			}
			
			return true;
		case TEXT_MENU_ID:
			textFlag =true;
			System.out.println("Pressed");
			v.invalidate();
			return true;
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		case EMBOSS_MENU_ID:
			if (mPaint.getMaskFilter() != mEmboss) {
				mPaint.setMaskFilter(mEmboss);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case BLUR_MENU_ID:
			if (mPaint.getMaskFilter() != mBlur) {
				mPaint.setMaskFilter(mBlur);
			} else {
				mPaint.setMaskFilter(null);
			}
			return true;
		case ERASE_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			mPaint.setAlpha(0x80);
			return true;
		case SRCATOP_MENU_ID:

			mPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.SRC_ATOP));
			mPaint.setAlpha(0x80);
			return true;
		case Save:
			AlertDialog.Builder editalert = new AlertDialog.Builder(MainActivity.this);
			editalert.setTitle("Please Enter the name with which you want to Save");
			final EditText input = new EditText(MainActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			input.setLayoutParams(lp);
			editalert.setView(input);
			editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					name= input.getText().toString();
					v.changeFlag(false);
					v.invalidate();
					Bitmap bitmap = v.getDrawingCache();

					
					System.out.println(path);

					File newPath = new File(path+"/PassOn");
					newPath.mkdirs();
					File file = new File(newPath,name+".png");          
					try 
					{
						if(!file.exists())
						{

							file.createNewFile();
						}else{

						}
						FileOutputStream ostream = new FileOutputStream(file);
						bitmap.compress(CompressFormat.PNG, 10, ostream);
						ostream.close();
						v.invalidate();                            
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}finally
					{

						v.setDrawingCacheEnabled(false);                           
					}
				}
			});

			editalert.show();       
			return true;    
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void colorChanged(int color) {
		// TODO Auto-generated method stub
		mPaint.setColor(color);
	}

	

}
