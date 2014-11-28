package com.fasterbids.ryan.accessoriespage;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class AccessoriesFragment extends Fragment {

    ImageView adminButton;
    static Context context;
    static boolean adminPermission = false;
    LinearLayout accTitleLayout;
    static View myView;
    TextView saveButton;
    TextView addAccButton;
    RelativeLayout addAndSave;
    static FragmentManager fmanager;
    static AccessoriesFragment fragment;
    static AccSQLiteHelper SQLiteHelper;
    static LinearLayout pContainer;
    static TextView pTotalCost;

    static ArrayList<AccessoryType> AccessoryList;

/* Methods below here
----------------------------------------------------------------------------------------------------
 */


    public AccessoriesFragment newInstance() {
        fragment = new AccessoriesFragment();
        return fragment;
    }
    public AccessoriesFragment() {
        fragment = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AccessoryList = new ArrayList<AccessoryType>();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.accessories_fragment, container, false);
        context = getActivity();
        SQLiteHelper = new AccSQLiteHelper(context);
        myView = v;

        addAccButton = (TextView) v.findViewById(R.id.add_acc);
        addAccButton.setOnClickListener(addAccessoryType);

        accTitleLayout = (LinearLayout) v.findViewById(R.id.accLinLayout);
        // add pricing view to bottom of linear Layout
        LinearLayout layout = (LinearLayout) AccessoriesFragment.myView.findViewById(R.id.accLinLayout);
        View pricing = inflater.inflate(R.layout.costs_layout, layout, false);
        accTitleLayout.addView(pricing);
        pContainer = (LinearLayout) pricing.findViewById(R.id.costs_container);
        pTotalCost = (TextView) pricing.findViewById(R.id.total_cost);
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
            SQLiteHelper.populateAccessoriesFromDB(AccessoryList);
        } else {
            /* create it */
            editor.putBoolean("inited", true);
            editor.commit();
            /* SQLite stuff here */
        }
        return v;
    }

    View.OnClickListener addAccessoryType = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NewAccessoryDialog dialog = new NewAccessoryDialog();
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

    public void updateTotalCosts(String sCost) {
        float cost = Float.parseFloat(sCost);
        float current = 0.0f;
        if (!pTotalCost.getText().toString().equals("$0.00")) {
            current = Float.parseFloat(pTotalCost.getText().toString());
        }
        pTotalCost.setText(String.format("%.2f", current + cost));
    }

}
