package com.MsoftTexas.WeatherOnMyTripRoute;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;

import com.MsoftTexas.WeatherOnMyTripRoute.DirectionApiModel.DirectionApi;
import com.MsoftTexas.WeatherOnMyTripRoute.DirectionApiModel.Route;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.context;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.custom_dialog;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.destination;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.googleMap;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.jstart_date_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.jstart_time_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.loading;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.loading_text;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.origin;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.polylineOptionsList;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.polylines;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.restrictions;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.setCameraWithCoordinationBounds;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.slidingUpPanelLayout;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.travelmode;
import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.weatherloaded;

/**
 * Created by kamlesh on 29-03-2018.
 */
public class RouteApi extends AsyncTask<Object,Object,DirectionsResult> {


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(DirectionsResult apidata) {

        try {
        //    DirectionApi apidata = new Gson().fromJson(data, DirectionApi.class);
            MapActivity.routeloaded = true;

            //      System.out.println("direction data : "+new Gson().toJson(apidata));
      //      MapActivity.directionapi = apidata;
            DirectionsRoute route = apidata.routes[0];


//            System.out.println("here is polyline : " + apidata.routes[0].overviewPolyline.getEncodedPath());
//            if (weatherloaded) {
//                custom_dialog.setVisibility(View.GONE);
//            } else {
//                MapActivity.loading_text.setText("loading weather..");
//            }

//            System.out.println("here is the route data :\n" + new Gson().toJson(apidata));
            if (new Gson().toJson(apidata) != null) {

                slidingUpPanelLayout.setAlpha(1);
            }
            System.out.println("direction success.............babes.......");
            MapActivity.polylines = new ArrayList<>();
            //add route(s) to the map.

            MapActivity.distance.setText("(" + route.legs[0].distance.humanReadable+ ")");
            MapActivity.duration.setText(route.legs[0].durationInTraffic!=null?route.legs[0].durationInTraffic.humanReadable:route.legs[0].duration.humanReadable);
            if (route.legs[0].duration.humanReadable != null) {
                slidingUpPanelLayout.setPanelHeight(context.getResources().getDimensionPixelSize(R.dimen.dragupsize));
            }


            polylineOptionsList = new ArrayList<>();
            System.out.println("route options : " + apidata.routes.length);
            Polyline selectedPolyline = null;
            if (apidata.routes.length > 0) {
               List<LatLng> lst = PolyUtil.decode(apidata.routes[0].overviewPolyline.getEncodedPath());

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(context.getResources().getColor(R.color.seletedRoute));
                polyOptions.width(14);
                polyOptions.addAll(lst);
                polylineOptionsList.add(polyOptions);
                MapActivity.polylines.add(selectedPolyline);
            }

            if (apidata.routes.length > 1) {
                for (int i = 1; i < apidata.routes.length; i++) {
                    List<LatLng> lst = PolyUtil.decode(apidata.routes[i].overviewPolyline.getEncodedPath());
                    //In case of more than 5 alternative routes
                    //   int colorIndex = i % COLORS.length;

                    PolylineOptions polyOptions = new PolylineOptions();

                    polyOptions.color(context.getResources().getColor(R.color.alternateRoute));
                    polyOptions.width(12);


                    polyOptions.addAll(lst);
                    Polyline polyline = MapActivity.googleMap.addPolyline(polyOptions);
                    MapActivity.polylines.add(polyline);
                    polyline.setClickable(true);
                    polylineOptionsList.add(polyOptions);
                }
            }

            if (polylineOptionsList != null && polylineOptionsList.get(0) != null) {
                selectedPolyline = googleMap.addPolyline(polylineOptionsList.get(0));
                polylines.set(0, selectedPolyline);
                selectedPolyline.setClickable(true);
            }

            setCameraWithCoordinationBounds(route);
        }catch (Exception e){
            e.printStackTrace();
            custom_dialog.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            loading_text.setVisibility(View.VISIBLE);
//            if(data.equals("NoInternet")){
//                loading_text.setText("No Internet Connection.Please Check Your Internet Connection");
//            }else {
//                loading_text.setText("Error :" + e.toString());
//            }
        }

    }

    @Override
    protected DirectionsResult doInBackground(Object[] objects) {
        try {
            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnected()) {

                GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyCv_imK5ydtkdWnGJP1Dbt-DT07UdvyDeo")
                        .build();

                final DirectionsApiRequest apiRequest = DirectionsApi.newRequest(context);
                apiRequest.origin(new com.google.maps.model.LatLng(origin.latitude,origin.longitude));
                apiRequest.destination(new com.google.maps.model.LatLng(destination.latitude,destination.longitude));
                apiRequest.alternatives(true);


                switch (travelmode){
                    case 0 :apiRequest.mode(TravelMode.DRIVING);
                        break;
                    case 1: apiRequest.mode(TravelMode.BICYCLING);
                        break;
                    case 2: apiRequest.mode(TravelMode.WALKING);
                        break;
                }

                for (String restriction:restrictions.split("")) {
                    try {
                        switch (Integer.parseInt(restrictions)) {
                            case 0: break;
                            case 1:
                                apiRequest.avoid(DirectionsApi.RouteRestriction.HIGHWAYS);
                                break;
                            case 2:
                                apiRequest.avoid(DirectionsApi.RouteRestriction.TOLLS);
                                break;
                            case 3:
                                apiRequest.avoid(DirectionsApi.RouteRestriction.FERRIES);
                                break;

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                long time=jstart_date_millis+jstart_time_millis;


                apiRequest.departureTime(new DateTime(time));
                return apiRequest.await();


//                    HttpClient client = new DefaultHttpClient();
//
//
//                    HttpResponse response = null;
//
//                    //nbsc-1518068960369.appspot.com
//                    System.out.println("https://maps.googleapis.com/maps/api/directions/json?origin="
//                            + origin.latitude + "," + origin.longitude
//                            + "&destination=" + destination.latitude + "," + destination.longitude
//                            + "&alternatives=true"
//                            + "&key=AIzaSyDi3B9R9hVpC9YTmOCCz_pCR1BKW3tIRGY");
//                    HttpGet request = new HttpGet("https://maps.googleapis.com/maps/api/directions/json?origin="
//                            + origin.latitude + "," + origin.longitude
//                            + "&destination=" + destination.latitude + "," + destination.longitude
//                            + "&alternatives=true"
//                            + "&key=AIzaSyDi3B9R9hVpC9YTmOCCz_pCR1BKW3tIRGY");
//                    BufferedReader rd = null;
//                    try {
//                        response = client.execute(request);
//                        rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//                        String line = "";
//                        StringBuilder sb = new StringBuilder();
//                        while ((line = rd.readLine()) != null) {
//                            sb.append(line);
//                        }
//                        return sb.toString();
//
//                    } catch (Exception e) {
//                        System.out.println("error : " + e.toString());
//                        String line = "";
//                    }
             }else{
               throw new NetworkErrorException();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


}
