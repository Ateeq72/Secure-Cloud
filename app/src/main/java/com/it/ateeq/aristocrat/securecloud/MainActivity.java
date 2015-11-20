package com.it.ateeq.aristocrat.securecloud;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;

import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.logging.LogRecord;
import com.it.ateeq.aristocrat.securecloud.uploadActivity;
import com.owncloud.android.lib.resources.files.DownloadRemoteFileOperation;

public class MainActivity extends DrawerActivity implements OnRemoteOperationListener, OnDatatransferProgressListener

{

    public static FilesArrayAdapter mFilesAdapter;
    Button refreshbtn;
    private static  String LOG_TAG = MainActivity.class.getCanonicalName();
    public static ProgressBar uploadprogress;
    static AlertDialog.Builder alert;
    static TextView downloadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acitvity_view);


                    // Your base layout here

        alert = new AlertDialog.Builder(this);


        mFilesAdapter = new FilesArrayAdapter(this, R.layout.list_item);
        ((ListView)findViewById(R.id.listView_items_oncloud)).setAdapter(mFilesAdapter);

        uploadprogress = (ProgressBar) findViewById(R.id.progressBar_download);
        downloadView = (TextView) findViewById(R.id.textView_downloadView);

        uploadActivity uact = new uploadActivity();
        uact.startRefresh();



        refreshbtn = (Button) findViewById(R.id.button_refresh);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Refreshing!",Toast.LENGTH_SHORT).show();
                uploadActivity uact = new uploadActivity();
                uact.startRefresh();
                Toast.makeText(MainActivity.this,"Done!",Toast.LENGTH_SHORT).show();




            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startDownload(String filePath, String targetDirectory) {
        DownloadRemoteFileOperation downloadOperation = new DownloadRemoteFileOperation(filePath, targetDirectory);
        downloadOperation.addDatatransferProgressListener(MainActivity.this);
        downloadOperation.execute( ownCloud_Credentials_Activity.mClient, this,ownCloud_Credentials_Activity.mHandler);
    }


    @Override
    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileAbsoluteName) {

        final long percentage = (totalToTransfer > 0 ? totalTransferredSoFar * 100 / totalToTransfer : 0);

        Log.d(LOG_TAG, "progressRate " + percentage);


        ownCloud_Credentials_Activity.mHandler.post(new Runnable() {

            @Override
            public void run() {

                int progressint = Integer.parseInt(String.valueOf(percentage));
                uploadprogress.setProgress(progressint);
                downloadView.setText("Downloading : " + FilesArrayAdapter.selectedOnCloudFile);
            }
        });
    }

    public void showalertbox(String status)
    {
        alert.setTitle("Download Status:");
        alert.setMessage(status);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (operation instanceof DownloadRemoteFileOperation) {
            if (result.isSuccess()) {
                // do your stuff here
                showalertbox("Download Success!");
                downloadView.setText("");
            }
            else{
                showalertbox("Download Failed!");
                downloadView.setText("");
            }
        }

    }
}










