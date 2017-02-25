package com.sighs.imputmethod.Overlay;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by stuart on 2/24/17.
 */

public class TouchAnalytics {
    public final static String PARTICIPANTKEY = "participant";
    public final static String SETTINGS = "cashkeyboard";
    private final static String LOGKEY = "SWOOSH_INPUT_TRACKING";
    private final static String OUTPUT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/cashkeyboard/");

    public static void WriteEvent(Context context, MotionEvent event) {
        String file = GetParticipant(context);
        StringBuilder logLine = new StringBuilder();
        logLine.append(DateFormat.format("yyyyy-mm-dd hh:mm:ss", System.currentTimeMillis()));
        logLine.append("- x: ");
        logLine.append(event.getX());
        logLine.append(" , y: ");
        logLine.append(event.getY());
        logLine.append(" => ");
        logLine.append(event.getAction());
        Log.d(LOGKEY ,logLine.toString());
        try {
            FileOutputStream fOut = context.openFileOutput(file , Context.MODE_APPEND);
            logLine.append("\n");
            fOut.write(logLine.toString().getBytes());
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void WriteMessage(Context context, String logkey, String msg) {
        String file = GetParticipant(context);
        StringBuilder logLine = new StringBuilder();
        logLine.append(DateFormat.format("yyyyy-mm-dd hh:mm:ss ", System.currentTimeMillis()));
        logLine.append(logkey);
        logLine.append(" : ");
        logLine.append(msg);
        Log.d(LOGKEY ,logLine.toString());
        try {
            FileOutputStream fOut = context.openFileOutput(file , Context.MODE_APPEND);
            logLine.append("\n");
            fOut.write(logLine.toString().getBytes());
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ClearLog(Context context) {
        String file = GetParticipant(context);
        context.deleteFile(file);
        Log.d(LOGKEY ,"Deleting: " + file);
    }

    public static void ArchiveLog(Context context, String source, String dest) {
        try {
            File dir = new File(OUTPUT_DIR);
            if(!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }

            Calendar calendar = Calendar.getInstance();
            Date time = calendar.getTime();
            long milliseconds = time.getTime();

            // Initiate ZipFile object with the path/name of the zip file.
            dest = OUTPUT_DIR+dest+"_"+milliseconds+".zip";
            ZipFile zipFile = new ZipFile(dest);

            // Open File
            File file = context.getFileStreamPath(source);

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc.
            ZipParameters parameters = new ZipParameters();

            // set compression method to store compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // Add folder to the zip file
            zipFile.addFile(file, parameters);
            Log.d(LOGKEY ,"Archiving: " + source + " to " + dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SetParticipant(Context context, String name) {
        try {
            context.deleteFile(PARTICIPANTKEY);
            FileOutputStream out = context.openFileOutput(PARTICIPANTKEY, Context.MODE_PRIVATE);
            out.write(name.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String GetParticipant(Context context) {
        try {
            FileInputStream in = context.openFileInput(PARTICIPANTKEY);
            byte[] buffer = new byte[1024];
            StringBuffer fileContent = new StringBuffer("");
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            in.close();
            if(fileContent.length() == 0) {
                return "anon";
            }
            return fileContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "anon";
        }
    }
}
