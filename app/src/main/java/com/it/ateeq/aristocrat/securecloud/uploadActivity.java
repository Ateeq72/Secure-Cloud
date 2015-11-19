package com.it.ateeq.aristocrat.securecloud;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadRemoteFileOperation;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.RemoteFile;
import com.owncloud.android.lib.resources.files.RemoveRemoteFileOperation;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class uploadActivity extends DrawerActivity implements OnRemoteOperationListener, OnDatatransferProgressListener  {

    private static final int FILE_CODE = 0;
    private static  String LOG_TAG = uploadActivity.class.getCanonicalName();

    Button uploadbtn;
    Button browsebtn;
    TextView fileselected;
    ProgressBar uploadprogress;
    static File selectedFile;
    static Uri uri;


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

                if (selectedFile != null) {
                    startUpload();

                } else {
                    Toast.makeText(getBaseContext(), "Please Select a File", Toast.LENGTH_SHORT).show();
                }

            }
        });

        browsebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickfile();

            }
        });

    }

    void pickfile()
    {
        Intent i = new Intent(this, FilePickerActivity.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    selectedFile = new File(uri.getPath());
                    fileselected = (TextView) findViewById(R.id.textView_file_selected);
                    fileselected.setText(selectedFile.getPath());

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



                @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (!result.isSuccess()) {
            Toast.makeText(this,  "Operation Failed :( ", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, result.getLogMessage(), result.getException());

        } else if (operation instanceof ReadRemoteFolderOperation) {
            onSuccessfulRefresh((ReadRemoteFolderOperation)operation, result);

        } else if (operation instanceof UploadRemoteFileOperation ) {
           onSuccessfulUpload((UploadRemoteFileOperation) operation, result);

        } else if (operation instanceof RemoveRemoteFileOperation) {
            onSuccessfulRemoteDeletion((RemoveRemoteFileOperation)operation, result);

        } else if (operation instanceof DownloadRemoteFileOperation) {
            onSuccessfulDownload((DownloadRemoteFileOperation)operation, result);

        } else {
            Toast.makeText(this,"Operation Completed Successfully! :) ", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSuccessfulRefresh(ReadRemoteFolderOperation operation, RemoteOperationResult result) {
        MainActivity.mFilesAdapter.clear();
        List<RemoteFile> files = new ArrayList<RemoteFile>();
        for(Object obj: result.getData()) {
            files.add((RemoteFile) obj);
        }
        if (files != null) {
            Iterator<RemoteFile> it = files.iterator();
            while (it.hasNext()) {
                MainActivity.mFilesAdapter.add(it.next());
            }
            MainActivity.mFilesAdapter.remove(MainActivity.mFilesAdapter.getItem(0));
        }
        MainActivity.mFilesAdapter.notifyDataSetChanged();
    }

    private void onSuccessfulUpload(UploadRemoteFileOperation operation, RemoteOperationResult result) {

        if(result.isSuccess()) {
            Toast.makeText(uploadActivity.this,"Upload Success!",Toast.LENGTH_SHORT).show();
            uploadprogress = (ProgressBar) findViewById(R.id.progressBar_progressbar);
            uploadprogress.setProgress(0);
            fileselected.setText("No file Selected!");
            AlertDialog.Builder alert = new AlertDialog.Builder(uploadActivity.this);
            alert.setTitle("Upload Success! :)");
            alert.setMessage("Want to upload another file?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            });
            alert.show();
        }
        else
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(uploadActivity.this);
            alert.setTitle("Upload Failed ! :( ");
            alert.setMessage("Unknown Reason");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }

    }


    private void onSuccessfulRemoteDeletion(RemoveRemoteFileOperation operation, RemoteOperationResult result) {
        startRefresh();
        uploadprogress = (ProgressBar) findViewById(R.id.progressBar_progressbar);
        if (uploadprogress != null) {
            uploadprogress.setProgress(0);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSuccessfulDownload(DownloadRemoteFileOperation operation, RemoteOperationResult result) {


    }

    public void startRefresh() {
        ReadRemoteFolderOperation refreshOperation = new ReadRemoteFolderOperation(FileUtils.PATH_SEPARATOR);
        refreshOperation.execute(ownCloud_Credentials_Activity.mClient, this, ownCloud_Credentials_Activity.mHandler);
    }



    private void startUpload() {
        String remotePath =  FileUtils.PATH_SEPARATOR + selectedFile.getName();
        String mimeType = "content";
        UploadRemoteFileOperation uploadOperation = new UploadRemoteFileOperation(selectedFile.getAbsolutePath(), remotePath, mimeType);
        uploadOperation.addDatatransferProgressListener(this);
        uploadOperation.execute(ownCloud_Credentials_Activity.mClient, this, ownCloud_Credentials_Activity.mHandler);
        fileselected.setText(String.format("Uploading : %s", selectedFile.getName()));
    }


    @Override
    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, final String fileAbsoluteName) {

        final long percentage = (totalToTransfer > 0 ? totalTransferredSoFar * 100 / totalToTransfer : 0);

        Log.d(LOG_TAG, "progressRate " + percentage);


        ownCloud_Credentials_Activity.mHandler.post(new Runnable() {

            @Override
            public void run() {

                int progressint = Integer.parseInt(String.valueOf(percentage));
                uploadprogress = (ProgressBar) findViewById(R.id.progressBar_progressbar);
                uploadprogress.setProgress(progressint);
            }
        });

    }






}
