package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Set;

/**
 * Created by ismile on 5/26/2017.
 */

public class PetProvider extends ContentProvider {

    private PetDbHelper mDbHelper;
    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }




    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor=null;

        // Figure out if the URI matcher can match the URI to a specific code
        try {
            int match = sUriMatcher.match(uri);
            switch (match) {
                case PETS:
                    // For the PETS code, query the pets table directly with the given
                    // projection, selection, selection arguments, and sort order. The cursor
                    // could contain multiple rows of the pets table.
                    // TODO: Perform database query on pets table
                    cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection,
                            selectionArgs, null, null, sortOrder);

                    break;
                case PET_ID:
                    // For the PET_ID code, extract out the ID from the URI.
                    // For an example URI such as "content://com.example.android.pets/pets/3",
                    // the selection will be "_id=?" and the selection argument will be a
                    // String array containing the actual ID of 3 in this case.
                    //
                    // For every "?" in the selection, we need to have an element in the selection
                    // arguments that will fill in the "?". Since we have 1 question mark in the
                    // selection, we have 1 String in the selection arguments' String array.
                    selection = PetContract.PetEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    // This will perform a query on the pets table where the _id equals 3 to return a
                    // Cursor containing that row of the table.
                    cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "inside query", e);
        }
        return cursor;
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // TODO: Insert a new pet into the pets database table with the given ContentValues

        // Check that the name is not null
        String testString = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if (   (testString==null)
            || (testString.length()==0) ){
            throw new IllegalArgumentException("Pet requires a name");
        }

        /* breed can be null or zero length
        testString = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        if (   (testString==null)
                || (testString.length()==0) ){
            throw new IllegalArgumentException("Pet requires a breed");
        }
        */

        int testInt= values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if (  (testInt < PetContract.PetEntry.GENDER_UNKNOWN )
            ||(testInt > PetContract.PetEntry.GENDER_FEMALE ) ){
            throw new IllegalArgumentException("Pet's gender choice only unknown, male & female");
        }

        testInt= values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (testInt < 0 ) {
            throw new IllegalArgumentException("Pet need to have positive weight");
        }


        // Gets the database in write mode
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long newRowId = database.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        if (newRowId == -1){
            Log.e(LOG_TAG, "*************cannot insert");
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }



    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected pets in the pets database table with the given ContentValues

        // TODO: Return the number of rows that were affected

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Check that the name is not null

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            String testString = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if (   (testString==null)
                    || (testString.length()==0) ){
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_BREED)){
            String testString = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
            if (   (testString==null)
                    || (testString.length()==0) ){
                throw new IllegalArgumentException("Pet has breed key but do not have value");
            }
        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)){
            int testInt= values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if (  (testInt < PetContract.PetEntry.GENDER_UNKNOWN )
                    ||(testInt > PetContract.PetEntry.GENDER_FEMALE ) ){
                throw new IllegalArgumentException("Pet's gender choice only unknown, male & female");
            }

        }

        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            int testInt = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if (testInt < 0) {
                throw new IllegalArgumentException("Pet need to have positive weight");
            }
        }

        int result = db.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
        return result;
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        try {
            switch (match) {
                case PETS:
                    // Delete all rows that match the selection and selection args
                    return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                case PET_ID:
                    // Delete a single row given by the ID in the URI
                    selection = PetContract.PetEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }
        } catch ( Exception e) {
            Log.e(LOG_TAG, "delete database " + uri.toString() + " selection=" + selection, e);
        }
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
