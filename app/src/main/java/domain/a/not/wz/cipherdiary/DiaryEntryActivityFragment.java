package domain.a.not.wz.cipherdiary;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import domain.a.not.wz.cipherdiary.data.DiaryContract;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;

/**
 * A fragment for the view of a diary entry
 */
public class DiaryEntryActivityFragment extends Fragment {

    private String mId;
    private TextView mTitleView;
    private TextView mDateView;
    private TextView mContentView;

    public DiaryEntryActivityFragment() {
    }

    public void setId(String id) {
        mId = id;
        Cursor c = DiaryProviderHelper.getDiaryEntry(getContext().getContentResolver(), id);
        if(!c.moveToFirst()) {
            throw new UnsupportedOperationException("id invalid");
        }
        mTitleView.setText(c.getString(c.getColumnIndex("title")));
        mContentView.setText(c.getString(c.getColumnIndex("content")));

        long date = c.getLong(c.getColumnIndex("date")) * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(date);
        mDateView.setText(sdf.format(cal.getTime()));
        c.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_entry, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.diary_entry_title);
        mDateView = (TextView) rootView.findViewById(R.id.diary_entry_date);
        mContentView= (TextView) rootView.findViewById(R.id.diary_entry_content);

        return rootView;
    }
}
