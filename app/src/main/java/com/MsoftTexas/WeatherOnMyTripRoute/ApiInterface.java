package com.MsoftTexas.WeatherOnMyTripRoute;

import com.MsoftTexas.WeatherOnMyTripRoute.Models.Apidata;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Input;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    
    @Headers({"Content-Type: application/json",
            "Accept: application/json"})
    @POST("dev/")
    Call<Apidata> inputCall(@Body Input input);
}
