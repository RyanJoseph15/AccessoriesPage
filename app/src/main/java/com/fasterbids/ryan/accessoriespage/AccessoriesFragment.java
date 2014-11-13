package com.fasterbids.ryan.accessoriespage;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    static JSONArray jsonAcc = new JSONArray();

    //Generic class for layout items
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
                    NewItemDialog dialog = new NewItemDialog(container, AccessoriesFragment.this);
                    dialog.show(fmanager, "NewItemDialog");
                }
            });
            this.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (adminPermission) {
                        /* display are you sure to delete dialog */
                        removeAccessoryType(AccessoryType.this);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    ArrayList<AccessoryType> AccessoryList = new ArrayList<AccessoryType>();

    public class Accessory {
        String id;  // main text: lower case and stripped of spaces
        View view;
        EditText count;
        TextView main;
        RelativeLayout ccBox;
        EditText costAmount;
        TextView costUnits;
        LinearLayout item;
        public Accessory(View view, EditText mCount, TextView mMain, RelativeLayout mCCBox, EditText mCostAmount, TextView mCostUnits, LinearLayout item) {
            this.view = view;
            this.count = mCount;
            this.main = mMain;
            this.ccBox = mCCBox;
            this.costAmount = mCostAmount;
            this.costUnits = mCostUnits;
            this.item = item;

            this.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("main.onClick", "start");
                    if (Accessory.this.count.getText().toString().equals("")) {
                        v.setBackgroundColor(context.getResources().getColor(R.color.blue));
                        Accessory.this.count.setText("1");
                    } else {
                        Accessory.this.count.setText(String.valueOf(Integer.valueOf(Accessory.this.count.getText().toString()) + 1));
                    }
                }
            });
            this.main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (adminPermission) {
                        /* display are you sure to delete dialog */
                        for (AccessoryType accT : AccessoryList) {
                            for (Accessory acc : accT.accessoryList) {
                                if (acc.equals(Accessory.this)) {
                                    accT.accessoryList.remove(acc);
                                    accT.container.removeView(acc.view);
                                    break;
                                }
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

/* Methods below here
----------------------------------------------------------------------------------------------------
 */


    public AccessoriesFragment newInstance() {
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
            }
        });

        adminButton = (ImageView) v.findViewById(R.id.admin_button);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Password confirm for admin permissions
                if (!adminPermission) {
                    PasswordDialog dialog = new PasswordDialog(AccessoriesFragment.this);
                    dialog.show(getFragmentManager(), "passwordDialog");
                } else {
                    revokeAdminPermission();
                }
            }
        });

        fmanager = getFragmentManager();

        SharedPreferences accPrefs = context.getSharedPreferences("accessories", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = accPrefs.edit();
        boolean hasDb = accPrefs.getBoolean("inited", false);
        if (hasDb) {
            /* we need to load it into the layout */
            Log.d("hasDb", "true");
            if (AccessoryList.isEmpty()) {
                /* new instance of the fragment. popping off stack does not destroy fragment */
                Log.d("AccessoryList", "empty");
                int size = jsonAcc.length();

                for (int i = 0; i < size; i++) {
                    try {
                        JSONObject obj = jsonAcc.getJSONObject(i);
                        String title = obj.getString("title");
                        String type = obj.getString("type");
                        if (title != null && type != null) {
                            //removeAccType(title);
                            addAccessoryType(title, type);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                /* fragment has been around, just need items to make visible? */
                Log.d("AccessoryList", "not empty");
                UpdateLinearLayout();
            }
        } else {
            /* create it */
            Log.d("hasDb", "false");
            editor.putBoolean("inited", true);
            /* SQLite stuff here */
        }

        return v;
    }

    View.OnClickListener addAccessoryType = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NewAccessoryDialog dialog = new NewAccessoryDialog(AccessoriesFragment.this);
            dialog.show(getFragmentManager(), "newAccessoryDialog");
        }
    };

    public void grantAdminPermission() {
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

    public void revokeAdminPermission() {
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

    private Accessory MakeAccessory(TextView mName, EditText mCost, LinearLayout parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout item = (LinearLayout) parent.findViewById(R.id.linear_layout);
        View acc = inflater.inflate(R.layout.accessory_entry, item, false);
        //create Accessory object
        TextView main = (TextView) acc.findViewById(R.id.button);
        main.setText(mName.getText().toString());
        TextView costUnits = (TextView) acc.findViewById(R.id.cost_units);
        EditText count = (EditText) acc.findViewById(R.id.count);
        EditText costAmount = (EditText) acc.findViewById(R.id.cost_amount);
        costAmount.setText(mCost.getText().toString());
        RelativeLayout ccBox = (RelativeLayout) acc.findViewById(R.id.ccContainer);
        Accessory newAcc = new Accessory(acc, count, main, ccBox, costAmount, costUnits, item);
        // find parent view
        AccessoryType parentType = null;
        for (AccessoryType type : AccessoryList) {
            if (type.container.equals(parent)) {
                parentType = type;
            }
        }
        // initialize some values
        if (parentType != null) {
            newAcc.id = newAcc.main.getText().toString().toLowerCase().replaceAll("\\s+", "");
            Log.d("newAcc.id", newAcc.id);
            String[] unitTypes = context.getResources().getStringArray(R.array.units_array);
            String type = parentType.type;
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

    private boolean PlaceAccInAccList(Accessory acc, ArrayList<Accessory> accList) {
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

    private void AddAccToLinLayout(Accessory acc, LinearLayout parent) {
        acc.item.addView(acc.view);
    }

    public void AddAccessory(EditText mName, EditText mCost, LinearLayout parent) {
        Accessory acc = MakeAccessory(mName, mCost, parent);
        // find parent type
        AccessoryType parentType = null;
        for (AccessoryType type : AccessoryList) {
            if (type.container.equals(parent)) {
                parentType = type;
            }
        }
        if (parentType != null) {
            boolean acceptedAcc = PlaceAccInAccList(acc, parentType.accessoryList);
            if (acceptedAcc) {
                AddAccToLinLayout(acc, parent);
                acc.main.setText(mName.getText().toString());
                acc.costAmount.setText(mCost.getText().toString());
                acc.ccBox.setVisibility(View.VISIBLE);
            } else {
                Toast toast = Toast.makeText(context, "Accessory: \"" + mName.getText().toString() + "\" already in list.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public AccessoryType MakeAccessoryType(String mTitle, String mType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout row = (LinearLayout) myView.findViewById(R.id.accLinLayout);
        View acc = inflater.inflate(R.layout.accessory_type_layout, row, false);
        View line = inflater.inflate(R.layout.line_divider, row, false);

        //create accessoryType object
        TextView title = (TextView) acc.findViewById(R.id.title);
        title.setText(mTitle);
        TextView subTitleText = (TextView) acc.findViewById(R.id.sub_title_text);
        TextView subTitleAmount = (TextView) acc.findViewById(R.id.sub_title_amount);
        TextView add = (TextView) acc.findViewById(R.id.add_item_button);
        LinearLayout container = (LinearLayout) acc.findViewById(R.id.linear_layout);
        RelativeLayout ccBox = (RelativeLayout) acc.findViewById(R.id.sub_title);
        AccessoryType newAccT = new AccessoryType(acc, mType, title, subTitleText, subTitleAmount, add, container, ccBox, row, line);
        newAccT.id = mTitle.toLowerCase().replaceAll("\\s+", "");
        Log.d("newAccT", newAccT.id);
        return newAccT;
    }

    private boolean PlaceAccTypeInAccTypeList(String title, AccessoryType accT) {
        boolean okayToAdd = true;
        for (AccessoryType accType : AccessoryList) {
            if (accType.id.equals(accT.id)) {
                okayToAdd = false;
            }
        }
        if (okayToAdd) {
            //TODO: Add to SQLite
            AccessoryList.add(accT);
        }
        return okayToAdd;   // if the acc was added
    }

    private void AddAccTypeToLinLayout(AccessoryType accT) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (accT.type.equals("Linear ft")) {
            accT.ccBox.setVisibility(View.VISIBLE);
        } else {
            accT.ccBox.setVisibility(View.INVISIBLE);
        }
        //add views
        accT.row.addView(accT.view);
        accT.row.addView(accT.line);
    }

    public void addAccessoryType(String mTitle, String mType) {
        AccessoryType accType = MakeAccessoryType(mTitle, mType);
        boolean acceptedAccType = PlaceAccTypeInAccTypeList(mTitle, accType);
        if (acceptedAccType) {
            AddAccTypeToLinLayout(accType);
        } else {
            Toast toast = Toast.makeText(context, "Accessory: \"" + mTitle + "\" already in list.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void removeAccessoryType(AccessoryType accT) {
        accT.row.removeView(accT.view);
        accT.row.removeView(accT.line);
        AccessoryList.remove(accT);
    }

    private void UpdateLinearLayout() {
        /* necessary to update the views once the fragment gets recreated */
        // TODO: Reduce overhead
        ArrayList<AccessoryType> typeTemp = new ArrayList<AccessoryType>(AccessoryList);
        AccessoryList.clear();
        /* for each accessory type */
        for (AccessoryType accType : typeTemp) {
            AccessoryType newAccType = MakeAccessoryType(accType.title.getText().toString(), accType.type);
            AddAccTypeToLinLayout(newAccType);
            // same must be done for the list of items
            ArrayList<Accessory> accTemp = new ArrayList<Accessory>(accType.accessoryList);
            accType.accessoryList.clear();
            for (Accessory acc : accTemp) {
                Accessory newAcc = MakeAccessory(acc.main, acc.costAmount, newAccType.container);
                AddAccToLinLayout(newAcc, newAccType.container);
                newAccType.accessoryList.add(newAcc);
            }
            AccessoryList.add(newAccType);
        }
        revokeAdminPermission();
    }

}
