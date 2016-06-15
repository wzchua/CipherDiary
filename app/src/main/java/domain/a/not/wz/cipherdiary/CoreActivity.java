package domain.a.not.wz.cipherdiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

public class CoreActivity extends AppCompatActivity implements CoreActivityFragment.OnDiaryEntrySelectedListener {
    private static final String LOG_TAG = CoreActivity.class.getSimpleName();
    public static final String DIARY_NAME_KEY = "diaryName";
    private boolean mTwoPane = false;
    private String mDiaryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        Toolbar toolbar = (Toolbar) findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);

        mDiaryName = getIntent().getStringExtra(DIARY_NAME_KEY);

        CoreActivityFragment fragment = new CoreActivityFragment();
        Bundle args = new Bundle();
        args.putInt(CoreActivityFragment.CORE_TYPE_KEY, CoreActivityFragment.CORE_FRAGMENT_YEAR_MONTH_LISTVIEW);
        args.putString(DIARY_NAME_KEY, mDiaryName);
        fragment.setArguments(args);
        fragment.setOnDiarySelectedListener(this);

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

        //TODO: add a lock button: action terminates this activity and starts LoginActivity
        if (id == R.id.action_add_entry) {
            Intent intent = new Intent(this, InputEntryActivity.class);
            startActivity(intent);
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
    public void onItemSelected(String id) {
        Intent intent = new Intent(this, DiaryEntryActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra(DIARY_NAME_KEY, mDiaryName);
        startActivity(intent);
    }

    @Override
    public void onYearMonthSelected(int year, int month) {
        CoreActivityFragment fragment = new CoreActivityFragment();
        Bundle args = new Bundle();
        args.putInt(CoreActivityFragment.CORE_TYPE_KEY, CoreActivityFragment.CORE_FRAGMENT_DAY_LISTVIEW);
        args.putString(CoreActivityFragment.CORE_YEAR_KEY,
                String.format(Locale.ENGLISH, "%04d", year));
        args.putString(CoreActivityFragment.CORE_MONTH_KEY,
                String.format(Locale.ENGLISH, "%02d", month));
        args.putString(DIARY_NAME_KEY, mDiaryName);
        fragment.setArguments(args);
        fragment.setOnDiarySelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_core_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onYearMonthDaySelected(int year, int month, int day) {
        CoreActivityFragment fragment = new CoreActivityFragment();
        Bundle args = new Bundle();
        args.putInt(CoreActivityFragment.CORE_TYPE_KEY, CoreActivityFragment.CORE_FRAGMENT_ENTRIES_LISTVIEW);
        args.putString(CoreActivityFragment.CORE_YEAR_KEY,
                String.format(Locale.ENGLISH, "%04d", year));
        args.putString(CoreActivityFragment.CORE_MONTH_KEY,
                String.format(Locale.ENGLISH, "%02d", month));
        args.putString(CoreActivityFragment.CORE_DAY_KEY,
                String.format(Locale.ENGLISH, "%02d", day));
        args.putString(DIARY_NAME_KEY, mDiaryName);
        fragment.setArguments(args);
        fragment.setOnDiarySelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_core_container, fragment)
                .addToBackStack(null)
                .commit();

    }
}
