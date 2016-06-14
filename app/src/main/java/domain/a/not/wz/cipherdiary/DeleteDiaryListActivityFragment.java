package domain.a.not.wz.cipherdiary;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import domain.a.not.wz.cipherdiary.data.DiaryContract;
import domain.a.not.wz.cipherdiary.data.DiaryListRecyclerViewAdapter;
import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;
import domain.a.not.wz.cipherdiary.tools.RecyclerViewCursorAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class DeleteDiaryListActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DIARY_LIST_LOADER = 0;
    private DiaryListRecyclerViewAdapter mAdapter;
    private View mEmptyView;
    private RecyclerView mRecyclerView;

    //TODO: delete a diary
    public DeleteDiaryListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_delete_diary_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.diary_list_view);
        mEmptyView = rootView.findViewById(R.id.empty_view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mAdapter = new DiaryListRecyclerViewAdapter(getActivity(), null);
        getLoaderManager().initLoader(DIARY_LIST_LOADER, null, this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DiaryListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final Cursor cursor, int adapterType) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete this entry"); //TODO: change to string res
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String diaryFileName = cursor.getString(cursor.getColumnIndex(DiaryContract.DiaryList.COLUMN_FILE_NAME));
                        DiaryProviderHelper.deleteDiary(getContext().getContentResolver(), diaryFileName);
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        getActivity().setTitle(R.string.title_delete_diary_list);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DiaryProviderHelper.getDiaryListLoader(getContext());
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
