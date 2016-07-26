package co.ar_smart.www.helpers;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static co.ar_smart.www.helpers.Constants.BASE_URL;

/**
 * This class implemments static methods for the REST client
 * Created by Gabriel on 5/4/2016.
 */
public class RetrofitServiceGenerator {

    /**
     * The okhttp web client instance
     */
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    /**
     * The base URL for the retrofit client and gson converter
     */
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, "");
    }

    /**
     * Creates a service for a REST interface with a JWT token instance
     *
     * @param serviceClass the REST interface to use
     * @param jwtToken     the JWT token for doing the requests
     * @param <S>          the type of the response
     * @return a retrofit service
     */
    public static <S> S createService(Class<S> serviceClass, String jwtToken) {
        final String token = "JWT  " + jwtToken;
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
