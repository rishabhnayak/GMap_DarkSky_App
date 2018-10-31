package com.MsoftTexas.WeatherOnMyTripRoute;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.AdapterList;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.DistanceUnit;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.FERRIES;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.HIGHWAYS;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.TOLLS;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.context;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.destination;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.displayError;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_date_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_time_millis;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.origin;


import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.progress;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.recyclerView;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.timezone;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.travelmode;


/**
 * Created by kamlesh on 29-03-2018.
 */
public class RouteApi extends AsyncTask<Object,Object,DirectionsResult> {
    String emsgHead="error";
    String emsg="";

    @Override
    protected void onPreExecute() {


        progress.setTitle("Loading Routes...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        progress.show();
    }

    @Override
    protected void onPostExecute(DirectionsResult apidata) {


        progress.dismiss();
        if(apidata !=null) {
            if (apidata.routes != null && apidata.routes.length > 0) {

                TravelWithActivity.directionapi = apidata;
                LinearLayoutManager manager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(manager);
                recyclerView.setHasFixedSize(true);

                AdapterList adapterList = new AdapterList(TravelWithActivity.context, apidata);

                recyclerView.setAdapter(adapterList);
            } else {
                Toast.makeText(context, "No Routes Available", Toast.LENGTH_SHORT).show();
                displayError("No Route Available", "no Routes found between the Given Start and End Address");
            }
        }else {
            displayError(emsgHead,emsg);
            }

    }

    @Override
    protected DirectionsResult doInBackground(Object[] objects) {
        try {
            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();


            if (netInfo != null && netInfo.isConnected()) {

                GeoApiContext context = new GeoApiContext.Builder().apiKey(TravelWithActivity.googleAPIkey)
                        .build();

                final DirectionsApiRequest apiRequest = DirectionsApi.newRequest(context);
                apiRequest.origin(origin);
                apiRequest.destination(destination);
                apiRequest.alternatives(true);


              switch (DistanceUnit){
                  case 0:break;
                  case 1:apiRequest.units(Unit.IMPERIAL);break;
                  case 2:apiRequest.units(Unit.METRIC);break;
                  default:
              }

                switch (travelmode){
                    case 0 :apiRequest.mode(TravelMode.DRIVING);
                        break;
                    case 1: apiRequest.mode(TravelMode.BICYCLING);
                        break;
                    case 2: apiRequest.mode(TravelMode.WALKING);
                        break;
                }


                List<DirectionsApi.RouteRestriction> restrictions=new ArrayList<>();
                if(HIGHWAYS) restrictions.add(DirectionsApi.RouteRestriction.HIGHWAYS);

                if(TOLLS) restrictions.add(DirectionsApi.RouteRestriction.TOLLS);

                if(FERRIES)restrictions.add(DirectionsApi.RouteRestriction.FERRIES);


                apiRequest.avoid(restrictions.toArray(new DirectionsApi.RouteRestriction[restrictions.size()]));


                long time=jstart_date_millis+jstart_time_millis;

                DateTime t= new DateTime(time, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezone)));
                System.out.println(t.toDateTime());


                if(t.getMillis()>Calendar.getInstance().getTimeInMillis())
                apiRequest.departureTime(t);


                return apiRequest.await();

             }else{
                 this.emsgHead="No Internet Connection";
                 this.emsg="Please TurnOn Your Mobile Data";
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.emsg=e.getMessage();
            this.emsgHead="Error";
        }
        return null;

    }


}
