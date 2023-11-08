package com.info121.nativelimo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.info121.nativelimo.R;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.models.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {
    private Context mContext;
    List<Patient> mPatientList;

    public PatientAdapter(Context mContext) {
        this.mContext = mContext;
        mPatientList = new ArrayList<>();
    }

    public void UpdatePatientHistory(List<Patient> patientList){
        mPatientList = patientList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View v = inflater.inflate(R.layout.cell_patient, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.jobType.setText(mPatientList.get(i).getJobType());
        holder.translator.setText(mPatientList.get(i).getTranslator());
        holder.jobDate.setText(mPatientList.get(i).getJobDate());
        holder.jobTime.setText(mPatientList.get(i).getJobTime());
        holder.pickup.setText(mPatientList.get(i).getPickupPoint());
        holder.dropoff.setText(mPatientList.get(i).getAlightPoint());
        holder.update.setText(mPatientList.get(i).getUpdates());
    }

    @Override
    public int getItemCount() {
        return mPatientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

       TextView jobType, translator, jobDate, jobTime, pickup, dropoff,  update;
//        CardView root;

        public ViewHolder(View itemView) {
            super(itemView);

            jobType = itemView.findViewById(R.id.job_type);
            translator = itemView.findViewById(R.id.translator);
            jobDate = itemView.findViewById(R.id.job_date);
            jobTime = itemView.findViewById(R.id.job_time);
            pickup = itemView.findViewById(R.id.pickup);
            dropoff = itemView.findViewById(R.id.dropoff);
            update = itemView.findViewById(R.id.updateText);
        }
    }
}
