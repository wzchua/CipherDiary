package domain.a.not.wz.cipherdiary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class DiaryEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);//crashing here
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DiaryEntryActivityFragment fragment = (DiaryEntryActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_diaryentry);
        String id = getIntent().getStringExtra("ID");
        Log.v("DiaryEntryActivity", "onCreate " + id);
        fragment.setId(id);
    }

}
