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

    private EditText roomNumberId;
    private EditText corridorNumberId;
    private EditText logOutput;
    private Button submitButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomNumberId = (EditText) findViewById(R.id.editTextRoom);
        corridorNumberId = (EditText) findViewById(R.id.editTextCorridor);
        logOutput = (EditText) findViewById(R.id.editTextLog);
        submitButton = (Button) findViewById(R.id.buttonSubmit);
        radioGroup = (RadioGroup) findViewById(R.id.roomConnectionRadioId);

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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                radioButton = (RadioButton) findViewById(i);
                switch (radioButton.getId()){
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            String roomNumber, corridorNumber;

            @Override
            public void onClick(View view) {
                roomNumber = roomNumberId.getText().toString();
                corridorNumber = corridorNumberId.getText().toString();
                if(!roomNumber.toString().equals("") && !corridorNumber.toString().equals("") && direction.length > 1) {
                    StringBuilder unitToSave = new StringBuilder();
                    logOutput.append("Room "+roomNumber+" added.\n");
                    unitToSave.append("CREATE ("+roomNumber+")-[:Access{connection:['"+direction[0]+"']}]->("+corridorNumber+")\n");
                    unitToSave.append("CREATE ("+corridorNumber+")-[:Access{connection:['"+direction[1]+"']}]->("+roomNumber+")\n");
                    data.add(unitToSave.toString());
//                    CREATE (r208)-[:Access{connection:['right']}]->(c19)
//                            CREATE (c19)-[:Access{connection:['left']}]->(r207)
                    try {
                        saveToFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    roomNumberId.setText("");
                }
            }
        });
    }

    private void readFromFile() throws IOException{
        String line;
        int dataRead = 0;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        FileInputStream inFile = new FileInputStream(file);
        InputStreamReader inStream = new InputStreamReader(inFile);
        BufferedReader inData = new BufferedReader(inStream);
        while ((line = inData.readLine()) != null) {
            data.add(line+"\n");
            dataRead++;
        }
        logOutput.append("Data read from file: "+dataRead);
    }

    private void saveToFile() throws IOException {
        StringBuilder dataToSave = new StringBuilder();
        for (String s : data) {
            dataToSave.append(s);
            //dataToSave.append("\n");
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
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
