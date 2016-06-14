package domain.a.not.wz.cipherdiary.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

/**
 * Created by Wz on 002, Jun 02.
 */
public class DiaryProviderHelper {
    //methods relating to the diary entires

    public static CursorLoader getYearMonthListViewLoader(Context context) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI.buildUpon().appendPath("diaryDate").build();
        String[] projection = new String[] {
            DiaryContract.DiaryDateViewEntry.COLUMN_YEAR,
            DiaryContract.DiaryDateViewEntry.COLUMN_MONTH,
            "rowid AS _id" //cursoradapter requires a _id column, rowid doesn't work for views, so it is null
        };
        String sortOrder = DiaryContract.DiaryDateViewEntry.COLUMN_DATE + " DESC";

        return new CursorLoader(context, uri, projection, null, null, sortOrder);
    }

    public static Cursor getYearMonthListView(ContentResolver contextResolver) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI.buildUpon().appendPath("diaryDate").build();
        String[] projection = new String[] {
                DiaryContract.DiaryDateViewEntry.COLUMN_YEAR,
                DiaryContract.DiaryDateViewEntry.COLUMN_MONTH
        };
        String sortOrder = DiaryContract.DiaryDateViewEntry.COLUMN_DATE + " DESC";

        return contextResolver.query(uri,
                projection, null, null, sortOrder);
    }

    public static CursorLoader getDaysOfMonthViewLoader(Context context, int year, int month) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI.buildUpon().appendPath("diaryDate").build();
        String[] projection = new String[] {
                DiaryContract.DiaryDateViewEntry.COLUMN_DAY,
                "rowid AS _id"
        };
        String selection = DiaryContract.DiaryDateViewEntry.COLUMN_YEAR + " = ? AND "
                + DiaryContract.DiaryDateViewEntry.COLUMN_MONTH + " = ? ";
        String yearString = String.format("%04d", year);
        String monthString = String.format("%02d", month);
        String[] selectionArgs = new String[] {yearString, monthString};
        String sortOrder = DiaryContract.DiaryDateViewEntry.COLUMN_DATE + " DESC";

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public static Cursor getDaysOfMonthView(ContentResolver contextResolver, int year, int month)  {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI.buildUpon().appendPath("diaryDate").build();
        String[] projection = new String[] {
                DiaryContract.DiaryDateViewEntry.COLUMN_DAY
        };
        String selection = DiaryContract.DiaryDateViewEntry.COLUMN_YEAR + " = ? AND "
                + DiaryContract.DiaryDateViewEntry.COLUMN_MONTH + " = ? ";
        String yearString = String.format("%04d", year);
        String monthString = String.format("%02d", month);
        String[] selectionArgs = new String[] {yearString, monthString};
        String sortOrder = DiaryContract.DiaryDateViewEntry.COLUMN_DATE + " DESC";

        return contextResolver.query(uri,
                projection, selection, selectionArgs, sortOrder);
    }

    public static CursorLoader getDiaryEntriesOfDateLoader(Context context, int year, int month, int day) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI;
        String selection = "strftime('%Y-%m-%d', " + DiaryContract.DiaryEntry.COLUMN_DATE +
                ", 'unixepoch') = ? ";
        String yearString = String.format("%04d", year);
        String monthString = String.format("%02d", month);
        String dayString = String.format("%02d", day);
        Log.v("GetDEntriesLoader",yearString + "-" + monthString + "-" + dayString );
        String[] selectionArgs = new String[] { yearString + "-" + monthString + "-" + dayString };
        String sortOrder = DiaryContract.DiaryEntry.COLUMN_DATE + " DESC";

        return new CursorLoader(context, uri, null, selection, selectionArgs, sortOrder);
    }

    public static Cursor getDiaryEntriesOfDate(ContentResolver contextResolver, int year, int month, int day)  {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI;
        String selection = "strftime('%Y-%m-%d', " + DiaryContract.DiaryEntry.COLUMN_DATE +
                ", 'unixepoch') = ? ";
        String yearString = String.format("%04d", year);
        String monthString = String.format("%02d", month);
        String dayString = String.format("%02d", day);
        String[] selectionArgs = new String[] { yearString + "-" + monthString + "-" + dayString };
        String sortOrder = DiaryContract.DiaryEntry.COLUMN_DATE + " DESC";
        return contextResolver.query(uri,
                null, selection, selectionArgs, sortOrder);
    }

    public static Cursor getDiaryEntry(ContentResolver contextResolver, String id) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI;
        String selection = DiaryContract.DiaryEntry._ID + " = ? ";
        String[] selectionArgs = new String[] { id };

        return contextResolver.query(uri,
                null, selection, selectionArgs, null);
    }

    public static Uri createDiaryEntry(ContentResolver contentResolver,
                                       long date, String title, String content) {
        ContentValues values = new ContentValues();
        values.put(DiaryContract.DiaryEntry.COLUMN_DATE, date);
        values.put(DiaryContract.DiaryEntry.COLUMN_TITLE, title);
        values.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, content);

        return createDiaryEntry(contentResolver, values);
    }
    public static Uri createDiaryEntry(ContentResolver contentResolver, ContentValues contentValues) {
        return contentResolver.insert(DiaryContract.DiaryEntry.CONTENT_URI, contentValues);
    }

    public static int deleteDiaryEntry(ContentResolver contentResolver, String id) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI;
        String selection = DiaryContract.DiaryEntry._ID + " = ? ";
        String[] selectionArgs = new String[] { id };

        return contentResolver.delete(uri, selection, selectionArgs);
    }

    public static int updateDiaryEntry(ContentResolver contentResolver, String id,
                                       long date, String title, String content) {
        ContentValues values = new ContentValues();
        values.put(DiaryContract.DiaryEntry.COLUMN_DATE, date);
        values.put(DiaryContract.DiaryEntry.COLUMN_TITLE, title);
        values.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, content);
        return updateDiaryEntry(contentResolver, id, values);
    }
    public static int updateDiaryEntry(ContentResolver contentResolver, String id, ContentValues contentValues) {
        Uri uri = DiaryContract.DiaryEntry.CONTENT_URI;
        String selection = DiaryContract.DiaryEntry._ID + " = ? ";
        String[] selectionArgs = new String[] { id };

        return contentResolver.update(uri, contentValues, selection, selectionArgs);
    }


    //methods relating to the diary list
    public static CursorLoader getDiaryListLoader(Context context) {
        Uri uri = DiaryContract.DiaryList.CONTENT_URI;
        String[] projection = new String[] {
                DiaryContract.DiaryList.COLUMN_NAME,
                DiaryContract.DiaryList.COLUMN_FILE_NAME,
                "rowid AS _id"
        };

        return new CursorLoader(context, uri, projection, null, null, null);
    }

    public static Cursor getDiaryList(ContentResolver contextResolver) {
        return contextResolver.query(DiaryContract.DiaryList.CONTENT_URI,
                null, null, null, null);
    }

    public static Uri createNewDiary(ContentResolver contentResolver, String diaryName, String password) {
        Uri uri = DiaryContract.DiaryList.buildDiaryListNewUri(diaryName, password);
        return contentResolver.insert(uri, null);
    }

    public static Cursor selectDiary(ContentResolver contentResolver, String dbName, String password) {
        Uri uri = DiaryContract.DiaryList.buildDiaryListSelectUri(dbName, password);
        return contentResolver.query(uri, null, null, null, null);
    }

    public static int deleteDiary(ContentResolver contextResolver, String dbName) {
        if(dbName.isEmpty()) {
            return -1;
        }
        Uri uri = DiaryContract.DiaryList.CONTENT_URI.buildUpon().appendPath("delete")
                .appendQueryParameter("dbName", dbName).build();

        return contextResolver.delete(uri, null, null);
    }

    public static int renameDiaryName(ContentResolver contentResolver, String dbName, String newDiaryName) {
        Uri uri = DiaryContract.DiaryList.CONTENT_URI;
        String selection = DiaryContract.DiaryList.COLUMN_FILE_NAME + " = ? ";
        String[] selectionArgs = new String[] { dbName };
        ContentValues values = new ContentValues();
        values.put(DiaryContract.DiaryList.COLUMN_NAME, newDiaryName);
        return contentResolver.update(uri, values, selection, selectionArgs);
    }
}
