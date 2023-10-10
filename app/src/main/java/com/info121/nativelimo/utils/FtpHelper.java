package com.info121.nativelimo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.adeel.library.easyFTP;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.JobRes;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KZHTUN on 8/3/2017.
 */


public class FtpHelper {

    public static class uploadTask extends AsyncTask<String, String, String> {
        ProgressDialog prg;
        Context context;
        InputStream inputStream;
        String mJobNo;
        String mFileName;
        String mType;
        String mAction;

        public uploadTask(Context context, InputStream inputStream) {
            this.context = context;
            this.inputStream = inputStream;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prg = new ProgressDialog(context);
            prg.setMessage("Initializing ... \n\nPlease wait. This may take 10 to 15 seconds ");
            prg.show();
        }

//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                easyFTP ftp = new easyFTP();
//
//                ftp.connect(params[0], params[1], params[2]);
//
//                boolean status = false;
//
//                if (!params[3].isEmpty()) {
//                    status = ftp.setWorkingDirectory(params[3]); // if User say provided any Destination then Set it , otherwise
//                }
//                // Upload will be stored on Default /root level on server
//                publishProgress("Uploading ...");
//                ftp.uploadFile(inputStream, params[4]);
//
//                publishProgress("Upload Successful ...");
//                mFileName = params[4];
//                mJobNo = params[5];
//                mType = params[6];
//
//                return new String("Upload Successful");
//
//            } catch (Exception e) {
//                String t = "Failure : " + e.getLocalizedMessage();
//                return t;
//            }
//        }


        @Override
        protected String doInBackground(String... params) {
            try {
                boolean status = false;

                mFileName = params[4];
                mJobNo = params[5];
                mType = params[6];
                mAction = params[7];

//                FTPClient ftpClient = new FTPClient();
//
//                ftpClient.connect(App.FTP_URL);
//                ftpClient.login(App.FTP_USER, App.FTP_PASSWORD);
//
//                if (!params[3].isEmpty()) {
//                    status = ftpClient.changeWorkingDirectory(params[3]); // if User say provided any Destination then Set it , otherwise
//                }
//
//                FileInputStream fs = new FileInputStream(mFileName);
//                ftpClient.storeFile(mFileName, fs);
//
//                ftpClient.logout();


                FTPClient ftpClient = new FTPClient();
                try {
                    ftpClient.setConnectTimeout(12000);
                    ftpClient.connect(InetAddress.getByName(params[0]));
                    ftpClient.login(params[1], params[2]);
                } catch (SocketException e) {
                    // no ftp connection
                     //EventBus.getDefault().post(mType + "_UPLOAD_FAILED");
                    // prg.dismiss();
                     return "Connection is not open";
                }

                if (!params[3].isEmpty()) {
                    status = ftpClient.changeWorkingDirectory(params[3]); // if User say provided any Destination then Set it , otherwise
                }

                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                BufferedInputStream buffIn = null;
                //   buffIn = new BufferedInputStream(new FileInputStream(file));
                ftpClient.enterLocalPassiveMode();
                ftpClient.storeFile(mFileName, inputStream);

                buffIn.close();
                ftpClient.logout();
                ftpClient.disconnect();

                publishProgress("Upload Successful ...");

                return new String("Upload Successful");

            } catch (Exception e) {
                String t = "Failure : " + e.getMessage();
                return t;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            prg.setMessage(values[0]);
        }


        @Override
        protected void onPostExecute(String str) {
           // failed to connect to /128.106.129.15 (port 21) from /:: (port 35610): connect failed: ECONNREFUSED (Connection refused)
         //   failed to connect to /128.106.129.15 (port 21) from /:: (port 46822): connect failed: ENETUNREACH (Network is unreachable)
         //   Attempt to invoke virtual method 'void java.io.BufferedInputStream.close()' on a null object reference
            if(str != null){
               if(str.indexOf("failed to connect to") >= 0 || str.indexOf("Connection is not open") >= 0){
                    EventBus.getDefault().post(mType + "_UPLOAD_FAILED");
                    prg.dismiss();
                    return;
                }

               // str.equalsIgnoreCase()
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    prg.dismiss();

                    if (mType.equalsIgnoreCase("SHOW"))
                        callSaveShowPhoto(context, mJobNo, mFileName);

                    if (mType.equalsIgnoreCase("NOSHOW"))
                        callSaveNoShowPhoto(context, mJobNo, mFileName);

                    if (mType.equalsIgnoreCase("SIGNATURE"))
                        callSaveSignature(context, mJobNo, mFileName);

                    if (mAction != null)
                        EventBus.getDefault().post(mAction);

                }
            }, 2000);


            // Toast.makeText(demo.this,str,Toast.LENGTH_LONG).show();
        }
    }

    public class downloadTask extends AsyncTask<String, Void, String> {
        ProgressDialog prg;
        Context context;
        InputStream inputStream;

        public downloadTask(Context context) {
            this.context = context;
            this.inputStream = inputStream;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prg = new ProgressDialog(context);
            prg.setMessage("Downloading...");
            prg.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                easyFTP ftp = new easyFTP();
//
                ftp.connect(params[0], params[1], params[2]);
                ftp.downloadFile(params[3], params[4]);
                return new String("Download Successful");
            } catch (Exception e) {
                String t = "Failure : " + e.getLocalizedMessage();
                return t;
            }
        }

        @Override
        protected void onPostExecute(String str) {
            prg.dismiss();
            // Toast.makeText(demo.this,str,Toast.LENGTH_LONG).show();
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private static void callSaveShowPhoto(final Context context, String jobNo, String fileName) {
        Call<JobRes> call = RestClient.COACH().getApiService().SaveShowPic(
                jobNo,
                fileName
        );


        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Toast.makeText(context, "Show save successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Toast.makeText(context, "Show save failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void callSaveNoShowPhoto(final Context context, String jobNo, String fileName) {
        Call<JobRes> call = RestClient.COACH().getApiService().SaveNoShowPic(
                jobNo,
                fileName
        );


        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Toast.makeText(context, "No show save successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Toast.makeText(context, "No show save failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void callSaveSignature(final Context context, String jobNo, String fileName) {
        Call<JobRes> call = RestClient.COACH().getApiService().SaveSignature(
                jobNo,
                fileName
        );


        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                Toast.makeText(context, "Signature save successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {
                Toast.makeText(context, "Signature save failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
