package net.masanoriyono.MannerCamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MannerCameraActivity extends Activity {
	
	private static final String TAG = "MannerCamera -> ImageManager";
	private static final String APPLICATION_NAME = "MannerCamera";  
    private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
    private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/" + APPLICATION_NAME;
    
	private CameraView mCameraPreview;
	private Camera mCamera;
//	private MyView mView;
//	private int screen_width;
//    private int screen_height;
    
    /**
     * CameraView
     */
    class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    	CameraView(Context context) {
    		super(context);


    		SurfaceHolder mHolder = getHolder();
    		mHolder.addCallback(this);
    		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    	}

    	/**
    	 * Surfaceに変化があった場合に呼ばれる
    	 */
    	public void surfaceChanged(SurfaceHolder holder, int format, int width,
    			int height) {
    		Camera.Parameters parameters = mCamera.getParameters();
    		
    		android.view.ViewGroup.LayoutParams lp = getLayoutParams();
    		int ch = parameters.getPreviewSize().height;
    		int cw = parameters.getPreviewSize().width;

    		Log.d("surfaceChanged","width:" + width
    				+ " height:" + height
    				+ " getPreviewSize().width:" + cw
    				+ " getPreviewSize().height:" + ch);
    		
    		if(ch/cw > height/width){
    			lp.width = height*cw/ch;
    			lp.height = height;
    		}else{
    			lp.width = width;
    			lp.height = width*ch/cw;
    		}
    		
    		
    		setLayoutParams(lp);

    		mCamera.setParameters(parameters);
    		
    		mCamera.startPreview();
    	}
    	
    	/**
    	 * Surfaceが生成された際に呼ばれる
    	 */
    	public void surfaceCreated(SurfaceHolder holder) {
    		if (mCamera != null) {
                return;
            }
            mCamera = Camera.open();
            try {
    			mCamera.setPreviewDisplay(holder);
    		} catch (Exception exception) {
    			mCamera.release();
    			mCamera = null;
    		}
    	}

    	/**
    	 * Surfaceが破棄された場合に呼ばれる
    	 */
    	public void surfaceDestroyed(SurfaceHolder holder) {
    		if (mCamera != null) {
    	        mCamera.stopPreview();
    	        mCamera.release();
                mCamera = null;
    	    }
    	}
    	
    }
    
    /**
	 * スケールを取得する
	 * @param data 縮尺を調査する画像データ
	 * @param width 変更する横のサイズ
	 * @param height 変更する縦のサイズ
	 * @return 変更したいサイズのスケールの値
	 */
	public int getScale(byte[] data, int width, int height){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		
		int scaleW = options.outWidth / width + 1;
		int scaleH = options.outHeight / height + 1;
	
		int scale = Math.max(scaleW, scaleH);
		options.inJustDecodeBounds = false;
		
		return scale;
	}
	
