package com.example.parcial3.logs;

import android.content.Context;
import android.widget.Toast;

public class Logger {
    public static void shortToast(Context context, String message){
        Toast toast = Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        );
        toast.show();
    }
}
