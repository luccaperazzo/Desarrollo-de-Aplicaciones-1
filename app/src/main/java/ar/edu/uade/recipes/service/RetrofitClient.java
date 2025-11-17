package ar.edu.uade.recipes.service;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ar.edu.uade.recipes.util.UserManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static Retrofit retrofitTranscription;
    private static final String BASE_URL = "https://tpo-desappi.vercel.app/";
    private static final String TRANSCRIPTION_URL = "https://flask-xi-liard.vercel.app/";
    private static OkHttpClient client;

    private static OkHttpClient getClient(Context context) {
        if (client == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d("Retrofit", message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        UserManager userManager = new UserManager(context);
                        String token = userManager.getToken();
                        Request.Builder builder = original.newBuilder();
                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();
        }
        return client;
    }

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitInstanceForTranscription(Context context) {
        if (retrofitTranscription == null) {
            retrofitTranscription = new retrofit2.Retrofit.Builder()
                    .baseUrl(TRANSCRIPTION_URL)
                    .client(getClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitTranscription;
    }
}
