package com.embedsky.administrator.mycardemulation;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity  {
    public static Handler dataHandler;
    private TextView data;
    private EditText dataedit;
    private EditText passwordedit;
    public static StringBuilder sb=new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data= (TextView) findViewById(R.id.data_tv);
        dataedit= (EditText) findViewById(R.id.data_edt);
        passwordedit= (EditText)findViewById(R.id.password);
        dataedit.setText(AccountStorage.GetAccount(this));
        dataedit.addTextChangedListener(new AccountUpdater());
        passwordedit.setText(AccountStorage.GetPassword(this));
        passwordedit.addTextChangedListener(new PasswordUpdater());
        dataHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1){
                    data.setText(sb.toString());
                }
                super.handleMessage(msg);
            }
        };
    }


    private class AccountUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String account = s.toString();
            String TAG="test";
            AccountStorage.SetAccount(MainActivity.this, account);                                  //检测文本框变化

           /* String account1 = AccountStorage.GetAccount(MainActivity.this);
            String pwheader = new String(CardService.PASSWORD_HEADER);
            String password1 = AccountStorage.GetPassword(MainActivity.this);
            String all = account1 + pwheader + password1;
            Log.i(TAG, "encode: " + all);
            byte[] accountBytes1 = all.getBytes();
            try {
            String res = CardService.encrypt(CardService.AES_PASSWORD, new String(accountBytes1));
                Log.i(TAG, "encode: " + res);
            }catch (Exception e) {
                Log.i(TAG, "error ");
            }*/
        }
    }
    private class PasswordUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = s.toString();
            AccountStorage.SetPassword(MainActivity.this, password);
        }
    }

}
