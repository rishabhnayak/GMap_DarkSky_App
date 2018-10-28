package com.MsoftTexas.WeatherOnMyTripRoute;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.DragupListAdapter_weather;
import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.DragupListAdapter_route;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Apidata;
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

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;

import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.destination;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.directionapi;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.origin;
import static com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity.selectedroute;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_ENDED;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_NOT_YET_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_OVER;
import static io.trialy.library.Constants.STATUS_TRIAL_RUNNING;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback
        ,View.OnClickListener

        {

    static Context context;


    static LottieAnimationView loading;
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
            static TextView step_time,step_weather;
            static ImageView step_icon;



     static int travelmode=0;
     static String restrictions="0";
//            boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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

//sliding up layout
        slidingUpPanelLayout=findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelHeight(0);


        link = (RecyclerView) findViewById(R.id.dragup_list_recycler);
        link.setLayoutManager(new LinearLayoutManager(this));
//Markers with text.................................................................................

        routeadapter = new DragupListAdapter_route(context, directionapi.routes[selectedroute]);
        link.setAdapter(routeadapter);

        //provide layout with its id in Xml
        relativeLayout=findViewById(R.id.show);
        step_time=findViewById(R.id.step_time);
        step_weather=findViewById(R.id.step_weather);
        step_icon=findViewById(R.id.step_icon);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.km10:
                item.setChecked(true);
                MapActivity.interval=10000;
                return true;
            case R.id.km20:
                item.setChecked(true);
                MapActivity.interval=20000;
                return true;
            case R.id.km30:
                 item.setChecked(true);
                MapActivity.interval=30000;
                Toast.makeText(this, "30km", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.km40:
               item.setChecked(true);
               MapActivity.interval=40000;
                return true;
            case R.id.km50:
                item.setChecked(true);
                MapActivity.interval=50000;
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
    public void onClick(View view) {

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
            if (directionapi.routes.length > 0) {
               List<LatLng> lst = PolyUtil.decode(directionapi.routes[0].overviewPolyline.getEncodedPath());

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(context.getResources().getColor(R.color.seletedRoute));
                polyOptions.width(14);
                polyOptions.addAll(lst);
                polylineOptionsList.add(polyOptions);
                MapActivity.polylines.add(selectedPolyline);
            }

            if (directionapi.routes.length > 1) {
                for (int i = 1; i < directionapi.routes.length; i++) {
                    List<LatLng> lst = PolyUtil.decode(directionapi.routes[i].overviewPolyline.getEncodedPath());
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


            setCameraWithCoordinationBounds(directionapi.routes[0]);



    }

            public void showWeather(View view) {
                new WeatherApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
       }
