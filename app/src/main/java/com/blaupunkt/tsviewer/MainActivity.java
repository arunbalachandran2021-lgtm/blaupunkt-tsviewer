package com.blaupunkt.tsviewer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this);
        title.setText("Blaupunkt TS Viewer");
        title.setTextSize(26);

        TextView info = new TextView(this);
        info.setText("Select a Blaupunkt .TS recording to begin.");
        info.setTextSize(18);

        Button select = new Button(this);
        select.setText("Select .TS Video");

        layout.addView(title);
        layout.addView(info);
        layout.addView(select);

        setContentView(layout);
    }
}
