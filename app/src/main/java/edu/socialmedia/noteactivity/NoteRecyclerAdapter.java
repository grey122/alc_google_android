package edu.socialmedia.noteactivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.viewHolder> {

   private final Context mContext;
   private final List<NoteInfo> mNotes;
    private final LayoutInflater mLayoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        mNotes = notes;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        NoteInfo note = mNotes.get(position);
        holder.mTextCourse.setText(note.getCourse().getTitle());
        holder.mTextTitle.setText(note.getTitle());
        holder.mCurrentPosition= position;
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{


        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mCurrentPosition;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(MainActivity.NOTE_POSITION, mCurrentPosition);
                    mContext.startActivity(intent);

                }
            });

        }
    }

}
