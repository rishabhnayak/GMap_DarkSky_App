package com.MsoftTexas.WeatherOnMyTripRoute;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.maps.GeoApiContext;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.LatLng;

import java.util.Calendar;
import java.util.TimeZone;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.context;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.displayError;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mDay;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mHour;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mMinute;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mMonth;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mYear;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.month;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.progress;


public class TimezoneOfOrigin extends AsyncTask<String,String,String> {

    String emsgHead="Error";
    String emsg="";
    private long starttime;
    private LatLng startpoint;

    TimezoneOfOrigin(long starttime, LatLng startpoint) {
        this.startpoint=startpoint;
        this.starttime=starttime;
    }

    @Override
    protected void onPreExecute() {
        progress.setTitle("Fetching Local Time");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    protected void onPostExecute(String timezone) {


        progress.dismiss();
        if(timezone!=null) {
            TravelWithActivity.timezone = timezone;
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone(timezone));
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            TravelWithActivity.jstart_date_millis = c.getTimeInMillis() - ((mHour * 60 + mMinute) * 60 * 1000);
            TravelWithActivity.jstart_time_millis = (mHour * 60 + mMinute) * 60 * 1000;


            String sHour = mHour < 10 ? "0" + mHour : "" + mHour;
            String sMinute = mMinute < 10 ? "0" + mMinute : "" + mMinute;
            String curr_time = sHour + ":" + sMinute;
            //       time.setText(curr_time);
            TravelWithActivity.departAt.setText(curr_time + "," + mDay + " " + month[mMonth] + " " + String.valueOf(mYear).substring(2));
        }else{
              displayError(emsgHead,emsg);
        }
    }

    @Override
    protected String doInBackground(String... strings) {


        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(TravelWithActivity.googleAPIkey)
                    .build();

            try {
                return TimeZoneApi.getTimeZone(context, startpoint).await().getID();
            } catch (Exception e) {
                e.printStackTrace();
                this.emsgHead="Fetching Local Time Error";
                this.emsg=e.getMessage();
                 return null;
            }
        }else{
            this.emsgHead="No Internet Connection";
            this.emsg="Please Turn On Your Mobile Data";
            return null;
        }
    }
}
