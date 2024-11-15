package com.info121.nativelimo.api;


import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.models.RequestMobileLog;
import com.info121.nativelimo.models.RequestUpdateChannelID;
import com.info121.nativelimo.models.RequestUpdateJob;
import com.info121.nativelimo.models.RequestUpdatePassword;
import com.info121.nativelimo.models.RequestUpdateRemark;
import com.info121.nativelimo.models.RequestValidateDriver;

import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    @POST("upload")
    Call<ObjectRes> Upload(@Path("uploadFile") byte[] uploadFile);

    @GET("checkSession")
    Call<ObjectRes> CheckSession();

    @GET("getcurrentversion/{version}")
    Call<ObjectRes> CheckVersion(@Path("version") String version);

    @GET("saveshowpic/{jobno},{filename}")
    Call<JobRes> SaveShowPic(@Path("jobno") String jobno, @Path("filename") String filename);

    @GET("savesignature/{jobno},{filename}")
    Call<JobRes> SaveSignature(@Path("jobno") String jobno, @Path("filename") String filename);

    @GET("savenoshowpic/{jobno},{filename}")
    Call<JobRes> SaveNoShowPic(@Path("jobno") String jobno, @Path("filename") String filename);

    @GET("validatedriver/{user}")
    Call<ObjectRes> ValidateDriver(@Path("user") String user);

    @GET("getdrivercurrentlocation/{latitude},{longitude},{status},{address}")
    Call<JobRes> UpdateDriverLocation(@Path("latitude") String latitude, @Path("longitude") String longitude, @Path("status") int status, @Path("address") String addresss);


    @GET("updatedevice/{deviceId},{platform},{fcm_token}")
    Call<ObjectRes> UpdateDevice(@Path("deviceId") String deviceId, @Path("platform") String platform, @Path("fcm_token") String fcm_token);

    @GET("getJobsCount")
    Call<JobRes> GetJobsCount();

    @GET("getTodayJobsList")
    Call<JobRes> GetTodayJobs();

    @GET("confirmjobreminder/{jobno}")
    Call<JobRes> ConfirmJobReminder(@Path("jobno") String jobno);

    @GET("getJobDetails/{jobno}")
    Call<JobRes> GetJobDetails(@Path("jobno") String jobno);

    @GET("getTomorrowJobsList")
    Call<JobRes> GetTomorrowJobs();

    @GET("getFutureJobsList/{fromDate},{toDate},{passenger},{sort}")
    Call<JobRes> GetFutureJobs(@Path("fromDate") String fromDate,@Path("toDate") String toDate, @Path("passenger") String passenger, @Path("sort") String sort);

    @GET("getHistoryJobsList/{fromDate},{toDate},{passenger},{updates},{sort}")
    Call<JobRes> GetHistoryJobs(@Path("fromDate") String fromDate,@Path("toDate") String toDate, @Path("passenger") String passenger, @Path("updates") String updates, @Path("sort") String sort);

    @GET("getPatientHistory/{customercode},{datefrom},{dateto},{sort}")
    Call<JobRes> GetPatientHistory(@Path("customercode") String customercode,@Path("datefrom") String datefrom, @Path("dateto") String dateto, @Path("sort") String sort);

    @GET("updateJobStatus/{jobno},{address},{status}")
    Call<JobRes> UpdateJobStatus(@Path("jobno") String jobno, @Path("address") String address, @Path("status") String status);

    @GET("updateJobRemark/{jobno},{remark}")
    Call<JobRes> UpdateJobRemark(@Path("jobno") String jobno, @Path("remark") String remark);

    @POST("updatelongremarks")
    Call<JobRes> UpdateLongRemarks(@Body RequestUpdateRemark requestUpdateRemark);

    @GET("updateShowConfirmJob/{jobno},{address},{remarks},{status}")
    Call<JobRes> UpdateShowConfirmJob(@Path("jobno") String jobno, @Path("address") String address, @Path("remarks") String remarks, @Path("status") String status);

    @GET("updateNoShowJob/{jobno},{address},{remarks},{status}")
    Call<JobRes> UpdateNoShowConfirmJob(@Path("jobno") String jobno, @Path("address") String address, @Path("remarks") String remarks, @Path("status") String status);

    @GET("updateCompleteJob/{jobno},{address},{remarks},{status}")
    Call<JobRes> UpdateCompletJob(@Path("jobno") String jobno, @Path("address") String address, @Path("remarks") String remarks, @Path("status") String status);

    @POST("updatemobilelog")
    Call<JobRes> UpdateMobileLog(@Body RequestMobileLog requestMobileLog);

    @POST("UpdateShowPassengerPhotoSignature")
    Call<JobRes> UpdateShowPassengerPhotoSignature(@Body RequestUpdateJob requestUpdateJob);

    @POST("UpdateNoShowPassengerPhoto")
    Call<JobRes> UpdateNoShowPassengerPhoto(@Body RequestUpdateJob requestUpdateJob);


    @POST("ValidateDriverCredential")
    Call<ObjectRes> ValidateDriverCredential(@Body RequestValidateDriver requestValidateDriver);

    @POST("UpdatePassword")
    Call<ObjectRes> UpdatePassword(@Body RequestUpdatePassword requestUpdatePassword);

    @POST("UpdateChannelID")
    Call<ObjectRes> UpdateChannelID(@Body RequestUpdateChannelID requestUpdateChannelID);


//    //amad,123,android,241jlksfljsaf
//    @GET("updatedriverdetail/{user},{deviceId},{deviceType},{fcm_token}")
//    Call<UpdateDriverDetailRes> updateDriverDetail(@Path("user") String user, @Path("deviceId") String deviceId, @Path("deviceType") String deviceType, @Path("fcm_token") String fcm_token);
//
//    //amad,1.299654,103.855107,0
//    @GET("getdriverlocation/{user},{latitude},{longitude},{status},{address}")
//    Call<UpdateDriverLocationRes> updateDriverLocation(@Path("user") String user, @Path("latitude") String latitude, @Path("longitude") String longitude, @Path("status") int status, @Path("address") String addresss);
//
//
//    @GET("saveshowpic/{user},{job_no},{filename}")
//    Call<SaveShowPicRes> saveShowPic(@Path("user") String user, @Path("job_no") String jobNo, @Path("filename") String fileName);
//
//    @GET("confirmjobreminder/{job_no}")
//    Call<ConfirmJobRes> confirmJob(@Path("job_no") String jobNo);
//
//    @GET("remindmelater/{job_no}")
//    Call<RemindLaterRes> remindLater(@Path("job_no") String jobNo);
//
//    @GET("product")
//    Call<List<product>> getProduct();
//
//    @GET("getversion/AndriodV-{versionCode}")
//    Call<VersionRes> checkVersion(@Path("versionCode") String versionCode);
//
//
//    @GET("savesignature/{jobNo},{fileName}")
//    Call<SaveSignatureRes> savesignature(@Path("jobNo") String jobNo, @Path("fileName") String fileName);
}
