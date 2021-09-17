package com.example.facial_features_detection;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.CAMERA_SERVICE;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FaceDetectionFragment extends Fragment implements FaceDetected {

    private FacialDetection facialDetection;


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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).startCamera();
    }


/*    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable = () -> {
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
                facialDetection.executeFacialDetection(image);
            });

            handler.postDelayed(runnable, delay);
        }, delay);
    }*/


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
/*        MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
        inputImage.getByteBuffer()
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.addPart("fileUpload", inputImage.getByteBuffer());*/


    }


}