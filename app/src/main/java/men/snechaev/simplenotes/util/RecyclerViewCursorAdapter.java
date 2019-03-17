package men.snechaev.simplenotes.util;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements Filterable, CursorFilter.CursorFilterClient {

    private boolean mDataValid;
    private Cursor mCursor;
    private int mRowIDColumn;
    private ChangeObserver mChangeObserver;
    private DataSetObserver mDataSetObserver;
    private CursorFilter mCursorFilter;
    private FilterQueryProvider mFilterQueryProvider;
    private static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x02;

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    public RecyclerViewCursorAdapter(Context context, Cursor c, int flags) {
        init(c, flags);
    }

    private void init(Cursor c, int flags) {

        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        if ((flags & FLAG_REGISTER_CONTENT_OBSERVER) == FLAG_REGISTER_CONTENT_OBSERVER) {
            mChangeObserver = new ChangeObserver();
            mDataSetObserver = new MyDataSetObserver();
        } else {
            mChangeObserver = null;
            mDataSetObserver = null;
        }

        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
        setHasStableIds(true);
    }


    @Override
    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
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

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder, mCursor);
    }


    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }


    private Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;

            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;

            notifyDataSetChanged();
        }
        return oldCursor;
    }


    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }


    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }

        return mCursor;
    }

    public Filter getFilter() {
        if (mCursorFilter == null) {
            mCursorFilter = new CursorFilter(this);
        }
        return mCursorFilter;
    }


    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }


    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }


    protected abstract void onContentChanged();

    private class ChangeObserver extends ContentObserver {
        ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}
