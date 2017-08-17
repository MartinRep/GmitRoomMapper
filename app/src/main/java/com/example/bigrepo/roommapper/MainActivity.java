package com.example.bigrepo.roommapper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ArrayList<String> data = new ArrayList<String>();
    private final String fileName = "gmitRooms.cql";
    String[] direction = new String[2];
    String roomNumber;

    private EditText roomNumberId;
    private EditText corridorNumberId;
    private EditText logOutput;
    private Button submitButton;
    private RadioGroup directionRadioGroup;
    private RadioButton directionRadioButton;
    private Button undoButton;
    private RadioGroup pictureRadioGroup;
    private RadioButton  pictureRadioButton;
    private TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomNumberId = (EditText) findViewById(R.id.editTextRoom);
        corridorNumberId = (EditText) findViewById(R.id.editTextCorridor);
        logOutput = (EditText) findViewById(R.id.editTextLog);
        submitButton = (Button) findViewById(R.id.buttonSubmit);
        directionRadioGroup = (RadioGroup) findViewById(R.id.roomConnectionRadioId);
        undoButton = (Button) findViewById(R.id.undoButton);
        pictureRadioGroup = (RadioGroup) findViewById(R.id.PictureRadioGroupId);
        touchImageView = (TouchImageView) findViewById(R.id.imageView);

        logOutput.setBackgroundColor(Color.BLACK);
        logOutput.setTextColor(Color.GREEN);

        //Checks for storage permissions and ask for then if needed
        if(!verifyStoragePermissions(MainActivity.this)){
            //display error message for lack of storage permission
        }
        try {
            readFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        directionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                directionRadioButton = (RadioButton) findViewById(i);
                switch (directionRadioButton.getId()){
                    case R.id.radioButtonUp :
                        direction[0] = "up";
                        direction[1] = "down";
                        break;
                    case R.id.radioButtonDown :
                        direction[0] = "down";
                        direction[1] = "up";
                        break;
                    case R.id.radioButtonLeft:
                        direction[0] = "left";
                        direction[1] = "right";
                        break;
                    case R.id.radioButtonRight:
                        direction[0] = "right";
                        direction[1] = "left";
                        break;
                }
            }
        });

        pictureRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                pictureRadioButton = (RadioButton) findViewById(i);
                switch (pictureRadioButton.getId()){
                    case R.id.radioButtonGmap0Id :
                        touchImageView.setImageDrawable(getDrawable(R.drawable.gmit0));
                        break;
                    case R.id.radioButtonGmap1Id :
                        touchImageView.setImageDrawable(getDrawable(R.drawable.gmit1));
                        break;
                    case R.id.radioButtonDgmapId :
                        touchImageView.setImageDrawable(getDrawable(R.drawable.dmaps1));
                        break;
                    case R.id.radioButtonDmap0Id :
                        touchImageView.setImageDrawable(getDrawable(R.drawable.dmap0));
                        break;
                    case R.id.radioButtonDmap1Id :
                        touchImageView.setImageDrawable(getDrawable(R.drawable.dmaps1));
                        break;
                    case R.id.radioButtonDmap2Id :
                        touchImageView.setImageDrawable(getDrawable(R.drawable.dmap2));
                        break;
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String corridorNumber;
                roomNumber = roomNumberId.getText().toString();
                corridorNumber = corridorNumberId.getText().toString();
                if(!roomNumber.toString().equals("") && !corridorNumber.toString().equals("") && direction[0] != null) {
                    StringBuilder unitToSave = new StringBuilder();
                    logOutput.append("Room "+roomNumber+" added.\n");
                    unitToSave.append("CREATE ("+roomNumber+")-[:Access{connection:['"+direction[0]+"']}]->("+corridorNumber+")\n");
                    unitToSave.append("CREATE ("+corridorNumber+")-[:Access{connection:['"+direction[1]+"']}]->("+roomNumber+")\n");
                    data.add(unitToSave.toString());
                    try {
                        saveToFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    roomNumberId.setText("");
                    roomNumberId.requestFocus();
                } else {
                    logOutput.append("Set all parameters!\n");
                }
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(data.size()-1);
                data = new ArrayList<String>(data.subList(0,data.size()-1));
                logOutput.append("Room "+roomNumber.toString()+" removed\n");
                try {
                    saveToFile();
                } catch (IOException e) {
                    logOutput.append(e.toString());
                }

            }
        });
    }

    private void readFromFile() throws IOException{
        String line;
        String lastRoom = "";
        int dataRead = 0;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        FileInputStream inFile = new FileInputStream(file);
        InputStreamReader inStream = new InputStreamReader(inFile);
        BufferedReader inData = new BufferedReader(inStream);
        while ((line = inData.readLine()) != null) {
            data.add(line+"\n");
            dataRead++;
            lastRoom = line.substring(44,line.length()-1);
        }
        logOutput.append(dataRead+" records added.\n");
        inStream.close();
        logOutput.append("Last room added: "+lastRoom+"\n");
    }

    private void saveToFile() throws IOException {
        StringBuilder dataToSave = new StringBuilder();
        for (String s : data) {
            dataToSave.append(s);
            //dataToSave.append("\n");
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        FileOutputStream outFile = new FileOutputStream(file);
        //FileOutputStream outFile = openFileOutput(fileName, MODE_APPEND);
        outFile.write(dataToSave.toString().getBytes());
        outFile.close();
    }

    public static boolean verifyStoragePermissions(Activity activity) {
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
        if (permission != PackageManager.PERMISSION_GRANTED) return false;
        else return true;
    }
}
