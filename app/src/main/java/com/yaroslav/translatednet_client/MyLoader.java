package com.yaroslav.translatednet_client;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.yaroslav.translatednet_client.Constants.LOG_TAG;

public class MyLoader extends AsyncTaskLoader<Translation> {
    Translation translation;
    public MyLoader(Context context, Translation translation) {
        super(context);
        Log.d(LOG_TAG, "Myloader oncreate, "+ translation.getTranslFrom());
        this.translation = translation;
    }

    public String convertFromUnicode (String string){

        Log.d(LOG_TAG, "converting from unicode");
        try {
            byte[] utf = string.getBytes("UTF-8");
            string = new String(utf,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    @Override
    public Translation loadInBackground() {

        Log.d(LOG_TAG, "Loader do in bakground");


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        Api api = retrofit.create(Api.class);

        //translation.putLanguages();
        String query = translation.getTranslFrom();
        String langpair = translation.getLangFrom()+"|"+translation.getLangTo();
        Call<ServerResponse> call = api.getTranslation(query, langpair);
        Response<ServerResponse> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG,""+e.toString());
        }
        Log.d(Constants.LOG_TAG, "response created. HTTP status code=" + response.code() + "; isSuccess=" + response.isSuccess() + "; message=" + response.message() + " Response.body()=" + response.body().getResponse());

        Map<String,String> responseData = response.body().getResponse();

        translation.setTranslatedText(convertFromUnicode(responseData.get("translatedText")));


        Log.d(LOG_TAG, "transl: " + "orig - " + translation.getTranslFrom() + ", to - "
                + translation.getTranslatedText() + ", langpair - " + translation.getLangFrom() +"|"+ translation.getLangTo());

        return translation;
    }


}
