package domain.a.not.wz.cipherdiary.tools;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Wz on 025, May 25.
 * This is designed for use with CursorLoader. No observers are registered internally.
 */
public abstract class RecyclerViewCursorAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIDColumn;

    public RecyclerViewCursorAdapter(Cursor cursor) {
        boolean cursorPresent = cursor != null;
        mCursor = cursor;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? cursor.getColumnIndexOrThrow("_id") : -1;

        setHasStableIds(true);
    }

    /**
     * Returns the cursor.
     * @return the cursor.
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }
    /**
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public abstract void onBindViewHolder(T viewHolder, Cursor cursor, int position);

    @Override
    public void onBindViewHolder(T viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor, position);
    }

    /**
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
        }
        // notify the observers about the change
        notifyDataSetChanged();
        return oldCursor;
    }
}
