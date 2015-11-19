/* ownCloud Android Library is available under MIT license
 *   Copyright (C) 2015 ownCloud Inc.
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 *   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 *   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 *
 */
package com.it.ateeq.aristocrat.securecloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.owncloud.android.lib.resources.files.RemoteFile;

public class FilesArrayAdapter extends ArrayAdapter<RemoteFile> {

    public static String selectedOnCloudFile;

    public FilesArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView textView = (TextView)super.getView(position, convertView, parent);
        textView.setText(getItem(position).getRemotePath());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOnCloudFile = textView.getText().toString();
                Toast.makeText(getContext(),selectedOnCloudFile + " selected",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                b.setTitle("Please Enter Local Directory to Download ");
                final EditText input = new EditText(getContext());
                input.setText(Environment.getExternalStorageDirectory().getPath() + "/owncloud" + selectedOnCloudFile);
                b.setView(input);
                b.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // SHOULD NOW WORK

                        String selectedOnLocalFile = input.getText().toString();
                        MainActivity a = new MainActivity();
                        a.startDownload(selectedOnCloudFile,selectedOnLocalFile);

                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.create().show();
            }
        });
        return textView;
    }
}
