package com.example.facial_features_detection;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
                    .setLandmarkMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();
    FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

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
                                if (!isValidPosition(face) || !isValidExpression(face) || !areLandmarksValid(face)) {
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

    private boolean areLandmarksValid(Face face) {
        for(FaceLandmark faceLandmark:face.getAllLandmarks())
            Log.println(Log.ASSERT, this.getClass().getName(), "face landmark:" + faceLandmark.getLandmarkType());
        return true;
    }

    private boolean isValidPosition(Face face) {
        if (face.getHeadEulerAngleX() > 8 || face.getHeadEulerAngleX() < -8) {
            return false;
        }
        if (face.getHeadEulerAngleY() > 8 || face.getHeadEulerAngleY() < -8) {
            return false;
        }
        if (face.getHeadEulerAngleZ() > 8 || face.getHeadEulerAngleZ() < -8) {
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
                face.getSmilingProbability() < 0.08
                        && face.getLeftEyeOpenProbability() > 0.9
                        && face.getRightEyeOpenProbability() > 0.9;
    }


}
