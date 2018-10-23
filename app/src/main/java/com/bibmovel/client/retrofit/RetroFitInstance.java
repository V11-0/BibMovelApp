package com.bibmovel.client.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vinibrenobr11 on 17/10/18 at 17:51
 */
public class RetroFitInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.0.100:8080/BibMovel_war_exploded/rest/";

    public static Retrofit getRetrofitInstance() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
