package com.yaroslav.translatednet_client;


import android.util.Log;

import static com.yaroslav.translatednet_client.Constants.LOG_TAG;

public class Translation {


    private String translFrom;
    private  String translatedText;
    private String langFrom;
    private String langTo;


    Translation(){

    }

    Translation(String translFrom, String translatedText, String langFrom, String langTo){
        this.translFrom = translFrom;
        this.translatedText = translatedText;
        this.langFrom = langFrom;
        this.langTo = langTo;

    }


    public String getLangFrom() {
        return langFrom;
    }

    public String getLangTo() {
        return langTo;
    }

    public String getTranslatedText() {

        return translatedText;
    }

    public String getTranslFrom() {
        return translFrom;
    }

    public void setLangFrom(String langFrom) {
        this.langFrom = langFrom;
    }

    public void setLangTo(String langTo) {
        this.langTo = langTo;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
        Log.d(LOG_TAG,"translated text set: " + this.translatedText );
    }

    public void setTranslFrom(String translFrom) {
        this.translFrom = translFrom;
    }



    public void putLanguages (){


        char[] translfromarray = translFrom.toCharArray();
        boolean isEng = true;
        for (char i : translfromarray){
           if (Character.UnicodeBlock.CYRILLIC.equals(Character.UnicodeBlock.of(i)))
               isEng = false;
        }
        if (isEng){
            langFrom = "en";
            langTo = "ru";
        }
        else {
            langTo = "en";
            langFrom = "ru";
        }

        Log.d(LOG_TAG, "rasstanovka lang: from - " + langFrom + " to - " + langTo);

    }
}
