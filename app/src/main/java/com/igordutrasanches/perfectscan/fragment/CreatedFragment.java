package com.igordutrasanches.perfectscan.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Key;
import com.igordutrasanches.perfectscan.db.Create;
import com.igordutrasanches.perfectscan.db.Historical;
import com.igordutrasanches.perfectscan.db.HistoricalInfo;

import java.util.List;

public class CreatedFragment extends Fragment {
    private static ListView recyclerView;
    private static Create create;
    private static View view;

    private static int getShorting(){
        return Key.getShortingCreaated(view.getContext());
    }

    public static void updated() {
        if(recyclerView != null){
            recyclerView.setAdapter(new MenuAdaptor(view.getContext(), R.layout.historical_item_list_create, create.lista(getShorting())));
            ((LinearLayout)view.findViewById(R.id.historical_clear_list)).setVisibility(create.lista(getShorting()).size() == 0 ? View.VISIBLE : View.GONE);
            ((LinearLayout)view.findViewById(R.id.not_fount)).setBackgroundResource(create.lista(getShorting()).size() == 0 ? R.drawable.list_clear : R.color.colorPrimary);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        updated();
    }
    public CreatedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_list_created, container, false);
        try{

            final Context context = view.getContext();
            recyclerView = (ListView) view.findViewById(R.id.list);
            create = new Create(view.getContext());
            updated();
            Historical historical = new Historical(context);
            recyclerView.setOnItemClickListener(historical.listenerCreate);
            registerForContextMenu(recyclerView);
        }catch (Exception x){
            Toast.makeText(view.getContext(), x.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        try{
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Historical historical = new Historical(this.view.getContext());
            return historical.onContextItemSelectedIsCreate(item, menuInfo.id);
        }catch (Exception x){
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info){
        super.onCreateContextMenu(menu, view, info);
        try{
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)info;
            Historical historical = new Historical(this.view.getContext());
            historical.onCreateContextMenuCreate(menu, view, info, menuInfo.id);
        }catch (Exception x){
        }
    }


    private static class MenuAdaptor extends ArrayAdapter<HistoricalInfo> {

        private List<HistoricalInfo> appsLisMain = null;
        private Context context = null;

        public MenuAdaptor(Context context, int resource, List<HistoricalInfo> apps) {
            super(context, resource, apps);

            this.context = context;
            this.appsLisMain = apps;
        }

        @Override
        public int getCount() {
            return ((null != appsLisMain) ? appsLisMain.size() : 0);

        }

        @Override
        public HistoricalInfo getItem(int i) {
            return ((null != appsLisMain) ? appsLisMain.get(i) : null);
        }

        @Override
        public long getItemId(int i) {
            return appsLisMain.get(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup g) {
            try {
                if (view == null) {
                    LayoutInflater l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = l.inflate(R.layout.historical_item_list_create, null);
                }

                HistoricalInfo info = appsLisMain.get(i);
                ((TextView) view.findViewById(R.id.historical_title)).setText(info.getTitle());
                ((TextView) view.findViewById(R.id.historical_summary)).setText(info.getData() + " ~ " + info.getCode());
                ((ImageView) view.findViewById(R.id.historical_icon)).setImageResource(info.getIcon());
                return view;

            } catch (Exception x) {
                Toast.makeText(view.getContext(), x.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return view;
        }
    }

}
