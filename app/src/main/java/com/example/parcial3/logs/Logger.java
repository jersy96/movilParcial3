package com.example.parcial3.logs;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class Logger {
    public static void shortToast(Context context, String message){
        Toast toast = Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        );
        toast.show();
    }

    public static void showAlert(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
                .setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
