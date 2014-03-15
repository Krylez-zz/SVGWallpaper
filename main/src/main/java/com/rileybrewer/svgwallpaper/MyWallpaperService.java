package com.rileybrewer.svgwallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MyWallpaperService extends WallpaperService {

    private static final String LOG_TAG = MyWallpaperService.class.getSimpleName();
    final static Random RANDY = new Random();

    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperEngine();
    }

    private class MyWallpaperEngine extends Engine {
        private final Handler handler = new Handler();

        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int currentColor = 0xffffffff;

        private final SvgHelper mSvg = new SvgHelper(mPaint);
        private int mSvgResource = R.raw.android;

        private final Object mSvgLock = new Object();
        private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);
        private Thread mLoader;

        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };
        private float mPhase = 1f;
        private boolean visible = true;

        public MyWallpaperEngine() {

            mPaint.setStrokeWidth(3.5f);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     final int width, final int height) {
            super.onSurfaceChanged(holder, format, width, height);

            if (mLoader != null) {
                try {
                    mLoader.join();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Unexpected error", e);
                }
            }

            mLoader = new Thread(new Runnable() {
                @Override
                public void run() {
                    mSvg.load(MyWallpaperService.this, mSvgResource);
                    synchronized (mSvgLock) {
                        mPaths = mSvg.getPathsForViewport(width, height);
                        shouldRedraw = true;
                    }
                }
            }, "SVG Loader");
            mLoader.start();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            boolean didRedraw = false;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null && shouldRedraw) {
                    synchronized (mSvgLock) {
                        canvas.drawColor(Color.BLACK);
                        final int count = mPaths.size();
                        for (int i = 0; i < count; i++) {
                            SvgHelper.SvgPath svgPath = mPaths.get(i);
                            svgPath.paint.setColor(currentColor);
                            canvas.drawPath(svgPath.path, svgPath.paint);
                        }
                        shouldRedraw = false;
                    }
                    didRedraw = true;
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            if (didRedraw) {
                setPhase(mPhase - 0.005f);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 50);
            }
        }

        private void updatePathsPhaseLocked() {
            final int count = mPaths.size();
            for (int i = 0; i < count; i++) {
                SvgHelper.SvgPath svgPath = mPaths.get(i);
                svgPath.paint.setPathEffect(createPathEffect(svgPath.length, mPhase, 0.0f));
            }
        }
        private boolean shouldRedraw = false;

        private void setPhase(float phase) {
            if (phase < 0) {
                mPhase = 1.0f;
                currentColor = 0xff000000 | RANDY.nextInt(0x00ffffff);
            } else {
                mPhase = phase;
            }
            synchronized (mSvgLock) {
                updatePathsPhaseLocked();
                shouldRedraw = true;
            }
        }
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }
}