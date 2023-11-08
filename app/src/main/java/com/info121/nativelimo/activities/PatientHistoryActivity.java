package com.info121.nativelimo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.adapters.JobsAdapter;
import com.info121.nativelimo.adapters.PatientAdapter;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.models.Patient;
import com.info121.nativelimo.models.SearchParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientHistoryActivity extends AppCompatActivity {
    Calendar myCalendar;

    @BindView(R.id.from_date)
    EditText mFromDate;

    @BindView(R.id.to_date)
    EditText mToDate;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_patient)
    RecyclerView mRecyclerView;

    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;

    @BindView(R.id.search)
    Button btnSearch;

    Context mContext;
    PatientAdapter patientAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        ButterKnife.bind(this);

        mContext = PatientHistoryActivity.this;

        // set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Patient History");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        patientAdapter = new PatientAdapter(mContext);
        mRecyclerView.setAdapter(patientAdapter);

        Intent intent = getIntent();
        App.patientSearchParams.setCustomercode(intent.getExtras().getString("CUSTOMER_NAME"));

        // set default DESC
        mRadioGroup.check(R.id.sort_desc);

        // radio click
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.sort_asc:
                        App.patientSearchParams.setSort("0");
                        break;
                    case R.id.sort_desc:
                        App.patientSearchParams.setSort("1");
                        break;
                }

                getPatientHistory();
            }
        });

        // search click
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.patientSearchParams.setDatefrom(mFromDate.getText().toString().trim());
                App.patientSearchParams.setDateto(mToDate.getText().toString().trim());
                getPatientHistory();
            }
        });


        getPatientHistory();
    }


    private  void getPatientHistory(){

        Call<JobRes> call = RestClient.COACH().getApiService().GetPatientHistory(
                App.patientSearchParams.getCustomercode(),
                App.patientSearchParams.getDatefrom(),
                App.patientSearchParams.getDateto(),
                App.patientSearchParams.getSort()
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    patientAdapter.UpdatePatientHistory(response.body().getPatients());
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.from_date)
    public void fromDateOnClick(){
        showDateDialog(mFromDate);
    }

    @OnClick(R.id.to_date)
    public void toDateOnClick(){
        showDateDialog(mToDate);
    }

    private void showDateDialog(final EditText target) {
        myCalendar = Calendar.getInstance();
        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd MMM yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                target.setText(sdf.format(myCalendar.getTime()));
            }
        };


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

//            if (target.getTag().toString().equalsIgnoreCase("TO_DATE")) {
//                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
//            }


        datePickerDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle Action bar item clicks here. The Action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}