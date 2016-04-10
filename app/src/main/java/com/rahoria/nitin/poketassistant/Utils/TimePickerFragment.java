package com.rahoria.nitin.poketassistant.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by nitin on 4/2/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener{

    private TimeListener listener;

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String formattedTime = timeFormatter.format(c.getTime());
        if (listener != null)
        {
            listener.onReturnTime(formattedTime);

        }
    }

    public interface TimeListener{
        public void onReturnTime(String time);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (TimeListener)getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,false);
    }
}
