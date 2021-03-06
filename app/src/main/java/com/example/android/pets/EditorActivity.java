/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    private static final String LOG_TAG= EditorActivity.class.getName();

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    /*
    private void insertPet() {
        // TODO: Insert a single pet into the database
        PetDbHelper mDbHelper = new PetDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        String name=  mNameEditText.getText().toString();
        String breed = mBreedEditText.getText().toString();
        int gender=PetEntry.GENDER_UNKNOWN;
        switch (mGenderSpinner.getSelectedItem().toString()) {
            case PetEntry.GENDER_STRING_UNKNOWN:
                gender = PetEntry.GENDER_UNKNOWN;
                break;
            case PetEntry.GENDER_STRING_MALE:
                gender = PetEntry.GENDER_MALE;
                break;
            case PetEntry.GENDER_STRING_FEMALE:
                gender = PetEntry.GENDER_FEMALE;
                break;
        }
        int weight= Integer.parseInt(mWeightEditText.getText().toString());


        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, name);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breed);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, gender);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        if (newRowId == -1){
            Log.e(LOG_TAG, "*************cannot insert");
        }
        Toast.makeText(this, "newRowId= " + newRowId + " end", Toast.LENGTH_LONG).show();

    }

    */

    /**
     * Get user input from editor and save new pet into database.
     */
    private void insertPet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        if (nameString.length()==0){
            throw new IllegalArgumentException("need to enter name");
        }
        if (weightString.length()==0){
                throw new IllegalArgumentException("need to enter weight");
        }

        int weight = Integer.parseInt(weightString);

        // Create database helper
        PetDbHelper mDbHelper = new PetDbHelper(this);


        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

        Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);


        // Insert the new row, returning the primary key value of the new row
        long newRowId= ContentUris.parseId(newUri);

        if (newRowId == -1){
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "*************cannot insert");
        } else {
            Toast.makeText(this, getString(R.string.pet_saved)+ " " + newRowId, Toast.LENGTH_LONG).show();

        }

    }

    private int deletePets(){
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        String [] selectionArgs={};
        String selection="" ;
        boolean firstClause=false;

        if (nameString.length() >0) {
            selection = selection + PetEntry.COLUMN_PET_NAME + "=? ";
            String [] tempString = new String[selectionArgs.length+1];
            System.arraycopy(selectionArgs,0,tempString,0,selectionArgs.length);
            tempString[selectionArgs.length ] = nameString;
            selectionArgs = tempString;
            firstClause=true;
        }

        if (breedString.length() >0) {
            selection = selection + (firstClause ? " AND " : "" )+ PetEntry.COLUMN_PET_BREED + "=? ";
            String [] tempString = new String[selectionArgs.length+1];
            System.arraycopy(selectionArgs,0,tempString,0,selectionArgs.length);
            tempString[selectionArgs.length ] = breedString;
            selectionArgs = tempString;
            firstClause=true;
        }

        if (weightString.length() >0) {
            selection = selection + (firstClause ? " AND " : "" )+ PetEntry.COLUMN_PET_WEIGHT + "=? ";
            String [] tempString = new String[selectionArgs.length+1];
            System.arraycopy(selectionArgs,0,tempString,0,selectionArgs.length);
            tempString[selectionArgs.length ] = weightString;
            selectionArgs = tempString;
            firstClause=true;
        }

        selection = selection +(firstClause ? " AND " : "" ) + PetEntry.COLUMN_PET_GENDER + "=?  ";
        String[] tempString = new String[selectionArgs.length + 1];
        System.arraycopy(selectionArgs,0,tempString,0,selectionArgs.length);
        tempString[selectionArgs.length ] = String.valueOf(mGender);
        selectionArgs = tempString;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PetContract.PetEntry._ID,
                };

        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection,
                selection,selectionArgs,null);
        int idColumnIndex = cursor.getColumnIndex(PetContract.PetEntry._ID);
        int numOfRowDelete=0;

        if (cursor.getCount()==1){
            //delete only one row
            // Gets the database in write mode
            cursor.moveToNext();
            long currentID = (long) cursor.getInt(idColumnIndex);
            Uri newUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, currentID);
            selection = PetEntry._ID + "=? ";
            String [] newSelectionArgs = { String.valueOf(currentID) };
            numOfRowDelete = getContentResolver().delete(newUri,selection, newSelectionArgs);
            if (numOfRowDelete == 1){
                Toast.makeText(this, getString(R.string.pet_deleted, numOfRowDelete), Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "numofrowdelete == 1 pet_deleted");
            } else {
                Toast.makeText(this, getString(R.string.pet_cannot_deleted), Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "numofrowdelete == 1 pet_cannot_deleted");
            }
        } else if (cursor.getCount() > 1) {
            //delete multiple rows
            numOfRowDelete = getContentResolver().delete(PetEntry.CONTENT_URI,selection, selectionArgs);
            if (numOfRowDelete > 1){
                Toast.makeText(this, getString(R.string.pet_deleted, numOfRowDelete), Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "numofrowdelete > 1 pet_deleted");
            } else {
                Toast.makeText(this, getString(R.string.pet_cannot_deleted), Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "numofrowdelete > 1 pet_cannot_deleted");
            }
        } else {
            Toast.makeText(this, getString(R.string.pet_cannot_deleted), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "cusor == 0  pet_cannot_deleted");
        }

        cursor.close();
        return numOfRowDelete;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                insertPet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                deletePets();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}