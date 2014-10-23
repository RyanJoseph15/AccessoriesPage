package com.fasterbids.ryan.accessoriespage;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class AccessoriesFragment extends Fragment {

    ImageView adminButton;
    static TextView addTrimButton;
    static Context context;
    static boolean adminPermission = false;
    LinearLayout accTitleLayout;
    static View myView;
    static TextView saveButton;
    static TextView addAccButton;
    static RelativeLayout addAndSave;
    static FragmentManager fmanager;

    //Generic class for layout items
    public static class AccessoryType {
        View view;
        String type;
        TextView title;
        TextView subTitleText;
        TextView subTitleAmount;
        TextView add;
        LinearLayout container;
        RelativeLayout ccBox;
        ArrayList<Accessory> accessoryList = new ArrayList<Accessory>();

        public AccessoryType(View mView, String mType, TextView mTitle, TextView mSubTitleText, TextView mSubTitleAmount, TextView mAdd, LinearLayout mContainer, RelativeLayout mCCBox) {
            this.view = mView;
            this.type = mType;
            this.title = mTitle;
            this.subTitleText = mSubTitleText;
            this.subTitleAmount = mSubTitleAmount;
            this.add = mAdd;
            this.container = mContainer;
            this.ccBox = mCCBox;

            this.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewItemDialog dialog = new NewItemDialog(container);
                    dialog.show(fmanager, "NewItemDialog");
                }
            });

        }
    }
    static ArrayList<AccessoryType> AccessoryList = new ArrayList<AccessoryType>();

    public static class Accessory {
        static EditText count;
        TextView main;
        RelativeLayout ccBox;
        EditText costAmount;
        TextView costUnits;
        public Accessory(EditText mCount, TextView mMain, RelativeLayout mCCBox, EditText mCostAmount, TextView mCostUnits) {
            this.count = mCount;
            this.main = mMain;
            this.ccBox = mCCBox;
            this.costAmount = mCostAmount;
            this.costUnits = mCostUnits;

            this.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: make increment counter
                }
            });
        }
    }

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
        myView = v;

        addAccButton = (TextView) v.findViewById(R.id.add_acc);
        addAccButton.setOnClickListener(addAccessoryType);

        accTitleLayout = (LinearLayout) v.findViewById(R.id.accLinLayout);
        addAndSave = (RelativeLayout) v.findViewById(R.id.add_and_save);

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

        fmanager = getFragmentManager();

        return v;
    }

    View.OnClickListener addAccessoryType = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NewAccessoryDialog dialog = new NewAccessoryDialog();
            dialog.show(getFragmentManager(), "newAccessoryDialog");
        }
    };

    public static void grantAdminPermission() {
        //Called from PasswordDialog.java
        //This is where we will make all of the admin features (editing) visible
        adminPermission = true;
        Toast toast = Toast.makeText(context, "admin", Toast.LENGTH_SHORT);
        toast.show();
        addAccButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);

        for (AccessoryType accType : AccessoryList) {
            accType.add.setVisibility(View.VISIBLE);
            for (Accessory acc : accType.accessoryList) {
                acc.ccBox.setVisibility(View.VISIBLE);
            }
        }

    }

    public static void revokeAdminPermission() {
        adminPermission = false;
        addAccButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        for (AccessoryType accType : AccessoryList) {
            accType.add.setVisibility(View.INVISIBLE);
            for (Accessory acc : accType.accessoryList) {
                acc.ccBox.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void saveState() {
        //save the state of the arrayList
    }

    public static void makeNewItem(EditText mName, EditText mCost, Spinner mUnits, LinearLayout parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //RelativeLayout item = (RelativeLayout) parent.findViewById(R.id.item);
        LinearLayout item = (LinearLayout) parent.findViewById(R.id.linear_layout);
        View acc = inflater.inflate(R.layout.accessory_entry, item, false);

        //create Accessory object
        TextView main = (TextView) acc.findViewById(R.id.button);
        TextView costUnits = (TextView) acc.findViewById(R.id.cost_units);
        EditText count = (EditText) acc.findViewById(R.id.count);
        EditText costAmount = (EditText) acc.findViewById(R.id.cost_amount);
        RelativeLayout ccBox = (RelativeLayout) acc.findViewById(R.id.ccContainer);
        Accessory newAcc = new Accessory(count, main, ccBox, costAmount, costUnits);

        AccessoryType parentType = null;
        for (AccessoryType type : AccessoryList) {
            if (type.container.equals(parent)) {
                parentType = type;
            }
        }
        if (parentType != null) {
            boolean okayToAdd = true;
            for (Accessory accItem : parentType.accessoryList) {
                String toCompare = accItem.main.getText().toString();
                if (toCompare.equals(main.getText().toString())) {
                    okayToAdd = false;
                }
            }

            if (okayToAdd) {
                //final setup
                newAcc.main.setText(mName.getText().toString());
                newAcc.costAmount.setText(mCost.getText().toString());
                newAcc.costUnits.setText(mUnits.getSelectedItem().toString());
                newAcc.ccBox.setVisibility(View.VISIBLE);

                parentType.accessoryList.add(newAcc);
                item.addView(acc);

            }
        }
    }

    public static void addAccessoryType(String mTitle, String mType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout row = (LinearLayout) myView.findViewById(R.id.accLinLayout);
        View acc = inflater.inflate(R.layout.accessory_type_layout, row, false);
        View line = inflater.inflate(R.layout.line_divider, row, false);

        //create accessoryType object
        TextView title = (TextView) acc.findViewById(R.id.title);
        TextView subTitleText = (TextView) acc.findViewById(R.id.sub_title_text);
        TextView subTitleAmount = (TextView) acc.findViewById(R.id.sub_title_amount);
        TextView add = (TextView) acc.findViewById(R.id.add_item_button);
        LinearLayout container = (LinearLayout) acc.findViewById(R.id.linear_layout);
        RelativeLayout ccBox = (RelativeLayout) acc.findViewById(R.id.sub_title);
        AccessoryType newAcc = new AccessoryType(acc, mType, title, subTitleText, subTitleAmount, add, container, ccBox);

        //wouldn't make sense to have more than one title that is the same:
        boolean okayToAdd = true;
        for (AccessoryType accType : AccessoryList) {
            String toCompare = accType.title.getText().toString();
           if (toCompare.equals(mTitle)) {
               okayToAdd = false;
           }
        }
       if (okayToAdd) {
           AccessoryList.add(newAcc);

           //modify values
           newAcc.title.setText(mTitle);
           if (newAcc.type.equals("Linear ft")) {
               newAcc.ccBox.setVisibility(View.VISIBLE);
           } else {
               newAcc.ccBox.setVisibility(View.INVISIBLE);
           }

           //add views
           row.addView(acc);
           row.addView(line);

       } else {
           Toast toast = Toast.makeText(context, "Accessory: \"" + mTitle + "\" already in list.", Toast.LENGTH_SHORT);
           toast.show();
       }



    }

}
