package com.MsoftTexas.WeatherOnMyTripRoute;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.DragupListAdapter_weather;
import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.DragupListAdapter_route;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Apidata;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Input;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Item;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.MStep;
import com.MsoftTexas.WeatherOnMyTripRoute.util.IabBroadcastReceiver;
import com.MsoftTexas.WeatherOnMyTripRoute.util.IabHelper;
import com.MsoftTexas.WeatherOnMyTripRoute.util.IabResult;
import com.MsoftTexas.WeatherOnMyTripRoute.util.Inventory;
import com.MsoftTexas.WeatherOnMyTripRoute.util.Purchase;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsRoute;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.DistanceUnit;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.FERRIES;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.HIGHWAYS;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.TOLLS;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.context;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.destination;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.directionapi;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_date_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.jstart_time_millis;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.origin;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.selectedroute;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.timezone;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.travelmode;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_ENDED;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_NOT_YET_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_OVER;
import static io.trialy.library.Constants.STATUS_TRIAL_RUNNING;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback
   {

    static Context context;

    final MapActivity cont=MapActivity.this;
            static android.app.AlertDialog.Builder bld;
    static List<PolylineOptions> polylineOptionsList;
    static List<Polyline> polylines=new ArrayList<>();
    static List<Marker> markersInterm = new ArrayList<>();
    static List<Marker> markersSteps = new ArrayList<>();
    static TextView loading_text;

    static long interval=50000;

    static ProgressDialog progressDialog;

    static  SlidingUpPanelLayout slidingUpPanelLayout;

    static private Marker originMarker, dstnMarker;
    private List<Marker> markers = new ArrayList<>();
    static Apidata apiData=null;
    static GoogleMap googleMap;
    private String serverKey = "AIzaSyDi3B9R9hVpC9YTmOCCz_pCR1BKW3tIRGY";

    static TextView distance, duration;


    protected GeoDataClient mGeoDataClientS, mGeoDataClientD;
    SharedPreferences sd;

    static RecyclerView link;
    DragupListAdapter_route routeadapter;

            static Layout_to_Image layout_to_image;
            static LinearLayout relativeLayout;
            static TextView step_time,step_weather,location_name;
            static ImageView step_icon;
            static ImageView location_icon;




Menu menu;
SharedPreferences.Editor editor;
            int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle("MapView");
        editor = getSharedPreferences("distance", MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences("distance", MODE_PRIVATE);
        int a=prefs.getInt("10",0);
        switch(a){
            case 10:
                //  Toast.makeText(mApp, "10", Toast.LENGTH_SHORT).show();

                try {
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MenuItem item = menu.findItem(R.id.km10);
                            interval=10000;
                            i=1;
                            item.setChecked(true);
                        }
                    }, 2000);
                }catch (Exception e){

                }
                break;
            case 20:
                //  Toast.makeText(mApp, "20", Toast.LENGTH_SHORT).show();
                try {
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MenuItem item = menu.findItem(R.id.km20);
                            item.setChecked(true);
                            interval=20000;
                            i=2;
                        }
                    }, 2000);
                }catch (Exception e){

                }

                break;
            case 30:
                //  Toast.makeText(mApp, "30", Toast.LENGTH_SHORT).show();
                try {
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MenuItem item = menu.findItem(R.id.km30);
                            item.setChecked(true);
                            interval=30000;
                            i=3;
                        }
                    }, 2000);
                }catch (Exception e){

                }
                break;
            case 40:
                // Toast.makeText(mApp, "40", Toast.LENGTH_SHORT).show();
                try {
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MenuItem item = menu.findItem(R.id.km40);
                            item.setChecked(true);
                            interval=40000;
                            i=4;
                        }
                    }, 2000);
                }catch (Exception e){

                }
                break;
            case 50:
                // Toast.makeText(mApp, "50", Toast.LENGTH_SHORT).show();
                try {
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MenuItem item = menu.findItem(R.id.km50);
                            item.setChecked(true);
                            interval=50000;
                            i=5;
                        }
                    }, 2000);
                }catch (Exception e){

                }
                break;
            default:
                //   Toast.makeText(mApp, "0", Toast.LENGTH_SHORT).show();
        }


        progressDialog=new ProgressDialog(this);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MapActivity");
 //       mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        sd = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=getApplicationContext();



        //setting title null
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar back = (Toolbar) findViewById(R.id.toolbar);

        back.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                MapActivity.super.onBackPressed();
            }
        });

