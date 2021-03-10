package com.wpavelev.museneurofeedback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.widget.Toast.makeText;

/**
 * Created by Vlad on 22.05.2018.
 */

public class Logger {

    String filename = "";
    boolean isLogging = false;


    Context context;
    private boolean logReady = false;

    public Logger(Context context) {
        this.context = context;
    }



    public final static String FILE_FOLDER = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "MuseLogger" + File.separator;


    public static final String EX = ".csv";


    private File relativeAlpha;
    private FileWriter writer;


    public void initLog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Titel Dataset");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogBuilder.setView(input);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                filename = input.getText().toString() + EX;
                logReady = true;
                initWriters();
            }
        });
        dialogBuilder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();


    }


    public boolean isLogReady() {
        return logReady;
    }



    public void stopLogging() {
        Toast.makeText(context, "Log Beendet, Speichere Datei!", Toast.LENGTH_SHORT).show();
       closeWriters();
    }

    public void pauseRecord() {
        Toast.makeText(context, "Log pausiert!", Toast.LENGTH_SHORT).show();
        isLogging = false;
    }

    public void record() {
        Toast.makeText(context, "Log gestartet", Toast.LENGTH_SHORT).show();
        isLogging = true;
    }

    public void writePacket(double[] workingBuffer) {

        if (isLogging) {

            if (writer != null) {
                try {
                    writer.write(
                            getTimestamp() + ";" +
                                    workingBuffer[0] + ";" +
                                    workingBuffer[1] + ";" +
                                    workingBuffer[2] + ";" +
                                    workingBuffer[3] + "\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }



    private boolean initWriters() {
        File dir = makeDir();
        long currentTime = System.currentTimeMillis();

        relativeAlpha = new File(dir, filename);


        try {
            writer = new FileWriter(relativeAlpha);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean closeWriters() {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        makeText(context, "Dataset: " + filename, Toast.LENGTH_SHORT).show();
        return true;
    }

    private File makeDir() {
        File dir = new File(FILE_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    private String getTimestamp() {
        return System.currentTimeMillis() + "";
    }



}
