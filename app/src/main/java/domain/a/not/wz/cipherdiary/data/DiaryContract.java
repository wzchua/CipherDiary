package domain.a.not.wz.cipherdiary.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Wz on 024, May 24.
 */
public class DiaryContract {
    public static final String CONTENT_AUTHORITY = "domain.a.not.wz.cipherdiary";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_DIARY_ENTRY = "diary_entry";
    public static final String PATH_DIARY_LIST = "diary_list";

    public static final class DiaryList implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY_LIST).build();
        public static final String TABLE_NAME = "diary_list";

        public static final String COLUMN_NAME = "diary_name";
        public static final String COLUMN_FILE_NAME = "filename";
        public static final String NAME_KEY = "name";
        public static final String PASSWORD_KEY = "password";
        public static final String DB_NAME_KEY = "dbName";

        public static Uri buildDiaryListUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildDiaryListUri(String diaryName) {
            return CONTENT_URI.buildUpon().appendPath(diaryName).build();
        }

        public static Uri buildDiaryListNewUri(String diaryName, String password) {
            return CONTENT_URI.buildUpon().appendPath("new")
                    .appendQueryParameter(NAME_KEY, diaryName)
                    .appendQueryParameter(PASSWORD_KEY, password)
                    .build();
        }

        public static Uri buildDiaryListSelectUri(String dbName, String password) {
            return CONTENT_URI.buildUpon().appendPath("select")
                    .appendQueryParameter(DB_NAME_KEY, dbName)
                    .appendQueryParameter(PASSWORD_KEY, password)
                    .build();
        }

        public static String getDiaryNameFromNewUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("new")) {
                return uri.getQueryParameter(NAME_KEY);
            } else {
                throw new UnsupportedOperationException("Unknown id uri: " + uri);
            }
        }
        public static String getPasswordFromNewUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("new")) {
                return uri.getQueryParameter(PASSWORD_KEY);
            } else {
                throw new UnsupportedOperationException("Unknown id uri: " + uri);
            }
        }
        public static String getDiaryDbNameFromSelectUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("select")) {
                return uri.getQueryParameter(DB_NAME_KEY);
            } else {
                throw new UnsupportedOperationException("Unknown id uri: " + uri);
            }
        }
        public static String getPasswordFromSelectUri(Uri uri) {
            if(uri.getPathSegments().get(1).equals("select")) {
                return uri.getQueryParameter(PASSWORD_KEY);
            } else {
                throw new UnsupportedOperationException("Unknown id uri: " + uri);
            }
        }

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_FILE_NAME + " TEXT PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT NOT NULL" +
                        ");";
    }

    public static final class DiaryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DIARY_ENTRY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY_ENTRY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY_ENTRY;

        public static final String TABLE_NAME = "diary";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildDiaryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " INTEGER NOT NULL, " +
                        COLUMN_TITLE + " TEXT NOT NULL, " +
                        COLUMN_CONTENT + " TEXT NOT NULL " +
                        ");";
    }

    public static final class DiaryDateViewEntry implements BaseColumns {
        public static final String DATE_VIEW_NAME = "diaryDate";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_DAY = "day";

        public static final String SQL_CREATE_DATE_VIEW =
                "CREATE VIEW " + DATE_VIEW_NAME + " AS " +
                        "SELECT " + _ID + ", " + DiaryEntry.COLUMN_DATE + ", " +
                        "strftime('%Y', " + DiaryEntry.COLUMN_DATE + ", 'unixepoch') AS " + COLUMN_YEAR + ", " +
                        "strftime('%m', " + DiaryEntry.COLUMN_DATE + ", 'unixepoch') AS " + COLUMN_MONTH + ", " +
                        "strftime('%d', " + DiaryEntry.COLUMN_DATE + ", 'unixepoch') AS " + COLUMN_DAY + " " +
                        "FROM " + DiaryEntry.TABLE_NAME + ";";
    }
}
