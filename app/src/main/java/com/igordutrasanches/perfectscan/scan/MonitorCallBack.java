
package com.igordutrasanches.perfectscan.scan;


import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.igordutrasanches.perfectscan.activity.manager.Monitor;

public final class MonitorCallBack implements ResultPointCallback {

  private final Monitor monitor;


  public MonitorCallBack(Monitor monitor) {
    this.monitor = monitor;
  }

  @Override
  public void foundPossibleResultPoint(ResultPoint point) {
    monitor.addPossibleResultPoint(point);
  }

}
