package org.botgverreiro.utils;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RequestsClass {
    private static Requests buildRequests() {
        return new Retrofit.Builder()
                .baseUrl(System.getenv("URL_SITE"))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(Requests.class);
    }
    private static Requests instance = null;

    public static Requests getRequests() {
        if (instance == null)
            instance = buildRequests();
        return instance;
    }
}
