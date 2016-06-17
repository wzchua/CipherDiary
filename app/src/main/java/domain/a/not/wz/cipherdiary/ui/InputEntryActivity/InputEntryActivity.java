package domain.a.not.wz.cipherdiary.ui.InputEntryActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;

public class InputEntryActivity extends AppCompatActivity implements InputEntryFragment.OnFragmentInteractionListener {
    static final int DATE_PICKER_KEY = 100;
    static final int SAVE_ENTRY_KEY = 200;

    private int lastSelectedYear = -1;
    private int lastSelectedMonth = -1;
    private int mLastSelectedDay = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.input_entry_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(int key, ContentValues values) {
        switch(key) {
            case DATE_PICKER_KEY: {
                DatePickerFragment datePickerFragment = generateDatePicker();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            }
            case SAVE_ENTRY_KEY: {
                DiaryProviderHelper.createDiaryEntry(getContentResolver(), values);
                finish();
                break;
            }
            default:
                throw new UnsupportedOperationException("key invalid: " + key);
        }
    }

    private DatePickerFragment generateDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        if(lastSelectedMonth != -1) {
            Bundle args = new Bundle();
            args.putInt("YEAR", lastSelectedYear);
            args.putInt("MONTH", lastSelectedMonth);
            args.putInt("DAY", mLastSelectedDay);
            newFragment.setArguments(args);
        }
        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                lastSelectedYear = year;
                lastSelectedMonth = monthOfYear;
                mLastSelectedDay = dayOfMonth;
                updateDate(new GregorianCalendar(year, monthOfYear, dayOfMonth));
            }
        });
        return newFragment;
    }

    public void updateDate(GregorianCalendar c) {
        InputEntryFragment ief = (InputEntryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_input_entry);
        ief.setDateText(c);
    }


    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Bundle args = getArguments();
            int year, month, day;
            if(args == null) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = args.getInt("YEAR");
                month = args.getInt("MONTH");
                day = args.getInt("DAY");
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), mListener, year, month, day);
        }

        public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener){
            mListener = listener;
        }
    }
}
