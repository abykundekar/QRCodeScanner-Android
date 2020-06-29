package com.test.qrcodereadnscanner;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.test.qrcodereadnscanner.helper.EncryptionHelper;
import com.test.qrcodereadnscanner.helper.QRCodeHelper;
import com.test.qrcodereadnscanner.models.UserObject;

public class GenerateQRCodeActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText;
    private Button generateQRCodeButton;
    private ImageView qrCodeImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        nameEditText = findViewById(R.id.fullNameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        generateQRCodeButton = findViewById(R.id.generateQrCodeButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        generateQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEditText()){
                    hideKeyboard();
                    UserObject userObject = new UserObject();
                    userObject.setFullName(nameEditText.getText().toString());
                    userObject.setAge(Integer.parseInt(ageEditText.getText().toString()));
                    String serializedString = new Gson().toJson(userObject);
                    String encrypted =  EncryptionHelper.getInstance().encryptionString(serializedString).encryptMsg();
                    setImageBitmap(encrypted);
                }
            }
        });

    }

    private void setImageBitmap(String encryptedString) {
        Bitmap bitmap = QRCodeHelper.newInstance(this).setContent(encryptedString).setErrorCorrectionLevel(ErrorCorrectionLevel.Q).setMargin(2).getQRCOde();
        qrCodeImageView.setImageBitmap(bitmap);
    }
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view != null) {
            //Find the currently focused view, so we can grab the correct window token from it.
            InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean checkEditText(){
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            nameEditText.setError("can't be empty!!");
            return false;
        }
        if (TextUtils.isEmpty(ageEditText.getText().toString())){
            ageEditText.setError("can't be empty!!");
            return false;
        }
        return true;
    }
}
