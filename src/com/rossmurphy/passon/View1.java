package com.rossmurphy.passon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class View1 extends View {

	Paint paint;
	int templateNo;
	String dialogText;
	float textPosX,textPosY;
	ArrayList<String> list1 = new ArrayList<String>();
	ArrayList<Float> list2 = new ArrayList<Float>();
	ArrayList<Float> list3 = new ArrayList<Float>();
	ArrayList<Integer> list4 = new ArrayList<Integer>();
	boolean saveFlag =true;
	AlertDialog.Builder editalert;

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;
	private Bitmap  mBitmap;
	private Canvas  mCanvas;
	private Path    mPath,posPath;
	private Paint   mBitmapPaint,currentPosPaint,currentPosPaintCenter;
	private Paint paintText;
	Context context;
	View v;

	public View1(Context context, int templateNo) {
		super(context);
		this.context =context;
		paint= new Paint();
		this.templateNo = templateNo;

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);  
		dialogText ="";
		v=this;

		paintText = new Paint(); 
		paintText.setColor(MainActivity.mPaint.getColor()); 
		paintText.setTextSize(50);

		posPath= new Path();
		currentPosPaint = new Paint();
		currentPosPaint.setStrokeWidth(0.02f);
		currentPosPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		currentPosPaint.setColor(Color.rgb(220,220,220));
		currentPosPaint.setXfermode(new PorterDuffXfermode(
				PorterDuff.Mode.SRC_ATOP));
		currentPosPaint.setAlpha(0x80);

		currentPosPaintCenter = new Paint();
		currentPosPaintCenter.setColor(Color.BLACK);

	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

	}
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);




		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);

		if(templateNo==1){
			paint.setColor(Color.rgb(220,220,220));

			for(int i = 0; i<=canvas.getWidth();i+=20){
				canvas.drawLine(i,0,i,canvas.getHeight(),paint);

			}
			for(int i = 0; i<=canvas.getHeight();i+=20){
				canvas.drawLine(0,i,canvas.getWidth(),i,paint);
			}



		}
		paint.reset();

		canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
		if(saveFlag==true){
			canvas.drawCircle( textPosX,textPosY,50, currentPosPaint);
			canvas.drawCircle( textPosX,textPosY,5, currentPosPaintCenter);
		}
		

		if(MainActivity.textFlag==true){

			//System.out.println(textPosX+ " " + textPosY);


			//System.out.println("showDialog");
			showDialog();

			//System.out.println("draw");
			MainActivity.textFlag=false; 

		}


		if(!dialogText.equals("")){

			list1.add(dialogText);
			list2.add(textPosX);
			list3.add(textPosY);
			list4.add(MainActivity.mPaint.getColor());

			dialogText="";
		}


		for(int i=0;i<list1.size();i++){
			paintText.setColor(list4.get(i)); 
			canvas.drawText(list1.get(i).toString(), list2.get(i),  list3.get(i), paintText);

		}



		canvas.drawPath( mPath, MainActivity.mPaint);
		saveFlag = true;
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		//showDialog(); 
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;

	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;
		}
	}
	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, MainActivity.mPaint);
		// kill this so we don't double draw
		mPath.reset();
		MainActivity.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
		//mPaint.setMaskFilter(null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		textPosX=x;
		textPosY=y;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:

			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}  

	public void showDialog(){

		editalert = new AlertDialog.Builder(context);
		editalert.setTitle("Enter text you want to place");
		final EditText input = new EditText(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		editalert.setView(input);
		editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {


				dialogText= input.getText().toString();
				v.invalidate();

			}
		});
		editalert.show();

	}
	
	public void changeFlag(boolean a){
		this.saveFlag=a;
	}
}

