//package com.MsoftTexas.WeatherOnMyTripRoute;
//
//import android.accounts.NetworkErrorException;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.DragupListAdapter_weather;
//import com.MsoftTexas.WeatherOnMyTripRoute.Models.Apidata;
//import com.MsoftTexas.WeatherOnMyTripRoute.Models.Input;
//import com.MsoftTexas.WeatherOnMyTripRoute.Models.Item;
//import com.MsoftTexas.WeatherOnMyTripRoute.Models.MStep;
//import com.google.android.gms.maps.model.BitmapDescriptor;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.gson.Gson;
//
////import org.apache.http.HttpResponse;
////import org.apache.http.client.HttpClient;
////import org.apache.http.client.methods.HttpPost;
////import org.apache.http.entity.StringEntity;
////import org.apache.http.impl.client.DefaultHttpClient;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.apiData;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.context;
//
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.link;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.location_icon;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.markersSteps;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.progressDialog;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.DistanceUnit;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.FERRIES;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.HIGHWAYS;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.TOLLS;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.destination;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.googleMap;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.interval;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.displayError;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_date_millis;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_time_millis;
//
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.markersInterm;
//
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.origin;
//
//
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.progress;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.selectedroute;
//import static com.MsoftTexas.WeatherOnMyTripRoute.MapActivity.slidingUpPanelLayout;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.timezone;
//import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.travelmode;
//
///**
// * Created by kamlesh on 29-03-2018.
// */
//
//public class WeatherApi extends AsyncTask<Object,Object,Apidata> {
//
//    String emsgHead="error";
//    String emsg="";
//
//    @Override
//    protected void onPreExecute() {
//        progressDialog.setTitle("Loading Weather Data...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setIndeterminate(true);
//       (TravelWithActivity.activity).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//               WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//      //  progressDialog
//                //.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABL
//
//
//        progressDialog.show();
//    }
//
//    @Override
//    protected void onPostExecute(Apidata apidata) {
//        progressDialog.dismiss();
//        try {
//
//         if(apidata !=null) {
//             apiData = apidata;
//             int c = -1;
//             if (apidata.getItems() != null) {
//                 for (final Item item : apidata.getItems()) {
//                     c++;
//                     final int finalC = c;
//                     //Layout To Bitmap items............................................................................
//                     TextView weather = MapActivity.step_weather;
//
//                     TextView time = MapActivity.step_time;
//                     String time_data[] = item.getArrtime().split(",", 2);
//                     if (time_data.length >= 2)
//                         time.setText(time_data[0] + "\n" + time_data[1]);
//                     else time.setText(item.getArrtime());
//                     if (item.getLname() != null) {
//                         String lname[] = item.getLname().split(",");
//                         if (lname.length >= 2)
//                             MapActivity.location_name.setText(lname[0].length() < 20 ? lname[0] : lname[0].substring(0, 19) + "..,\n" + lname[1]);
//                         else {
//                             MapActivity.location_name.setText(lname[0]);
//                         }
//                     }
//                     Bitmap bitmap = MapActivity.layout_to_image.convert_layout();
//
//
//                     ImageView image = MapActivity.step_icon;
//                     switch (item.getWlist().getIcon()) {
//                         case "clear_day":
//                             image.setBackgroundResource(R.drawable.clear_day);
//                             weather.setText("Clear Day");
//                             break;
//                         case "cloudy":
//                             image.setBackgroundResource(R.drawable.cloudy);
//                             weather.setText("Cloudy");
//                             break;
//                         case "clear-night":
//                             image.setBackgroundResource(R.drawable.clear_night);
//                             weather.setText("Clear Night");
//                             break;
//                         case "fog":
//                             image.setBackgroundResource(R.drawable.fog);
//                             weather.setText("Fog");
//                             break;
//                         case "hail":
//                             image.setBackgroundResource(R.drawable.hail);
//                             weather.setText("Hail");
//                             break;
//                         case "partly-cloudy-day":
//                             image.setBackgroundResource(R.drawable.partly_cloudy_day);
//                             weather.setText("Partly Cloudy Day");
//                             break;
//                         case "partly-cloudy-night":
//                             image.setBackgroundResource(R.drawable.partly_cloudy_night);
//                             weather.setText("Partly Cloudy Night");
//                             break;
//                         case "rain":
//                             image.setBackgroundResource(R.drawable.rain);
//                             weather.setText("Rain");
//                             break;
//                         case "sleet":
//                             image.setBackgroundResource(R.drawable.sleet);
//                             weather.setText("Sleet");
//                             break;
//                         case "snow":
//                             image.setBackgroundResource(R.drawable.snow);
//                             weather.setText("Snow");
//                             break;
//                         case "thunderstorm":
//                             image.setBackgroundResource(R.drawable.thunderstorm);
//                             weather.setText("Thunderstorm");
//                             break;
//                         case "tornado":
//                             image.setBackgroundResource(R.drawable.tornado);
//                             weather.setText("Tornado");
//                             break;
//                         case "wind":
//                             image.setBackgroundResource(R.drawable.wind);
//                             weather.setText("Wind");
//                             break;
//                         default:
//                             image.setBackgroundResource(R.drawable.clear_day);
//                             weather.setText("Clear Day");
//                     }
//
////..................................................................................................
//                     BitmapDescriptor icon = new bitmapfromstring(item.getWlist().getIcon()).getIcon();
//                     Marker marker = googleMap.addMarker(new MarkerOptions()
//                             .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                             .position(new LatLng(item.getPoint().lat, item.getPoint().lng)));
//                     marker.setTag("I" + finalC);
//                     markersInterm.add(marker);
//
//                 }
//             } else {
//                 System.out.println("api data is null or api.getlist is null");
//             }
//
//
//             if (apidata.getSteps() != null) {
//
//                 link.setAdapter(new DragupListAdapter_weather(context, apidata.getSteps()));
//                 for (final MStep mStep : apidata.getSteps()) {
//                     c++;
//
//                     final int finalC = c;
////Layout to Bitmap steps............................................................................
//                     TextView weather = MapActivity.step_weather;
//
//                     TextView time = MapActivity.step_time;
//                     String time_data[] = mStep.getArrtime().split(",", 2);
//                     if (time_data.length >= 2)
//                         time.setText(time_data[0] + "\n" + time_data[1]);
//                     else time.setText(mStep.getArrtime());
//                     MapActivity.location_name.setText("");
//                     location_icon.setVisibility(View.GONE);
//                     Bitmap bitmap = MapActivity.layout_to_image.convert_layout();
//
//
//                     ImageView image = MapActivity.step_icon;
//                     switch (mStep.getWlist().getIcon()) {
//                         case "clear_day":
//                             image.setBackgroundResource(R.drawable.clear_day);
//                             weather.setText("Clear Day");
//                             break;
//                         case "cloudy":
//                             image.setBackgroundResource(R.drawable.cloudy);
//                             weather.setText("Cloudy");
//                             break;
//                         case "clear-night":
//                             image.setBackgroundResource(R.drawable.clear_night);
//                             weather.setText("Clear Night");
//                             break;
//                         case "fog":
//                             image.setBackgroundResource(R.drawable.fog);
//                             weather.setText("Fog");
//                             break;
//                         case "hail":
//                             image.setBackgroundResource(R.drawable.hail);
//                             weather.setText("Hail");
//                             break;
//                         case "partly-cloudy-day":
//                             image.setBackgroundResource(R.drawable.partly_cloudy_day);
//                             weather.setText("Partly Cloudy Day");
//                             break;
//                         case "partly-cloudy-night":
//                             image.setBackgroundResource(R.drawable.partly_cloudy_night);
//                             weather.setText("Partly Cloudy Night");
//                             break;
//                         case "rain":
//                             image.setBackgroundResource(R.drawable.rain);
//                             weather.setText("Rain");
//                             break;
//                         case "sleet":
//                             image.setBackgroundResource(R.drawable.sleet);
//                             weather.setText("Sleet");
//                             break;
//                         case "snow":
//                             image.setBackgroundResource(R.drawable.snow);
//                             weather.setText("Snow");
//                             break;
//                         case "thunderstorm":
//                             image.setBackgroundResource(R.drawable.thunderstorm);
//                             weather.setText("Thunderstorm");
//                             break;
//                         case "tornado":
//                             image.setBackgroundResource(R.drawable.tornado);
//                             weather.setText("Tornado");
//                             break;
//                         case "wind":
//                             image.setBackgroundResource(R.drawable.wind);
//                             weather.setText("Wind");
//                             break;
//                         default:
//                             image.setBackgroundResource(R.drawable.clear_day);
//                             weather.setText("Clear Day");
//                     }
////..................................................................................................
//
//                     BitmapDescriptor icon = new bitmapfromstring(mStep.getWlist().getIcon()).getIcon();
//
//
//                     Marker marker = googleMap.addMarker(new MarkerOptions()
//                             .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                             .position(new LatLng(mStep.getStep().startLocation.lat, mStep.getStep().startLocation.lng)));
//                     marker.setTag("S" + finalC);
//                     markersSteps.add(marker);
//
//                 }
//
//             } else {
//
//             }
//         }else {
//             displayError(emsgHead,emsg);
//         }
//
//        }catch (Exception e){
//            displayError("Weather Display Error ","Error While Parsing Weather");
//        }
//
//    }
//
//    @Override
//    protected Apidata doInBackground(Object[] objects) {
//        try {
//            ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
//
//            if (netInfo != null && netInfo.isConnected()) {
//
//                Input input=new Input();
//                input.setOrigin(origin);
//                input.setDestination(destination);
//                input.setRoute(selectedroute);
//                input.setInterval(interval);
//                input.setTimeZone(timezone);
//                input.setTime(jstart_date_millis+jstart_time_millis);
//                input.setTravelmode(travelmode);
//                input.setDistanceUnit(DistanceUnit);
//
//                String restrictions="0";
//                if(HIGHWAYS)restrictions+="1";
//                if(TOLLS)restrictions+="2";
//                if(FERRIES)restrictions+="3";
//                input.setRestrictions(restrictions);
//
//
//                OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                        .readTimeout(120, TimeUnit.SECONDS)
//                        .writeTimeout(120, TimeUnit.SECONDS)
//                        .connectTimeout(120, TimeUnit.SECONDS)
//                        //.addInterceptor(loggingInterceptor)
//                        //.addNetworkInterceptor(networkInterceptor)
//                        .build();
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("https://4svktzsdok.execute-api.ap-south-1.amazonaws.com/")
//                        .client(okHttpClient)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                ApiInterface apiService = retrofit.create(ApiInterface.class);
//                Call<Apidata> call = apiService.inputCall(input);
//
//                return call.execute().body();
//
//            }else{
//                this.emsgHead="No Internet Connection";
//                this.emsg="Please TurnOn Your Mobile Data";
//            }
//
//        } catch (Exception e) {
//            this.emsg=e.getMessage();
//            this.emsgHead="Error while Dowloading Weather";
//        }
//        return null;
//    }
//
//
//}