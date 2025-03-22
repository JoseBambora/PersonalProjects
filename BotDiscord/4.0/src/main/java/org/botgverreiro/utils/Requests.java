package org.botgverreiro.utils;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public interface Requests {
    @GET("modalidade/futebol/?eqp=687#calendario")
    Call<String> getNextGames();

    @GET("modalidade/futebol/?eqp=687#resultados")
    Call<String> getResults();
}