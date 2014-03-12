package com.rileybrewer.svgwallpaper;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

/**
 * Created by AccountName on 3/11/14.
 */
public class MyGLWallpaperService extends GLWallpaperService {
    public MyGLWallpaperService() {
        super();
    }

    public Engine onCreateEngine() {
        MyEngine engine = new MyEngine();
        return engine;
    }

    class MyEngine extends GLEngine {
        MyRenderer renderer;
        public MyEngine() {
            super();
            // handle prefs, other initialization
            renderer = new MyRenderer();
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        public void onDestroy() {
            super.onDestroy();
            if (renderer != null) {
                renderer.release();
            }
            renderer = null;
        }
    }
}
