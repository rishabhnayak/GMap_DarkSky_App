package com.MsoftTexas.WeatherOnMyTripRoute;


import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.maps.GeoApiContext;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.LatLng;

import java.util.Calendar;
import java.util.TimeZone;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mDay;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mHour;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mMinute;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mMonth;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.mYear;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.month;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.progress;


public class TimezoneOfOrigin extends AsyncTask<String,String,String> {

//    public static void main(String... args){
//
//
//        double lat=39.738453;
//        double lng=-104.984853;
//        long time=System.currentTimeMillis()/1000;
//
//        System.out.println(new TimezoneOfOrigin(time,new LatLng(lat,lng)).timezone());
//
//    }

    private long starttime;
    private LatLng startpoint;

    TimezoneOfOrigin(long starttime, LatLng startpoint) {
        this.startpoint=startpoint;
        this.starttime=starttime;
    }

    @Override
    protected void onPreExecute() {
        progress.setTitle("Fetching Timezone");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    protected void onPostExecute(String timezone) {

        progress.dismiss();
        TravelWithActivity.timezone= timezone;
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone(timezone));
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TravelWithActivity.jstart_date_millis=c.getTimeInMillis()-((mHour*60+mMinute)*60*1000);
        TravelWithActivity.jstart_time_millis=(mHour*60+ mMinute)*60*1000;


        String sHour = mHour < 10 ? "0" + mHour : "" + mHour;
        String sMinute = mMinute < 10 ? "0" + mMinute : "" + mMinute;
        String curr_time = sHour + ":" + sMinute;
        //       time.setText(curr_time);
        TravelWithActivity.departAt.setText(curr_time+","+mDay+" "+month[mMonth]+" "+String.valueOf(mYear).substring(2)+" "+timezone);

    }

    @Override
    protected String doInBackground(String... strings) {

        GeoApiContext context = new GeoApiContext.Builder().apiKey(TravelWithActivity.googleAPIkey)
                .build();

        try {


              return TimeZoneApi.getTimeZone(context, startpoint).await().getID();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
