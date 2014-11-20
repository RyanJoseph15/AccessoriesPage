package com.fasterbids.ryan.accessoriespage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ryan on 11/13/2014.
 */
public class AccessoryType {
    String id;
    View view;
    String type;
    TextView title;
    TextView subTitleText;
    TextView subTitleAmount;
    TextView add;
    LinearLayout container;
    RelativeLayout ccBox;
    LinearLayout row;
    View line;
    ArrayList<Accessory> accessoryList = new ArrayList<Accessory>();

    public AccessoryType(View mView, String mType, TextView mTitle, TextView mSubTitleText,
                         TextView mSubTitleAmount, TextView mAdd, LinearLayout mContainer,
                         RelativeLayout mCCBox, LinearLayout row, View line) {
        this.view = mView;
        this.type = mType;
        this.title = mTitle;
        this.subTitleText = mSubTitleText;
        this.subTitleAmount = mSubTitleAmount;
        this.add = mAdd;
        this.container = mContainer;
        this.ccBox = mCCBox;
        this.row = row;
        this.line = line;

        this.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewItemDialog dialog = new NewItemDialog(container);
                dialog.show(AccessoriesFragment.fmanager, "NewItemDialog");
            }
        });
        this.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (AccessoriesFragment.adminPermission) {
                /* display are you sure to delete dialog */
                    removeAccessoryType(AccessoryType.this);
                    AccessoriesFragment.SQLiteHelper.removeAccT(AccessoryType.this);
                    return true;
                }
                return false;
            }
        });
    }

    public static AccessoryType MakeAccessoryType(String mTitle, String mType) {
        LayoutInflater inflater = (LayoutInflater) AccessoriesFragment.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout row = (LinearLayout) AccessoriesFragment.myView.findViewById(R.id.accLinLayout);
        View acc = inflater.inflate(R.layout.accessory_type_layout, row, false);
        View line = inflater.inflate(R.layout.line_divider, row, false);

        //create accessoryType object
        TextView title = (TextView) acc.findViewById(R.id.title);
        title.setText(mTitle);
        TextView subTitleText = (TextView) acc.findViewById(R.id.sub_title_text);
        TextView subTitleAmount = (TextView) acc.findViewById(R.id.sub_title_amount);
        TextView add = (TextView) acc.findViewById(R.id.add_item_button);
        if (!AccessoriesFragment.adminPermission) {
            add.setVisibility(View.INVISIBLE);
        }
        LinearLayout container = (LinearLayout) acc.findViewById(R.id.linear_layout);
        RelativeLayout ccBox = (RelativeLayout) acc.findViewById(R.id.sub_title);
        AccessoryType newAccT = new AccessoryType(acc, mType, title, subTitleText, subTitleAmount, add, container, ccBox, row, line);
        newAccT.id = mTitle.toLowerCase().replaceAll(" ", "");
        return newAccT;
    }

    public static boolean PlaceAccTypeInAccTypeList(String title, AccessoryType accT) {
        boolean okayToAdd = true;
        for (AccessoryType accType : AccessoriesFragment.AccessoryList) {
            if (accType.id.equals(accT.id)) {
                okayToAdd = false;
            }
        }
        if (okayToAdd) {
            AccessoriesFragment.AccessoryList.add(accT);
        }
        return okayToAdd;   // if the acc was added
    }

    public static void AddAccTypeToLinLayout(AccessoryType accT) {
        if (accT.type.equals("Linear ft")) {
            accT.ccBox.setVisibility(View.VISIBLE);
        } else {
            accT.ccBox.setVisibility(View.INVISIBLE);
        }
        //add views
        int index = (AccessoriesFragment.AccessoryList.size() - 1) * 2;
        // -1 to account for self (already added above)
        // *2 to account for dividers
        accT.row.addView(accT.view, index);
        accT.row.addView(accT.line, index + 1);
    }

    public static AccessoryType addAccessoryType(String mTitle, String mType, boolean ADD) {
        // main call to add acc type
        AccessoryType accType = MakeAccessoryType(mTitle, mType);
        boolean acceptedAccType = PlaceAccTypeInAccTypeList(mTitle, accType);
        if (acceptedAccType) {
            if (ADD) AccessoriesFragment.SQLiteHelper.addType(accType);
            AddAccTypeToLinLayout(accType);
            return accType;
        } else {
            Toast toast = Toast.makeText(AccessoriesFragment.context, "Accessory: \"" + mTitle + "\" already in list.", Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }

        // TODO: add to pricing linear layout
        
    }

    public static void removeAccessoryType(AccessoryType accT) {
        accT.row.removeView(accT.view);
        accT.row.removeView(accT.line);
        AccessoriesFragment.AccessoryList.remove(accT);
    }
}
