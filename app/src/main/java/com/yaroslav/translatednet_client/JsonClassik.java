package com.yaroslav.translatednet_client;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class JsonClassik  extends AsyncTask<View,Void,Void>{
    Translation translation;

    JsonClassik(Translation translation){
        this.translation = translation;
    }

    @Override
    protected Void doInBackground(View... params) {
        translation.putLanguages();

        String body = null;
        String query = null;
        String langpair = null;
/*
        try{
         query = URLEncoder.encode(translation.getTranslFrom(), "UTF-8");
         langpair = URLEncoder.encode(translation.getLangFrom() + "|" + translation.getLangTo(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = "http://mymemory.translated.net/api/get?q="+query+"&langpair="+langpair;
        HttpClient hc = new DefaultHttpClient();
        HttpGet hg = new HttpGet(url);
        HttpResponse hr = hc.execute(hg);
        if(hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            JSONObject response = new JSONObject(EntityUtils.toString(hr.getEntity()));
                body = response.getJSONObject("responseData").toString();
        }
  */

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();

            Api api = retrofit.create(Api.class);

        query = translation.getTranslFrom();
        langpair = translation.getLangFrom()+"|"+translation.getLangTo();
            Call<ServerResponse> call = api.getTranslation(query, langpair);
        Response<ServerResponse> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.LOG_TAG, "response created. HTTP status code=" + response.code() + "; isSuccess=" + response.isSuccess() + "; message=" + response.message() + " Response.body()=" + response.body().getResponse());

            //Log.d(Constants.LOG_TAG, response.getTranslatedText());



   // Log.d(Constants.LOG_TAG, body);

        return null;
    }
}
