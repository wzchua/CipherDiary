package domain.a.not.wz.cipherdiary.ui.InputEntryActivity;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.data.DiaryContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputEntryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InputEntryFragment extends Fragment {

    private TextView mTitleView;
    private TextView mContentView;
    private TextView mDateView;
    private GregorianCalendar mSelectedDate;

    private OnFragmentInteractionListener mListener;

    public InputEntryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_input_entry, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.input_entry_title);
        mContentView = (TextView) rootView.findViewById(R.id.input_entry_content);

        //set date setter to current date
        mDateView = (TextView) rootView.findViewById(R.id.input_entry_date_display);

        setDateText(new GregorianCalendar());

        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onFragmentInteraction(InputEntryActivity.DATE_PICKER_KEY, null);
                }
            }
        });

        Button save_btn = (Button) rootView.findViewById(R.id.button_save_entry);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mTitleView.getText()) || TextUtils.isEmpty(mContentView.getText())) {
                    Snackbar.make(v, "Text field should not be empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }

                if(mListener != null) {
                    String title = mTitleView.getText().toString();
                    String content = mContentView.getText().toString();
                    long date = mSelectedDate.getTimeInMillis()/1000L;

                    ContentValues values = new ContentValues();
                    values.put(DiaryContract.DiaryEntry.COLUMN_DATE, date);
                    values.put(DiaryContract.DiaryEntry.COLUMN_TITLE, title);
                    values.put(DiaryContract.DiaryEntry.COLUMN_CONTENT, content);

                    mListener.onFragmentInteraction(InputEntryActivity.SAVE_ENTRY_KEY, values);
                }
            }
        });
        return rootView;
    }

    public void setDateText(GregorianCalendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, EEE");
        mSelectedDate = c;
        mDateView.setText(sdf.format(c.getTime()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDiaryEntrySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int key, ContentValues values);
    }
}
