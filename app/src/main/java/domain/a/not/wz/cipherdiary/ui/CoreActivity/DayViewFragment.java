package domain.a.not.wz.cipherdiary.ui.CoreActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Locale;

import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;
import domain.a.not.wz.cipherdiary.data.DiaryRecyclerViewAdapter;

/**
 * Created by Wz on 017, Jun 17.
 */
public class DayViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DiaryRecyclerViewAdapter.OnItemClickListener {

    private static final int DIARY_LOADER = 1;
    public static final int CORE_FRAGMENT_ID = 200;
    static final String CORE_YEAR_KEY = "CORE_FRAGMENT_YEAR";
    static final String CORE_MONTH_KEY = "CORE_FRAGMENT_MONTH";

    private View mEmptyView;
    private RecyclerView mRecyclerView;
    private DiaryRecyclerViewAdapter mAdapter;
    private OnDaySelectedListener mListener;

    public interface OnDaySelectedListener {
        public void onDaySelected(int year, int month, int day);
    }

    private void setActionBarTitle() {
        Bundle args = getArguments();
        String diaryName = args.getString(CoreActivity.DIARY_NAME_KEY);
        //TODO: change to date format
        String yearString = args.getString(CORE_YEAR_KEY);
        int month = Integer.parseInt(args.getString(CORE_MONTH_KEY));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month - 1);
        String monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        String title = diaryName + " - " + yearString + " " + monthString;
        getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_core, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.date_list_view);
        mEmptyView = rootView.findViewById(R.id.empty_view);

        mAdapter = new DiaryRecyclerViewAdapter(null, CORE_FRAGMENT_ID, this);
        getLoaderManager().initLoader(DIARY_LOADER, null, this);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new DiaryRecyclerViewAdapter.DividerItemDecoration(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        setActionBarTitle();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDaySelectedListener) {
            mListener = (OnDaySelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEntrySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View view, Cursor cursor) {
        int year = Integer.parseInt(getArguments().getString(CORE_YEAR_KEY));
        int month = Integer.parseInt(getArguments().getString(CORE_MONTH_KEY));
        int day = Integer.parseInt(cursor.getString(cursor.getColumnIndex("day")));
        mListener.onDaySelected(year, month, day);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String year = getArguments().getString(CORE_YEAR_KEY);
        String month = getArguments().getString(CORE_MONTH_KEY);
        //TODO: consolidate whether yy mm dd should be passed in int or string
        return DiaryProviderHelper.getDaysOfMonthViewLoader(getActivity(),
                Integer.parseInt(year), Integer.parseInt(month));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if(mAdapter.getItemCount() == 0){
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
