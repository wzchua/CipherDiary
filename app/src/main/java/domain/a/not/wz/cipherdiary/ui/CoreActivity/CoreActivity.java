package domain.a.not.wz.cipherdiary.ui.CoreActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

import domain.a.not.wz.cipherdiary.ui.DiaryEntryActivity.DiaryEntryActivity;
import domain.a.not.wz.cipherdiary.ui.InputEntryActivity.InputEntryActivity;
import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.ui.LoginActivity.LoginActivity;

public class CoreActivity extends AppCompatActivity implements YearMonthViewFragment.OnYearMonthSelectedListener, DayViewFragment.OnDaySelectedListener, EntryViewFragment.OnEntrySelectedListener {
    private static final String LOG_TAG = CoreActivity.class.getSimpleName();
    public static final String DIARY_NAME_KEY = "diaryName";
    public static final int CORE_FRAGMENT_YEAR_MONTH_LISTVIEW = 100;
    public static final int CORE_FRAGMENT_DAY_LISTVIEW = 200;
    public static final int CORE_FRAGMENT_ENTRIES_LISTVIEW = 300;
    static final String CORE_TYPE_KEY = "CORE_FRAGMENT_TYPE";
    static final String CORE_YEAR_KEY = "CORE_FRAGMENT_YEAR";
    static final String CORE_MONTH_KEY = "CORE_FRAGMENT_MONTH";
    static final String CORE_DAY_KEY = "CORE_FRAGMENT_DAY";

    private boolean mTwoPane = false;
    private String mDiaryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        Toolbar toolbar = (Toolbar) findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);

        mDiaryName = getIntent().getStringExtra(DIARY_NAME_KEY);

        YearMonthViewFragment fragment = new YearMonthViewFragment();
        Bundle args = new Bundle();
        args.putInt(CORE_TYPE_KEY, CORE_FRAGMENT_YEAR_MONTH_LISTVIEW);
        args.putString(DIARY_NAME_KEY, mDiaryName);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_core_container, fragment)
                .commit();

        //don't really like this but will leave it as it is
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_core, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_entry) {
            Intent intent = new Intent(this, InputEntryActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_lock_diary) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id == android.R.id.home) {
            FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEntrySelected(String id) {
        Intent intent = new Intent(this, DiaryEntryActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra(DIARY_NAME_KEY, mDiaryName);
        startActivity(intent);
    }

    @Override
    public void onYearMonthSelected(int year, int month) {
        DayViewFragment fragment = new DayViewFragment();
        Bundle args = new Bundle();
        args.putInt(CORE_TYPE_KEY, CORE_FRAGMENT_DAY_LISTVIEW);
        args.putString(CORE_YEAR_KEY,
                String.format(Locale.ENGLISH, "%04d", year));
        args.putString(CORE_MONTH_KEY,
                String.format(Locale.ENGLISH, "%02d", month));
        args.putString(DIARY_NAME_KEY, mDiaryName);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_core_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDaySelected(int year, int month, int day) {
        EntryViewFragment fragment = new EntryViewFragment();
        Bundle args = new Bundle();
        args.putInt(CORE_TYPE_KEY, CORE_FRAGMENT_ENTRIES_LISTVIEW);
        args.putString(CORE_YEAR_KEY,
                String.format(Locale.ENGLISH, "%04d", year));
        args.putString(CORE_MONTH_KEY,
                String.format(Locale.ENGLISH, "%02d", month));
        args.putString(CORE_DAY_KEY,
                String.format(Locale.ENGLISH, "%02d", day));
        args.putString(DIARY_NAME_KEY, mDiaryName);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_core_container, fragment)
                .addToBackStack(null)
                .commit();

    }
}
