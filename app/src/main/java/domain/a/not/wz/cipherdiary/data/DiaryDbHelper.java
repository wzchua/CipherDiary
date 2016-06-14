package domain.a.not.wz.cipherdiary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Wz on 024, May 24.
 */
public class DiaryDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "main.db";
    private static final String DEFAULT_ATTACHED_DB_NAME = "diary";
    private boolean mHasAttached = false;
    private Context mContext;
    private String attachedDBFileName;

    public DiaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public DiaryDbHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DiaryContract.DiaryList.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void detachDB() {
        if(!mHasAttached) {
            return;
        }
        this.getWritableDatabase("").rawExecSQL("DETACH DATABASE "+ DEFAULT_ATTACHED_DB_NAME);
        attachedDBFileName = null;
        mHasAttached = false;
    }

    public boolean attachDB(String dbName, String password) {
        if(mHasAttached){
            detachDB();
        }
        SQLiteDatabase db = this.getWritableDatabase("");
        Cursor cursor = db.query(DiaryContract.DiaryList.TABLE_NAME,
                new String[] {DiaryContract.DiaryList.COLUMN_FILE_NAME},
                DiaryContract.DiaryList.COLUMN_FILE_NAME + " = ? ",
                new String[] { dbName },
                null,
                null,
                null);

        if(!cursor.moveToFirst()) {
            throw new UnsupportedOperationException("database not found");
        }
        cursor.close();

        String path = mContext.getDatabasePath(dbName).getPath();
        File dbPathFile = new File (path);
        try{
            db.rawExecSQL("ATTACH DATABASE '" + dbPathFile
                    + "' AS " + DEFAULT_ATTACHED_DB_NAME + " KEY '" + password +"'");
        } catch (SQLiteException e) {

            return false;
        }
        mHasAttached = true;
        attachedDBFileName = dbName;
        return true;
    }

    public String createDb(String diaryEntryName, String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        md.update(diaryEntryName.getBytes());
        String dbName = bytesToHex(md.digest()) + ".db";
        String dbNameSelect = DiaryContract.DiaryList.COLUMN_FILE_NAME + " = ? ";
        String[] dbNameSelectArgs = new String[] {dbName};
        SQLiteDatabase db = getWritableDatabase("");
        Cursor cursor = db.query(DiaryContract.DiaryList.TABLE_NAME,
                null, dbNameSelect, dbNameSelectArgs,
                null, null, null);
        //TODO: add check that fileName doesn't exist in file system
        while(cursor.moveToFirst()) {
            md.update(dbName.getBytes());
            dbName = bytesToHex(md.digest()) + ".db";
            dbNameSelectArgs = new String[] {dbName};
            cursor = db.query(DiaryContract.DiaryList.TABLE_NAME,
                    null, dbNameSelect, dbNameSelectArgs,
                    null, null, null);
        }
        cursor.close();

        String path = mContext.getDatabasePath(dbName).getPath();
        File dbPathFile = new File (path);
        boolean isNew = true;
        if(dbPathFile.exists()) {
            Log.e("DBHELPER", "file exists, deleting");
            dbPathFile.delete();
        }
        SQLiteDatabase diaryDb = SQLiteDatabase.openOrCreateDatabase(path, password, null);

        diaryDb.execSQL(DiaryContract.DiaryEntry.SQL_CREATE_TABLE);
        diaryDb.execSQL(DiaryContract.DiaryDateViewEntry.SQL_CREATE_DATE_VIEW);
        diaryDb.close();
        ContentValues diaryValues = new ContentValues();
        diaryValues.put(DiaryContract.DiaryList.COLUMN_NAME, diaryEntryName);
        diaryValues.put(DiaryContract.DiaryList.COLUMN_FILE_NAME, dbName);
        long id = db.insertOrThrow(DiaryContract.DiaryList.TABLE_NAME, null, diaryValues);

        return dbName;
    }

    public boolean hasDBAttached() {
        return mHasAttached;
    }

    public int deleteDB(String dbName) {
        SQLiteDatabase db = this.getWritableDatabase("");
        String[] projection = new String[] {DiaryContract.DiaryList.COLUMN_FILE_NAME};
        String nameSelection = DiaryContract.DiaryList.COLUMN_FILE_NAME + " = ? ";
        String[] nameSelectionArgs = new String[] { dbName };

        Cursor cursor = db.query(DiaryContract.DiaryList.TABLE_NAME,
                projection, nameSelection, nameSelectionArgs,
                null, null, null);

        if(!cursor.moveToFirst()) {
            cursor.close();
            return -1;
        }
        cursor.close();

        String path = mContext.getDatabasePath(dbName).getPath();
        File dbPathFile = new File (path);
        if(dbName.equals(attachedDBFileName)) {
            detachDB();
        }
        boolean result = dbPathFile.delete();

        db.delete(DiaryContract.DiaryList.TABLE_NAME, nameSelection, nameSelectionArgs);
        return 1;
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
