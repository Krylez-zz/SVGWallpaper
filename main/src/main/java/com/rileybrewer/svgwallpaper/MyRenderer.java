package com.rileybrewer.svgwallpaper;

import android.opengl.GLES20;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLWallpaperService.Renderer {

    private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);

    private int mDuration;
    private float mFadeFactor;

    private final Object mSvgLock = new Object();


    public void onDrawFrame(GL10 gl) {
        // Your rendering code goes here
        GLES20.glClearColor(0.4f, 0.4f, 0.2f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        final int count = mPaths.size();
        for (int i = 0; i < count; i++) {
            SvgHelper.SvgPath svgPath = mPaths.get(i);
        }

//        final int count = mPaths.size();
//        for (int i = 0; i < count; i++) {
//            SvgHelper.SvgPath svgPath = mPaths.get(i);
//
//            // We use the fade factor to speed up the alpha animation
//            int alpha = (int) (Math.min((1.0f - mPhase) * mFadeFactor, 1.0f) * 255.0f);
//            svgPath.paint.setAlpha(alpha);
//
//            canvas.drawPath(svgPath.path, svgPath.paint);
//        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    /**
     * Called when the engine is destroyed. Do any necessary clean up because
     * at this point your renderer instance is now done for.
     */
    public void release() {
    }

}