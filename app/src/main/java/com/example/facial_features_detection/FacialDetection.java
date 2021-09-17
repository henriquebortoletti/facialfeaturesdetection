package com.example.facial_features_detection;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT;

public class FacialDetection {

    public static List<Integer> FRAMES = new ArrayList<>();
    FaceDetected faceDetected;

    FacialDetection(FaceDetected faceDetected) {
        this.faceDetected = faceDetected;
    }

    FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();
    FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

    public void executeFacialDetection(InputImage image) {

    }

    public void executeFacialDetection(InputImage image, int i) {
        detector.process(image)
                .addOnSuccessListener(
                        faces -> {
                            if (faces.size() == 1) {
                                Face face = faces.get(0);
                                /*Log.println(Log.ASSERT, this.getClass().getName(), "face headEulerAngleX: " + face.getHeadEulerAngleX());
                                Log.println(Log.ASSERT, this.getClass().getName(), "face headEulerAngleY: " + face.getHeadEulerAngleY());
                                Log.println(Log.ASSERT, this.getClass().getName(), "face headEulerAngleZ: " + face.getHeadEulerAngleZ());
                                Log.println(Log.ASSERT, this.getClass().getName(), "face similing: " + face.getSmilingProbability());
                                Log.println(Log.ASSERT, this.getClass().getName(), "face left eye open: " + face.getLeftEyeOpenProbability());
                                Log.println(Log.ASSERT, this.getClass().getName(), "face right eye open: " + face.getRightEyeOpenProbability());*/
                                if (!isValidPosition(face) || !isValidExpression(face)) {
                                    return;
                                }
                                Log.println(Log.ASSERT, this.getClass().getName(), "face detected");
                                FRAMES.add(i);
                                faceDetected.faceDetected(face, image);
                            }
                        });
    }

    private boolean validateImageSize(InputImage image) {
        return image.getHeight() >= 480 && image.getWidth() >= 360;
    }

    private boolean isValidPosition(Face face) {
        if (face.getHeadEulerAngleX() > 12 || face.getHeadEulerAngleX() < -12) {
            return false;
        }
        if (face.getHeadEulerAngleY() > 12 || face.getHeadEulerAngleY() < -12) {
            return false;
        }
        if (face.getHeadEulerAngleZ() > 12 || face.getHeadEulerAngleZ() < -12) {
            return false;
        }
        return true;
    }

    private boolean isValidExpression(Face face) {
        if (face.getSmilingProbability() == null
                || face.getRightEyeOpenProbability() == null
                || face.getLeftEyeOpenProbability() == null) {
            return false;
        }
        return
                face.getSmilingProbability() < 0.1
                        && face.getLeftEyeOpenProbability() > 0.9
                        && face.getRightEyeOpenProbability() > 0.9;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void testClass() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(Environment.getExternalStorageDirectory().toString() + "/Download/my_video.mp4");
        int totalFrames = Integer.parseInt(retriever.extractMetadata(METADATA_KEY_VIDEO_FRAME_COUNT));

        for (int i = 0; i < totalFrames; i += 30) {
            Log.println(Log.ASSERT, this.getClass().getName(), "Actual Frame: " + i);
            Bitmap img = retriever.getFrameAtIndex(i);
            InputImage image = InputImage.fromBitmap(img, 0);
            executeFacialDetection(image, i);
        }
        Log.println(Log.ASSERT, this.getClass().getName(), "Total frames: " + totalFrames);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        for (int i = 0; i < FRAMES.size(); i++) {
            Log.println(Log.ASSERT, this.getClass().getName(), "Frame selected: " + FRAMES.get(i));
        }
    }
}
