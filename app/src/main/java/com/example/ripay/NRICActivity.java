package com.example.ripay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class NRICActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context curContext;

    private String cameraId;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession cameraCaptureSessions;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private SurfaceView cameraView;
    private ImageReader imageReader;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_r_i_c);
        curContext = this;

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent prevRegistrationStep = getIntent();
        String firstNameString = prevRegistrationStep.getStringExtra("firstName");
        String lastNameString = prevRegistrationStep.getStringExtra("lastName");
        String newEmailString = prevRegistrationStep.getStringExtra("email");
        String newPasswordConfirmString = prevRegistrationStep.getStringExtra("password");

        Log.d("debug", "nric scanning");
        cameraView = findViewById(R.id.cameraView);
        initCamera();

        /*mAuth.createUserWithEmailAndPassword(newEmailString, newPasswordConfirmString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FocusSnackbar.show(curContext, R.string.registration_successful, findViewById(R.id.registrationScreen));
                            completeRegistration(user);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                // Weak password
                                FocusSnackbar.show(curContext, R.string.registration_weak_password, findViewById(R.id.registrationScreen));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Wrong email
                                FocusSnackbar.show(curContext, R.string.registration_invalid_email, findViewById(R.id.registrationScreen));
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email already registered
                                FocusSnackbar.show(curContext, R.string.registration_email_exists, findViewById(R.id.registrationScreen));
                            } catch (Exception e) {
                                // Unknown exception
                                Log.d("exception", e.getMessage());
                            }
                        }
                    }
                });*/
    }

    private void initCamera() {
        CameraManager cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //imageDimension = map.getOutputSizes(SurfaceView.class)[0];

            // Check if camera permissions are allowed
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA//,
                        //Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        imageReader = ImageReader.newInstance(640, 380, ImageFormat.JPEG, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Log.d("debug", "image got");

                Image image = null;
                try {
                     image = imageReader.acquireLatestImage();
                } catch (Exception e) {
                    Log.d("debug", "no image");
                }

            }
        }, mBackgroundHandler);
    }

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreview() {
        try {
            Surface surface = cameraView.getHolder().getSurface();

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            //captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null) {
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.d("debug", "the bad thing happened");
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null) {
            Log.d("debug", "error");
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    captureStillPicture();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureStillPicture() {
        try {
            if (cameraDevice == null) {
                return;
            }
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureBuilder.addTarget(imageReader.getSurface());
            //captureBuilder.set(CaptureRequest.FLASH_MODE, torchStatus);
            //captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            captureBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
            //captureBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, range);
            CameraCaptureSession.CaptureCallback captureStillCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                }

                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }
            };
            cameraCaptureSessions.capture(captureBuilder.build(), captureStillCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        initCamera();
        //if(textureView.isAvailable())
        //    openCamera();
        //else
        //    textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void completeRegistration(FirebaseUser currentUser) {
        Intent completeLoginIntent = new Intent(this, PrimaryActivity.class);
        completeLoginIntent.putExtra("uid", currentUser.getUid());

        Intent finishLoginActivityIntent = new Intent("finish_activity");
        sendBroadcast(finishLoginActivityIntent);

        startActivity(completeLoginIntent);
        finish();
    }
}
