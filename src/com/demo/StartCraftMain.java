package com.demo;


//import dalvik.system.VMRuntime;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StartCraftMain extends Activity {
    public static int mDisplayWidth  = 800;
    public static int mDisplayHeight = 480;
    private final static float TARGET_HEAP_UTILIZATION = 0.75f; 
    
    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION); 
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                  WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                  
        Display display = this.getWindow().getWindowManager().getDefaultDisplay();

        mDisplayWidth  = display.getWidth();
        mDisplayHeight = display.getHeight();
        display        = null;
        setContentView(new mainView(this));
    }

    private static class mainView extends View {
        private Bitmap       mbmpMarauder   = null;
        public HandleThreads ModelThd       = new HandleThreads(Process.THREAD_PRIORITY_URGENT_AUDIO);
        private Bitmap       vBitmap        = null;
        private Bitmap       mbmpBackground = null;
        public Handler       mHandler       = null;
        private Context      mcontext       = null;
        private static int   mnIndex        = 0;

        public mainView(Context context) {
            super(context);

            // TODO Auto-generated constructor stub
            mcontext = context;
            setFocusable(true);

            /**
             * set background
             */
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;

            Bitmap bmpTmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg);
            Bitmap bmpBG  = Bitmap.createScaledBitmap(bmpTmp, mDisplayWidth, mDisplayHeight, true);

            mbmpBackground = bmpBG;
            bmpTmp.recycle();
            bmpBG = null;
            vBitmap        = BitmapFactory.decodeResource(this.getResources(), R.drawable.marauder);
            initBmp(context, 0);
            this.setOnTouchListener(TouchListener);
            ModelThd.startHandler();
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    // TODO Auto-generated method stub
                    super.handleMessage(msg);
                    mainView.this.invalidate();
                }
            };
        }

        @Override
        protected void onDraw(Canvas canvas) {

            // TODO Auto-generated method stub
            super.onDraw(canvas);
            canvas.drawColor(0xFFAAAAAA);
            canvas.drawBitmap(mbmpBackground, 0, 0, null);

            if (800 > mDisplayWidth) {
                canvas.drawBitmap(mbmpMarauder, 40, 60, null);
            } else {
                canvas.drawBitmap(mbmpMarauder, 150, 60, null);
            }
        }

        private void initBmp(Context context, int nIndex) {
            int    nHeight = vBitmap.getHeight();
            Bitmap bmpCut  = Bitmap.createBitmap(vBitmap, nIndex * 212, 0, 212, nHeight);
            Bitmap vB2     = Bitmap.createScaledBitmap(bmpCut, 300, 300, true);

            mbmpMarauder = vB2;
            bmpCut.recycle();
            vB2 = null;
        }

        OnTouchListener TouchListener = new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                // TODO Auto-generated method stub
                int nAction = arg1.getAction();

                if (MotionEvent.ACTION_DOWN == nAction) {
                    ModelThd.getHandler().sendEmptyMessage(0);
                }

                return false;
            }
        };

        private class HandleThreads {
            private static final String TAG             = "ModelThreads";
            private Handler             mHandler        = null;
            private HandlerThread       mHandlerThread  = null;
            private Looper              mLooper         = null;
            private int                 mnCount         = 0;
            public int                  mnFireballCount = 0;

            public HandleThreads(int nPriority) {
                mHandlerThread = new HandlerThread(TAG, nPriority);
            }

            public void startHandler() {
                if (null != mHandlerThread) {
                    mHandlerThread.start();
                    mLooper  = mHandlerThread.getLooper();
                    mHandler = new Handler(mLooper) {
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                            case 0 :
                                this.removeMessages(msg.what);
                                initBmp(mcontext, mnIndex++);

                                if (31 == mnIndex) {
                                    mnIndex = 0;
                                }

                                invalidDateView(msg.what, 0);
                                mHandler.sendEmptyMessageDelayed(0, 100);

                                break;
                            }
                        }
                    };
                }
            }

            public void stopHandler() {
                if (null != mHandlerThread) {
                    mHandlerThread.stop();
                }
            }

            @SuppressLint("NewApi")
			public void closeHandler() {
                if (null != mHandlerThread) {
                    mHandlerThread.quit();
                    mHandlerThread = null;
                }
            }

            public Handler getHandler() {
                return mHandler;
            }

            public Looper getLooper() {
                return mLooper;
            }
        }


        private void invalidDateView(int nWhat, int nDuration) {
            if ((null == mHandler)) {
                return;
            }

            mHandler.sendEmptyMessage(nWhat);
        }
    }
}
