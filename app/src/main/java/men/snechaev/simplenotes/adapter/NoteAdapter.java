package men.snechaev.simplenotes.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import men.snechaev.simplenotes.R;
import men.snechaev.simplenotes.util.RecyclerViewCursorAdapter;


public class NoteAdapter extends RecyclerViewCursorAdapter<NoteAdapter.MyNoteViewHolder> {

    private final Context mContext;
    private RecyclerViewOnItemClickListener mOnItemClickListener;
    private onSwipeListener mOnSwipeListener;


    public NoteAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, parent, false);
        return new MyNoteViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final MyNoteViewHolder holder, Cursor cursor) {
        int position = cursor.getPosition();
        holder.tv.setText(cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_CONTENT)));
        holder.tv_dateTime.setText(cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_DATETIME)));
        holder.mRowtab.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_IMPORTANT)) == 1 ?
                mContext.getResources().getColor(R.color.colorAccent) : mContext.getResources().getColor(android.R.color.white)
        );
        holder.root.setTag(position);
        ((SwipeMenuLayout) holder.root.findViewById(R.id.swipeMenuLayout)).setIos(false).setLeftSwipe(false).setSwipeEnable(true);

        holder.btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSwipeListener != null) {
                    mOnSwipeListener.onTop(holder.getAdapterPosition());
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSwipeListener != null) {
                    mOnSwipeListener.onDel(holder.getAdapterPosition());
                }
            }
        });

        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(view, holder.getAdapterPosition());
                }
            }
        });


    }

//    @Override
//    protected void onContentChanged() { }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

//    public RecyclerViewOnItemClickListener getOnItemClickListener() {
//        return mOnItemClickListener;
//    }
//
//    public onSwipeListener getOnSwipeListener() {
//        return mOnSwipeListener;
//    }


    public void setOnSwipeListener(onSwipeListener mOnSwipeListener) {
        this.mOnSwipeListener = mOnSwipeListener;
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    public interface onSwipeListener {
        void onDel(int pos);
        void onTop(int pos);
    }


    class MyNoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv;
        private final TextView tv_dateTime;
        private final View mRowtab;
        private final Button btnTop;
        private final Button btnDelete;
        private final View root;

        MyNoteViewHolder(View root) {
            super(root);
            this.root = root;
            tv = root.findViewById(R.id.row_text);
            tv_dateTime = root.findViewById(R.id.tv_note_time);
            mRowtab = root.findViewById(R.id.row_tab);
            btnTop = root.findViewById(R.id.btnTop);
            btnDelete = root.findViewById(R.id.btnDelete);
        }
    }

}
