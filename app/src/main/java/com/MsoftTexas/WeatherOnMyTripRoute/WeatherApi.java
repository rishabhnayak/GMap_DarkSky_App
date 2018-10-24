package com.MsoftTexas.WeatherOnMyTripRoute;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.DragupListAdapter_weather;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Apidata;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Input;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Item;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.MStep;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.apiData;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.context;

import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.link;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.markersSteps;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.destination;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.googleMap;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.interval;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_date_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_time_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.loading;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.loading_text;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.markersInterm;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.origin;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.restrictions;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.selectedroute;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.slidingUpPanelLayout;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.timezone;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.travelmode;

/**
 * Created by kamlesh on 29-03-2018.
 */

public class WeatherApi extends AsyncTask<Object,Object,String> {

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String data) {
        try {
        Apidata apidata=null;

        apidata = new Gson().fromJson(data, Apidata.class);

        System.out.println("weather data call has started........");
    //    MapActivity.weatherloaded=true;
   //     System.out.println("here is the list of intermediate Points:");
        apiData=apidata;
        int c=-1;
        if(apidata!=null && apidata.getItems()!=null){
            for(final Item item:apidata.getItems()) {
                c++;
   //             System.out.println(new Gson().toJson(item));
   //             loading.setVisibility(View.GONE);
   //             loading_text.setVisibility(View.GONE);
   //             slidingUpPanelLayout.setAlpha(1);
                //   googleMap.addMarker(new MarkerOptions().position(item.getPoint()));
                final int finalC = c;
                //Layout To Bitmap items............................................................................
                TextView weather=MapActivity.step_weather;

                TextView time=MapActivity.step_time;
                time.setText(item.getArrtime());

                Bitmap bitmap=MapActivity.layout_to_image.convert_layout();



                ImageView image= MapActivity.step_icon;
                switch (item.getWlist().getIcon()){
                    case "clear_day":image.setBackgroundResource(R.drawable.clear_day);
                        weather.setText("Clear Day");
                        break;
                    case "cloudy":image.setBackgroundResource(R.drawable.cloudy);
                        weather.setText("Cloudy");
                        break;
                    case "clear-night":image.setBackgroundResource(R.drawable.clear_night);
                        weather.setText("Clear Night");
                        break;
                    case "fog":image.setBackgroundResource(R.drawable.fog);
                        weather.setText("Fog");
                        break;
                    case "hail":image.setBackgroundResource(R.drawable.hail);
                        weather.setText("Hail");
                        break;
                    case "partly-cloudy-day":image.setBackgroundResource(R.drawable.partly_cloudy_day);
                        weather.setText("Partly Cloudy Day");
                        break;
                    case "partly-cloudy-night":image.setBackgroundResource(R.drawable.partly_cloudy_night);
                        weather.setText("Partly Cloudy Night");
                        break;
                    case "rain":image.setBackgroundResource(R.drawable.rain);
                        weather.setText("Rain");
                        break;
                    case "sleet":image.setBackgroundResource(R.drawable.sleet);
                        weather.setText("Sleet");
                        break;
                    case "snow":image.setBackgroundResource(R.drawable.snow);
                        weather.setText("Snow");
                        break;
                    case "thunderstorm":image.setBackgroundResource(R.drawable.thunderstorm);
                        weather.setText("Thunderstorm");
                        break;
                    case "tornado":image.setBackgroundResource(R.drawable.tornado);
                        weather.setText("Tornado");
                        break;
                    case "wind":image.setBackgroundResource(R.drawable.wind);
                        weather.setText("Wind");
                        break;
                    default:image.setBackgroundResource(R.drawable.clear_day);
                        weather.setText("Clear Day");
                }

//..................................................................................................
                BitmapDescriptor icon = new bitmapfromstring(item.getWlist().getIcon()).getIcon();
                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        .position(new LatLng(item.getPoint().lat, item.getPoint().lng)));
                                marker.setTag("I"+finalC);
                                markersInterm.add(marker);

            }
        }else{
            System.out.println("api data is null or api.getlist is null");
        }


        if(apidata!=null && apidata.getSteps()!=null){

            link.setAdapter(new DragupListAdapter_weather(context, apidata.getSteps()));
            for(final MStep mStep:apidata.getSteps()) {
                c++;
    //            System.out.println(new Gson().toJson(mStep));
    //            loading.setVisibility(View.GONE);
    //            loading_text.setVisibility(View.GONE);
    //            slidingUpPanelLayout.setAlpha(1);
                //   googleMap.addMarker(new MarkerOptions().position(item.getPoint()));
                final int finalC = c;
//Layout to Bitmap steps............................................................................
                TextView weather=MapActivity.step_weather;

                TextView time=MapActivity.step_time;
                time.setText(mStep.getArrtime());

                Bitmap bitmap=MapActivity.layout_to_image.convert_layout();


                ImageView image= MapActivity.step_icon;
                switch (mStep.getWlist().getIcon()){
                    case "clear_day":image.setBackgroundResource(R.drawable.clear_day);
                        weather.setText("Clear Day");
                        break;
                    case "cloudy":image.setBackgroundResource(R.drawable.cloudy);
                        weather.setText("Cloudy");
                        break;
                    case "clear-night":image.setBackgroundResource(R.drawable.clear_night);
                        weather.setText("Clear Night");
                        break;
                    case "fog":image.setBackgroundResource(R.drawable.fog);
                        weather.setText("Fog");
                        break;
                    case "hail":image.setBackgroundResource(R.drawable.hail);
                        weather.setText("Hail");
                        break;
                    case "partly-cloudy-day":image.setBackgroundResource(R.drawable.partly_cloudy_day);
                        weather.setText("Partly Cloudy Day");
                        break;
                    case "partly-cloudy-night":image.setBackgroundResource(R.drawable.partly_cloudy_night);
                        weather.setText("Partly Cloudy Night");
                        break;
                    case "rain":image.setBackgroundResource(R.drawable.rain);
                        weather.setText("Rain");
                        break;
                    case "sleet":image.setBackgroundResource(R.drawable.sleet);
                        weather.setText("Sleet");
                        break;
                    case "snow":image.setBackgroundResource(R.drawable.snow);
                        weather.setText("Snow");
                        break;
                    case "thunderstorm":image.setBackgroundResource(R.drawable.thunderstorm);
                        weather.setText("Thunderstorm");
                        break;
                    case "tornado":image.setBackgroundResource(R.drawable.tornado);
                        weather.setText("Tornado");
                        break;
                    case "wind":image.setBackgroundResource(R.drawable.wind);
                        weather.setText("Wind");
                        break;
                    default:image.setBackgroundResource(R.drawable.clear_day);
                        weather.setText("Clear Day");
                }
//..................................................................................................

                BitmapDescriptor icon = new bitmapfromstring(mStep.getWlist().getIcon()).getIcon();


                Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                            .position(new LatLng(mStep.getStep().startLocation.lat, mStep.getStep().startLocation.lng)));
                                    marker.setTag("S" + finalC);
                                    markersSteps.add(marker);

            }

        }else {
            System.out.println("api data is null or api.getlist is null");
        }

