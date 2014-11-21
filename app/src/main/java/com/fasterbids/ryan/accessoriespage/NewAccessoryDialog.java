package com.fasterbids.ryan.accessoriespage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Ryan on 10/23/2014.
 */
public class NewAccessoryDialog extends DialogFragment  {

    public NewAccessoryDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_accessory_type_dialog, null);
        builder.setView(view)
                .setPositiveButton("add",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText myTitle = (EditText) view.findViewById(R.id.acc_title);
                                Spinner mySpinner = (Spinner) view.findViewById(R.id.acc_type);
                                String title = myTitle.getText().toString();
                                String type = mySpinner.getSelectedItem().toString();
//                                AccessoryType accT = new AccessoryType(null, null, null, null, null, null, null, null, null, null);
//                                accT.addAccessoryType(title, type, true);
                                AccessoryType accT = AccessoryType.addAccessoryType(title, type, true);
                                getDialog().dismiss();
                            }
                        })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getDialog().cancel();
                            }
                        });
        return builder.create();
    }

}
