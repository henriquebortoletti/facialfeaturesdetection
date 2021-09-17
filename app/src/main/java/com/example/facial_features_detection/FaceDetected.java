package com.example.facial_features_detection;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;

public interface FaceDetected {

    void faceDetected(Face face, InputImage inputImage);
}
