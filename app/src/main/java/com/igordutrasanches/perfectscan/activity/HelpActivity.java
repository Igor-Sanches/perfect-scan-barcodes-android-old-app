package com.igordutrasanches.perfectscan.activity;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.igordutrasanches.perfectscan.R;
import com.igordutrasanches.perfectscan.compoments.Go;
import com.igordutrasanches.perfectscan.compoments.Services;

import static com.igordutrasanches.perfectscan.manager.Manager.ZAP_SUPPORT;

public class HelpActivity extends AppCompatActivity {

    public void onBack(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ((ListView)findViewById(R.id.list_help)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: Go.by(HelpActivity.this).onRateApp(); break;
                    case 1: launchSupport(getString(R.string.help_prob)); break;
                    case 2: launchSupport(getString(R.string.help_recurso)); break;
                    case 3: launchSupport(getString(R.string.help_bug)); break;
                    case 4: Services.from(HelpActivity.this).putTranslate(); break;
                }
            }
        });
    }

    private void launchSupport(String msg) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ZAP_SUPPORT + msg)));
    }
}
