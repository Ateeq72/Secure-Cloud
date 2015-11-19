package com.it.ateeq.aristocrat.securecloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;

public class ownCloud_Credentials_Activity extends AppCompatActivity {
    public static String server = "";
    public static String user = "";
    public static String passt = "";
    Button setbtn;
    public static  Handler mHandler;
    public static  OwnCloudClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        setbtn = (Button) findViewById(R.id.button_set_credentials);
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setcredentials();
            }
        });
    }

    private void setcredentials() {

        EditText serverip = (EditText) findViewById(R.id.editText_server_address);
        EditText usered = (EditText) findViewById(R.id.editText_server_user_name);
        EditText pass = (EditText) findViewById(R.id.editText_server_password);
        server = serverip.getText().toString();
        user =  usered.getText().toString();
        passt = pass.getText().toString();
        if(!serverip.getText().toString().equals("") && !usered.getText().toString().equals("") && !pass.getText().toString().equals("")  )
        {
            mHandler = new Handler() ;
            Uri serverUri = Uri.parse(serverip.getText().toString());
            mClient = OwnCloudClientFactory.createOwnCloudClient(serverUri, this, true);
            mClient.setCredentials(
                    OwnCloudCredentialsFactory.newBasicCredentials(
                            usered.getText().toString(),
                            pass.getText().toString()
                    )
            );
            startActivity(new Intent(this,MainActivity.class));
        }
        else{
            Toast.makeText(this, "Please check your Credentials!", Toast.LENGTH_LONG).show();
        }
    }
}
