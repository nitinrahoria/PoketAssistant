package com.rahoria.nitin.poketassistant;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rahoria.nitin.poketassistant.MsgScheduling.CreateDelayMsgActivity;
import com.rahoria.nitin.poketassistant.Provider.MsgProvider;

import java.text.SimpleDateFormat;


/**
 * Created by nitin on 4/8/2016.
 */
public class ItemDetails extends DialogFragment implements View.OnClickListener{

    private int id;
    private String type;

    public ItemDetails(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        id = getArguments().getInt("ID");
        View rootView = inflater.inflate(R.layout.details_fragment, container, false);
        final String URL = "content://" + MsgProvider.PROVIDER_NAME + "/msg_scheduler/"+id;
        final Uri CONTENT_URI = Uri.parse(URL);
        Cursor c = getActivity().getContentResolver().query(CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        type = c.getString(MsgProvider.MSG_SCHEDULER_TYPE_INDEX);
        String msgSub = c.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_SUBJECT_INDEX);
        String sendingTo = c.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX);
        String msgBody = c.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_BODY_INDEX);
        String sendingBy = c.getString(MsgProvider.MSG_SCHEDULER_SENDING_AT_INDEX);
        String createdOn = c.getString(MsgProvider.MSG_SCHEDULER_CREATED_AT_INDEX);
        String empty = getResources().getString(R.string.empty);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"+" "+"hh:mm a");
        String time = sdf.format(Long.parseLong(c.getString(MsgProvider.MSG_SCHEDULER_SENDING_AT_INDEX), 10));
        String time1 = sdf.format(Long.parseLong(c.getString(MsgProvider.MSG_SCHEDULER_CREATED_AT_INDEX), 10));

        if(type == MsgProvider.MSG_SCHEDULER_TYPE_EMAIL) {
            ((TextView) rootView.findViewById(R.id.msgSubject)).setText(!msgSub.isEmpty()?msgSub:empty);
        }else {
            ((TextView) rootView.findViewById(R.id.msgSubject)).setVisibility(View.GONE);
            ((TextView) rootView.findViewById(R.id.msgSubjectTitle)).setVisibility(View.GONE);
        }

        switch (type) {
            case MsgProvider.MSG_SCHEDULER_TYPE_SMS : getDialog().setTitle("SMS Details");
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_EMAIL : getDialog().setTitle("Email Details");
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_WHATSAPP : getDialog().setTitle("WhatsApp Details");
                break;
        }
        ((TextView)rootView.findViewById(R.id.status)).setText(c.getString(MsgProvider.MSG_SCHEDULER_STATUS_INDEX));
        ((TextView)rootView.findViewById(R.id.sendingTo)).setText(!sendingTo.isEmpty()?sendingTo:empty);
        ((TextView)rootView.findViewById(R.id.msgBody)).setText(!msgBody.isEmpty()?msgBody:empty);
        ((TextView)rootView.findViewById(R.id.sendingBy))
                .setText(!sendingBy.
                        isEmpty() ? sdf.format(Long.parseLong(sendingBy, 10)) : empty);
        ((TextView)rootView.findViewById(R.id.createdOn))
                .setText(!createdOn.
                        isEmpty() ? sdf.format(Long.parseLong(createdOn, 10)) : empty);
        ((Button)rootView.findViewById(R.id.close)).setOnClickListener(this);
        ((Button)rootView.findViewById(R.id.edit)).setOnClickListener(this);
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                this.dismiss();
                break;
            case R.id.edit:
                Intent intent = new Intent(getActivity(), CreateDelayMsgActivity.class);
                intent.putExtra("ID",id);
                intent.putExtra("msg_type",type);
                startActivity(intent);
                this.dismiss();
                break;
        }
    }
}
