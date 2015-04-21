package com.jawnnypoo.openmeh.service;

import com.jawnnypoo.openmeh.BuildConfig;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Meh
 * Created by John on 4/17/2015.
 */
public class MehClient {
    //Replace with your API_KEY
    private static final String API_KEY_VALUE = BuildConfig.MEH_API_KEY;
    private static final String API_URL = "https://api.meh.com/1";
    private static final String PARAM_API_KEY = "apikey";

    private static Meh mMeh;

    public interface Meh {
        @GET("/current.json")
        void getMeh(
                Callback<MehResponse> responseCallback
        );

        @GET("/current.json")
        MehResponse getMeh();
    }

    public static Meh instance() {
        if (mMeh == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .setRequestInterceptor(new MehRequestInterceptor())
                    .build();
            mMeh = restAdapter.create(Meh.class);
        }
        return mMeh;
    }

    public static class MehRequestInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam(PARAM_API_KEY, API_KEY_VALUE);
        }
    }
}
