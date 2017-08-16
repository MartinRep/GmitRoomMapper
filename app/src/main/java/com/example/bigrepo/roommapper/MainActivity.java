package com.example.bigrepo.roommapper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.example.bigrepo.roommapper.TouchImageView;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] data;

    private EditText roomNumber;
    private EditText corridorNumber;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomNumber = (EditText) findViewById(R.id.editTextRoom);
        corridorNumber = (EditText) findViewById(R.id.editTextCorridor);
        submitButton = (Button) findViewById(R.id.buttonSubmit);

        verifyStoragePermissions(MainActivity.this);
        try {
            saveToFile(getPublicFile("GmitRooms.cql"), data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getPublicFile(String fileName){
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        return file;
    }

    private void saveToFile(File file, String data) throws IOException {
        if (!file.exists()) file.createNewFile();
        FileOutputStream outFile = new FileOutputStream(file);
        outFile.write(data.getBytes());
        outFile.close();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
