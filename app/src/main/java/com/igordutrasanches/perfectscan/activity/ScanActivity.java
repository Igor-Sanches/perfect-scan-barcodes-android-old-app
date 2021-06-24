package com.igordutrasanches.perfectscan.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.manager.CodeView;
import com.igordutrasanches.perfectscan.activity.manager.Monitor;
import com.igordutrasanches.perfectscan.camera.CameraManager;
import com.igordutrasanches.perfectscan.compoments.CodeVerificador;
import com.igordutrasanches.perfectscan.compoments.GetColor;
import com.igordutrasanches.perfectscan.compoments.HelperManager;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.LightManager;
import com.igordutrasanches.perfectscan.db.Historical;
import com.igordutrasanches.perfectscan.scan.InactivityTimer;
import com.igordutrasanches.perfectscan.scan.IntentSource;
import com.igordutrasanches.perfectscan.scan.Intents;
import com.igordutrasanches.perfectscan.scan.ScanHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

public class ScanActivity extends Activity implements SurfaceHolder.Callback {

    boolean battery = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.intent.action.BATTERY_CHANGED")){
                int n = intent.getIntExtra("level", 0);
                if(n <= 15)
                    battery = true;
            }
        }
    };

    private static final String TAG = ScanActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String[] SCAN_LINKS = { "http://zxing.appspot.com/scan", "zxing://scan/" };

    private static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    private FloatingActionButton color_invert, btn_massa;
    private CameraManager cameraManager;
    private ScanHandler handler;
    private Monitor monitor;
    private Result lastResult;
    private boolean hasSurface;
    private IntentSource source;
    private String sourceUrl;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType,?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private HelperManager helperManager;
    private LightManager lightManager;

    public Monitor getMonitor() {
        return monitor;
    }
    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.content_scan);
            btn_massa = (FloatingActionButton) findViewById(R.id.fab_massa);
            color_invert = (FloatingActionButton) findViewById(R.id.fab_invert_color);
            registerForBroadcasts();
            hasSurface = false;
            inactivityTimer = new InactivityTimer(this);
            helperManager = new HelperManager(this, this);
            lightManager = new LightManager(this);


            getBtnMassa();
            getBtnInvertColor();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onColorInvert(View view){
        Key.setDisableInvert(this, !Key.getDisableInvert(this));
        getBtnInvertColor();
        Toast.makeText(this, getString(R.string.invert_colro) + " " + getOnOff(Key.getDisableInvert(this)), Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(this, ScanActivity.class));
    }

    private String getOnOff(boolean on) {
        String res;
        if(on) res = getString(R.string.on);
        else res = getString(R.string.off);
        return res;
    }

    private void getBtnInvertColor() {
        if(Key.getDisableInvert(this)){
            color_invert.setImageResource(R.drawable.ic_action_invert_color_off);
        }else color_invert.setImageResource(R.drawable.ic_action_invert_color);
    }

    public void onMassaScan(View view){
        Key.setMassa(this, !Key.getMassa(this));
        getBtnMassa();
        Toast.makeText(this, getString(R.string.massa) + " " + getOnOff(Key.getMassa(this)), Toast.LENGTH_SHORT).show();
    }

    private void getBtnMassa() {
        if(Key.getMassa(this)){
            btn_massa.setImageResource(R.drawable.ic_action_massa_off);
        }else btn_massa.setImageResource(R.drawable.ic_action_massa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onShowScanner();
    }

    public void onCamera(View view){
        if(Key.getCamera(this) == 0)
            Key.setCamera(this, 1);
        else Key.setCamera(this, 0);

        getCameraUpdate();
    }

    private void getCameraUpdate() {
        finish();
        startActivity(new Intent(this, ScanActivity.class));
    }

    private void onShowScanner() {
        try{

            cameraManager = new CameraManager(getApplication());
            int cameras = Camera.getNumberOfCameras();

            FloatingActionButton btn_camera = (FloatingActionButton)findViewById(R.id.fab_camera);
            btn_camera.setVisibility(cameras >= 2 ? View.VISIBLE : View.GONE);
            ((FloatingActionButton) findViewById(R.id.fab_flash)).setEnabled(Key.getCamera(this) == 0 ? true : false);

            monitor = (Monitor) findViewById(R.id.viewfinder_view);
            monitor.setCameraManager(cameraManager);
            ((View)findViewById(R.id.laser1)).setBackgroundResource(GetColor.laser(this));
            ((View)findViewById(R.id.laser2)).setBackgroundResource(GetColor.laser(this));

            handler = null;
            lastResult = null;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            resetStatusView();


            helperManager.updatePrefs();
            lightManager.start(cameraManager);

            inactivityTimer.onResume();
            source = IntentSource.NONE;
            sourceUrl = null;
            decodeFormats = null;
            characterSet = null;
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            if (hasSurface) {
                // The activity was paused but not stopped, so the surface still exists. Therefore
                // surfaceCreated() won't be called, so init the camera here.
                initCamera(surfaceHolder);
            } else {
                // Install the callback and wait for surfaceCreated() to init the camera.
                surfaceHolder.addCallback(this);
            }  }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        lightManager.stop();
        helperManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void registerForBroadcasts(){
        try{
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            registerReceiver(receiver, filter);
        }catch (Exception x){
        }
    }

    boolean isFlash = false;
    public void onFash(View view){
        if(!battery){
            isFlash = !isFlash;
            ((FloatingActionButton)findViewById(R.id.fab_flash)).setImageResource(isFlash ? R.drawable.ic_action_flash_off : R.drawable.ic_action_flash_on);
            cameraManager.setTorch(isFlash);
        }else{
            Toast.makeText(this, getString(R.string.battery), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        lastResult = rawResult;

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            Historical hit = new Historical(this);
            hit.adicionarFromScan(rawResult);
            helperManager.playBeepSoundAndVibrate();
            drawResultPoints(barcode, scaleFactor, rawResult);
        }
        if (fromLiveScan && Key.getMassa(this)) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.msg_massa),
                    Toast.LENGTH_SHORT).show();
            // Wait a moment or else it will scan the same barcode continuously about 3 times
            restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
        } else {
            handleDecodeInternally(rawResult, barcode);
        }
    }

    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }

    // Put up our own UI for how to handle the decoded contents.
    private void handleDecodeInternally(Result rawResult, Bitmap barcode) {

        try{

            Historical hit = new Historical(this);
            hit.adicionarFromScan(rawResult);
            CodeView.by(this).show(rawResult.getText(), rawResult.getBarcodeFormat().toString(), CodeVerificador.by(rawResult.getText(), rawResult.getBarcodeFormat().toString()).getNameCode(this), 0, 1);

        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
    private void handleDecodeExternally(Result rawResult, Bitmap barcode) {

        if (barcode != null) {
            monitor.drawResultBitmap(barcode);
        }

        long resultDurationMS;
        if (getIntent() == null) {
            resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        } else {
            resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
                    DEFAULT_INTENT_RESULT_DURATION_MS);
        }

        if (resultDurationMS > 0) {
            String rawResultString = String.valueOf(rawResult);
            if (rawResultString.length() > 32) {
                rawResultString = rawResultString.substring(0, 32) + " ...";
            }
            Toast.makeText(this, "" + " : " + rawResultString, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        if (handler != null) {
            Message message = Message.obtain(handler, id, arg);
            if (delayMS > 0L) {
                handler.sendMessageDelayed(message, delayMS);
            } else {
                handler.sendMessage(message);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new ScanHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    boolean permission = true;
    private void displayFrameworkBugMessageAndExit() {
        permission = false;
        showPermision();
    }

    public void showPermision() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permision)
                .setMessage(R.string.permision_camera)
                .setPositiveButton(R.string.btn_yes_permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        permissionCamera();
                        permission = true;
                    }
                }).setNegativeButton(R.string.btn_not_permission, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                Toast.makeText(ScanActivity.this, R.string.msg_permission, Toast.LENGTH_SHORT).show();
                permission = false;
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!permission) {
                    finish();
                    Toast.makeText(ScanActivity.this, R.string.msg_permission, Toast.LENGTH_SHORT).show();
                }
            }
        }).create().show();
    }

    private void permissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 554);
            } catch (Exception x) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandPermission) {
        if (requestCode == 554 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
        }
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        lastResult = null;
    }

    public void drawViewfinder() {
        monitor.drawViewfinder();
    }
}