package com.igordutrasanches.perfectscan.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.activity.manager.CodeView;
import com.igordutrasanches.perfectscan.compoments.CodeVerificador;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.compoments.ResourceLoader;
import com.igordutrasanches.perfectscan.db.Historical;
import com.igordutrasanches.perfectscan.manager.StorageFile;
import com.igordutrasanches.perfectscan.menuManager.MenuDelegate;
import com.igordutrasanches.perfectscan.menuManager.NavigationMenuManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScanImageActivity extends AppCompatActivity {


    private ImageView imageVeiw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageVeiw = (ImageView) findViewById(R.id.cropper_image);
        BottomNavigationView nv = (BottomNavigationView) findViewById(R.id.menu_image_bottom);
        nv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_buscar_camera) {
                    onCapture();

                }else{
                    onGalerry();

                }
                return false;
            }
        });
        if(savedInstanceState != null){
            setView();
        }

        if(getIntent().getAction() != null){
            intent = getIntent();
            String action = intent.getAction();
            if(Intent.ACTION_SEND.equals(action)){
                getPersionIntent();
            }
        }
        NavigationMenuManager manager = new NavigationMenuManager(this);
        MenuDelegate menuDelegate = new MenuDelegate(this, toolbar, manager);
    }

    @Override
    public void onResume() {
        super.onResume();
        Key.setPageView("scan_image",this);

    }
    Intent intent;


    private void getPersionIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            } catch (Exception x) {
                onSetImage();
            }
        }
    }

    private void onSetImage() {
        Uri image = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        try{
            if (image != null){
                Key.set(DATA, getPath(image), this);
                Key.set(CODE_TYPE, "90", this);
                setView();
            }
        }catch (Exception x) {
            try {
                if (image != null) {
                    Key.set(DATA, getPathForMore(image), this);
                    Key.set(CODE_TYPE, "90", this);
                    setView();
                }
            }catch(Exception x2){
                    Toast.makeText(this, x2.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    File file;
    private String getPathForMore(Uri image) {
        Uri uri = image;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameI = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeI = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        String name = (cursor.getString(nameI));
        String size = (Long.toString(cursor.getLong(sizeI)));
        file = StorageFile.getFileSend("." + name);
        try{
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBuffer = 1 * 1024 * 1024;
            int bytes = inputStream.available();

            int bufferSize = Math.min(bytes, maxBuffer);
            final byte[] buffer = new byte[bufferSize];
            while ((read = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    private void onCapture() {
        isCamera = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 555);

            } catch (Exception x) {
                startCameraActivity();
            }
        }
    }

    public void onDecode(View view){
       iniciarLeitura();
    }

    private static final String DATA = "dataImage", CODE_TYPE = "codeType";

    @Override
    public void onActivityResult(int requesteCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if(requesteCode == 90){
                Key.set(DATA, getPath(data.getData()), this);
                Key.set(CODE_TYPE, "90", this);

            } else{
                Key.set(CODE_TYPE, "0", this);
                Bundle b = data.getExtras();
                Intent v = getIntent();
                v.putExtra(DATA, b);
            }
            setView();
        }
    }

    private void setView() {
        try{
            if(Key.get(CODE_TYPE, "", this).equals("90")){
              try{  String path = Key.get(DATA, "", this);
                BitmapFactory.Options fac = new BitmapFactory.Options();
                fac.inPurgeable = true;
                fac.inDensity = 0;
                fac.inTargetDensity = 0;
                fac.inDensity = 0;
                fac.inScaled = false;
                codigo = BitmapFactory.decodeFile(path, fac);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                codigo.compress(Bitmap.CompressFormat.PNG, 100, stream);
              }catch (Exception x){
                  Toast.makeText(this, getString(R.string.erro_image_gallery), Toast.LENGTH_SHORT).show();
              }
            }else{
              try{  Intent i = getIntent();
                Bundle b = (Bundle)i.getExtras().get(DATA);
                codigo = (Bitmap)b.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                codigo.compress(Bitmap.CompressFormat.PNG, 100, stream);
              }catch (Exception x){
                  Toast.makeText(this, getString(R.string.erro_image_capture), Toast.LENGTH_SHORT).show();
              }
            }
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
        imageVeiw.setVisibility(codigo != null ? View.VISIBLE : View.GONE);
        imageVeiw.setImageBitmap(codigo);
        ((TextView)findViewById(R.id.view_dsc_preview)).setVisibility(codigo == null ? View.VISIBLE : View.GONE);
        ((FloatingActionButton)findViewById(R.id.fab_digitalizar)).setVisibility(codigo != null ? View.VISIBLE : View.GONE);
    }

    public String getPath(Uri uri){

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int columa_i = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columa_i);
        }
        return uri.getPath();
    }

    private void onGalerry() {
        isCamera = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 555);

            } catch (Exception x) {
                startGalleryActivity();
            }
        }
    }

    String getRes(int index){
        return ResourceLoader.getString(this, index);
    }

    boolean isCamera = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandPermission) {
        if (requestCode == 555 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
            if(isCamera)
                startCameraActivity();
            else
                startGalleryActivity();
        } else if (requestCode == 100 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
            onSetImage();
        } else {
            Toast.makeText(this, getRes(R.string.msg_permission), Toast.LENGTH_SHORT).show();
        }
    }

    private void startGalleryActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), 90);
    }

    private void startCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private ProgressDialog mProgressDialog;

    Bitmap codigo = null;

    private void iniciarLeitura() {
        try{
            mProgressDialog = ProgressDialog.show(ScanImageActivity.this, getRes(R.string.scan_init_title), getRes(R.string.scan_init), true);
            mProgressDialog.show();
            int[] intarray = new int[codigo.getWidth() * codigo.getHeight()];
            codigo.getPixels(intarray, 0, (int)codigo.getWidth(), 0, 0, (int)codigo.getWidth(), (int)codigo.getHeight());
            LuminanceSource source = new RGBLuminanceSource(codigo.getWidth(), codigo.getHeight(), intarray);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            try{
                Result result = reader.decode(binaryBitmap);

                if(result.getBarcodeFormat() != BarcodeFormat.DATA_MATRIX){
                if(!result.getText().equals("")){
                    mProgressDialog.dismiss();
                    Historical hit = new Historical(ScanImageActivity.this);
                    hit.adicionarFromScan(result);
                    CodeView.by(ScanImageActivity.this).show(result.getText(), result.getBarcodeFormat().toString(), CodeVerificador.by(result.getText(), result.getBarcodeFormat().toString()).getNameCode(ScanImageActivity.this), 0, 1);
                }else{
                    setMessageError(false);
                }
                }else{
                    mProgressDialog.dismiss();
                    setMessageError(true);
                }
            }catch (Exception x){
                mProgressDialog.dismiss();
                setMessageError(false);
            }
        }catch (Exception c){
        }
    }

    private void setMessageError(boolean data){
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_scan_message)
                .setMessage(data ? R.string.error_data_matrix : R.string.error_scan_message_error)
                .setPositiveButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
