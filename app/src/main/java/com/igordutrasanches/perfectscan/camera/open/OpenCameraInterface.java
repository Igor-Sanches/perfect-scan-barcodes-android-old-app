
package com.igordutrasanches.perfectscan.camera.open;

import android.hardware.Camera;
import android.util.Log;

@SuppressWarnings("deprecation") // camera APIs
public final class OpenCameraInterface {

  private static final String TAG = OpenCameraInterface.class.getName();

  /** For {@link #open(int)}, means no preference for which camera to open. */
  public static final int NO_REQUESTED_CAMERA = -1;

  private OpenCameraInterface() {
  }

  public static OpenCamera open(int cameraId) {

    int numCameras = Camera.getNumberOfCameras();
    if (numCameras == 0) {
      Log.w(TAG, "No cameras!");
      return null;
    }
    if (cameraId >= numCameras) {
      Log.w(TAG, "Requested camera does not exist: " + cameraId);
      return null;
    }

    if (cameraId <= NO_REQUESTED_CAMERA) {
      cameraId = 0;
      while (cameraId < numCameras) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        if (CameraFacing.values()[cameraInfo.facing] == CameraFacing.BACK) {
          break;
        }
        cameraId++;
      }
      if (cameraId == numCameras) {
        Log.i(TAG, "No camera facing " + CameraFacing.BACK + "; returning camera #0");
        cameraId = 0;
      }
    }

    Log.i(TAG, "Opening camera #" + cameraId);
    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    Camera.getCameraInfo(cameraId, cameraInfo);
    Camera camera = Camera.open(cameraId);
    if (camera == null) {
      return null;
    }
    return new OpenCamera(cameraId,
                          camera,
                          CameraFacing.values()[cameraInfo.facing],
                          cameraInfo.orientation);
  }

}
