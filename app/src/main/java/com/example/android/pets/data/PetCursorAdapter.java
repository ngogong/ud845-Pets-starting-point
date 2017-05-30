package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pets.R;

/**
 * Created by ismile on 5/29/2017.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_NAME));
        String summary = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_BREED));
        // Populate fields with extracted properties
        nameTextView.setText(name);
        summaryTextView.setText(summary);
    }
}
