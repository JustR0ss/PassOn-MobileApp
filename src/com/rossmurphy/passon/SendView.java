package com.rossmurphy.passon;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SendView extends View{
	FileInputStream imageInFile;
	URL url;
	String numberMob;
	Paint paint,direactionPaint,paintText;
	private Bitmap  mBitmap,textBitmap;
	private Paint   mBitmapPaint;
	private Path    mPath;
	private Canvas  mCanvas,textCanvas;
	float textPosX,textPosY;
	Matrix matrix;
	int w;
	int h;
	int posY;
	int posX;
	float currentDegree;
	float finalDegree;
	Bitmap b,b2;
	Bitmap rotatedBitmap;
	String name;
	String distance;
	int _id;
	Context con;
	DecimalFormat df = new DecimalFormat("0.00#");
	RequestParams reqParams = new RequestParams();

	public SendView(Context context,String number) {
		super(context);
		this.con=context;
		numberMob = number;
		paint = new Paint();
		mPath = new Path();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(20);
		paint.setColor(Color.BLUE);
		direactionPaint = new Paint();
		direactionPaint.setColor(Color.BLACK);
		mCanvas = new Canvas();
		textCanvas = new Canvas();
		paintText = new Paint(); 
		paintText.setColor(Color.BLACK); 
		paintText.setTextSize(60);
		matrix = new Matrix();
		
		posX=0;
		posY=0;
		finalDegree=0;
		 b=BitmapFactory.decodeResource(getResources(), R.drawable.compass_rose);
		 
		 DisplayMetrics metrics = getResources().getDisplayMetrics();         
		  w = metrics.widthPixels;
		  h = metrics.heightPixels;
		  b2 = getResizedBitmap(b,w,w);
		  posY = (h/2)-(w/2);
		  matrix.postRotate(0,b2.getWidth()/2,b2.getHeight()/2);
		matrix.postTranslate(0, posY);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);  
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		//System.out.println("size changed");
		mCanvas = new Canvas(mBitmap);
		//System.out.println(w+" "+"400");
		textBitmap = Bitmap.createBitmap(w, 180, Bitmap.Config.ARGB_8888);
		textCanvas = new Canvas(textBitmap);

	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		canvas.drawColor(Color.YELLOW);
		
		canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
		canvas.drawBitmap( textBitmap, 0, 0, paintText);
		canvas.drawBitmap(b2, matrix, null);
		
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

	
	private void touch_up(float x,float y) {
		//mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		double newX;
		double newY;
		//System.out.println("pre wtf");
		mCanvas.drawLine(mX,mY,x,y, paint);
		//System.out.println("wtf");
		System.out.println("my = " + mY);
		System.out.println("y = " + y);
		if(mY<=y){
			System.out.println("y less than orig");
			newY = y-mY;
			newY=-newY;
		}else{
			System.out.println(" y greater than orig");
			newY = mY-y;
			
		}
		System.out.println("mx = " + mX);
		System.out.println("x = " + x);
		if(mX>=x){
			System.out.println("x less than orig");
			newX = mX-x;
			newX=-newX;
		}else{
			System.out.println("x greater than orig");
			newX = x-mX;
		}
		//System.out.println(newY+ " xxx "+ newX);
		newY= newY/10;
		newX= newX/10;
		int intX = (int)newX;
		int intY = (int)newY;
		
		startSendingMessage((double)intX,(double)intY);
		// kill this so we don't double draw
		


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
			mCanvas.drawColor(Color.YELLOW);
			touch_start(x,y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:

			
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up(x,y);
			invalidate();
			break;
		}
		return true;
	}  
	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
	public void drawText(String str ,float x,float y,float degree){
		
		
		if(degree<0){
			degree= Math.abs(degree);
		}
		else if(degree>0){
			degree = -degree;
		}
		textCanvas.drawColor(Color.YELLOW);
		
		textCanvas.drawText(str, x, y, paintText);
		Matrix newm = new Matrix();
		//System.out.println(degree);
		newm.postRotate(degree,b2.getWidth()/2,b2.getHeight()/2);
		newm.postTranslate(0, posY);
		matrix.set(newm);
		
		invalidate();
	}
	public  void startSendingMessage(double x,double y){
		String[] args2 = new String[1];
		//change url name
		
	    
		args2[0] = "http://54.76.199.8:8080/PassOn/services/Main/sendMessage?longi="+String.valueOf(SendActivity.lng)+"&lat="+String.valueOf(SendActivity.lat)+"&x="+String.valueOf((int)x)+"&y="+String.valueOf((int)y)+"&number="+numberMob;
		System.out.println(args2[0]);
		new SendMessage().execute(args2);
	}
	
	public void showRetry(){
		Toast.makeText(con.getApplicationContext(), "Search Again", Toast.LENGTH_SHORT).show();
	}
	public void showDialog(){
		AlertDialog.Builder editalert = new AlertDialog.Builder(con);
		editalert.setTitle("Do you want to send picture '"+SendActivity.pic+"' to "+name+" "+df.format(Double.parseDouble(distance))+"km away;");
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(con.getApplicationContext(), "Send picture ", Toast.LENGTH_SHORT).show();
				System.out.println(MainActivity.path+"/PassOn/"+SendActivity.pic+".png");
				 File file = new File(MainActivity.path+"/PassOn",SendActivity.pic+".png");
				 byte imageData[] = new byte[(int) file.length()];
				 try {
					    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
					    buf.read(imageData, 0, imageData.length);
					    buf.close();
					} catch (FileNotFoundException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					} catch (IOException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					}
				
					//imageInFile = new FileInputStream(file);
					
					
					//imageData = Files.readAllBytes(f.toPath());
					String imageDataString = encodeImage(imageData);
					//System.out.println(imageDataString);
					String[] argsImage = new String[3];
					argsImage[0] = imageDataString;
					argsImage[1] = _id+"";
					
					argsImage[2] = SendActivity.pic+".png";
					
					//System.out.println(Arrays.toString(argsImage));
					new SendImage().execute(argsImage);
				
	 
	            // Converting Image byte array into Base64 String
	            
			}
		});
		editalert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(con.getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
			}
		});
		editalert.show();
	}
	class SendMessage extends AsyncTask<String, Void, Void>{
		String returnStringTest="";

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			
			try {
				url = new URL(params[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringReturned st = new StringReturned(url);
			try {
				String mydata = st.getContents().toString();
				System.out.println(mydata);
				Pattern pattern = Pattern.compile("<ns:return>(.*?)</ns:return>");
				Matcher matcher = pattern.matcher(mydata);
				if (matcher.find())
				{
					if(matcher.group(1).toString().equals(returnStringTest)){
						
					}else{
					String prepare = matcher.group(1).toString().replace("[","").replace("]","");
					returnStringTest = prepare;
					String[] split1 = prepare.split(",");
				    
					
				    for(int i =0; i<split1.length;i++){
				    	
				    	_id=Integer.parseInt(split1[0]);
				    	name=split1[1];
				    	System.out.println(split1[2]);
				    	distance = split1[2];
				    	
				    }
				    
					}
					
				}
				
				//System.out.println(st.getContents());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(Void unused) {
			
			if(!returnStringTest.equals("")){
				showDialog();
			}else{
				showRetry();
			}
		}
	}
	public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray,Base64.DEFAULT);
    }
	class SendImage extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			reqParams.put("id",params[1]);
			reqParams.put("imageData",params[0]);
			reqParams.put("name",params[2]);
			
			return null;
		}
		
		@Override
        protected void onPostExecute(Void unused) {
			makeHttpCall();
        }
	}
	public void makeHttpCall(){
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://54.76.199.8:8080/PassOn/uploadimg.jsp",
				reqParams, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1,
							byte[] arg2, Throwable arg3) {
						// TODO Auto-generated method stub
						Toast.makeText(con.getApplicationContext(), "failed",
                                Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] arg2) {
						// TODO Auto-generated method stub
						Toast.makeText(con.getApplicationContext(), "done",
                                Toast.LENGTH_SHORT).show();
					}
			
		});
	}
}
