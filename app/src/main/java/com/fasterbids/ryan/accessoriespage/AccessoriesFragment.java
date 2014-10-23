package com.fasterbids.ryan.accessoriespage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class AccessoriesFragment extends Fragment {

    ImageView adminButton;
    static TextView addTrimButton;
    static Context context;
    static boolean adminPermission = false;
    LinearLayout myView;    //TODO: not a permanent solution
    static TextView saveButton;
    static TextView addAccButton;

    //Generic class for layout items
    public class ListItem {
        String type;
        RelativeLayout obj;
        TextView title;
        RelativeLayout ccContainer;
        EditText count;
        TextView cost;
        public ListItem(String type, RelativeLayout obj, TextView title, RelativeLayout ccContainer, EditText count, TextView cost) {
            this.type = type;
            this.obj = obj;
            this.title = title;
            this.ccContainer = ccContainer;     //count cost container
            this.count = count;
            this.cost = cost;
        }
    }

    static ArrayList<ListItem> TrimList = new ArrayList<ListItem>();

/* Methods below here
----------------------------------------------------------------------------------------------------
 */


    public static AccessoriesFragment newInstance() {
        AccessoriesFragment fragment = new AccessoriesFragment();
        return fragment;
    }
    public AccessoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.accessories_fragment, container, false);
        context = getActivity();

        addTrimButton = (TextView) v.findViewById(R.id.add_trim_button);
        addTrimButton.setOnClickListener(addButtons);

        addAccButton = (TextView) v.findViewById(R.id.add_acc);

        saveButton = (TextView) v.findViewById(R.id.save_changes);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAdminPermission();
                saveState();    //doesn't do anything yet
            }
        });

        adminButton = (ImageView) v.findViewById(R.id.admin_button);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Password confirm for admin permissions
                if (!adminPermission) {
                    PasswordDialog dialog = new PasswordDialog();
                    dialog.show(getFragmentManager(), "passwordDialog");
                } else {
                    revokeAdminPermission();
                }
            }
        });

        RelativeLayout trim1 = (RelativeLayout) v.findViewById(R.id.trim1);
        TextView trim1Button = (TextView) v.findViewById(R.id.trim1_button);
        RelativeLayout trim1ccContainer = (RelativeLayout) v.findViewById(R.id.trim1_ccContainer);
        EditText trim1Count = (EditText) v.findViewById(R.id.trim1_count);
        TextView trim1Cost = (TextView) v.findViewById(R.id.trim1_cost_amount);
        TrimList.add(new ListItem("Trim", trim1, trim1Button, trim1ccContainer, trim1Count, trim1Cost));

        myView = (LinearLayout) v.findViewById(R.id.trim_linear_layout);

        for (ListItem item : TrimList) {
            //fail-safe check for Trim Type
            if (item.type.equals("Trim")) {
                item.title.setOnClickListener(titleButtons);
            }
        }

        return v;
    }

    View.OnClickListener titleButtons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO
            //find the associated "subtext" to the "title"
            //Log.d("v: ", v.toString());
            TextView count = null;
            for (ListItem item : TrimList) {
                //Log.d("item.title: ", item.title.toString());
                if (item.type.equals("Trim") && item.title.equals(v)) {
                    Log.d("Trim && equal", "match");
                    count = item.count;
                } else if (item.type.equals("Trim") &&
                        !item.title.equals(v) &&
                        Integer.valueOf(item.count.getText().toString()) != 0) {
                    //if there is another trim that is selected, we want to reset all it's values
                    Log.d("Trim && !equal && selected", "match");
                    item.title.setBackgroundColor(getResources().getColor(R.color.wallet_highlighted_text_holo_dark));
                    item.count.setVisibility(View.INVISIBLE);
                    item.count.setText("0");
                }
            }
            v.setBackground(getResources().getDrawable(R.drawable.border_blue_filled));
            if (count != null) {
                //increment the count
                count.setText(String.valueOf(Integer.parseInt(count.getText().toString()) + 1));
            }
            //and have the "subtext" appear when "title" is clicked.
            //if not already selected, turn blue
            //add 1 to subtext
        }
    };

    View.OnClickListener addButtons = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /* layout is as follows:
                relative layout - new item
                    edit text - count
                    text view - title
                    relative layout - pricing
                        edit text - price
                        text view - per unit
             */
            //dialog here would be good
            String type = "Trim";   //TODO: make not hard coded
            RelativeLayout ccContainer = new RelativeLayout(context);
            LinearLayout parentLinearLayout = (LinearLayout) myView.findViewById(R.id.trim_linear_layout);   //TODO: make not hard coded
            NewItemDialog dialog = new NewItemDialog(type, ccContainer, parentLinearLayout);
            dialog.show(getFragmentManager(), "newItemDialog");
        }
    };

    View.OnClickListener addAccessory = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /* layout is as follows:
                relative layout
                        relative layout
                            textview - title for new accessory
                            relative layout - additional information (like lin. ft)
                                textview - title of add. info
                                textview - value of add. info
                        horizontalscrollview
                            linear layout
                                relative layout - new item
                                ...


             */
        }
    };

    public static void grantAdminPermission() {
        //Called from PasswordDialog.java
        //This is where we will make all of the admin features (editing) visible
        Toast toast = Toast.makeText(context, "admin", Toast.LENGTH_SHORT);
        toast.show();

        for (ListItem item : TrimList) {
            item.ccContainer.setVisibility(View.VISIBLE);
        }
        addAccButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        addTrimButton.setVisibility(View.VISIBLE);
        adminPermission = true;
    }

    public static void revokeAdminPermission() {

        for (ListItem item : TrimList) {
            item.ccContainer.setVisibility(View.INVISIBLE);
        }
        addAccButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        addTrimButton.setVisibility(View.INVISIBLE);
        adminPermission = false;
    }

    private void saveState() {
        //save the state of the arrayList
    }

    public static void makeNewItem(String mType, EditText mName, RelativeLayout ccContainer, EditText mCost, Spinner mUnits, LinearLayout parent) {
        /*
        //Generic class for layout items
        public class ListItem {
            String type;
            RelativeLayout obj;
            TextView title;
            RelativeLayout ccContainer;
            EditText count;
            TextView cost;
            public ListItem(String type, RelativeLayout obj, TextView title, RelativeLayout ccContainer, EditText count, TextView cost) {
                this.type = type;
                this.obj = obj;
                this.title = title;
                this.ccContainer = ccContainer;     //count cost container
                this.count = count;
                this.cost = cost;
            }
        }
         */

        //use layout inflater


        RelativeLayout obj = new RelativeLayout(context);
        TextView title = (TextView) mName;
        EditText count = new EditText(context);
        TextView cost = (TextView) mCost;
        //TextView units = (TextView) mUnits;

        //need to layout the components now:
        //obj is the super Relative Layout
/*
        RelativeLayout.LayoutParams superRelative = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        superRelative.setMargins(10,10,10,10);

        RelativeLayout.LayoutParams containerRelative = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        obj.addView(count, superRelative);
        obj.addView(title, superRelative);
        obj.addView(ccContainer, superRelative);

        ccContainer.addView(cost, containerRelative);
        ccContainer.addView(units, containerRelative);
 */
        if (parent == null) {
            Toast toast = Toast.makeText(context, "parent null", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "parent not null", Toast.LENGTH_SHORT);
            toast.show();
        }



    }

}
