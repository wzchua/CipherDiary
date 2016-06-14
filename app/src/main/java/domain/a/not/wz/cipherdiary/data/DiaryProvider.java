package domain.a.not.wz.cipherdiary.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.GregorianCalendar;

/**
 * Created by Wz on 024, May 24.
 */
public class DiaryProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DiaryDbHelper mOpenHelper;

    static final int DIARY_LIST = 100;
    static final int DIARY_LIST_NEW = 101;
    static final int DIARY_LIST_SELECT = 102;
    static final int DIARY_LIST_DELETE = 103;
    static final int DIARY_ENTRY = 200;
    static final int DIARY_ENTRY_DATE_VIEW = 300;

    @Override
    public boolean onCreate() {
        SQLiteDatabase.loadLibs(getContext());
        mOpenHelper = new DiaryDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DiaryContract.CONTENT_AUTHORITY;

        //database list
        matcher.addURI(authority, DiaryContract.PATH_DIARY_LIST, DIARY_LIST);
        matcher.addURI(authority, DiaryContract.PATH_DIARY_LIST + "/new", DIARY_LIST_NEW);
        // this is a special query command, all arguements are ignored, only uri is used
        matcher.addURI(authority, DiaryContract.PATH_DIARY_LIST + "/select", DIARY_LIST_SELECT);
        matcher.addURI(authority, DiaryContract.PATH_DIARY_LIST + "/delete", DIARY_LIST_DELETE);

        // diary data in a attached database
        matcher.addURI(authority, DiaryContract.PATH_DIARY_ENTRY, DIARY_ENTRY);
        //date view has to contain the same path to trigger obs notif
        matcher.addURI(authority, DiaryContract.PATH_DIARY_ENTRY + "/diaryDate", DIARY_ENTRY_DATE_VIEW);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);

        //checkDB
        checkDB(match);

        Cursor retCursor;
        switch(match) {
            case DIARY_LIST: {
                retCursor = mOpenHelper.getReadableDatabase("").query(DiaryContract.DiaryList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            //select will select and verify the password, returns empty cursor if password fails
            // this is a special query command, all arguements are ignored, only uri is used
            case DIARY_LIST_SELECT: {
                String diaryDbName = DiaryContract.DiaryList.getDiaryDbNameFromSelectUri(uri);
                String diaryPassword = DiaryContract.DiaryList.getPasswordFromSelectUri(uri);
                if(!mOpenHelper.attachDB(diaryDbName, diaryPassword)) {
                    return new MatrixCursor(new String[] {"empty"}); //return empty cursor
                }
                selection = DiaryContract.DiaryList.COLUMN_FILE_NAME + " = ? ";
                selectionArgs = new String[] {diaryDbName};
                retCursor = mOpenHelper.getReadableDatabase("").query(DiaryContract.DiaryList.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            case DIARY_ENTRY: {
                retCursor = mOpenHelper.getReadableDatabase("").query(true,
                        DiaryContract.DiaryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder, null);
                break;
            }
            case DIARY_ENTRY_DATE_VIEW: {
                retCursor = mOpenHelper.getReadableDatabase("").query(true,
                        DiaryContract.DiaryDateViewEntry.DATE_VIEW_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder, null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DIARY_ENTRY:
                return DiaryContract.DiaryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase("");
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        checkDB(match);

        switch (match) {
            case DIARY_LIST_NEW: {
                //special: also ignores values
                String diaryName = DiaryContract.DiaryList.getDiaryNameFromNewUri(uri);
                String diaryPassword = DiaryContract.DiaryList.getPasswordFromNewUri(uri);
                String dBName = mOpenHelper.createDb(diaryName, diaryPassword);
                if(!dBName.equals("")) {
                    returnUri = DiaryContract.DiaryList.buildDiaryListUri(diaryName);
                } else {
                    throw new android.database.SQLException("Failed to create new diary " + uri);
                }

                break;
            }
            case DIARY_ENTRY: {
                long _id = db.insert(DiaryContract.DiaryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DiaryContract.DiaryEntry.buildDiaryUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase("");
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        checkDB(match);
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case DIARY_LIST_DELETE: {
                //special requirements, will ignore selection arguments
                String dbName = uri.getQueryParameter("dbName");
                rowsDeleted = mOpenHelper.deleteDB(dbName);
                break;
            }
            case DIARY_ENTRY: {
                rowsDeleted = db.delete(DiaryContract.DiaryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase("");
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        checkDB(match);

        switch (match) {
            case DIARY_LIST: {
                rowsUpdated = db.update(DiaryContract.DiaryList.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case DIARY_ENTRY: {
                rowsUpdated = db.update(DiaryContract.DiaryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsUpdated;
    }

    /**
     * Checks if there's a diary attached
     * @param match the match code for diary operations
     */
    private void checkDB(int match) {
        if(match >= DIARY_ENTRY) {
            if(!mOpenHelper.hasDBAttached())
                //TODO: change to boolean to resolve the issue without crashing the app
                throw new UnsupportedOperationException("No database attached");
        }
    }

    public static long getTimeInSeconds(int year, int monthOfYear, int dayOfMonth) {
        return (new GregorianCalendar(year, monthOfYear - 1, dayOfMonth)).getTimeInMillis() / 1000L;
    }
}
