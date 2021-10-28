package com.example.facial_features_detection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageAnalysis;
import androidx.fragment.app.Fragment;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.CAMERA_SERVICE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FaceDetectionFragment extends Fragment implements FaceDetected {
    public static List<Integer> FRAMES = new ArrayList<>();
    private FacialDetection facialDetection;

    private Executor executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_face_detection, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Captura facial");
        facialDetection = new FacialDetection(this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).startCamera();
        testClass();

    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onResume() {
        super.onResume();

/*        handler.postDelayed(runnable = () -> {
            cameraKitView.captureImage((cameraKitView, bytes) -> {
                int rotation = 0;
                try {
                    rotation = getRotationCompensation(((MainActivity) getActivity()).getCameraId(), ((MainActivity) getActivity()), false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                Bitmap itmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap bitmap = Bitmap.createScaledBitmap(itmap, cameraKitView.getWidth(), cameraKitView.getHeight(), false);
                InputImage image = InputImage.fromBitmap(bitmap, rotation);
                facialDetection.executeFacialDetection(image,0);
            });

            handler.postDelayed(runnable, delay);
        }, delay);*/
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
            facialDetection.executeFacialDetection(image, i);
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

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).stopCamera();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
    }


    @Override
    public void faceDetected(Face face, InputImage inputImage) {
        try {
            saveToInternalStorage(inputImage.getBitmapInternal(), inputImage.hashCode()+"");
        } catch (Exception e) {

        }

    }


    private boolean saveToInternalStorage(Bitmap bitmapImage, String imageName) throws IOException {

        boolean saved;
        OutputStream fos;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = ((MainActivity) getActivity()).getApplicationContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + imageName);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator +imageName;

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, imageName + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        return saved;
    }


}