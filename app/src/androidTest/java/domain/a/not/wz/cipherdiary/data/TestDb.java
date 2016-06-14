package domain.a.not.wz.cipherdiary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wz on 026, May 26.
 */

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    private static DiaryDbHelper mOpenHelper = null;
    private static String dbFileName = null;
    private static final String DIARY_NAME = "diaryName";
    private static final String DIARY_PASSWORD = "pass";

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DiaryDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        SQLiteDatabase.loadLibs(mContext);
        deleteTheDatabase();
        mOpenHelper = new DiaryDbHelper(mContext);
        dbFileName = mOpenHelper.createDb(DIARY_NAME, DIARY_PASSWORD);
        mOpenHelper.attachDB(dbFileName, DIARY_PASSWORD);
    }

    @Override
    protected void tearDown() throws Exception {
        mOpenHelper.deleteDB(dbFileName);
        mOpenHelper.close();
        super.tearDown();
    }

    public void testDiaryTable() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase("");
        final long TEST_DATE = DiaryProvider.getTimeInSeconds(2014, 1, 20);

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(DiaryContract.DiaryEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                DiaryContract.DiaryEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor
        cursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertIntoDiaryDb(DiaryDbHelper dbHelper , ContentValues testValues) {
        // insert our test records into the database
        SQLiteDatabase db = dbHelper.getWritableDatabase("");

        long locationRowId;
        locationRowId = db.insert(DiaryContract.DiaryEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert values", locationRowId != -1);

        return locationRowId;
    }
}