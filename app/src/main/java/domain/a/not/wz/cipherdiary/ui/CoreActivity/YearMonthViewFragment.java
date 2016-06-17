package domain.a.not.wz.cipherdiary.ui.CoreActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;
import domain.a.not.wz.cipherdiary.data.DiaryRecyclerViewAdapter;

/**
 * Created by Wz on 017, Jun 17.
 */
public class YearMonthViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DiaryRecyclerViewAdapter.OnItemClickListener {

    private static final int DIARY_LOADER = 1;
    public static final int CORE_FRAGMENT_ID = 100;

    private View mEmptyView;
    private RecyclerView mRecyclerView;
    private DiaryRecyclerViewAdapter mAdapter;
    private OnYearMonthSelectedListener mListener;

    public interface OnYearMonthSelectedListener {
        public void onYearMonthSelected(int year, int month);
    }

    private void setActionBarTitle() {
        String diaryName = getArguments().getString(CoreActivity.DIARY_NAME_KEY);
        getActivity().setTitle(diaryName);
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
        if (context instanceof OnYearMonthSelectedListener) {
            mListener = (OnYearMonthSelectedListener) context;
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
        int year = cursor.getInt(cursor.getColumnIndex("year"));
        int month = cursor.getInt(cursor.getColumnIndex("month"));
        mListener.onYearMonthSelected(year, month);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DiaryProviderHelper.getYearMonthListViewLoader(getActivity());
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
