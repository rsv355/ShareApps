package com.example.android.shareapps;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btn;
    ListView listView;
    AppAdapter adapter=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        listView = (ListView)findViewById(R.id.listView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processFetchList();
            }
        });
    }

    private void processFetchList(){
        PackageManager pm=getPackageManager();
        Intent sendIntent=new Intent(Intent.ACTION_MAIN, null);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "test");
        sendIntent.setType("text/plain");


       // sendIntent.addCategory(Intent.CATEGORY_LAUNCHER);


        List<ResolveInfo> launchables=pm.queryIntentActivities(sendIntent, 0);

        for (ResolveInfo resolveInfo : launchables) {

            Log.e("### PCK NAME",""+resolveInfo.activityInfo.packageName);
            //packages.add(resolveInfo.activityInfo.packageName);
        }


        Collections.sort(launchables,
                new ResolveInfo.DisplayNameComparator(pm));

        adapter = new AppAdapter(pm,launchables);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ResolveInfo launchable = adapter.getItem(position);
                ActivityInfo activity = launchable.activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                        activity.name);
                Intent i = new Intent(Intent.ACTION_MAIN);
/*
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                i.setComponent(name);*/

                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, "test");
                i.setType("text/plain");
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                i.setComponent(name);

                startActivity(i);
            }
        });



    }

    class AppAdapter extends ArrayAdapter<ResolveInfo> {
        private PackageManager pm=null;

        AppAdapter(PackageManager pm, List<ResolveInfo> apps) {
            super(MainActivity.this, R.layout.row_view, apps);
            this.pm=pm;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            if (convertView==null) {
                convertView=newView(parent);
            }

            bindView(position, convertView);

            return(convertView);
        }

        private View newView(ViewGroup parent) {
            return(getLayoutInflater().inflate(R.layout.row_view, parent, false));
        }

        private void bindView(int position, View row) {
            TextView label=(TextView)row.findViewById(R.id.txtName);

            label.setText(getItem(position).loadLabel(pm));

            ImageView icon=(ImageView)row.findViewById(R.id.img);

            icon.setImageDrawable(getItem(position).loadIcon(pm));
        }
    }

    //end of main class
}
