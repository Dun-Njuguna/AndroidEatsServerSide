package com.dunk.androideatsserverside.Interface;

import com.dunk.androideatsserverside.model.MyResponse;
import com.dunk.androideatsserverside.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key = AAAAk7353b0:APA91bFGzmD6yWVEXDvmr9f_CE-Ru_Fy4x5ndbfQwTdf9c-_axf_AF7kl01h4wfYQm9Vpsgsks-zc9u5wsLwG4_2G1t5xPvlixrDm8y6x5M_f1jITx3gEAQuaZtsxtgA-KQub4pdfO6-"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
