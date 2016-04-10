package com.rahoria.nitin.poketassistant.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahoria.nitin.poketassistant.ItemDetails;
import com.rahoria.nitin.poketassistant.Provider.MsgProvider;
import com.rahoria.nitin.poketassistant.R;

import java.text.SimpleDateFormat;

/**
 * Created by nitin on 4/4/2016.
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MyViewHolder> {

    private final Context context;
    private final Cursor cursor;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView sendingTo, msgBody, sendingAt;
        public ImageView icon;
        private int id;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            icon = (ImageView) view.findViewById(R.id.msgRowImageView);
            sendingTo = (TextView) view.findViewById(R.id.toTextView);
            msgBody = (TextView) view.findViewById(R.id.msgBodyTextView);
            sendingAt = (TextView) view.findViewById(R.id.sendingTimeTextView);
        }

        @Override
        public void onClick(View v) {
            Log.d("NITIN","onClick  : id :"+id);
            FragmentManager fr = ((Activity)context).getFragmentManager();
            ItemDetails itemDetails = new ItemDetails();
            Bundle bundle = new Bundle();
            bundle.putInt("ID",id);
            itemDetails.setArguments(bundle);
            itemDetails.show(fr, "recyclerviewItemDetails");

        }

        public void setId(int id) {
            this.id  = id;
        }
    }


    public MsgAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_row_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setId(cursor.getInt(MsgProvider.MSG_SCHEDULER_ID_INDEX));
        Log.d("NITIN", "IN Adapter onBindViewHolder");
        switch (cursor.getString(MsgProvider.MSG_SCHEDULER_TYPE_INDEX)){
            /*case MsgProvider.MSG_SCHEDULER_TYPE_DRAFT:
                holder.icon.setImageResource(R.drawable.ic_insert_invitation_black);
                holder.icon.setBackgroundResource(R.color.colorAccent);
                break;*/
            case MsgProvider.MSG_SCHEDULER_TYPE_SMS:
                holder.icon.setImageResource(R.drawable.ic_textsms_black);
                holder.icon.setBackgroundResource(R.color.smsFab);
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_EMAIL:
                holder.icon.setImageResource(R.drawable.ic_mail_outline_black);
                holder.icon.setBackgroundResource(R.color.emailFab);
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_WHATSAPP:
                break;
        }
        holder.sendingTo.setText(cursor.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX));
        holder.msgBody.setText(cursor.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_BODY_INDEX));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"+" "+"hh:mm a");
        String time = sdf.format(Long.parseLong(cursor.getString(MsgProvider.MSG_SCHEDULER_SENDING_AT_INDEX), 10));
        holder.sendingAt.setText(time);
    }

    @Override
    public int getItemCount() {
        if(cursor != null)
            return cursor.getCount();
        else
            return 0;
    }
}