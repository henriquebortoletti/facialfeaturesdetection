package com.example.facial_features_detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT;
import static com.example.facial_features_detection.FacialDetection.FRAMES;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.facial_features_detection", appContext.getPackageName());

    }

    @Test
    public void testFaceDetection() throws IOException {
        FaceDetectionFragment facialDetection = new FaceDetectionFragment();
        facialDetection.testClass();
    }

    private class FaceDetectedImpl implements FaceDetected {

        @Override
        public void faceDetected(Face face, InputImage inputImage) {
        }
    }
}