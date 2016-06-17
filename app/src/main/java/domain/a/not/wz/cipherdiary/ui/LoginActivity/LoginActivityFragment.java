package domain.a.not.wz.cipherdiary.ui.LoginActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.data.DiaryContract;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;
import domain.a.not.wz.cipherdiary.tools.NothingSelectedSpinnerAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {
    private static final String LOG_TAG = LoginActivityFragment.class.getSimpleName();

    private EditText mPasswordView;
    private OnPasswordInputListener mListener;
    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private String[] mFileDbNameMap;
    private InputFilter passwordFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            if (source instanceof SpannableStringBuilder) {
                SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder)source;
                for (int i = end - 1; i >= start; i--) {
                    char currentChar = source.charAt(i);
                    if (!Character.isLetterOrDigit(currentChar) && !Character.isSpaceChar(currentChar)) {
                        sourceAsSpannableBuilder.delete(i, i+1);
                    }
                }
                return source;
            } else {
                StringBuilder filteredStringBuilder = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);
                    if (Character.isLetterOrDigit(currentChar) || Character.isSpaceChar(currentChar)) {
                        filteredStringBuilder.append(currentChar);
                    }
                }
                return filteredStringBuilder.toString();
            }
        }
    };


    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mPasswordView = (EditText) rootView.findViewById(R.id.password_text);
        mSpinner = (Spinner) rootView.findViewById(R.id.database_spinner);
        Button decryptButton = (Button) rootView.findViewById(R.id.decrypt_button);

        mPasswordView.setFilters(new InputFilter[] {passwordFilter});

        setUpSpinner();

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDecryption(v);
            }
        });

        loadSelectedDiaryPref();

        return rootView;
    }

    private void loadSelectedDiaryPref() {
        //load database prefs
        Context context = getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int pos = sharedPref.getInt(getString(R.string.database_selected_key), 0);
        mSpinner.setSelection(pos);
    }

    public void updateSpinnerList() {
        List<String> items = getSpinnerList();
        mSpinnerAdapter.clear();
        if(items != null) {
            mSpinnerAdapter.addAll(items);
        }
    }

    private List<String> getSpinnerList() {
        List<String> items = new ArrayList<String>();
        Cursor c = DiaryProviderHelper.getDiaryList(getContext().getContentResolver());

        mFileDbNameMap = new String[c.getCount()];
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            String diaryName = c.getString(c.getColumnIndex(DiaryContract.DiaryList.COLUMN_NAME));
            String dbName = c.getString(c.getColumnIndex(DiaryContract.DiaryList.COLUMN_FILE_NAME));
            items.add(diaryName);
            mFileDbNameMap[i] = dbName;
            c.moveToNext();
        }
        c.close();
        return items;

    }

    private void setUpSpinner(){
        List<String> items = getSpinnerList();

        mSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerAdapter test = new NothingSelectedSpinnerAdapter(mSpinnerAdapter,
                R.layout.spinner_unseleted, getContext());
        mSpinner.setAdapter(test);
    }

    private void performDecryption(View v) {
        if (inputCheck(v)) {
            return;
        }
        if(mListener != null) {
            String selectedDbName = mFileDbNameMap[mSpinner.getSelectedItemPosition() - 1];
            String diaryName = (String)mSpinner.getSelectedItem();
            String password = mPasswordView.getText().toString();

            if(!mListener.onDatabaseAuthentication(diaryName, selectedDbName, password)){
                //error message password fail
                Snackbar.make(v, "Password incorrect", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            storeSelectedDiaryPref();
        }
    }

    private boolean inputCheck(View v) {
        if(mSpinner.getSelectedItemPosition() == 0){
            //error message
            Snackbar.make(v, "datebase not selected", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        if(TextUtils.isEmpty(mPasswordView.getText())){
            //error message
            Snackbar.make(v, "Password field empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        return false;
    }

    private void storeSelectedDiaryPref() {
        // store database pref
        Context context = getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.database_selected_key), mSpinner.getSelectedItemPosition());
        editor.apply();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPasswordInputListener) {
            mListener = (OnPasswordInputListener) context;
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

    public interface OnPasswordInputListener {
        boolean onDatabaseAuthentication(String diaryName, String dbFileName, String password);
    }
}
