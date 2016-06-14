package domain.a.not.wz.cipherdiary.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import net.sqlcipher.database.SQLiteDatabase;

import domain.a.not.wz.cipherdiary.data.utils.PollingCheck;

/**
 * Created by Wz on 006, Jun 06.
 */
public class TestProviderDiaryEntries extends AndroidTestCase {
    public static final String LOG_TAG = TestProviderDiaryEntries.class.getSimpleName();
    private static DiaryDbHelper mOpenHelper = null;
    private static String dbFileName = null;
    private static final String DIARY_NAME = "diaryName";
    private static final String DIARY_PASSWORD = "pass";
    private static final long TEST_DATE = DiaryProvider.getTimeInSeconds(2014, 1, 20);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SQLiteDatabase.loadLibs(mContext);
        mOpenHelper = new DiaryDbHelper(mContext);
        dbFileName = mOpenHelper.createDb(DIARY_NAME, DIARY_PASSWORD);
        mOpenHelper.attachDB(dbFileName, DIARY_PASSWORD);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mOpenHelper.deleteDB(dbFileName);
    }

    public void selectDiary() {
        Uri newDiaryUri = DiaryContract.DiaryList.buildDiaryListSelectUri(dbFileName, DIARY_PASSWORD);
        Cursor cursor = mContext.getContentResolver().query(
                newDiaryUri,
                null,
                null,
                null,
                null
        );
        assertTrue("attach fail", cursor.moveToFirst());
    }

    public void testBasicDiaryQueries() {
        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");
        TestDb.insertIntoDiaryDb(mOpenHelper, testValues);

        selectDiary();
        Uri raw = DiaryContract.DiaryEntry.CONTENT_URI;
        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                raw,
                null,
                null,
                null,
                null
        );
        validateCursor("testBasicDiaryQueries, diary query", cursor, testValues);
    }

    public void testMainDiaryListQuery() {
        selectDiary();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");
        TestDb.insertIntoDiaryDb(mOpenHelper, testValues);

        ContentValues resultValues = new ContentValues();
        resultValues.put("year", "2014");
        resultValues.put("month", "01");

        // Test the basic content provider query
        Cursor cursor = DiaryProviderHelper.getYearMonthListView(mContext.getContentResolver());

        validateCursor("testBasicDiaryQueries, diary query", cursor, resultValues);
    }

    public void testMainDayListQuery() {

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");
        TestDb.insertIntoDiaryDb(mOpenHelper, testValues);

        ContentValues resultValues = new ContentValues();
        resultValues.put("day", "20");

        selectDiary();
        // Test the basic content provider query
        Cursor cursor = DiaryProviderHelper.getDaysOfMonthView(mContext.getContentResolver(), 2014, 1);
        validateCursor("testBasicDiaryQueries, diary query", cursor, resultValues);
    }

    public void testDiaryDayListQuery() {
        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");
        long id = TestDb.insertIntoDiaryDb(mOpenHelper, testValues);

        selectDiary();

        // Test the basic content provider query
        Cursor cursor = DiaryProviderHelper.getDiaryEntriesOfDate(mContext.getContentResolver(), 2014, 1, 20);
        validateCursor("testBasicDiaryQueries, diary query", cursor, testValues);
    }

    public void testDiaryEntryQuery() {
        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");
        long id = TestDb.insertIntoDiaryDb(mOpenHelper, testValues);

        selectDiary();
        // Test the basic content provider query
        Cursor cursor = DiaryProviderHelper.getDiaryEntry(mContext.getContentResolver(), Long.toString(id));

        assertTrue("more than two rows, id is unique", cursor.getCount() == 1);
        validateCursor("testBasicDiaryQueries, diary query", cursor, testValues);
    }


    public void testInsertReadDiaryEntryProvider() {
        selectDiary();

        ContentValues testValues = new ContentValues();
        testValues.put(DiaryContract.DiaryEntry.COLUMN_DATE, TEST_DATE);
        testValues.put(DiaryContract.DiaryEntry.COLUMN_TITLE, "North Pole");
        testValues.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, "Content ... ");

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestContentObserver tco = getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DiaryContract.DiaryEntry.CONTENT_URI, true, tco);
        Uri uri = mContext.getContentResolver().insert(DiaryContract.DiaryEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();;
        mContext.getContentResolver().unregisterContentObserver(tco);

        long rowId = ContentUris.parseId(uri);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Test the basic content provider query
        Cursor cursor = DiaryProviderHelper.getDiaryEntry(mContext.getContentResolver(), Long.toString(rowId));
        validateCursor("testBasicDiaryQueries, diary query", cursor, testValues);
    }

    public void testDeleteDiaryEntries() {
        testInsertReadDiaryEntryProvider();

        TestContentObserver tco = getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DiaryContract.DiaryEntry.CONTENT_URI, true, tco);

        deleteAllDiaryEntries();
        tco.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(tco);
    }

    public void deleteAllDiaryEntries() {
        mContext.getContentResolver().delete(
                DiaryContract.DiaryEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                DiaryContract.DiaryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from table during delete", 0, cursor.getCount());
        cursor.close();
    }


    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        TestDb.validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
