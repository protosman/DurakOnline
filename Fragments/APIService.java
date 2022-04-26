package ru.tmkstd.cardgamedurakonline.Fragments;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.Call;
import ru.tmkstd.cardgamedurakonline.Notification.MyResponse;
import ru.tmkstd.cardgamedurakonline.Notification.Sender;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAWUp_5Ek:APA91bEvO0oW4Ludl6CRZ5zkvrnA4H0pnFROLjE4DAry_ygbalMPXIshH6AgjszhpLGYgyTjhcfnAqS8Fb2jsaO0_eJ1_3MJjq8QBBJjBfnC3YHn-sq_HkkNZblg_aWP9NvPScvorCXg"
            }

    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
