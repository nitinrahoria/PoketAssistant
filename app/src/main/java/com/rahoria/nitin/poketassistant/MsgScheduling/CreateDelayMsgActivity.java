package com.rahoria.nitin.poketassistant.MsgScheduling;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.rahoria.nitin.poketassistant.R;
import com.rahoria.nitin.poketassistant.Utils.DatePickerFragment;
import com.rahoria.nitin.poketassistant.Utils.TimePickerFragment;
import com.rahoria.nitin.poketassistant.Provider.MsgProvider;


public class CreateDelayMsgActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        DatePickerFragment.TheListener, TimePickerFragment.TimeListener{

    private static final String TAG = "CreateDelayMsgActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_delay_msg_layout);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (getIntent().getStringExtra("msg_type")){
            case  MsgProvider.MSG_SCHEDULER_TYPE_SMS:
                SMSCreateFragment sms;
                setTitle("SMS Compose");
                sms =(SMSCreateFragment)fragmentManager.findFragmentByTag("SMSFragment");
                if(sms != null && sms.isAdded()) {
                    break;
                }else{
                    sms = new SMSCreateFragment();
                }
                fragmentTransaction.add(R.id.msgCreateFragementFill, sms, "SMSFragment");
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_EMAIL :
                setTitle("Email Compose");
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_WHATSAPP :
                setTitle("WhatsApp Compose");
                break;
            default:
                Log.e(TAG, "Not a valid selection");
                return;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void returnDate(String date) {
        SMSCreateFragment skss;
        if((skss = (SMSCreateFragment)getFragmentManager().findFragmentByTag("SMSFragment"))!=null){
            skss.dateTextView.setText(date);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onReturnTime(String time) {
        SMSCreateFragment skss;
        if((skss = (SMSCreateFragment)getFragmentManager().findFragmentByTag("SMSFragment"))!=null){
            skss.timeTextView.setText(time);
        }
    }
}
