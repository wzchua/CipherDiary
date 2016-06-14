package domain.a.not.wz.cipherdiary.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
 * Created by Wz on 001, Jun 01.
 */
public class DiaryRecyclerViewAdapter extends RecyclerViewCursorAdapter<DiaryRecyclerViewAdapter.ViewHolder>{

    private Context mContext;
    private OnItemClickListener mListener;
    private int mAdapterType; //controls the list type (ym/d/entries)

    public DiaryRecyclerViewAdapter(Context context, Cursor cursor, int adapterType) {
        super(cursor);
        mContext = context;
        mAdapterType = adapterType;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        viewHolder.bindView(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_list_item, parent, false);

        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, Cursor cursor, int adapterType);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeader = (TextView)itemView.findViewById(R.id.basic_list_item_text);
            itemView.setOnClickListener(this);
        }
        //TODO: style header
        public void bindView(Cursor c) {
            switch(mAdapterType) {
                case CoreActivityFragment.CORE_FRAGMENT_YEAR_MONTH_LISTVIEW: {
                    String header = c.getString(c.getColumnIndex("year")) + "-" +
                            c.getString(c.getColumnIndex("month"));
                    mHeader.setText(header);
                    break;
                }
                case CoreActivityFragment.CORE_FRAGMENT_DAY_LISTVIEW: {
                    String header = c.getString(c.getColumnIndex("day"));
                    mHeader.setText(header);

                    break;
                }
                case CoreActivityFragment.CORE_FRAGMENT_ENTRIES_LISTVIEW: {
                    String header = c.getString(c.getColumnIndex("title"));
                    mHeader.setText(header);

                    break;
                }
                default:
                    throw new UnsupportedOperationException("adapter type unrecognized: " + mAdapterType);
            }
        }

        @Override
        public void onClick(View v) {
            Log.v("DiaryRVAdapter", "ViewHolder.onClick");
            if(mListener != null) {
                int pos = getAdapterPosition();
                getCursor().moveToPosition(pos);
                mListener.onItemClick(v, getCursor(), mAdapterType);
            }
        }
    }
    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            mDivider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