//    /**
//     * オーバーレイ描画用のクラス
//     */
//    class MyView extends View {
//
//    	private int mYaw;
//    	private int mRoll;
//    	private int mPitch;
//
//    	private double mLat;
//    	private double mLon;
//    	private double mBearing;
//
//    	private int mCurX;
//    	private int mCurY;
//
//    	/**
//    	 * コンストラクタ
//    	 * 
//    	 * @param c
//    	 */
//    	public MyView(Context c) {
//    		super(c);
//    		setFocusable(true);
//    	}
//
//    	/**
//    	 * 描画処理
//    	 */
//    	protected void onDraw(Canvas canvas) {
//    		super.onDraw(canvas);
//
//    		/* 背景色を設定 */
//    		canvas.drawColor(Color.TRANSPARENT);
//
//    		/* 描画するための線の色を設定 */
//    		Paint mPaint = new Paint();
//    		mPaint.setStyle(Paint.Style.FILL);
//    		mPaint.setTextSize(18);
//    		mPaint.setARGB(255, 255, 255, 100);
//
//    		/* 文字を描画 */
////    		canvas.drawText("curX: " + mCurX, 20, 20, mPaint);
////    		canvas.drawText("curY: " + mCurY, 80, 20, mPaint);
////
////    		canvas.drawText("mYaw: " + mYaw, 20, 45, mPaint);
////    		canvas.drawText("mRoll: " + mRoll, 120, 45, mPaint);
////    		canvas.drawText("mPitch: " + mPitch, 200, 45, mPaint);
////
////    		canvas.drawText("Latitude: " + mLat, 20, 70, mPaint);
////    		canvas.drawText("Longitude: " + mLon, 20, 95, mPaint);
////    		
////    		canvas.drawText("Bearing: " + mBearing , 20, 120, mPaint);
//    	}
//
//
////    	public void onOrientationChanged(int yaw, int roll, int pitch) {
////    		mYaw = yaw;
////    		mRoll = roll;
////    		mPitch = pitch;
////    		invalidate();
////    	}
////
////    	public void onGpsChanged(double lat, double lon, double bearing) {
////    		mLat = lat;
////    		mLon = lon;
////    		mBearing = bearing;
////    		invalidate();
////    	}
//
//    	/**
//    	 * タッチイベント
//    	 */
//    	public boolean onTouchEvent(MotionEvent event) {
//
//    		/* X,Y座標の取得 */
//    		mCurX = (int) event.getX();
//    		mCurY = (int) event.getY();
//    		/* 再描画の指示 */
//    		invalidate();
//    		
//    		Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
//   		 
//    	    switch (event.getAction()) {
//    	    case MotionEvent.ACTION_DOWN:
//    	        Log.d("TouchEvent", "getAction()" + "ACTION_DOWN");
//    	        break;
//    	    case MotionEvent.ACTION_UP:
//    	        Log.d("TouchEvent", "getAction()" + "ACTION_UP");
//    	        
////    	        mCamera.setOneShotPreviewCallback(editPreviewImage);
//    	        
//    	        break;
//    	    case MotionEvent.ACTION_MOVE:
//    	        Log.d("TouchEvent", "getAction()" + "ACTION_MOVE");
//    	        break;
//    	        
//    	    case MotionEvent.ACTION_CANCEL:
//    	        Log.d("TouchEvent", "getAction()" + "ACTION_CANCEL");
//    	        break;
//    	    }
//    	    
//    		return true;
//    	}
//    }
    
	public void autoFocus(){
		if( mCamera != null ){
			//　オートフォーカスのあと撮影に行くようにコールバック設定
			mCamera.autoFocus( new Camera.AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					camera.autoFocus( null );
//					camera.stopPreview();
//					Thread trd = new Thread(new Runnable(){
//						public void run() {
//						try{
//							Thread.sleep(1000);
//						}catch(Exception e){}
						
							mCamera.setOneShotPreviewCallback(editPreviewImage);
//						};
//					});
//					trd.start();
				}
			});
		}
	}
	
    private final Camera.PreviewCallback editPreviewImage =
        new Camera.PreviewCallback() {
    	
    	@Override
    	public void onPreviewFrame(byte[] data, Camera camera) {
    		// プレビューの幅:640
        	final int width = mCamera.getParameters().getPreviewSize().width;
        	// プレビューの高さ:480
        	final int height = mCamera.getParameters().getPreviewSize().height;
        	// ARGB8888の画素の配列
        	int[] rgb = new int[(width * height)];

        	try {
//        		//プレビュー撮影を有効にする
//				mView.setDrawingCacheEnabled(true);
//				Bitmap viewBitmap = Bitmap.createBitmap(mView.getDrawingCache());
//				// プレビュー撮影を無効にする
//				mView.setDrawingCacheEnabled(false);
				
//				//フルスクリーンの幅:960
//	        	screen_width = viewBitmap.getWidth();
//	        	//フルスクリーンの高さ:540
//	        	screen_height = viewBitmap.getHeight();
	        	
//				String t_res = "preview width: " + width + "\n" +
//					"preview height: " + height + "\n" +
//					"screen_width: " + screen_width + "\n" +
//					"screen_height: " + screen_height;
//				
//				Log.d(TAG, t_res);
        		// スケールを取得
        		int scale =  getScale(data, width, height);
        		
        		// Bitmap生成時のオプションを作成
        		BitmapFactory.Options options = new BitmapFactory.Options();
        		options.inSampleSize = scale;
        		
        		// ARGB8888で空のビットマップ作成
        		Bitmap bmp = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        		// 変換
        		decodeYUV420SP(rgb, data, width, height);
        		// 変換した画素からビットマップにセット
        		bmp.setPixels(rgb, 0, width, 0, 0, width, height);
        		
//        		Canvas canvas = new Canvas(bmp);
//        		canvas.drawBitmap(viewBitmap, null, new Rect(0, 0, width, height), null);

        		// 保存
        		long dateTaken = System.currentTimeMillis();  
                String filename = createName(dateTaken) + ".png";
                boolean f_res = saveToFile(PATH,filename,dateTaken,bmp);
                if (f_res){
                	Toast.makeText(MannerCameraActivity.this, "Save to gallery", Toast.LENGTH_SHORT).show();
                	//Log.e(TAG, "Save to gallery");
                }
                

        	} catch (Exception e) {
        		Log.e("Error",e.getMessage());
        	}
        }
    };
    
    private String createName(long dateTaken) {  
        return DateFormat.format("yyyyMMdd_kkmmss", dateTaken).toString();  
    }
    
    private boolean saveToFile(String directory,String filename,long dateTaken,Bitmap bitmap) {
    	
    	OutputStream outputStream = null;  
        try {  
            File dir = new File(directory+ "/");  
            if (!dir.exists()) {  
                dir.mkdirs();  
                Log.d(TAG, dir.toString() + " create");  
            }
            
            String filePath = directory + "/" + filename;
            
            File file = new File(directory + "/" + filename);  
            if (file.createNewFile()) {  
                outputStream = new FileOutputStream(file);  
                
            	bitmap.compress(CompressFormat.PNG, 100, outputStream);

                //ギャラリーへの登録。
            	ContentResolver contentResolver = getContentResolver();
            	
            	ContentValues values = new ContentValues();  
                values.put(Images.Media.TITLE, filename);  
                values.put(Images.Media.DISPLAY_NAME, filename);  
                values.put(Images.Media.DATE_TAKEN, dateTaken);  
                values.put(Images.Media.MIME_TYPE, "image/png");  
                values.put(Images.Media.DATA, filePath);  
                
            	contentResolver.insert(IMAGE_URI, values);
            	
            	Log.d(TAG, file.toString() + " write");
            	
            	return true;
                  
            }  
      
        } catch (FileNotFoundException ex) {  
            Log.w(TAG, ex);  
            return false;  
        } catch (IOException ex) {  
            Log.w(TAG, ex);  
            return false;  
        } finally {  
            if (outputStream != null) {  
                try {  
                    outputStream.close();  
                } catch (Throwable t) {  
                }  
            }  
        }
		return false; 
    }
    
    public static final void decodeYUV420SP(int[] rgb, byte[]yuv420sp, int width, int height) {
    	final int frameSize = width * height;
    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >>2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//省電力モードに入れない。
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mCameraPreview = new CameraView(this);
		setContentView(mCameraPreview);
		
//		
//		mView = new MyView(this);
//		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    }
    
    @Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		// 戻るボタンが押されたとき
		if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// ボタンが押されたとき
			if (e.getAction() == KeyEvent.ACTION_DOWN) {
				// アクティビティの終了

				finish();
			}
			// ボタンが離されたとき
			else if (e.getAction() == KeyEvent.ACTION_UP) {

			}

		}
		
		if (e.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			// ボタンが押されたとき
			if (e.getAction() == KeyEvent.ACTION_DOWN) {
				
				autoFocus();
//				mCamera.setOneShotPreviewCallback(editPreviewImage);
				
			}
			// ボタンが離されたとき
			else if (e.getAction() == KeyEvent.ACTION_UP) {

			}

		}
		
		return super.dispatchKeyEvent(e);
	}
}