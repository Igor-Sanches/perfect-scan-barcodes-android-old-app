
package com.igordutrasanches.perfectscan.camera;

import android.content.Context;

import com.igordutrasanches.perfectscan.compoments.Key;

public enum FrontLightMode {

  /** Always on. */
  ON,
  /** On only when ambient light is low. */
  AUTO,
  /** Always off. */
  OFF;

  private static FrontLightMode parse(String modeString) {
    return modeString == null ? OFF : valueOf(modeString);
  }

  public static FrontLightMode readPref(Context context) {
    return parse(Key.getFrontLightMode(context));
  }

}
