package app.nevvea.nomnom.util;

import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;

/**
 * Created by Anna on 6/5/16.
 */
public class DialogBuilder {

    public static AlertDialog buildAlert(Context context, String msg) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(msg)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        //create alert dialog
        return  alertDialogBuilder.create();
    }
    


}
