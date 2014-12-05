package com.fasterbids.ryan.accessoriespage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ryan on 10/9/2014.
 */
public class PasswordDialog extends DialogFragment implements DialogInterface.OnDismissListener {

    AccessoriesFragment frag;

    public PasswordDialog(AccessoriesFragment frag) {
        this.frag = frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.password_dialog, null);
        builder.setView(view)
                .setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                final EditText eText = (EditText) view.findViewById(R.id.passphrase_box);
                                checkPassphrase(eText);
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

    private void checkPassphrase(EditText eText) {
        String mess = "neither";
        Toast toast;
        Toast toast2 = Toast.makeText(getActivity(), "\"" + eText.getText().toString() + "\"", Toast.LENGTH_SHORT);
        //toast2.show();
        if (eText.getText().toString().equals("p")) {
            mess = "dismiss";
            toast  = Toast.makeText(getActivity(), mess, Toast.LENGTH_SHORT);
            getDialog().dismiss();
            frag.grantAdminPermission();
        } else {
            mess = "cancel";
            toast  = Toast.makeText(getActivity(), mess, Toast.LENGTH_SHORT);
            getDialog().cancel();
        }
        //toast.show();
    }

}
