package domain.a.not.wz.cipherdiary;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Locale;

import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;
import domain.a.not.wz.cipherdiary.data.DiaryRecyclerViewAdapter;

/**
 * A fragment containing for the view of list for navigating diary entries
 */
public class CoreActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int CORE_FRAGMENT_YEAR_MONTH_LISTVIEW = 100;
    public static final int CORE_FRAGMENT_DAY_LISTVIEW = 200;
    public static final int CORE_FRAGMENT_ENTRIES_LISTVIEW = 300;
    static final String CORE_TYPE_KEY = "CORE_FRAGMENT_TYPE";
    static final String CORE_YEAR_KEY = "CORE_FRAGMENT_YEAR";
    static final String CORE_MONTH_KEY = "CORE_FRAGMENT_MONTH";
    static final String CORE_DAY_KEY = "CORE_FRAGMENT_DAY";

    private int mCoreType;
    private OnDiaryEntrySelectedListener mListener;
    private DiaryRecyclerViewAdapter mAdapter;
    private static final int DIARY_LOADER = 1;

    private View mEmptyView;
    private RecyclerView mRecyclerView;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface OnDiaryEntrySelectedListener {
        public void onItemSelected(String id);
        public void onYearMonthDaySelected(int year, int month, int day);
        public void onYearMonthSelected(int year, int month);
    }


    public CoreActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_core, container, false);
        Bundle args = getArguments();
        mCoreType = args.getInt(CORE_TYPE_KEY);
        mAdapter = new DiaryRecyclerViewAdapter(getActivity(), null, mCoreType);
        getLoaderManager().initLoader(DIARY_LOADER, null, this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.date_list_view);
        mEmptyView = rootView.findViewById(R.id.empty_view);

        setActionBarTitle(args);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new DiaryRecyclerViewAdapter.DividerItemDecoration(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DiaryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Cursor cursor, int adapterType) {
                Log.v("CoreActivityFramgent", "setOnItemClickListener");
                switch(adapterType) {
                    case CORE_FRAGMENT_YEAR_MONTH_LISTVIEW: {
                        int year = cursor.getInt(cursor.getColumnIndex("year"));
                        int month = cursor.getInt(cursor.getColumnIndex("month"));
                        mListener.onYearMonthSelected(year, month);
                        break;
                    }
                    case CORE_FRAGMENT_DAY_LISTVIEW: {
                        int year = Integer.parseInt(getArguments().getString(CORE_YEAR_KEY));
                        int month = Integer.parseInt(getArguments().getString(CORE_MONTH_KEY));
                        //bug
                        //int day = cursor.getInt(cursor.getColumnIndex("day"));
                        int day = Integer.parseInt(cursor.getString(cursor.getColumnIndex("day")));
                        mListener.onYearMonthDaySelected(year, month, day);
                        break;
                    }
                    case CORE_FRAGMENT_ENTRIES_LISTVIEW: {
                        String id = cursor.getString(cursor.getColumnIndex("_id"));
                        mListener.onItemSelected(id);
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("invalid core type");
                }
            }
        });

        return rootView;
    }

    private void setActionBarTitle(Bundle args) {
        String diaryName = args.getString(CoreActivity.DIARY_NAME_KEY);
        switch(mCoreType) {
            case CORE_FRAGMENT_YEAR_MONTH_LISTVIEW: {
                getActivity().setTitle(diaryName);
                break;
            }
            case CORE_FRAGMENT_DAY_LISTVIEW: {
                //TODO: change to date format
                String yearString = args.getString(CORE_YEAR_KEY);
                int month = Integer.parseInt(args.getString(CORE_MONTH_KEY));
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, month - 1);
                String monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

                String title = diaryName + " - " + yearString + " " + monthString;
                getActivity().setTitle(title);
                break;
            }
            case CORE_FRAGMENT_ENTRIES_LISTVIEW: {
                String yearString = args.getString(CORE_YEAR_KEY);
                int month = Integer.parseInt(args.getString(CORE_MONTH_KEY));
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, month - 1);
                String monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
                String dayString = args.getString(CORE_DAY_KEY);
                String title = diaryName + " - " + yearString + " " + monthString + " " + dayString;

                getActivity().setTitle(title);
                break;
            }
            default:
                throw new IllegalArgumentException("invalid core type");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (mCoreType) {
            case CORE_FRAGMENT_YEAR_MONTH_LISTVIEW: {
                Log.v("CoreActivityFragment", "onCreateLoader YM list");
                cursorLoader = DiaryProviderHelper.getYearMonthListViewLoader(getActivity());
                break;
            }
            case CORE_FRAGMENT_DAY_LISTVIEW: {
                Log.v("CoreActivityFragment", "onCreateLoader D list");
                String year = getArguments().getString(CORE_YEAR_KEY);
                String month = getArguments().getString(CORE_MONTH_KEY);
                //TODO: consolidate whether yy mm dd should be passed in int or string
                cursorLoader = DiaryProviderHelper.getDaysOfMonthViewLoader(getActivity(),
                        Integer.parseInt(year), Integer.parseInt(month));
                break;
            }
            case CORE_FRAGMENT_ENTRIES_LISTVIEW: {
                Log.v("CoreActivityFragment", "onCreateLoader entry list");
                String year = getArguments().getString(CORE_YEAR_KEY);
                String month = getArguments().getString(CORE_MONTH_KEY);
                String day = getArguments().getString(CORE_DAY_KEY);

                cursorLoader = DiaryProviderHelper.getDiaryEntriesOfDateLoader(getActivity(),
                        Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                break;
            }
            default:
                throw new IllegalArgumentException("unknown core type");
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v("CoreActivityFragment", "onLoadFinished");
//        if(data.moveToFirst()) {
//            String[] names = data.getColumnNames();
//            for (String name : names) {
//                Log.v("CoreActivityFragment", "Cursor: " + name + " : "
//                        + data.getString(data.getColumnIndex(name)));
//            }
//        }
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

    public void setOnDiarySelectedListener(OnDiaryEntrySelectedListener listener) {
        mListener = listener;
    }
}
