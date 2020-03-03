package com.nick.simplereadwrite;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvHello;
    Button buttonRead;
    Button buttonWrite;

    private final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("Read & Write permission")
                        .withMessage("Both read and write permission are needed to make the app works.")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_launcher)
                        .build();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

        tvHello = findViewById(R.id.tvHello);
        buttonRead = findViewById(R.id.buttonRead);
        buttonWrite = findViewById(R.id.buttonWrite);

        buttonRead.setOnClickListener(this);
        buttonWrite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRead:
                tvHello.setText(String.format("%s", readFile()));
                break;
            case R.id.buttonWrite:
                File file = new File(Environment.getExternalStorageDirectory(), "oem/media/");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Toast.makeText(this, "Directory not created", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {

                    String strIncrement = readFile();
                    if (!TextUtils.isEmpty(strIncrement)) {
                        int total = Integer.parseInt(strIncrement.replace("Hello ", ""))  + 1;
                        strIncrement = "" + total;
                    } else {
                        strIncrement = "1";
                    }

                    File gpxfile = new File(file, "test1.txt");
                    if (!gpxfile.exists()) {
                        gpxfile.createNewFile();
                    }
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(String.format(Locale.US, "Hello %s", strIncrement));
                    writer.flush();
                    writer.close();
                    tvHello.setText(readFile());
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String readFile() {
        File fileEvents = new File(Environment.getExternalStorageDirectory() + "/oem/media/test1.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return text.toString();
    }
}
