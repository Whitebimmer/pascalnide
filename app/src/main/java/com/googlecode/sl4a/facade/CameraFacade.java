/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.sl4a.facade;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.googlecode.sl4a.BaseApplication;
import com.googlecode.sl4a.FileUtils;
import com.googlecode.sl4a.FutureActivityTaskExecutor;
import com.googlecode.sl4a.Log;
import com.googlecode.sl4a.future.FutureActivityTask;
import com.googlecode.sl4a.jsonrpc.RpcReceiver;
import com.googlecode.sl4a.rpc.PascalMethod;
import com.googlecode.sl4a.rpc.RpcDefault;
import com.googlecode.sl4a.rpc.RpcParameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * Access Camera functions.
 */
public class CameraFacade extends RpcReceiver {

    private final Context mContext;
    private final Parameters mParameters;

    public CameraFacade(FacadeManager manager) throws Exception {
        super(manager);
        mContext = manager.getContext();
        Camera camera = openCamera(0);
        try {
            mParameters = camera.getParameters();
        } finally {
            camera.release();
        }
    }

    public Camera openCamera(int cameraId) throws Exception {
        int sSdkLevel = Integer.parseInt(android.os.Build.VERSION.SDK);
        Camera result;
        if (sSdkLevel < 9) {
            result = Camera.open();
        } else {
            Method openCamera = Camera.class.getMethod("open", int.class);
            result = (Camera) openCamera.invoke(null, cameraId);
        }
        return result;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Take a picture and save it to the specified path.", returns = "A map of Booleans autoFocus and takePicture where True indicates success. cameraId also included.")
    public Bundle cameraCapturePicture(
            @RpcParameter(name = "targetPath") final String targetPath,
            @RpcParameter(name = "useAutoFocus") @RpcDefault("true") Boolean useAutoFocus,
            @RpcParameter(name = "cameraId", description = "Id of camera to use. SDK 9") @RpcDefault("0") Integer cameraId)
            throws Exception {
        final BooleanResult autoFocusResult = new BooleanResult();
        final BooleanResult takePictureResult = new BooleanResult();
        Camera camera = openCamera(cameraId);
        camera.setParameters(mParameters);

        try {
            Method method = camera.getClass().getMethod("setDisplayOrientation", int.class);
            method.invoke(camera, 90);
        } catch (Exception e) {
            Log.e(e);
        }

        try {
            FutureActivityTask<SurfaceHolder> previewTask = setPreviewDisplay(camera);
            camera.startPreview();
            if (useAutoFocus) {
                autoFocus(autoFocusResult, camera);
            }
            takePicture(new File(targetPath), takePictureResult, camera);
            previewTask.finish();
        } catch (Exception e) {
            Log.e(e);
        } finally {
            camera.release();
        }

        Bundle result = new Bundle();
        result.putBoolean("autoFocus", autoFocusResult.mmResult);
        result.putBoolean("takePicture", takePictureResult.mmResult);
        result.putInt("cameraId", cameraId);
        return result;
    }

    private FutureActivityTask<SurfaceHolder> setPreviewDisplay(Camera camera) throws IOException,
            InterruptedException {
        FutureActivityTask<SurfaceHolder> task = new FutureActivityTask<SurfaceHolder>() {
            @Override
            public void onCreate() {
                super.onCreate();
                final SurfaceView view = new SurfaceView(getActivity());
                getActivity().setContentView(view);
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
                view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                view.getHolder().addCallback(new Callback() {
                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                    }

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        setResult(view.getHolder());
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    }
                });
            }
        };
        FutureActivityTaskExecutor taskQueue =
                ((BaseApplication) mContext).getTaskExecutor();
        taskQueue.execute(task);
        camera.setPreviewDisplay(task.getResult());
        return task;
    }

    private void takePicture(final File file, final BooleanResult takePictureResult,
                             final Camera camera) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        camera.takePicture(null, null, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (!FileUtils.makeDirectories(file.getParentFile(), 493)) {
                    takePictureResult.mmResult = false;
                    return;
                }
                try {
                    FileOutputStream output = new FileOutputStream(file);
                    output.write(data);
                    output.close();
                    takePictureResult.mmResult = true;
                } catch (IOException e) {
                    Log.e("Failed to save picture.", e);
                    takePictureResult.mmResult = false;
                    return;
                } finally {
                    latch.countDown();
                }
            }
        });
        latch.await();
    }

    private void autoFocus(final BooleanResult result, final Camera camera)
            throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        {
            camera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    result.mmResult = success;
                    latch.countDown();
                }
            });
            latch.await();
        }
    }

    @Override
    public void shutdown() {
        // Nothing to clean up.
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Starts the image capture application to take a picture and saves it to the specified path.")
    public void cameraInteractiveCapturePicture(
            @RpcParameter(name = "targetPath") final String targetPath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(targetPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        AndroidFacade facade = mManager.getReceiver(AndroidFacade.class);
        facade.startActivityForResult(intent);
    }

    private class BooleanResult {
        boolean mmResult = false;
    }
}