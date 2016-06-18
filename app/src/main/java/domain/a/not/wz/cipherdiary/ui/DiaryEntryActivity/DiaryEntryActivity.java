package domain.a.not.wz.cipherdiary.ui.DiaryEntryActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;
import domain.a.not.wz.cipherdiary.ui.InputEntryActivity.InputEntryActivity;
import domain.a.not.wz.cipherdiary.ui.LoginActivity.LoginActivity;

public class DiaryEntryActivity extends AppCompatActivity {
    private String mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);//crashing here
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DiaryEntryActivityFragment fragment = (DiaryEntryActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_diaryentry);
        mID = getIntent().getStringExtra("ID");

        Log.v("DiaryEntryActivity", "onCreate " + mID);
        fragment.setId(mID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_diary_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete_entry) {
            Log.v("DiaryEntryActivity", "delete action button pressed");
            DiaryProviderHelper.deleteDiaryEntry(getContentResolver(), mID);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
