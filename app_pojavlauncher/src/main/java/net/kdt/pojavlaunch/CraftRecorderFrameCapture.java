package net.kdt.pojavlaunch;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

/**
 * Captures only the Minecraft render view.
 * Launcher touch controls are separate views and are not included.
 */
public final class CraftRecorderFrameCapture {
    private static final int MAX_WIDTH = 1280;
    private static final int MAX_HEIGHT = 720;

    public interface Callback {
        void onFrameCaptured(Bitmap bitmap);
        void onCaptureFailed(String reason);
    }

    private CraftRecorderFrameCapture() {
    }

    public static void capture(View renderView, Callback callback) {
        if (callback == null) return;

        if (renderView == null) {
            callback.onCaptureFailed("Minecraft render view is not available.");
            return;
        }

        int sourceWidth = renderView.getWidth();
        int sourceHeight = renderView.getHeight();

        if (sourceWidth <= 0 || sourceHeight <= 0) {
            callback.onCaptureFailed("Minecraft render view has no size yet.");
            return;
        }

        float scale = Math.min(
                1.0f,
                Math.min((float) MAX_WIDTH / sourceWidth,
                         (float) MAX_HEIGHT / sourceHeight)
        );

        int outputWidth = Math.max(1, Math.round(sourceWidth * scale));
        int outputHeight = Math.max(1, Math.round(sourceHeight * scale));

        if (renderView instanceof SurfaceView) {
            captureSurfaceView((SurfaceView) renderView,
                    outputWidth, outputHeight, callback);
            return;
        }

        if (renderView instanceof TextureView) {
            captureTextureView((TextureView) renderView,
                    outputWidth, outputHeight, callback);
            return;
        }

        callback.onCaptureFailed("Unsupported render view type: "
                + renderView.getClass().getSimpleName());
    }

    private static void captureSurfaceView(
            SurfaceView surfaceView,
            int width,
            int height,
            Callback callback
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            callback.onCaptureFailed(
                    "SurfaceView capture requires Android 7.0 or newer."
            );
            return;
        }

        if (!surfaceView.getHolder().getSurface().isValid()) {
            callback.onCaptureFailed("Minecraft surface is not ready.");
            return;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                width, height, Bitmap.Config.ARGB_8888
        );

        try {
            PixelCopy.request(
                    surfaceView,
                    bitmap,
                    result -> {
                        if (result == PixelCopy.SUCCESS) {
                            callback.onFrameCaptured(bitmap);
                        } else {
                            bitmap.recycle();
                            callback.onCaptureFailed(
                                    "PixelCopy failed with code: " + result
                            );
                        }
                    },
                    new Handler(Looper.getMainLooper())
            );
        } catch (RuntimeException error) {
            bitmap.recycle();
            callback.onCaptureFailed(
                    "Surface capture error: " + error.getMessage()
            );
        }
    }

    private static void captureTextureView(
            TextureView textureView,
            int width,
            int height,
            Callback callback
    ) {
        if (!textureView.isAvailable()) {
            callback.onCaptureFailed("Minecraft texture is not ready.");
            return;
        }

        Bitmap bitmap = textureView.getBitmap(width, height);
        if (bitmap == null) {
            callback.onCaptureFailed("Could not capture Minecraft texture.");
            return;
        }

        callback.onFrameCaptured(bitmap);
    }
}
