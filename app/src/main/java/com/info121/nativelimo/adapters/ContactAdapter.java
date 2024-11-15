package com.info121.nativelimo.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.nativelimo.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter {
    List<String> contacts = new ArrayList<String>();
    Context context;


    public ContactAdapter(List<String> phoneList, Context context) {
        this.contacts = phoneList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.cell_contact, null);

        TextView label = view.findViewById(R.id.label_mobile);
        TextView mobileNo = view.findViewById(R.id.mobile_no);
        ImageView call = view.findViewById(R.id.call);
        ImageView sms = view.findViewById(R.id.sms);
        ImageView whatsapp = view.findViewById(R.id.whatsapp);

        label.setText("MOBILE " + (i + 1));
        mobileNo.setText(contacts.get(i));


        final String phoneNo = contacts.get(i);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("tel:" + phoneNo);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                context.startActivity(callIntent);

                Toast.makeText(context, "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
            }
        });


        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("sms:" + phoneNo);
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, number);
                context.startActivity(smsIntent);

                Toast.makeText(context, "Send SMS .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
            }
        });



        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "This is whatsapp msg";
                //boolean installed = appInstalledOrNot("com.whatsapp");
                String phNo = "";

                if(phoneNo.length() > 8)
                    phNo = "+" + phoneNo;
                else
                    phNo = "+65" + phoneNo;

                try {
                    String text = "This is a test";// Replace with your message.

                  //  String toNumber = "xxxxxxxxxx"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                    //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+ phNo +"&text="+message));
                    context.startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

//                if (installed){
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+ phNo + "&text="+ message));
//                    context.startActivity(intent);
//                }else {
//                    Toast.makeText(context, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        return view;
    }

    private boolean appInstalledOrNot(String url){
        PackageManager packageManager =   context.getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }catch (PackageManager.NameNotFoundException e){
            app_installed = false;
        }
        return app_installed;
    }
}
