package com.rahoria.nitin.poketassistant.MsgScheduling;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rahoria.nitin.poketassistant.Provider.MsgProvider;
import com.rahoria.nitin.poketassistant.R;
import com.rahoria.nitin.poketassistant.Services.sendMsgService;
import com.rahoria.nitin.poketassistant.Utils.DatePickerFragment;
import com.rahoria.nitin.poketassistant.Utils.TimePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SMSCreateFragment extends Fragment implements View.OnClickListener {

    public TextView dateTextView, timeTextView;
    private EditText contactEditText, msgBodyEditText;
    private int id;
    private boolean isUpdateMode = false;
    private Button submitButton, cancelButton;

    public SMSCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.sms_fragment_layout,null);
        submitButton = (Button) layoutView.findViewById(R.id.submitButton);
        cancelButton = (Button) layoutView.findViewById(R.id.cancelButton);
        LinearLayout dateImage = (LinearLayout) layoutView.findViewById(R.id.dateView);
        LinearLayout timeImage = (LinearLayout)layoutView.findViewById(R.id.timeView);
        ImageView contactButton = (ImageView)layoutView.findViewById(R.id.contactButton);
        timeTextView = (TextView)layoutView.findViewById(R.id.timeTextView);
        dateTextView = (TextView)layoutView.findViewById(R.id.dateTextView);
        contactEditText = (EditText) layoutView.findViewById(R.id.toTextView);
        msgBodyEditText = (EditText) layoutView.findViewById(R.id.msgBodyTextView);
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        timeImage.setOnClickListener(this);
        dateImage.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        id = getActivity().getIntent().getIntExtra("ID",-1);
        if(id >= 0){
            isUpdateMode = true;
            fillData();
        }
    }

    private void fillData() {
        final String URL = "content://" + MsgProvider.PROVIDER_NAME + "/msg_scheduler/"+id;
        final Uri CONTENT_URI = Uri.parse(URL);
        Cursor c = getActivity().getContentResolver().query(CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        contactEditText.setText(c.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX));
        msgBodyEditText.setText(c.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_BODY_INDEX));

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
        String time1 = sdf1.format(Long.parseLong(c.getString(MsgProvider.MSG_SCHEDULER_SENDING_AT_INDEX), 10));
        String time2 = sdf2.format(Long.parseLong(c.getString(MsgProvider.MSG_SCHEDULER_SENDING_AT_INDEX), 10));
        dateTextView.setText(time1);
        timeTextView.setText(time2);
        submitButton.setText("Update");
    }

    @Override
    public void onClick(View v) {
        DialogFragment picker;
        Intent intent;
        switch(v.getId()){
            case R.id.cancelButton:
                if(msgBodyEditText.getText().length() > 0 || contactEditText.getText().length() > 0)
                    showConfirmationDialog();
                else
                    getActivity().finish();// Finish Activity.
                break;
            case R.id.submitButton:
                try {
                    if(setNewAlarm()){
                        getActivity().finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.dateView:
                picker = new DatePickerFragment();
                picker.show(getFragmentManager(),"datePicker");
                break;
            case R.id.timeView:
                picker = new TimePickerFragment();
                picker.show(getFragmentManager(),"timePicker");
                break;
            case R.id.contactButton:
                intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
            default:
                return;
        }
    }

    private Boolean setNewAlarm() throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"+"hh:mm a");
        Date date = sdf.parse((String)dateTextView.getText()+(String)timeTextView.getText());

        if(System.currentTimeMillis() > date.getTime()){
            Toast.makeText(getActivity(),"Can't send msg to past :)",Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            date = sdf.parse((String) dateTextView.getText() + (String) timeTextView.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_TYPE, MsgProvider.MSG_SCHEDULER_TYPE_SMS);
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_STATUS, MsgProvider.MSG_STATUS_SEND_PENDING);
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_SENDING_TO, contactEditText.getText().toString());
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_MESSAGE_BODY, msgBodyEditText.getText().toString());
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_SENDING_AT, String.valueOf(date.getTime()));
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_CREATED_AT, System.currentTimeMillis());
        if(isUpdateMode) {
            Log.d("NITIN","setNewAlain rm else for update");
            String url = MsgProvider.URL + "/"+id;
            Uri uri = Uri.parse(url);
            int count = getActivity().getContentResolver().update(uri, values, null, null);
        }else{
            Log.d("NITIN","setNewAlain rm else for create");
            Uri uri = getActivity().getContentResolver().insert(
                    MsgProvider.CONTENT_URI, values);
            id = Integer.parseInt(uri.getPathSegments().get(1));
        }
        if(id <= 0){
            Toast.makeText(getActivity(),"Sorry, We are getting some problem with your DB insertion request",Toast.LENGTH_LONG).show();
            return false;
        }

        cal.setTimeInMillis(date.getTime());

        Intent intent = new Intent(getActivity(), sendMsgService.class);
                intent.putExtra("ID", id);
        PendingIntent pintent = PendingIntent.getService(getActivity().getApplicationContext(), (int) id, intent, 0);

        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
        Toast.makeText(getActivity(),"Message is schedule to send at "+sdf.format(date),Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }
        Uri contactUri = data.getData();
        String[] proj = {ContactsContract.CommonDataKinds.Phone.NUMBER};
           Cursor phoneCur = getActivity().getContentResolver()
                .query(contactUri,
                        proj,
                        null, null,
                        null);
        phoneCur.moveToFirst();
        int column = phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String number = phoneCur.getString(column);
        phoneCur.close();
        if(number == null){
            return;
        }
        contactEditText.setText(number);
    }

    void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choosing time");
        builder.setMessage("Do you want to save the data?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy" + "hh:mm a");
                Date date = null;
                try {
                    date = sdf.parse((String) dateTextView.getText() + (String) timeTextView.getText());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ContentValues values = new ContentValues();
                values.put(MsgProvider.MSG_SCHEDULER_COLUMN_TYPE, MsgProvider.MSG_SCHEDULER_TYPE_SMS);
                values.put(MsgProvider.MSG_SCHEDULER_COLUMN_STATUS, MsgProvider.MSG_STATUS_DRAFT);
                values.put(MsgProvider.MSG_SCHEDULER_COLUMN_SENDING_TO, contactEditText.getText().toString());
                values.put(MsgProvider.MSG_SCHEDULER_COLUMN_MESSAGE_BODY, msgBodyEditText.getText().toString());
                values.put(MsgProvider.MSG_SCHEDULER_COLUMN_SENDING_AT, String.valueOf(date.getTime()));
                values.put(MsgProvider.MSG_SCHEDULER_COLUMN_CREATED_AT, System.currentTimeMillis());

                if(isUpdateMode) {
                    Log.d("NITIN","setNewAlain rm else for update");
                    String url = MsgProvider.URL + "/"+id;
                    Uri uri = Uri.parse(url);
                    int count = getActivity().getContentResolver().update(uri, values, null, null);
                }else{
                    Log.d("NITIN","setNewAlain rm else for create");
                    Uri uri = getActivity().getContentResolver().insert(
                            MsgProvider.CONTENT_URI, values);
                    id = Integer.parseInt(uri.getPathSegments().get(1));
                }
                if(id > 0){
                    Toast.makeText(getActivity(), "Successfully saved to draft", Toast.LENGTH_LONG);
                    getActivity().finish();
                }else {
                    Toast.makeText(getActivity(), "Problem in saving to draft", Toast.LENGTH_LONG);
                }
            }
        });
        builder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();// Finish Activity.
                    }
                });
        builder.create();
        builder.show();
    }

}
