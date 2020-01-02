package in.slanglabs.slangtravel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import in.slanglabs.platform.SlangBuddy;

public class MainActivity extends AppCompatActivity {

    private LinearLayout english, hindi;
    private EditText source, destination, startDate;
    private TextView contactUs;
    private Button search;
    private String locale;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableSubmitIfReady();
            }
        };

        locale = "en";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlangInterface.init(this.getApplication());

        source = findViewById(R.id.main_source_edit);
        destination = findViewById(R.id.main_dest_edit);
        startDate = findViewById(R.id.main_start_edit);
        english = findViewById(R.id.main_help_english);
        hindi = findViewById(R.id.main_help_hindi);
        startDate.setInputType(InputType.TYPE_NULL);
        source.addTextChangedListener(watcher);
        destination.addTextChangedListener(watcher);
        startDate.addTextChangedListener(watcher);

        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                }
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String dateText = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                                startDate.setText(dateText);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        search = findViewById(R.id.main_search);
        search.setEnabled(false);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("srcCity", source.getText().toString());
                intent.putExtra("dstCity", destination.getText().toString());
                intent.putExtra("startDate", startDate.getText().toString());
                intent.putExtra("newSearch", true);
                intent.putExtra("locale", "en");
                startActivity(intent);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(listener, new IntentFilter("localeChanged"));

        sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE);
        locale = sharedPreferences.getString("locale", "en");
        boolean ask = sharedPreferences.getBoolean("ask", true);
        if (ask) {
            Dialog dialog = createNoLocationDialog();
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        source.setText("");
        destination.setText("");
        startDate.setText("");
        SlangBuddy.getBuiltinUI().show(this);
        SlangBuddy.getBuiltinUI().clearIntentFiltersForDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    public void setSource(String sourceText) {
        source.setText(sourceText);
    }

    public void setDestination(String destText) {
        destination.setText(destText);
    }

    public void setStartDate(String startDateText) {
        startDate.setText(startDateText);
    }

    private void sendEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", "42@slanglabs.in", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hi, I tried your demo and have a feedback!");
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    private Dialog createNoLocationDialog() {
        View view = View.inflate(MainActivity.this, R.layout.dialog_intro, null);
        CheckBox neverShowDialog = view.findViewById(R.id.location_never_ask_again);

        neverShowDialog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Save the preference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ask", !isChecked);
                editor.apply();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.welcome_title)
                .setCancelable(false)
                .setNeutralButton("Let's jump in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(view);
        return builder.create();
    }

    private Dialog createEmailDialog() {
        View view = View.inflate(MainActivity.this, R.layout.dialog_contact_us, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.contact_us)
                .setCancelable(true)
                .setNeutralButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendEmailIntent();
                    }
                })
                .setView(view);
        return builder.create();
    }

    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            locale = intent.getStringExtra("localeBroadcast");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("locale", locale);
            editor.apply();
            if (locale.equals("en")) {
                english.setVisibility(View.VISIBLE);
                hindi.setVisibility(View.GONE);
            } else {
                english.setVisibility(View.GONE);
                hindi.setVisibility(View.VISIBLE);
            }
        }
    };

    private void enableSubmitIfReady() {
        boolean enable = !source.getText().toString().isEmpty() &&
                !destination.getText().toString().isEmpty() &&
                !startDate.getText().toString().isEmpty();
        search.setEnabled(enable);
    }
}
