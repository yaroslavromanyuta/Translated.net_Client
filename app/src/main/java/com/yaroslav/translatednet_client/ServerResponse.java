package com.yaroslav.translatednet_client;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class ServerResponse {

    @SerializedName("responseData")
    private Map<String,String> response;

    @SerializedName("responseDetails")
    private String details;

    @SerializedName("responseStatus")
    private int status;

    @SerializedName("responderId")
    private int responderId;

    @SerializedName("matches")
    private ArrayList<Map<String,String>> matches;




    public Map<String, String> getResponse() {
        return response;
    }

    public ArrayList<Map<String, String>> getMatches() {
        return matches;
    }

    public int getResponderId() {
        return responderId;
    }

    public String getDetails() {
        return details;
    }

    public int getStatus() {
        return status;
    }

    public void setResponse(Map<String, String> reponse) {
        this.response = reponse;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setMatches(ArrayList<Map<String, String>> matches) {
        this.matches = matches;
    }

    public void setResponderId(int responderId) {
        this.responderId = responderId;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
