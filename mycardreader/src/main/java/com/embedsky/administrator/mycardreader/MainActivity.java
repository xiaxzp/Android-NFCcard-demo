package com.embedsky.administrator.mycardreader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements LoyaltyCardReader.AccountCallback {

    public static final String TAG = "MyCardReader";
    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public LoyaltyCardReader mLoyaltyCardReader;
    private TextView mAccountField;
    private ToggleButton mToggleButton;
    private ToggleButton mNfcSwitch;
    public static StringBuilder sb=new StringBuilder();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAccountField= (TextView) findViewById(R.id.data_tv);
        mLoyaltyCardReader = new LoyaltyCardReader(this);
        mToggleButton=(ToggleButton)findViewById(R.id.toggleButton);
        mNfcSwitch=(ToggleButton)findViewById(R.id.TB_nfcswitch);
        mToggleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 当按钮第一次被点击时候响应的事件
                if (mToggleButton.isChecked()) {

                    Toast.makeText(MainActivity.this, "关闭NFC", Toast.LENGTH_SHORT).show();
                    disableReaderMode();

                  /*  String source="24D77A698FCF51F154C75172E30CD36B";
                    try {
                        byte[] res=LoyaltyCardReader.decrypt(LoyaltyCardReader.AES_PASSWORD, source);
                        Log.i(TAG, "decode"+new String(res));
                    }catch (Exception e){
                        Log.i(TAG, "error");
                    }*/
                }
                // 当按钮再次被点击时候响应的事件
                else {
                    Toast.makeText(MainActivity.this, "开启NFC", Toast.LENGTH_SHORT).show();
                    enableReaderMode();
                }
            }
        });

        boolean misallowed;
        misallowed=((UserManager) getApplicationContext().getSystemService(Context.USER_SERVICE))
                .hasUserRestriction(UserManager.DISALLOW_OUTGOING_BEAM);
        Toast.makeText(MainActivity.this, "NFC"+misallowed, Toast.LENGTH_SHORT).show();

        NfcManager nfcmanager = (NfcManager)getApplicationContext().getSystemService(Context.NFC_SERVICE);
        final NfcAdapter nfcadapter=nfcmanager.getDefaultAdapter();
        if(nfcadapter!=null&&nfcadapter.isEnabled()){
            mNfcSwitch.setChecked(true);
        }
        else{
            mNfcSwitch.setChecked(false);

        }
        mNfcSwitch.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 当按钮第一次被点击时候响应的事件
                if (mNfcSwitch.isChecked()) {
                    startActivityForResult(new Intent(Settings.ACTION_NFCSHARING_SETTINGS),1);
                    Toast.makeText(MainActivity.this, "close NFC", Toast.LENGTH_SHORT).show();
                }
                // 当按钮再次被点击时候响应的事件
                else {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    Toast.makeText(MainActivity.this, "开启NFC", Toast.LENGTH_SHORT).show();

                }
            }
        });

        enableReaderMode();
    }


    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mToggleButton.isChecked()==false){
            enableReaderMode();
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        Activity activity = this;
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, mLoyaltyCardReader, READER_FLAGS, null);
        }
        else{
            Toast.makeText(MainActivity.this, "没有NFC！", Toast.LENGTH_SHORT).show();

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.disableReaderMode(this);
        }
        else{
            Toast.makeText(MainActivity.this, "没有NFC！", Toast.LENGTH_SHORT).show();

        }
        if(nfc.isEnabled()==false){

            Toast.makeText(MainActivity.this, "NFC未开启！", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onAccountReceived() {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAccountField.setText(sb.toString());
            }
        });
    }
}