//sliding up layout
        slidingUpPanelLayout=findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelHeight(0);


        link = (RecyclerView) findViewById(R.id.dragup_list_recycler);
        link.setLayoutManager(new LinearLayoutManager(this));
//Markers with text.................................................................................


        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // only for gingerbread and newer versions
            ((LinearLayout)findViewById(R.id.show)).setBackgroundResource(R.drawable.chat);
        } else {
    ((LinearLayout)findViewById(R.id.show)).setBackgroundResource(R.drawable.ic_chat_bubble_black_24dp);

}


        routeadapter = new DragupListAdapter_route(context, directionapi.routes[selectedroute]);
        link.setAdapter(routeadapter);

        //provide layout with its id in Xml
        relativeLayout=findViewById(R.id.show);
        step_time=findViewById(R.id.step_time);
        step_weather=findViewById(R.id.step_weather);
        step_icon=findViewById(R.id.step_icon);
        location_name=findViewById(R.id.location_name);
        location_icon=findViewById(R.id.location_icon);
        layout_to_image=new Layout_to_Image(getApplicationContext(),relativeLayout);
        //now call the main working function ;) and hold the returned image in bitmap

//..................................................................................................


        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);


        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mGeoDataClientS = Places.getGeoDataClient(this, null);
        mGeoDataClientD = Places.getGeoDataClient(this, null);




    }

    @Override
    public void onBackPressed() {
        finish();
    }

       @Override
    public boolean onCreateOptionsMenu(Menu menu) {
           getMenuInflater().inflate(R.menu.mapactivity_menu, menu);
           this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.km10:
                item.setChecked(true);
                interval=10000;
                i=1;
                editor.putInt("10",10);
                editor.apply();

                return true;
            case R.id.km20:
                item.setChecked(true);
                interval=20000;
                i=2;
                editor.putInt("10",20);
                editor.apply();

                return true;
            case R.id.km30:
                item.setChecked(true);
                interval=30000;
                i=3;
                editor.putInt("10",30);
                editor.apply();

                return true;
            case R.id.km40:
                item.setChecked(true);
                interval=40000;
                i=4;
                editor.putInt("10",40);
                editor.apply();

                return true;
            case R.id.km50:
                item.setChecked(true);
                interval=50000;
                i=5;
                editor.putInt("10",50);
                editor.apply();

                return true;
            case R.id.action_retry:
                new WeatherApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Toast.makeText(this, "Fetching Weather...", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_clr:
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();

                recreate();
                return true;
                default:
                return super.onOptionsItemSelected(item);
        }
    }





            @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;



        drawRoute();
  

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {


             // link.setAdapter(adapter);
                int val=0;
                for(int k=0;k<polylines.size();k++){
                    polylines.get(k).remove();
                    if(!polylines.get(k).equals(polyline)){
                        polylineOptionsList.get(k).color(getResources().getColor(R.color.alternateRoute));
                        polylineOptionsList.get(k).width(15);
                        Polyline p=googleMap.addPolyline(polylineOptionsList.get(k));
                        p.setClickable(true);
                        polylines.set(k,p);
                    }else{
                        val=k;
                    }

                }
                selectedroute=val;
                routeadapter = new DragupListAdapter_route(context, directionapi.routes[selectedroute]);
                routeadapter.notifyDataSetChanged();

                polylineOptionsList.get(val).color(getResources().getColor(R.color.seletedRoute));
                polylineOptionsList.get(val).width(15);
                Polyline selectedPolyline=googleMap.addPolyline(polylineOptionsList.get(val));
                selectedPolyline.setClickable(true);
                polylines.set(val,selectedPolyline);

                distance.setText("("+directionapi.routes[val].legs[0].distance.humanReadable+")");
                duration.setText(directionapi.routes[val].legs[0].durationInTraffic!=null?directionapi.routes[val].legs[0].durationInTraffic.humanReadable:directionapi.routes[val].legs[0].duration.humanReadable);

                for(int k=0;k<markersSteps.size();k++){
                    markersSteps.get(k).remove();
                }
                for(int k=0;k<markersInterm.size();k++){
                    markersInterm.get(k).remove();
                }

            }
        });

                bld = new AlertDialog.Builder(cont);

    }



     void setCameraWithCoordinationBounds(DirectionsRoute route) {
        LatLng southwest = new LatLng(route.bounds.southwest.lat,route.bounds.southwest.lng);
        LatLng northeast =  new LatLng(route.bounds.northeast.lat,route.bounds.northeast.lng);
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width,height,padding));
    }

    void drawRoute(){
                    originMarker=googleMap.addMarker(new MarkerOptions().position(new LatLng(origin.lat,origin.lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinb)));
           // originMarker.setDraggable(true);
            originMarker.setTitle("source");

            dstnMarker=googleMap.addMarker(new MarkerOptions().position(new LatLng(destination.lat,destination.lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pina)));
         //   dstnMarker.setDraggable(true);
            dstnMarker.setTitle("destination");


                    MapActivity.polylines = new ArrayList<>();
            //add route(s) to the map.

            MapActivity.distance.setText("(" + directionapi.routes[selectedroute].legs[0].distance.humanReadable+ ")");
            MapActivity.duration.setText(directionapi.routes[selectedroute].legs[0].durationInTraffic!=null?directionapi.routes[selectedroute].legs[0].durationInTraffic.humanReadable:directionapi.routes[selectedroute].legs[0].duration.humanReadable);
            if (directionapi.routes[selectedroute].legs[0].duration.humanReadable != null) {
                slidingUpPanelLayout.setPanelHeight(context.getResources().getDimensionPixelSize(R.dimen.dragupsize));
            }


            polylineOptionsList = new ArrayList<>();
            System.out.println("route options : " + directionapi.routes.length);
            Polyline selectedPolyline = null;
            PolylineOptions SelectedpolyOptions = null;
//            if (directionapi.routes.length > 0) {
//               List<LatLng> lst = PolyUtil.decode(directionapi.routes[selectedroute].overviewPolyline.getEncodedPath());
//
//                PolylineOptions polyOptions = new PolylineOptions();
//                polyOptions.color(context.getResources().getColor(R.color.seletedRoute));
//                polyOptions.width(14);
//                polyOptions.addAll(lst);
//                polylineOptionsList.add(polyOptions);
//                MapActivity.polylines.add(selectedPolyline);
//            }

          //  if (directionapi.routes.length > 0) {
                for (int i = 0; i < directionapi.routes.length; i++) {
                    if (i != selectedroute) {
                        List<LatLng> lst = PolyUtil.decode(directionapi.routes[i].overviewPolyline.getEncodedPath());

                        PolylineOptions polyOptions = new PolylineOptions();
                        polyOptions.color(context.getResources().getColor(R.color.alternateRoute));
                        polyOptions.width(12);
                        polyOptions.addAll(lst);

                        Polyline polyline = MapActivity.googleMap.addPolyline(polyOptions);

                        MapActivity.polylines.add(polyline);
                        polyline.setClickable(true);

                        polylineOptionsList.add(polyOptions);
                    }else {
                        List<LatLng> lst = PolyUtil.decode(directionapi.routes[selectedroute].overviewPolyline.getEncodedPath());


                        SelectedpolyOptions = new PolylineOptions();
                        SelectedpolyOptions.color(context.getResources().getColor(R.color.seletedRoute));
                        SelectedpolyOptions.width(14);
                        SelectedpolyOptions.addAll(lst);
                        polylineOptionsList.add(SelectedpolyOptions);



                    }
                }

                if(SelectedpolyOptions !=null) {
                    selectedPolyline = googleMap.addPolyline(SelectedpolyOptions);
                    MapActivity.polylines.add(selectedPolyline);
                    selectedPolyline.setClickable(true);
                    }
 //           }

//            if (polylineOptionsList != null && polylineOptionsList.get(0) != null) {
//
//            }


            setCameraWithCoordinationBounds(directionapi.routes[selectedroute]);

            }

            public void showWeather(View view) {
                new WeatherApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }


            static void displayError(String title, String msg){

                bld.setMessage(msg);
                bld.setNeutralButton("OK", null);
                bld.setTitle(title);
                Log.d("TAG", "Showing alert dialog: " + msg);
                Dialog dialog=bld.create();
                //   dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            };



       class WeatherApi extends AsyncTask<Object,Object,Apidata> {

           String emsgHead="error";
           String emsg="";

           @Override
           protected void onPreExecute() {
               progressDialog.setTitle("Loading Weather Data...");
               progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               progressDialog.setIndeterminate(true);
              getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                       WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
               progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


               progressDialog.show();
           }

           @Override
           protected void onPostExecute(Apidata apidata) {
               progressDialog.dismiss();
               getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
               try {

                   if(apidata !=null) {
                       apiData = apidata;
                       int c = -1;
                       if (apidata.getItems() != null) {
                           for (final Item item : apidata.getItems()) {
                               c++;
                               final int finalC = c;
                               //Layout To Bitmap items............................................................................
                               TextView weather = MapActivity.step_weather;

                               TextView time = MapActivity.step_time;
                               String time_data[] = item.getArrtime().split(",", 2);
                               if (time_data.length >= 2)
                                   time.setText(time_data[0] + "\n" + time_data[1]);
                               else time.setText(item.getArrtime());
                               if (item.getLname() != null) {
                                   String lname[] = item.getLname().split(",");
                                   if (lname.length >= 2)
                                       MapActivity.location_name.setText(lname[0].length() < 20 ? lname[0] : lname[0].substring(0, 19) + "..,\n" + lname[1]);
                                   else {
                                       MapActivity.location_name.setText(lname[0]);
                                   }
                               }
                               Bitmap bitmap = MapActivity.layout_to_image.convert_layout();


                               ImageView image = MapActivity.step_icon;
                               switch (item.getWlist().getIcon()) {
                                   case "clear_day":
                                       image.setBackgroundResource(R.drawable.clear_day);
                                       weather.setText("Clear Day");
                                       break;
                                   case "cloudy":
                                       image.setBackgroundResource(R.drawable.cloudy);
                                       weather.setText("Cloudy");
                                       break;
                                   case "clear-night":
                                       image.setBackgroundResource(R.drawable.clear_night);
                                       weather.setText("Clear Night");
                                       break;
                                   case "fog":
                                       image.setBackgroundResource(R.drawable.fog);
                                       weather.setText("Fog");
                                       break;
                                   case "hail":
                                       image.setBackgroundResource(R.drawable.hail);
                                       weather.setText("Hail");
                                       break;
                                   case "partly-cloudy-day":
                                       image.setBackgroundResource(R.drawable.partly_cloudy_day);
                                       weather.setText("Partly Cloudy Day");
                                       break;
                                   case "partly-cloudy-night":
                                       image.setBackgroundResource(R.drawable.partly_cloudy_night);
                                       weather.setText("Partly Cloudy Night");
                                       break;
                                   case "rain":
                                       image.setBackgroundResource(R.drawable.rain);
                                       weather.setText("Rain");
                                       break;
                                   case "sleet":
                                       image.setBackgroundResource(R.drawable.sleet);
                                       weather.setText("Sleet");
                                       break;
                                   case "snow":
                                       image.setBackgroundResource(R.drawable.snow);
                                       weather.setText("Snow");
                                       break;
                                   case "thunderstorm":
                                       image.setBackgroundResource(R.drawable.thunderstorm);
                                       weather.setText("Thunderstorm");
                                       break;
                                   case "tornado":
                                       image.setBackgroundResource(R.drawable.tornado);
                                       weather.setText("Tornado");
                                       break;
                                   case "wind":
                                       image.setBackgroundResource(R.drawable.wind);
                                       weather.setText("Wind");
                                       break;
                                   default:
                                       image.setBackgroundResource(R.drawable.clear_day);
                                       weather.setText("Clear Day");
                               }

//..................................................................................................
                               BitmapDescriptor icon = new bitmapfromstring(item.getWlist().getIcon()).getIcon();
                               Marker marker = googleMap.addMarker(new MarkerOptions()
                                       .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                       .position(new LatLng(item.getPoint().lat, item.getPoint().lng)));
                               marker.setTag("I" + finalC);
                               markersInterm.add(marker);

                           }
                       } else {
                           System.out.println("api data is null or api.getlist is null");
                       }


                       if (apidata.getSteps() != null) {

                           link.setAdapter(new DragupListAdapter_weather(context, apidata.getSteps()));
                           for (final MStep mStep : apidata.getSteps()) {
                               c++;

                               final int finalC = c;
//Layout to Bitmap steps............................................................................
                               TextView weather = MapActivity.step_weather;

                               TextView time = MapActivity.step_time;
                               String time_data[] = mStep.getArrtime().split(",", 2);
                               if (time_data.length >= 2)
                                   time.setText(time_data[0] + "\n" + time_data[1]);
                               else time.setText(mStep.getArrtime());
                               MapActivity.location_name.setText("");
                               location_icon.setVisibility(View.GONE);
                               Bitmap bitmap = MapActivity.layout_to_image.convert_layout();


                               ImageView image = MapActivity.step_icon;
                               switch (mStep.getWlist().getIcon()) {
                                   case "clear_day":
                                       image.setBackgroundResource(R.drawable.clear_day);
                                       weather.setText("Clear Day");
                                       break;
                                   case "cloudy":
                                       image.setBackgroundResource(R.drawable.cloudy);
                                       weather.setText("Cloudy");
                                       break;
                                   case "clear-night":
                                       image.setBackgroundResource(R.drawable.clear_night);
                                       weather.setText("Clear Night");
                                       break;
                                   case "fog":
                                       image.setBackgroundResource(R.drawable.fog);
                                       weather.setText("Fog");
                                       break;
                                   case "hail":
                                       image.setBackgroundResource(R.drawable.hail);
                                       weather.setText("Hail");
                                       break;
                                   case "partly-cloudy-day":
                                       image.setBackgroundResource(R.drawable.partly_cloudy_day);
                                       weather.setText("Partly Cloudy Day");
                                       break;
                                   case "partly-cloudy-night":
                                       image.setBackgroundResource(R.drawable.partly_cloudy_night);
                                       weather.setText("Partly Cloudy Night");
                                       break;
                                   case "rain":
                                       image.setBackgroundResource(R.drawable.rain);
                                       weather.setText("Rain");
                                       break;
                                   case "sleet":
                                       image.setBackgroundResource(R.drawable.sleet);
                                       weather.setText("Sleet");
                                       break;
                                   case "snow":
                                       image.setBackgroundResource(R.drawable.snow);
                                       weather.setText("Snow");
                                       break;
                                   case "thunderstorm":
                                       image.setBackgroundResource(R.drawable.thunderstorm);
                                       weather.setText("Thunderstorm");
                                       break;
                                   case "tornado":
                                       image.setBackgroundResource(R.drawable.tornado);
                                       weather.setText("Tornado");
                                       break;
                                   case "wind":
                                       image.setBackgroundResource(R.drawable.wind);
                                       weather.setText("Wind");
                                       break;
                                   default:
                                       image.setBackgroundResource(R.drawable.clear_day);
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

                       } else {

                       }
                   }else {
                       displayError(emsgHead,emsg);
                   }

               }catch (Exception e){
                   displayError("Weather Display Error ","Error While Parsing Weather");
               }

           }

           @Override
           protected Apidata doInBackground(Object[] objects) {
               try {
                   ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                   NetworkInfo netInfo = mgr.getActiveNetworkInfo();

                   if (netInfo != null && netInfo.isConnected()) {

                       Input input=new Input();
                       input.setOrigin(origin);
                       input.setDestination(destination);
                       input.setRoute(selectedroute);
                       input.setInterval(interval);
                       input.setTimeZone(timezone);
                       input.setTime(jstart_date_millis+jstart_time_millis);
                       input.setTravelmode(travelmode);
                       input.setDistanceUnit(DistanceUnit);

                       String restrictions="0";
                       if(HIGHWAYS)restrictions+="1";
                       if(TOLLS)restrictions+="2";
                       if(FERRIES)restrictions+="3";
                       input.setRestrictions(restrictions);


                       OkHttpClient okHttpClient = new OkHttpClient.Builder()
                               .readTimeout(120, TimeUnit.SECONDS)
                               .writeTimeout(120, TimeUnit.SECONDS)
                               .connectTimeout(120, TimeUnit.SECONDS)
                               //.addInterceptor(loggingInterceptor)
                               //.addNetworkInterceptor(networkInterceptor)
                               .build();
                       Retrofit retrofit = new Retrofit.Builder()
                               .baseUrl("https://4svktzsdok.execute-api.ap-south-1.amazonaws.com/")
                               .client(okHttpClient)
                               .addConverterFactory(GsonConverterFactory.create())
                               .build();

                       ApiInterface apiService = retrofit.create(ApiInterface.class);
                       Call<Apidata> call = apiService.inputCall(input);

                       return call.execute().body();

                   }else{
                       this.emsgHead="No Internet Connection";
                       this.emsg="Please TurnOn Your Mobile Data";
                   }

               } catch (Exception e) {
                   this.emsg=e.getMessage();
                   this.emsgHead="Error while Dowloading Weather";
               }
               return null;
           }


       }

       }
