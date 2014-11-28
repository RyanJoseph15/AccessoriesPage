package com.fasterbids.ryan.accessoriespage;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Accessory {
    String id;  // main text: lower case and stripped of spaces
    String parentTitle;
    View view;
    EditText count;
    TextView title;
    RelativeLayout ccBox;
    EditText costAmount;
    TextView costUnits;
    LinearLayout item;
    boolean selected;

    public Accessory(View view, EditText mCount, TextView mMain, RelativeLayout mCCBox, EditText mCostAmount, TextView mCostUnits, LinearLayout item, boolean selected) {
        this.view = view;
        this.count = mCount;
        this.title = mMain;
        this.ccBox = mCCBox;
        this.costAmount = mCostAmount;
        this.costUnits = mCostUnits;
        this.item = item;
        this.selected = selected;

        this.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Accessory.this.costUnits.getText().toString().equals(" per unit")) {
                    if (Accessory.this.count.getText().toString().equals("")) {
                        Accessory.this.count.setText("1");
                    } else {
                        Accessory.this.count.setText(String.valueOf(Integer.valueOf(Accessory.this.count.getText().toString()) + 1));
                    }
                } else {
                    for (AccessoryType accT : AccessoriesFragment.AccessoryList) {
                        if (accT.container.equals(Accessory.this.item)) {
                            Log.d("accT.title", accT.id);
                            for (Accessory acc : accT.accessoryList) {
                                if (!acc.id.equals(Accessory.this.id)) {
                                    acc.title.setBackgroundColor(AccessoriesFragment.context.getResources().getColor(R.color.common_signin_btn_dark_text_default));
                                    acc.selected = false;
                                    AccessoriesFragment.SQLiteHelper.updateAcc(acc);
                                }
                            }
                            break;
                        }
                    }
                }
                // update the db info for acc
                Accessory.this.selected = true;
                Accessory.this.title.setBackgroundColor(AccessoriesFragment.context.getResources().getColor(R.color.blue));
                AccessoriesFragment.SQLiteHelper.updateAcc(Accessory.this);
            }
        });
        this.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (AccessoriesFragment.adminPermission) {
                /* display are you sure to delete dialog */
                    for (AccessoryType accT : AccessoriesFragment.AccessoryList) {
                        for (Accessory acc : accT.accessoryList) {
                            if (acc.equals(Accessory.this)) {
                                Obliterate(acc, accT);
                                break;
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        this.count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (Accessory.this.costUnits.getText().toString().equals(" per unit")) {
                    if (Accessory.this.count.getText().toString().equals("") || Integer.valueOf(Accessory.this.count.getText().toString()).equals(0)) {
                        Accessory.this.title.setBackgroundColor(AccessoriesFragment.context.getResources().getColor(R.color.common_signin_btn_dark_text_default));
                        Accessory.this.count.setVisibility(View.INVISIBLE);
                        Accessory.this.selected = false;
                    } else {
                        Accessory.this.title.setBackgroundColor(AccessoriesFragment.context.getResources().getColor(R.color.blue));
                        Accessory.this.count.setVisibility(View.VISIBLE);
                        Accessory.this.selected = true;
                    }
                    // update db info for the acc
                    AccessoriesFragment.SQLiteHelper.updateAcc(Accessory.this);
                }
            }
        });
    }

    private void Obliterate(Accessory acc, AccessoryType accT) {
        accT.accessoryList.remove(acc);
        accT.container.removeView(acc.view);
        AccessoriesFragment.SQLiteHelper.removeAcc(acc);
    }

    public static Accessory MakeAccessory(String mName, String mCost, LinearLayout parent, boolean selected) {
        LayoutInflater inflater = (LayoutInflater) AccessoriesFragment.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout item = (LinearLayout) parent.findViewById(R.id.linear_layout);
        View acc = inflater.inflate(R.layout.accessory_entry, item, false);
        //create Accessory object
        TextView main = (TextView) acc.findViewById(R.id.button);
        main.setText(mName);
        TextView costUnits = (TextView) acc.findViewById(R.id.cost_units);
        EditText count = (EditText) acc.findViewById(R.id.count);
        EditText costAmount = (EditText) acc.findViewById(R.id.cost_amount);
        costAmount.setText(mCost);
        RelativeLayout ccBox = (RelativeLayout) acc.findViewById(R.id.ccContainer);
        Accessory newAcc = new Accessory(acc, count, main, ccBox, costAmount, costUnits, item, selected);
        // find parent view
        AccessoryType parentType = null;
        for (AccessoryType type : AccessoriesFragment.AccessoryList) {
            if (type.container.equals(parent)) {
                parentType = type;
            }
        }
        // initialize some values
        if (parentType != null) {
            newAcc.id = newAcc.title.getText().toString().toLowerCase().replaceAll(" ", "");
            String[] unitTypes = AccessoriesFragment.context.getResources().getStringArray(R.array.units_array);
            String type = parentType.type;
            // special alterations per unit system
            if (type.equals("Linear ft")) {
                costUnits.setText(unitTypes[0]);
            } else if (type.equals("Square ft")) {
                costUnits.setText(unitTypes[1]);
            } else {
                costUnits.setText(unitTypes[2]);
            }
        }
        return newAcc;
    }

    public static boolean PlaceAccInAccList(Accessory acc, ArrayList<Accessory> accList) {
        boolean okayToAdd = true;
        for (Accessory Acc : accList) {
            if (Acc.id.equals(acc.id)) {
                /* using the id is a smarter way to check for duplicates */
                okayToAdd = false;
            }
        }
        if (okayToAdd) {
            accList.add(acc);
        }
        return okayToAdd;   // if the acc was added
    }

    public static void AddAccToLinLayout(Accessory acc, LinearLayout parent) {
        acc.item.addView(acc.view);

        // add accessories and calculate the prices
        LayoutInflater inflater = (LayoutInflater) AccessoriesFragment.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // TODO: could be optimized by adding a reference in the acc obj to the accT obj - Later
        AccessoryType parentT = null;
        for (AccessoryType accT : AccessoriesFragment.AccessoryList) {
            if (accT.container.equals(parent)) {
                parentT = accT;
                break;
            }
        }
        if (parentT != null && acc.selected) {
            // we have found the parent type and the accessory is selected
            View item = inflater.inflate(R.layout.cost_item, parentT.costs, false);
            TextView vCost = (TextView) item.findViewById(R.id.cost);
            TextView vTitle = (TextView) item.findViewById(R.id.title);
            TextView vQuant = (TextView) item.findViewById(R.id.quantity);
            vTitle.setText(acc.title.getText().toString());
            float cost = Float.parseFloat(acc.costAmount.getText().toString());
            if (parentT.type.equals("Linear ft") || parentT.type.equals("Square ft")) {
                int amount = Integer.valueOf(parentT.subTitleAmount.getText().toString());
                float fCost = amount * cost;;
                vCost.setText(String.valueOf(fCost));
                vQuant.setText(parentT.subTitleAmount.getText().toString());
            } else if (parentT.type.equals("per unit")) {
                int count = Integer.valueOf(acc.count.getText().toString());
                float fCost = count * cost;
                vCost.setText(String.valueOf(fCost));
                vQuant.setText(acc.count.getText().toString());
            }
            // add it
            parentT.costs.addView(item);
            acc.title.setBackgroundColor(AccessoriesFragment.context.getResources().getColor(R.color.blue));
        }
    }

    public static Accessory AddAccessory(String mName, String mCost, String count, LinearLayout parent, boolean ADD, boolean selected) {
        Accessory acc = MakeAccessory(mName, mCost, parent, selected);
        // find parent type
        AccessoryType parentType = null;
        for (AccessoryType type : AccessoriesFragment.AccessoryList) {
            if (type.container.equals(parent)) {
                parentType = type;
            }
        }
        if (parentType != null) {
            acc.costAmount.setText(mCost);
            acc.count.setText(count);
            boolean acceptedAcc = PlaceAccInAccList(acc, parentType.accessoryList);
            if (acceptedAcc) {
                AddAccToLinLayout(acc, parent);
                acc.parentTitle = parentType.title.getText().toString();
                acc.title.setText(mName);
                if (AccessoriesFragment.adminPermission) acc.ccBox.setVisibility(View.VISIBLE);
                if (ADD) AccessoriesFragment.SQLiteHelper.addAcc(acc);
                return acc;
            } else {
                Toast toast = Toast.makeText(AccessoriesFragment.context, "Accessory: \"" + mName + "\" already in list.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        return null;
    }
    
}
