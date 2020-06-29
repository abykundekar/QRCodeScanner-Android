package com.test.qrcodereadnscanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.test.qrcodereadnscanner.helper.EncryptionHelper;
import com.test.qrcodereadnscanner.models.UserObject;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ReadQRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String HUAWEI = "huawei";
    private static final int MY_CAMERA_REQUEST_CODE = 6515;
    private ZXingScannerView qrCodeScanner;
    private ImageView flashOnOffImageView, barCodeImageView;
    private RelativeLayout rootViewLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read_qrcode);

        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        flashOnOffImageView = findViewById(R.id.flashOnOffImageView);
        barCodeImageView = findViewById(R.id.barcodeBackImageView);
        rootViewLayout = findViewById(R.id.scanQrCodeRootView);
        setScannerProperties();
        barCodeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        flashOnOffImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * Set bar code scanner basic properties.
     */

    private void setScannerProperties(){
        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QR_CODE);
        qrCodeScanner.setFormats(list);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
        if (Build.MANUFACTURER.equalsIgnoreCase(HUAWEI)) {
            qrCodeScanner.setAspectTolerance(0.5f);
        }
    }

    /**
     * resume the qr code camera when activity is in onResume state.
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
                return;
            }
        }
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }

    @Override
    public void handleResult(Result p0) {
        if (p0 != null) {
            //String  decryptedString = EncryptionHelper.getInstance().getDecryptionString(p0.getText());
            //UserObject userObject = new Gson().fromJson(decryptedString, UserObject.class);
            //Toast.makeText(this,"name is "+userObject.getFullName() + " AND Age is "+ userObject.getAge(),Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Data is "+ p0.getText().toString(), Toast.LENGTH_SHORT).show();
        }
        resumeCamera();
    }
    /**
     * To check if user grant camera permission then called openCamera function.If not then show not granted
     * permission snack bar.
     *
     * @param requestCode  specify which request result came from operating system.
     * @param permissions  to specify which permission result is came.
     * @param grantResults to check if user granted the specific permission or not.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera();
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                showCameraSnackBar();
        }
    }

    private void showCameraSnackBar(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar snackbar = Snackbar.make(rootViewLayout, this.getResources().getString(R.string.app_needs_your_camera_permission_in_order_to_scan_qr_code), Snackbar.LENGTH_LONG);
            View view1 = snackbar.getView();
            view1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            TextView textView = view1.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            snackbar.show();
        }
    }

    private void openCamera(){
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();;
    }

    /**
     * Resume the camera after 2 seconds when qr code successfully scanned through bar code reader.
     */

    private void resumeCamera() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
    }
}
