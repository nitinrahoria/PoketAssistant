package com.rahoria.nitin.poketassistant.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.rahoria.nitin.poketassistant.Provider.MsgProvider;
import com.rahoria.nitin.poketassistant.R;

/**
 * Created by nitin on 4/3/2016.
 */
public class sendMsgService extends IntentService {

    int id;
    Cursor cursor;
    public static final String TAG = "sendMsgService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public sendMsgService(String name) {
        super(name);
    }

    public sendMsgService() {
        super(TAG + "_worker");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Nitin", "Service on stratcommand called");
        //startNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        id = intent.getIntExtra("ID", 0);
        if(id < 0){
            Log.e(TAG,"intent ID extra value not getting correct");
            return;
        }
        Log.d(TAG, "intent ID : " + id);
        String url = "content://" + MsgProvider.PROVIDER_NAME+"/msg_scheduler/"+id;
        Uri uri = Uri.parse(url);
        Log.d(TAG, "intent URI : " + uri);
        cursor = getContentResolver().query(uri, null, null, null, null);
        Log.d("NITIN","in service cursor count"+cursor.getCount() +"  intent ID : " + id);
        cursor.moveToFirst();
        switch (cursor.getString(MsgProvider.MSG_SCHEDULER_TYPE_INDEX)){
            /*case MsgProvider.MSG_SCHDULER_TYPE_DRAFT:
                Log.e(TAG,"!!!CAUTION!!!\n somewhere we are sending draft ::: a very big problem");
                break;*/
            case MsgProvider.MSG_SCHEDULER_TYPE_SMS:
                fireSMS();
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_EMAIL:
                break;
            case MsgProvider.MSG_SCHEDULER_TYPE_WHATSAPP:
                break;
            default:
        }

    }

    private void fireSMS() {
        Log.e(TAG, "sending msg details to : " + cursor.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX) + " msg :" + cursor.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_BODY_INDEX));
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(cursor.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX), null, cursor.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_BODY_INDEX), null, null);
        String url = MsgProvider.URL + "/"+id;
        Uri uri = Uri.parse(url);
        ContentValues values = new ContentValues();
        values.put(MsgProvider.MSG_SCHEDULER_COLUMN_STATUS, MsgProvider.MSG_STATUS_SENT);
        int count = getContentResolver().update(uri, values, null, null);
        startNotification();
    }

    private void startNotification()
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.sms_white);
        mBuilder.setTicker(cursor.getString(MsgProvider.MSG_SCHEDULER_TYPE_INDEX)
                + " " + cursor.getString(MsgProvider.MSG_SCHEDULER_STATUS_INDEX)
                +" to "+ cursor.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX));
        mBuilder.setContentTitle(cursor.getString(MsgProvider.MSG_SCHEDULER_TYPE_INDEX)
                + " " + cursor.getString(MsgProvider.MSG_SCHEDULER_STATUS_INDEX));
        mBuilder.setContentText("Hi, Your " + cursor.getString(MsgProvider.MSG_SCHEDULER_TYPE_INDEX)
                + "\n" + cursor.getString(MsgProvider.MSG_SCHEDULER_MESSAGE_BODY_INDEX)
                + "\n to " + cursor.getString(MsgProvider.MSG_SCHEDULER_SENDING_TO_INDEX)
                + "\n" + cursor.getString(MsgProvider.MSG_SCHEDULER_STATUS_INDEX));
        mNotificationManager.notify(id, mBuilder.build());
    }
}
