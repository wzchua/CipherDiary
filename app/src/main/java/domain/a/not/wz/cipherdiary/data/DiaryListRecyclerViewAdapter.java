package domain.a.not.wz.cipherdiary.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import domain.a.not.wz.cipherdiary.CoreActivityFragment;
import domain.a.not.wz.cipherdiary.R;
import domain.a.not.wz.cipherdiary.tools.RecyclerViewCursorAdapter;

/**
 * Created by Wz on 013, Jun 13.
 */
public class DiaryListRecyclerViewAdapter extends RecyclerViewCursorAdapter<DiaryListRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private int mAdapterType; //controls the list type (ym/d/entries)

    public interface OnItemClickListener {
        public void onItemClick(View view, Cursor cursor, int adapterType);
    }

    public DiaryListRecyclerViewAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(DiaryListRecyclerViewAdapter.ViewHolder viewHolder, Cursor cursor, int position) {
        viewHolder.bindView(cursor);
    }

    @Override
    public DiaryListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_list_item, parent, false);

        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeader = (TextView)itemView.findViewById(R.id.basic_list_item_text);
            itemView.setOnClickListener(this);
        }

        public void bindView(Cursor c) {
            String header = c.getString(c.getColumnIndex(DiaryContract.DiaryList.COLUMN_NAME));
            mHeader.setText(header);
        }

        @Override
        public void onClick(View v) {
            Log.v("DiaryListRVAdapter", "ViewHolder.onClick");
            if(mListener != null) {
                int pos = getAdapterPosition();
                getCursor().moveToPosition(pos);
                mListener.onItemClick(v, getCursor(), mAdapterType);
            }
        }
    }
}
