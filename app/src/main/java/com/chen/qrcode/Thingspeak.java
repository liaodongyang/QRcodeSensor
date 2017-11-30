package com.chen.qrcode;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**th
 * Created by Liao on 8/14/2017.
 */

public class Thingspeak implements Runnable{

    private String mData = null;
    private String mfield = null;
    private String mapi_key = null;
    private String R = null;
    private String G = null;
    private String B = null;
    private String Latitude = null;
    private String Longitude = null;

    void setData(String mData){
        this.mData = mData;
    }
    void setfield(String mfield){
        this.mfield = mfield;
    }
    void setKey(String mapi_key){
        this.mapi_key = mapi_key;
    }
    void setR(String R){
        this.R = R;
    }
    void setG(String G){
        this.G = G;
    }
    void setB(String B){
        this.B = B;
    }
    void setLatitude(String Latitude){
        this.Latitude = Latitude;
    }
    void setLongitude(String Longitude){
        this.Longitude = Longitude;
    }

    public void run() {
        //HttpClient httpCient = new DefaultHttpClient(); //http get
        //String path = "https://api.thingspeak.com/update?api_key="+mapi_key+"&field"+mfield+"="+mData;
        //Longitude ="0";
        //Latitude = "0";
        String path = "https://api.thingspeak.com/update?api_key="+mapi_key+"&field1="+mData+"&field2="+Longitude+"&field3="+Latitude+"&field4="+R+"&field5="+G+"&field6="+B;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            //Toast.makeText( context ,res.toString() ,Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