//        if(routeloaded) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //Do something after 100ms
//                    custom_dialog.setVisibility(View.GONE);
//                }
//            }, 1000);
//
//        }else{
//
//     //       custom_dialog.setVisibility(View.VISIBLE);
//     //       loading_text.setText("loading route...");
//        }

        }catch (Exception e){
            e.printStackTrace();
//            custom_dialog.setVisibility(View.VISIBLE);
//            loading.setVisibility(View.GONE);
//            loading_text.setVisibility(View.VISIBLE);
//            if(data.equals("NoInternet")){
//                loading_text.setText("No Internet Connection.Please Check Your Internet Connection");
//            }else {
//                loading_text.setText("Error :" + e.toString());
//            }
        }

    }

    @Override
    protected String doInBackground(Object[] objects) {
        try {
            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = null;

            BufferedReader rd=null;
                String url="https://4svktzsdok.execute-api.ap-south-1.amazonaws.com/dev";
              //  String url="https://dgaprckvs3.execute-api.ap-south-1.amazonaws.com/dev";
                HttpPost request = new HttpPost(url);

                Input input=new Input();
                input.setOrigin(origin);
                input.setDestination(destination);
                input.setRoute(selectedroute);
                input.setInterval(interval);
                input.setTimeZone(timezone);
                input.setTime(jstart_date_millis+jstart_time_millis);
                input.setRestrictions(restrictions);
                input.setTravelmode(travelmode);


                String json =new Gson().toJson(input);
                System.out.println("hre is json :\n"+json);
                StringEntity entity = new StringEntity(json);
                request.setEntity(entity);
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");


            response = client.execute(request);
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line="";
            StringBuilder sb=new StringBuilder();
            while ((line=rd.readLine())!=null){
                sb.append(line);
            }
            return sb.toString();
            }else{
                return "NoInternet";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}