package domain.a.not.wz.cipherdiary.ui.DiaryEntryActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import domain.a.not.wz.cipherdiary.R;

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
