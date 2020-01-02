package in.slanglabs.slangtravel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import in.slanglabs.platform.SlangBuddy;

public class DetailsActivity extends AppCompatActivity {

    private LinearLayout english, hindi;
    private TextView sort, updated, contactUs, searchCriteria;
    private static String locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Limit the help messages to only the search and sort intent
        Set<String> names = new ArraySet<>();
        names.add(SlangInterface.SEARCH_TRAIN);
        names.add(SlangInterface.SORT_TRAIN);
        SlangBuddy.getBuiltinUI().setIntentFiltersForDisplay(names);

        sort = findViewById(R.id.details_sort_trains);
        updated = findViewById(R.id.details_search_updated);
        english = findViewById(R.id.details_help_english);
        hindi = findViewById(R.id.details_help_hindi);
        searchCriteria = findViewById(R.id.search_criteria);

        updatePage(
            getIntent().getStringExtra("srcCity"),
            getIntent().getStringExtra("dstCity"),
            getIntent().getStringExtra("startDate"),
            getIntent().getBooleanExtra("newSearch", true),
            getIntent().getStringExtra("locale")
        );

        // Register a broadcast listener for handling locale changes
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("localeChanged"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlangBuddy.getBuiltinUI().show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    public void updatePage(String source, String destination, String date, boolean newSearch, String locale) {
        // Update the information on the screen based on what was passed to it
        String searchString = getSearchString(source, destination, date);

        // Update search string
        searchCriteria.setText(searchString);

        // If the source was search string, dont show the updated field. Else show it
        if (getIntent().getBooleanExtra("newSearch", true)) {
            updated.setVisibility(View.GONE);
        } else {
            updated.setVisibility(View.VISIBLE);
        }

        // Show the appropriate help strings based on current locale
        this.locale = getIntent().getStringExtra("locale");
        setHelp();
    }

    private String getSearchString(String srcCity, String dstCity, String startDate) {
        return String.format("Showing you trains from %s to %s starting on %s", srcCity, dstCity, startDate);
    }

    private void setHelp() {
        if (locale.contains("en_")) {
            english.setVisibility(View.VISIBLE);
            hindi.setVisibility(View.GONE);
        } else {
            english.setVisibility(View.GONE);
            hindi.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            locale = intent.getStringExtra("localeBroadcast");
            setHelp();
        }
    };
}
