/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import org.telegram.Util;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    private static final String TAG = "HOJJAT_AnDrawable";

    private static native int createDecoder(String src, int[] params);

    private static native void destroyDecoder(int ptr);

    private static native int getVideoFrame(int ptr, Bitmap bitmap, int[] params);

    private long lastFrameTime;
    private int lastTimeStamp;
    private int invalidateAfter = 50;
    private final int[] metaData = new int[3];  //[0]:width   [1]:height   [2]:time
    private Runnable loadFrameTask;
    private Bitmap renderingBitmap;
    private Bitmap nextRenderingBitmap;
    private Bitmap backgroundBitmap;
    private boolean destroyWhenDone;
    private boolean decoderCreated;
    private File path;
    private boolean recycleWithSecond;

    private BitmapShader renderingShader;
    private BitmapShader nextRenderingShader;
    private BitmapShader backgroundShader;

    private int roundRadius;
    private RectF roundRect = new RectF();
    private RectF bitmapRect = new RectF();
    private Matrix shaderMatrix = new Matrix();

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private boolean applyTransformation;
    private final android.graphics.Rect dstRect = new android.graphics.Rect();
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private volatile boolean isRunning;
    private volatile boolean isRecycled;
    private volatile int nativePtr;
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.AbortPolicy());

    private View parentView = null;
    private View secondParentView = null;


    public static final int AD_DURATION = 2500;
    boolean showAd = true;
    long adStartTime;
    int adCurrentTime;
    int[] adPixels;

    boolean firstFrameAfterAd = true;

    protected final Runnable mInvalidateTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mInvalidateTask");
            if (secondParentView != null) {
                secondParentView.invalidate();
            } else if (parentView != null) {
                parentView.invalidate();
            }
        }
    };

    private Runnable uiRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "uiRunnable");
            if (destroyWhenDone && nativePtr != 0) {
                destroyDecoder(nativePtr);
                nativePtr = 0;
            }
            if (nativePtr == 0) {
                if (backgroundBitmap != null) {
                    backgroundBitmap.recycle();
                    backgroundBitmap = null;
                }
                return;
            }
            loadFrameTask = null;
            nextRenderingBitmap = backgroundBitmap;
            nextRenderingShader = backgroundShader;
            if (metaData[2] < lastTimeStamp) {
                lastTimeStamp = 0;
            }
            if (metaData[2] - lastTimeStamp != 0) {
                invalidateAfter = metaData[2] - lastTimeStamp;
            }
            lastTimeStamp = metaData[2];
            if (secondParentView != null) {
                secondParentView.invalidate();
            } else if (parentView != null) {
                parentView.invalidate();
            }
        }
    };

    private Runnable loadFrameRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "loadFrameRunnable");
            if (!isRecycled) {
                if (!decoderCreated && nativePtr == 0) {
                    nativePtr = createDecoder(path.getAbsolutePath(), metaData);
                    decoderCreated = true;
                }
                try {
                    if (backgroundBitmap == null) {
                        try {
                            backgroundBitmap = Bitmap.createBitmap(metaData[0], metaData[1], Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                        if (backgroundShader == null && backgroundBitmap != null && roundRadius != 0) {
                            backgroundShader = new BitmapShader(backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        }
                    }
                    if (backgroundBitmap != null) {
                        if (showAd && adCurrentTime < AD_DURATION) {
                            getAdFrame();
                        } else {
                            if (showAd && firstFrameAfterAd) {
                                nextRenderingShader = null;
                                nextRenderingShader  = null;
                                renderingBitmap = null;
                                renderingShader = null;
                                try {
                                    backgroundBitmap = Bitmap.createBitmap(metaData[0], metaData[1], Bitmap.Config.ARGB_8888);
                                    backgroundShader = new BitmapShader(backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                } catch (Throwable e) {
                                    FileLog.e("tmessages", e);
                                }
                                firstFrameAfterAd = false;
                            }
                            getVideoFrame(nativePtr, backgroundBitmap, metaData);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
            AndroidUtilities.runOnUIThread(uiRunnable);
        }
    };

    private void getAdFrame() {
        if (adPixels == null) {
            adStartTime = System.currentTimeMillis();
            Context c = parentView.getContext();
            GiffAd ad = new AdProvider().getGiffAd(c, metaData[0], metaData[1]);
            float widthScale = (float) ad.bitmap.getWidth() / metaData[0];
            float heightScale = (float) ad.bitmap.getHeight() / metaData[1];
            float scale = Math.max(widthScale, heightScale);
            int w = (int) (metaData[0] * scale);
            int h = (int) (metaData[1] * scale);
            backgroundBitmap = Util.scalePreserveRatio(ad.bitmap, w, h, ad.backColor);
            backgroundShader = new BitmapShader(backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            adPixels = new int[backgroundBitmap.getHeight() * backgroundBitmap.getWidth()];
            backgroundBitmap.getPixels(adPixels, 0, backgroundBitmap.getWidth(), 0, 0,
                    backgroundBitmap.getWidth(), backgroundBitmap.getHeight());

            //avoid memory leaks
            ad.bitmap.recycle();
            ad.bitmap = null;
        } else {
            setProgress();
            backgroundBitmap.setPixels(adPixels, 0, backgroundBitmap.getWidth(), 0, 0,
                    backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        }
        adCurrentTime = (int) (System.currentTimeMillis() - adStartTime);
    }

    int progressHeight;

    private void setProgress() {
        if (progressHeight == 0 && dstRect.height() != 0) {
            progressHeight = (int) Util.dpToPx(parentView.getContext(), 4) * backgroundBitmap.getHeight() / dstRect.height();
            for (int i = backgroundBitmap.getHeight() - 1; i > backgroundBitmap.getHeight() - 2 * progressHeight; i--)
                for (int j = 0; j < backgroundBitmap.getWidth(); j++)
                    adPixels[backgroundBitmap.getWidth() * i + j] = Color.WHITE;
            for (int i = backgroundBitmap.getHeight() - (progressHeight / 2); i > backgroundBitmap.getHeight() - 3 * progressHeight / 2; i--)
                for (int j = 0; j < backgroundBitmap.getWidth(); j++)
                    adPixels[backgroundBitmap.getWidth() * i + j] = Color.GRAY;
        }
        float progress = (float) adCurrentTime / AD_DURATION;
        for (int i = backgroundBitmap.getHeight() - (progressHeight / 2); i > backgroundBitmap.getHeight() - 3 * progressHeight / 2; i--)
            for (int j = 0; j < (int) (backgroundBitmap.getWidth() * progress); j++)
                adPixels[backgroundBitmap.getWidth() * i + j] = Color.RED;
    }

    private final Runnable mStartTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mStartTask");
            if (secondParentView != null) {
                secondParentView.invalidate();
            } else if (parentView != null) {
                parentView.invalidate();
            }
        }
    };

    public AnimatedFileDrawable(File file, boolean createDecoder) {
        Log.d(TAG, "AnimatedFileDrawable()");
        path = file;
        if (createDecoder) {
            nativePtr = createDecoder(file.getAbsolutePath(), metaData);
            decoderCreated = true;
        }
    }

    protected void postToDecodeQueue(Runnable runnable) {
        executor.execute(runnable);
    }

    public void setParentView(View view) {
        parentView = view;
    }

    public void setSecondParentView(View view) {
        secondParentView = view;
        if (view == null && recycleWithSecond) {
            recycle();
        }
    }

    public void recycle() {
        if (secondParentView != null) {
            recycleWithSecond = true;
            return;
        }
        isRunning = false;
        isRecycled = true;
        if (loadFrameTask == null) {
            if (nativePtr != 0) {
                destroyDecoder(nativePtr);
                nativePtr = 0;
            }
            if (nextRenderingBitmap != null) {
                nextRenderingBitmap.recycle();
                nextRenderingBitmap = null;
            }
        } else {
            destroyWhenDone = true;
        }
        if (renderingBitmap != null) {
            renderingBitmap.recycle();
            renderingBitmap = null;
        }
    }

    protected static void runOnUiThread(Runnable task) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            task.run();
        } else {
            uiHandler.post(task);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void start() {
        Log.d(TAG, "start() called with: " + "");
        if (isRunning) {
            return;
        }
        isRunning = true;
        if (renderingBitmap == null) {
            scheduleNextGetFrame();
        }
        runOnUiThread(mStartTask);
    }

    private void scheduleNextGetFrame() {
        Log.d(TAG, "scheduleNextGetFrame(): " + loadFrameTask + " , " + nativePtr + " , " + decoderCreated + " , " + destroyWhenDone);
        if (loadFrameTask != null || nativePtr == 0 && decoderCreated || destroyWhenDone) {
            return;
        }
        postToDecodeQueue(loadFrameTask = loadFrameRunnable);
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getIntrinsicHeight() {
//        return decoderCreated ? metaData[1] : AndroidUtilities.dp(100);
        return backgroundBitmap.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
//        return decoderCreated ? metaData[0] : AndroidUtilities.dp(100);
        return backgroundBitmap.getWidth();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        applyTransformation = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (nativePtr == 0 && decoderCreated || destroyWhenDone) {
            return;
        }
        if (isRunning) {
            if (renderingBitmap == null && nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (Math.abs(System.currentTimeMillis() - lastFrameTime) >= invalidateAfter) {
                if (nextRenderingBitmap != null) {
                    scheduleNextGetFrame();
                    renderingBitmap = nextRenderingBitmap;
                    renderingShader = nextRenderingShader;
                    nextRenderingBitmap = null;
                    nextRenderingShader = null;
                    lastFrameTime = System.currentTimeMillis();
                }
            }
        }

        if (renderingBitmap != null) {
            if (applyTransformation) {
                dstRect.set(getBounds());
                scaleX = (float) dstRect.width() / renderingBitmap.getWidth();
                scaleY = (float) dstRect.height() / renderingBitmap.getHeight();
//                applyTransformation = false;
            }
            if (roundRadius != 0) {
                int bitmapW = renderingBitmap.getWidth();
                int bitmapH = renderingBitmap.getHeight();
                float scale = Math.max(scaleX, scaleY);

                if (renderingShader == null) {
                    renderingShader = new BitmapShader(backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
                getPaint().setShader(renderingShader);
                roundRect.set(dstRect);
                shaderMatrix.reset();
                if (Math.abs(scaleX - scaleY) > 0.00001f) {
                    int w = (int) Math.floor(dstRect.width() / scale);
                    int h = (int) Math.floor(dstRect.height() / scale);
                    bitmapRect.set((bitmapW - w) / 2, (bitmapH - h) / 2, w, h);
                    shaderMatrix.setRectToRect(bitmapRect, roundRect, Matrix.ScaleToFit.START);
                } else {
                    bitmapRect.set(0, 0, renderingBitmap.getWidth(), renderingBitmap.getHeight());
                    shaderMatrix.setRectToRect(bitmapRect, roundRect, Matrix.ScaleToFit.FILL);
                }
                renderingShader.setLocalMatrix(shaderMatrix);
                canvas.drawRoundRect(roundRect, roundRadius, roundRadius, getPaint());
            } else {
                canvas.translate(dstRect.left, dstRect.top);
                canvas.scale(scaleX, scaleY);
                canvas.drawBitmap(renderingBitmap, 0, 0, getPaint());
            }
            if (isRunning) {
                uiHandler.postDelayed(mInvalidateTask, invalidateAfter);
            }
        }
    }

    @Override
    public int getMinimumHeight() {
//        return decoderCreated ? metaData[1] : AndroidUtilities.dp(100);
        return backgroundBitmap.getHeight();
    }

    @Override
    public int getMinimumWidth() {
//        return decoderCreated ? metaData[0] : AndroidUtilities.dp(100);
        return backgroundBitmap.getWidth();
    }

    public Bitmap getAnimatedBitmap() {
        if (renderingBitmap != null) {
            return renderingBitmap;
        } else if (nextRenderingBitmap != null) {
            return nextRenderingBitmap;
        }
        return null;
    }

    public void setRoundRadius(int value) {
        roundRadius = value;
        getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public boolean hasBitmap() {
        return nativePtr != 0 && (renderingBitmap != null || nextRenderingBitmap != null);
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable drawable = new AnimatedFileDrawable(path, false);
        drawable.metaData[0] = metaData[0];
        drawable.metaData[1] = metaData[1];
        return drawable;
    }

    class AdProvider {
        Bitmap getAdBitmap(Context context) {
            try {
                InputStream ims;
                // get input stream
//                if (((int) (Math.random() * 3)) == 0)
//                    ims = context.getAssets().open("ad.jpg");
//                else
                ims = context.getAssets().open("nazdika.png");
                // load image as Drawable
                return BitmapFactory.decodeStream(ims);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        GiffAd getGiffAd(Context context, int width, int height) {
            float ratio = (float) width / height;
            GiffAd ad = new GiffAd();
            String file;
            if (ratio > 1.3) {
                file = "horizontal.jpg";
                ad.backColor = Color.argb(255, 220, 220, 220);
            } else if (ratio < 0.7) {
                file = "vertical.jpg";
                ad.backColor = Color.BLUE;
            } else {
                file = "square.jpg";
                ad.backColor = Color.RED;
            }
            try {
                InputStream ims = context.getAssets().open(file);
                ad.bitmap = BitmapFactory.decodeStream(ims);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return ad;
        }
    }

    class GiffAd {
        Bitmap bitmap;
        int backColor;
    }
}
