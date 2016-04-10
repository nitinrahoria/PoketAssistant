package com.rahoria.nitin.poketassistant;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rahoria.nitin.poketassistant.Adapter.MsgAdapter;
import com.rahoria.nitin.poketassistant.Provider.MsgProvider;


public class PageFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_PAGE_NUMBER = "page_number";
    private MsgAdapter msgAdapter;
    private RecyclerView recyclerView;

    public PageFragment() {
    }

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.home_content_recycler_view);

        //txt.setText(String.format("Page %d", page));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplication());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int page = getArguments().getInt(ARG_PAGE_NUMBER, -1);
        System.out.println("onActivityCreated()");
        getLoaderManager().initLoader(page, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        final String URL = "content://" + MsgProvider.PROVIDER_NAME + "/msg_scheduler";
        final Uri CONTENT_URI = Uri.parse(URL);
        String where;
        Log.d("NITIN","i am in onCreateLoader");
        switch (id){
            case 1:
                where = MsgProvider.MSG_SCHEDULER_COLUMN_STATUS+"=? OR "+MsgProvider.MSG_SCHEDULER_COLUMN_STATUS+"=? OR "+MsgProvider.MSG_SCHEDULER_COLUMN_STATUS+"=?";
                String[] selectionArgs1 = {MsgProvider.MSG_STATUS_DRAFT, MsgProvider.MSG_STATUS_SEND_PENDING, MsgProvider.MSG_STATUS_SENDING};
                Log.d("NITIN","1 i am in onCreateLoader URI : "+CONTENT_URI);
                return new CursorLoader(this.getContext(), CONTENT_URI, null, where, selectionArgs1, null);
            case 2:
                Log.d("NITIN","2 i am in onCreateLoader URI : "+CONTENT_URI);
                where = MsgProvider.MSG_SCHEDULER_COLUMN_STATUS+"=? OR "+MsgProvider.MSG_SCHEDULER_COLUMN_STATUS+"=?" ;
                String[] selectionArgs2 = {MsgProvider.MSG_STATUS_FAILED, MsgProvider.MSG_STATUS_SENT};
                return new CursorLoader(this.getContext(), CONTENT_URI, null, where, selectionArgs2, null);
            default:
                return null;
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null){
            data.moveToFirst();
            Log.d("NITIN", "cursor count is : " + data.getCount());
            msgAdapter= new MsgAdapter(this.getContext(), data);
            recyclerView.setAdapter(msgAdapter);
        }
        Log.d("NITIN","NO data");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
