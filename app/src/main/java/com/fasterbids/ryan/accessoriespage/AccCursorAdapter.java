package com.fasterbids.ryan.accessoriespage;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccCursorAdapter extends CursorAdapter {

    Context context;
    ArrayList<AccessoryType> list = new ArrayList<AccessoryType>();
    public int index;

    public AccCursorAdapter(Context context, ArrayList<AccessoryType> list, Cursor c, int flags) {
        super(context, c, flags);
        this.list = list;
        this.context = context;
        this.index = -1;
    }

    @Override
    public AccessoryType getItem(int position) {
        return list.get(position);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int position) {
        this.index = position;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        this.context = context;

        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

}
