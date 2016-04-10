package com.rahoria.nitin.poketassistant;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.rahoria.nitin.poketassistant.Adapter.MsgAdapter;
import com.rahoria.nitin.poketassistant.Adapter.TabsPagerAdapter;
import com.rahoria.nitin.poketassistant.MsgScheduling.CreateDelayMsgActivity;
import com.rahoria.nitin.poketassistant.Provider.MsgProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private final int DEFAULT_HOME_LIST = 1;
    private FloatingActionButton createFab, smsFab, emailFab, whatsAppFab;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private boolean isFabOpen = false;
    private MsgAdapter msgAdapter;
    private RecyclerView recyclerView;
    private List<MsgScheduler> scheduleMsgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setBackgroundResource(R.color.colorPrimary);
        pager.setAdapter(adapter);

        initView();
    }

    private void initView() {
        createFab = (FloatingActionButton) findViewById(R.id.createFab);
        smsFab = (FloatingActionButton)findViewById(R.id.smsFab);
        emailFab = (FloatingActionButton)findViewById(R.id.emailFab);
        whatsAppFab = (FloatingActionButton)findViewById(R.id.whatsAppFab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        createFab.setOnClickListener(this);
        smsFab.setOnClickListener(this);
        emailFab.setOnClickListener(this);
        whatsAppFab.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.home_content_recycler_view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void animateFAB(){

        if(isFabOpen){

            createFab.startAnimation(rotate_backward);
            smsFab.startAnimation(fab_close);
            emailFab.startAnimation(fab_close);
            whatsAppFab.startAnimation(fab_close);
            smsFab.setClickable(false);
            emailFab.setClickable(false);
            whatsAppFab.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            createFab.startAnimation(rotate_forward);
            smsFab.startAnimation(fab_open);
            emailFab.startAnimation(fab_open);
            whatsAppFab.startAnimation(fab_open);
            smsFab.setClickable(true);
            emailFab.setClickable(true);
            whatsAppFab.setClickable(true);
            isFabOpen = true;

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.createFab){
            animateFAB();
            return;
        }
        Intent intent = new Intent(getBaseContext(), CreateDelayMsgActivity.class);
        switch (id){

            case R.id.smsFab:
                intent.putExtra("msg_type",MsgProvider.MSG_SCHEDULER_TYPE_SMS);
                break;
            case R.id.emailFab:
                intent.putExtra("msg_type",MsgProvider.MSG_SCHEDULER_TYPE_EMAIL);
                break;
            case R.id.whatsAppFab:
                intent.putExtra("msg_type",MsgProvider.MSG_SCHEDULER_TYPE_WHATSAPP);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d("NITIN","i am in onCreateLoader");
        switch (id){
            case DEFAULT_HOME_LIST:
                final String URL = "content://" + MsgProvider.PROVIDER_NAME + "/msg_scheduler";
                final Uri CONTENT_URI = Uri.parse(URL);
                Log.d("NITIN","i am in onCreateLoader URI : "+CONTENT_URI);
                return new CursorLoader(this, CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null){
            data.moveToFirst();
            Log.d("NITIN","cursor count is : "+data.getCount());
            msgAdapter= new MsgAdapter(this, data);
            recyclerView.setAdapter(msgAdapter);
        }
        Log.d("NITIN","NO data");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
