package ar.edu.uade.recipes.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://tpo-desappi.vercel.app/";

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Interceptor de logging para monitorear requests/responses
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("Retrofit", message);
            });
            // Mostrar logs completos (cambiar a NONE en producción)
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
                        String token = prefs.getString("token", null);

                        Request.Builder builder = original.newBuilder();
                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
