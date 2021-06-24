
package com.igordutrasanches.perfectscan.compoments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.igordutrasanches.perfectscan.camera.CameraManager;
import com.igordutrasanches.perfectscan.camera.FrontLightMode;

public final class LightManager implements SensorEventListener {

  private static final float TOO_DARK_LUX = 45.0f;
  private static final float BRIGHT_ENOUGH_LUX = 450.0f;

  private final Context context;
  private CameraManager cameraManager;
  private Sensor lightSensor;

  public LightManager(Context context) {
    this.context = context;
  }

  public void start(CameraManager cameraManager) {
    this.cameraManager = cameraManager;
    if (FrontLightMode.readPref(context) == FrontLightMode.AUTO) {
      SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
      lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
      if (lightSensor != null) {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
      }
    }
  }

  public void stop() {
    if (lightSensor != null) {
      SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
      sensorManager.unregisterListener(this);
      cameraManager = null;
      lightSensor = null;
    }
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    float ambientLightLux = sensorEvent.values[0];
    if (cameraManager != null) {
      if (ambientLightLux <= TOO_DARK_LUX) {
        cameraManager.setTorch(true);
      } else if (ambientLightLux >= BRIGHT_ENOUGH_LUX) {
        cameraManager.setTorch(false);
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // do nothing
  }

}
