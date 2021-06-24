

package com.igordutrasanches.perfectscan.scan;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.igordutrasanches.perfectscan.activity.ScanActivity;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";
  public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

  private final ScanActivity activity;
  private final Map<DecodeHintType,Object> hints;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;

  public DecodeThread(ScanActivity activity,
                      Collection<BarcodeFormat> decodeFormats,
                      Map<DecodeHintType, ?> baseHints,
                      String characterSet,
                      ResultPointCallback resultPointCallback) {

    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);

    hints = new EnumMap<>(DecodeHintType.class);
    if (baseHints != null) {
      hints.putAll(baseHints);
    }

    if (decodeFormats == null || decodeFormats.isEmpty()) {
      decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
      if (true) {
        decodeFormats.addAll( DecodeFormatManager.PRODUCT_FORMATS);
      }
      if (true) {
        decodeFormats.addAll( DecodeFormatManager.INDUSTRIAL_FORMATS);
      }
      if (true) {
        decodeFormats.addAll( DecodeFormatManager.QR_CODE_FORMATS);
      }
      if (false) {
        decodeFormats.addAll( DecodeFormatManager.DATA_MATRIX_FORMATS);
      }
      if (true) {
        decodeFormats.addAll( DecodeFormatManager.AZTEC_FORMATS);
      }
      if (true) {
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
      }
    }
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }
    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(activity, hints);
    handlerInitLatch.countDown();
    Looper.loop();
  }

}
