package com.fasterbids.ryan.accessoriespage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Ryan on 10/16/2014.
 */
public class NewItemDialog extends DialogFragment {

    String type;
    RelativeLayout ccContainer;
    LinearLayout parent;

    public NewItemDialog(String type, RelativeLayout ccContainer, LinearLayout parent){
        this.type = type;
        this.ccContainer = ccContainer;
        this.parent = parent;
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_item_dialog, null);
        builder.setView(view)
                .setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText name = (EditText) view.findViewById(R.id.item_name_value);
                                EditText cost = (EditText) view.findViewById(R.id.item_cost_value);
                                Spinner units = (Spinner) view.findViewById(R.id.item_units_value);
                                AccessoriesFragment.makeNewItem(type, name, ccContainer, cost, units, parent);
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
