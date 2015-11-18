package com.it.ateeq.aristocrat.securecloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class uploadActivity extends DrawerActivity  {
    Button uploadbtn;
    Button browsebtn;
    TextView fileselected;
    ProgressBar uploadprogress;
    static File selectedfile;
    private static final int REQUEST_PICK_FILE = 1;
    private static final int RESULT_OK = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadbtn = (Button) findViewById(R.id.button_upload);
        browsebtn = (Button) findViewById(R.id.button_browse);


        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        browsebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(uploadActivity.this , FilePicker.class);
                startActivityForResult(intent, REQUEST_PICK_FILE);

            }
        });

    }
}
