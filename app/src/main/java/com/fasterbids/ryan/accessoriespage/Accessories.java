package com.fasterbids.ryan.accessoriespage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Array;


public class Accessories extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessories);

        final TextView Accessories = (TextView) findViewById(R.id.accessories_button);
        Accessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().findFragmentByTag("accFrag") == null) {
                    Log.e("accFrag found", "false");
                    Accessories.setBackgroundColor(getResources().getColor(R.color.grey));
                    getFragmentManager().beginTransaction().add(R.id.accessories_fragment_container,
                            new AccessoriesFragment(), "accFrag").addToBackStack(null).commit();
                } else {
                    Log.e("accFrag found", "true");
                    Accessories.setBackgroundColor(getResources().getColor(R.color.blue));
                    AccessoriesFragment frag = (AccessoriesFragment) getFragmentManager().findFragmentByTag("accFrag");
                    frag.revokeAdminPermission();
                    getFragmentManager().popBackStack();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accessories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
