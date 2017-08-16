package com.example.bigrepo.roommapper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.example.bigrepo.roommapper.TouchImageView;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ArrayList<String> data = new ArrayList<String>();

    private EditText roomNumber;
    private EditText corridorNumber;
    private EditText logOutput;
    private Button submitButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomNumber = (EditText) findViewById(R.id.editTextRoom);
        corridorNumber = (EditText) findViewById(R.id.editTextCorridor);
        logOutput = (EditText) findViewById(R.id.editTextLog);
        submitButton = (Button) findViewById(R.id.buttonSubmit);
        radioGroup = (RadioGroup) findViewById(R.id.roomConnectionRadioId);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                radioButton = (RadioButton) findViewById(i);
               switch (radioButton.getId()){


                    case R.id.radioButtonUp :
                        logOutput.setText("Up");
                        data.add("Up");
                        break;
                    case R.id.radioButtonDown :
                        logOutput.setText("Down");
                        data.add("Down");
                        break;
                    case R.id.radioButtonLeft:
                        logOutput.setText("Left");
                        data.add("Left");
                        break;
                    case R.id.radioButtonRight:
                        logOutput.setText("Right");
                        data.add("Right");
                        break;
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(MainActivity.this);
                try {
                    saveToFile("GmitRooms.cql", data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveToFile(String fileName, ArrayList<String> data) throws IOException {
        StringBuilder dataToSave = new StringBuilder();
        for (String s : data) {
            dataToSave.append(s);
            dataToSave.append("\n");
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        FileOutputStream outFile = new FileOutputStream(file);
        //FileOutputStream outFile = openFileOutput(fileName, MODE_APPEND);
        outFile.write(dataToSave.toString().getBytes());
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
