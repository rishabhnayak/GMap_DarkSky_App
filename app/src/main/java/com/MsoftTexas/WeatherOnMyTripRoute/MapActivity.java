package com.MsoftTexas.WeatherOnMyTripRoute;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.ArrrayAdapter;
import com.MsoftTexas.WeatherOnMyTripRoute.DirectionApiModel.DirectionApi;
import com.MsoftTexas.WeatherOnMyTripRoute.DirectionApiModel.Route;
import com.MsoftTexas.WeatherOnMyTripRoute.Models.Apidata;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.maps.model.DirectionsRoute;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;

import static io.trialy.library.Constants.STATUS_TRIAL_JUST_ENDED;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_NOT_YET_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_OVER;
import static io.trialy.library.Constants.STATUS_TRIAL_RUNNING;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback
        ,View.OnClickListener
    , IabBroadcastReceiver.IabBroadcastListener
        {

    static Context context;
    static RelativeLayout custom_dialog;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    static  LottieAnimationView loading;
    static  List<PolylineOptions> polylineOptionsList;
    static List<Polyline> polylines=new ArrayList<>();
    static List<Marker> markersInterm = new ArrayList<>();
    static List<Marker> markersSteps = new ArrayList<>();
    static TextView loading_text;
    static  int selectedroute=0;
    static String timezone;
    static int mYear,mMonth,mDay, mHour, mMinute;
    static long interval=50000;
    static Boolean weatherloaded=false, routeloaded=false;
 //   static TextView time;
    static CardView date_holder;
    static TextView departAt;
    static ImageView go;
    static TextView src,dstn;
    static  SlidingUpPanelLayout slidingUpPanelLayout;
    static long jstart_date_millis, jstart_time_millis;
    static private Marker originMarker, dstnMarker;
    private List<Marker> markers = new ArrayList<>();
    static Apidata apiData=null;
    static GoogleMap googleMap;
    private String serverKey = "AIzaSyDi3B9R9hVpC9YTmOCCz_pCR1BKW3tIRGY";
    static DirectionApi directionapi;
    static TextView distance, duration;
    ImageView RequestDirection;
    static LatLng origin = null;
    static LatLng destination = null;
    protected GeoDataClient mGeoDataClientS, mGeoDataClientD;
    SharedPreferences sd;
    String[] month={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private FirebaseAnalytics mFirebaseAnalytics;
    static RecyclerView link;

    static String TRIALY_APP_KEY = "CNXFXUSWNXNREPZN6FW"; //TODO: Replace with your app key, which can be found on your Trialy developer dashboard
    static String TRIALY_SKU = "t2_test"; //TODO: Replace with a trial SKU, which can be found on your Trialy developer dashboard. Each app can have multiple trials
    static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnh6LDOmwwPSQ8KesBlRQ/LrN/75xUFQhVmvfJG6uUlmgxU4iWiMzwr1iydveIz3cNT2C1IdnBpohHuDhn9RlOn5uaR3Cw0BDGrnRzwHZRPdoJ3/tAWIS+cLD/5LU7sriMOi6spMaPTYjgrT/Lck36goPwY88FK+e2G09cFrd54WQBPwHO+COKlKOFQ7Yt9yiCLlwivhdSDbacuVGg696JjAeTBvnw0eqks7Q/FHg2U0TlhBf/RU2+tvCnR2L0hk1kgkkdZFua8aDrZ1xQkEkBzlrrHrGnmqCyVoPHwMcxoOKM61BX511NMRuBJv9Eg19n4QITqT/fsR7vzmnljjxLQIDAQAB" ;

     IabHelper mHelper;

            // Provides purchase notification while this app is running
     IabBroadcastReceiver mBroadcastReceiver;
     Trialy mTrialy;

      String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
      String SKU_INFINITE_GAS_QUATERLY = "quaterly";
      String SKU_INFINITE_GAS_HALFYEARLY = "halfyearly";
      String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";

      boolean mSubscribedToInfiniteGas = true;
      boolean mAutoRenewEnabled = false;
     static boolean havetrial=true;
      String mInfiniteGasSku = "";
     static final String TAG = "Map Activity";
            static Layout_to_Image layout_to_image;
            static LinearLayout relativeLayout;
            static TextView step_time,step_weather;
            static ImageView step_icon;

            RadioGroup radioGroup;
            String selectedText="one",selectedUnit="automatic";

     static int travelmode=0;
     static String restrictions="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mTrialy = new Trialy(this, TRIALY_APP_KEY);
        mTrialy.checkTrial(TRIALY_SKU, mTrialyCallback);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MapActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        sd = this.getSharedPreferences("com.MsoftTexas.WeatherOnMyTripRoute", Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=getApplicationContext();
        src =findViewById(R.id.autocomplete_source);
        dstn=findViewById(R.id.autocomplete_destination);
        go=findViewById(R.id.request_direction);
        RequestDirection=findViewById(R.id.request_direction);
//loading.................lottie
        custom_dialog=findViewById(R.id.custom_dialog);
        loading=findViewById(R.id.loading);
        loading_text=findViewById(R.id.loading_text);


        //setting title null
        getSupportActionBar().setTitle("");

//sliding up layout
        slidingUpPanelLayout=findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelHeight(0);


        link = (RecyclerView) findViewById(R.id.dragup_list_recycler);
        link.setLayoutManager(new LinearLayoutManager(this));
//Markers with text.................................................................................


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

  //      time = findViewById(R.id.time);
        date_holder = findViewById(R.id.card_date);
        departAt=findViewById(R.id.date1);


        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mGeoDataClientS = Places.getGeoDataClient(this, null);
        mGeoDataClientD = Places.getGeoDataClient(this, null);


        // Retrieve the AutoCompleteTextView that will display Place suggestions.


        src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapActivity.this,SearchPlace.class);
                  intent.putExtra("SrcOrDstn","Src");
                    startActivity(intent);
            }
        });

        dstn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapActivity.this,SearchPlace.class);
                intent.putExtra("SrcOrDstn","Dstn");
                startActivity(intent);
            }
        });

        RequestDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.option_list).setVisibility(View.GONE);
                requestDirection();
            }
        });


        final Calendar c = Calendar.getInstance();
        timezone=c.getTimeZone().getID();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        jstart_date_millis=c.getTimeInMillis()-((mHour*60+mMinute)*60*1000);
        jstart_time_millis=(mHour*60+mMinute)*60*1000;


        String sHour = mHour < 10 ? "0" + mHour : "" + mHour;
        String sMinute = mMinute < 10 ? "0" + mMinute : "" + mMinute;
        String curr_time = sHour + ":" + sMinute;
 //       time.setText(curr_time);
        departAt.setText(curr_time+","+mDay+" "+month[mMonth]+" "+String.valueOf(mYear).substring(2));
        
//        Button clearButton = findViewById(R.id.button_clear);
//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recreate();
//            }
//        });
        date_holder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePicker();

            }
        });
//        time.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                tiemPicker();
//
//            }
//        });

        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(MapActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

//Custom radio button...............................................................................
        radioGroup=findViewById(R.id.test_radio);
        radioGroup.check(R.id._1);
        Toast.makeText(context, selectedText, Toast.LENGTH_SHORT).show();
        ((ImageView)(findViewById(R.id.a))).setImageResource(R.drawable.car_on);
        ((ImageView)(findViewById(R.id.b))).setImageResource(R.drawable.train_off);
        ((ImageView)(findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId =    radioGroup.getCheckedRadioButtonId();
                selectedText = ((RadioButton)(findViewById(selectedId))).getText().toString();
                System.out.println(selectedText);
                switch (selectedText){
                    case "one":
                        ((ImageView)(findViewById(R.id.a))).setImageResource(R.drawable.car_on);
                        ((ImageView)(findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView)(findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "two":
                        ((ImageView)(findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView)(findViewById(R.id.b))).setImageResource(R.drawable.train_on);
                        ((ImageView)(findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "three":
                        ((ImageView)(findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView)(findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView)(findViewById(R.id.c))).setImageResource(R.drawable.walk_on);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "four":
                        ((ImageView)(findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView)(findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView)(findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_on);
                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "five":
                        ((ImageView)(findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView)(findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView)(findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_on);
                        break;
                    default:

                }

            }
        });
        findViewById(R.id.a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id._1);
            }
        });
        findViewById(R.id.b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id._2);
            }
        });
        findViewById(R.id.c).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id._3);
            }
        });
        findViewById(R.id.d).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id._4);
            }
        });
        findViewById(R.id.e).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id._5);
            }
        });
        findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              int a =  findViewById(R.id.option_list).getVisibility();
              if (a==0){
                  findViewById(R.id.option_list).setVisibility(View.GONE);
              }else {
                  findViewById(R.id.option_list).setVisibility(View.VISIBLE);
              }

            }
        });

        ((RadioGroup)findViewById(R.id.distance_unit)).check(R.id.automatic);
        ((RadioGroup)findViewById(R.id.distance_unit)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId =    radioGroup.getCheckedRadioButtonId();
                selectedUnit = ((RadioButton)(findViewById(selectedId))).getText().toString();
                System.out.println(selectedUnit);
            }
        });

        boolean highway = ((CheckBox)findViewById(R.id.highway)).isChecked();
        boolean tolls = ((CheckBox)findViewById(R.id.tolls)).isChecked();
        boolean ferries = ((CheckBox)findViewById(R.id.ferries)).isChecked();

//..................................................................................................
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
        super.onBackPressed();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
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
                requestDirection();
                Toast.makeText(this, "Retrying...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Subscription:
                Intent intent=new Intent(MapActivity.this, Subscription.class);
                startActivity(intent);

 //               Toast.makeText(this, "Retrying...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_clr:
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
                origin=null;
                destination=null;
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

       


  

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {


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

                polylineOptionsList.get(val).color(getResources().getColor(R.color.seletedRoute));
                polylineOptionsList.get(val).width(15);
                Polyline selectedPolyline=googleMap.addPolyline(polylineOptionsList.get(val));
                selectedPolyline.setClickable(true);
                polylines.set(val,selectedPolyline);

                distance.setText("("+directionapi.getRoutes().get(val).getLegs().get(0).getDistance().getText()+")");
                duration.setText(directionapi.getRoutes().get(val).getLegs().get(0).getDuration().getText());

                for(int k=0;k<markersSteps.size();k++){
                    markersSteps.get(k).remove();
                }
                for(int k=0;k<markersInterm.size();k++){
                    markersInterm.get(k).remove();
                }
            //    custom_dialog.setVisibility(View.VISIBLE);
            //    loading.setVisibility(View.VISIBLE);
            //    loading_text.setVisibility(View.VISIBLE);
            //    loading_text.setText("loading weather...");
           //     new WeatherApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                if (originMarker == null) {
                    originMarker = googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinb)));
                    originMarker.setDraggable(true);
                    originMarker.setTitle("Source");
                    // originMarker.setTag(0);
                    origin=point;
                } else if (dstnMarker == null) {
                    dstnMarker=googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.pina)));
                    //    dstnMarker.setTag(1);
                    dstnMarker.setDraggable(true);
                    dstnMarker.setTitle("Destination");
                    destination=point;
                    requestDirection();
                }

            }});

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                System.out.println("marker tag :"+marker.getTag());

                try{

                    if(marker.getTag().toString().startsWith("I")) {

                        Item item = apiData.getItems().get(Integer.parseInt(marker.getTag().toString().replace("I","")));


                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapActivity.this);

//                        builderSingle.setTitle(
//                                item.getLname().substring(0, 20) + "...\n Arr :" + item.getArrtime() + "   Dist :" + item.getDistance()
//                        );

                        builderSingle.setIcon(R.drawable.ic_directions_black_24dp);
                        try {
                            builderSingle.setTitle(new Geocoder(getApplicationContext(), Locale.ENGLISH).getFromLocation(item.getPoint().getLatitude(), item.getPoint().getLatitude(), 1).get(0).getAddressLine(0));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        builderSingle.setMessage("Time :"+item.getArrtime()+"  "+"Traveled :"+String.format("%.2f", (Integer.valueOf(item.getDistance().split(" ")[0]))/(float)1000*(0.621371))+" miles");
                        final ArrrayAdapter Adapter = new ArrrayAdapter(MapActivity.this, item);

                        final ListView modeList = new ListView(MapActivity.this);
                        modeList.setAdapter(Adapter);


                        builderSingle.setView(modeList);
                        builderSingle.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builderSingle.show();
                    }else
                        if(marker.getTag().toString().startsWith("S")){
                        MStep step = apiData.getSteps().get(Integer.parseInt(marker.getTag().toString().replace("S","")));
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapActivity.this);

//                        builderSingle.setTitle(
//                                step.getStep().getManeuver() + "...\n Arr :" + step.getArrtime() + "   Dist :" + step.getAft_distance()
//                        );

                        builderSingle.setIcon(R.drawable.ic_directions_black_24dp);
                        builderSingle.setTitle(new Geocoder(getApplicationContext(), Locale.ENGLISH).getFromLocation(step.getStep().getStart_location().getLat(), step.getStep().getStart_location().getLng(), 1).get(0).getAddressLine(0));
                        builderSingle.setMessage("Time :"+step.getArrtime()+"  "+"Traveled :"+String.format("%.2f",step.getAft_distance()/(float)1000*(0.621371))+" miles");
                        final ArrrayAdapter Adapter = new ArrrayAdapter(MapActivity.this, step);

                        final ListView modeList = new ListView(MapActivity.this);
                        modeList.setAdapter(Adapter);


                        builderSingle.setView(modeList);
                        builderSingle.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builderSingle.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                return false;
            }
        });




        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker marker) {

            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng newLocation = marker.getPosition();

                if(marker!=null){
                    System.out.println("tag :"+marker.getTag());
                    System.out.println("title :"+marker.getTitle());
                }else{
                    System.out.println(" marker is null babes ");
                }

                if(marker.getTitle().equals(originMarker.getTitle())) {
                    origin = newLocation;
                    //     googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15.0f));

                }else if(marker.getTitle().equals(dstnMarker.getTitle())){
                    destination = newLocation;
                    //     googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15.0f));
                }
                requestDirection();
            }
            @Override
            public void onMarkerDragStart(Marker marker) {}

        });
    }




    public void requestDirection() {
        apiData=null;
        weatherloaded=false;
        routeloaded=false;
        markersInterm.clear();
        markersSteps.clear();

        if(origin!=null && destination!=null) {
            googleMap.clear();
          //  custom_dialog.setVisibility(View.VISIBLE);
          //  loading.setVisibility(View.VISIBLE);
          //  loading_text.setVisibility(View.VISIBLE);
           // slidingUpPanelLayout.setAlpha(0.5f);
          //  loading.setSpeed(1f);
          //  loading_text.setText("Loading Route");

            originMarker=googleMap.addMarker(new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinb)));
            originMarker.setDraggable(true);
            originMarker.setTitle("source");

            dstnMarker=googleMap.addMarker(new MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.drawable.pina)));
            dstnMarker.setDraggable(true);
            dstnMarker.setTitle("destination");


             new RouteApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
         //    new WeatherApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            Toast.makeText(getApplicationContext(),"origin or destination null", Toast.LENGTH_LONG).show();
        }
    }

    static void setCameraWithCoordinationBounds(DirectionsRoute route) {
        LatLng southwest = new LatLng(route.bounds.southwest.lat,route.bounds.southwest.lng);
        LatLng northeast =  new LatLng(route.bounds.northeast.lat,route.bounds.northeast.lng);
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
    }







    private void datePicker(){

        // Get Current Date


        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        departAt.setText(dayOfMonth + " " + month[monthOfYear] + " " + String.valueOf(year).substring(2));
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.HOUR_OF_DAY,0);
                        cal.set(Calendar.MINUTE,0);

                        jstart_date_millis=cal.getTimeInMillis();

                        timePicker();

                        //*************Call Time Picker Here ********************

                    }
                }, mYear, mMonth, mDay);



     //   datePickerDialog.getDatePicker().setMinDate(jstart_date_millis);
     //   datePickerDialog.getDatePicker().setMaxDate(jstart_date_millis+5*24*60*60*1000);
        datePickerDialog.show();
    }

    private void timePicker(){
        // Get Current Time


        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        String sHour = mHour < 10 ? "0" + mHour : "" + mHour;
                        String sMinute = mMinute < 10 ? "0" + mMinute : "" + mMinute;
                        String set_time = sHour + ":" + sMinute;
                        departAt.setText(set_time+","+departAt.getText());

                        jstart_time_millis=(mHour*60+mMinute)*60*1000;



                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

     IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    Log.d("TAG", "Query inventory finished.");

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // Is it a failure?
                    if (result.isFailure()) {
                        complain("Failed to query inventory: " + result);
                        return;
                    }

                    Log.d("TAG", "Query inventory was successful.");

                    /*
                     * Check for items we own. Notice that for each purchase, we check
                     * the developer payload to see if it's correct! See
                     * verifyDeveloperPayload().
                     */

                    // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

                    // First find out which subscription is auto renewing
                    Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
                    Purchase gasQuaterly = inventory.getPurchase(SKU_INFINITE_GAS_QUATERLY);
                    Purchase gasHalfYearly = inventory.getPurchase(SKU_INFINITE_GAS_HALFYEARLY);
                    Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
                    if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                        mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                        mAutoRenewEnabled = true;
                    }else if (gasQuaterly != null && gasQuaterly.isAutoRenewing()) {
                        mInfiniteGasSku = SKU_INFINITE_GAS_QUATERLY;
                        mAutoRenewEnabled = true;
                    }else if (gasHalfYearly!= null && gasHalfYearly.isAutoRenewing()) {
                        mInfiniteGasSku = SKU_INFINITE_GAS_HALFYEARLY;
                        mAutoRenewEnabled = true;
                    }
                    else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                        mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                        mAutoRenewEnabled = true;
                    } else {
                        mInfiniteGasSku = "";
                        mAutoRenewEnabled = false;
                    }

                    // The user is subscribed if either subscription exists, even if neither is auto
                    // renewing
                    mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                            || (gasQuaterly != null && verifyDeveloperPayload(gasQuaterly))
                            || (gasHalfYearly != null && verifyDeveloperPayload(gasHalfYearly))
                            || (gasYearly != null && verifyDeveloperPayload(gasYearly));
                    Log.d("TAG", "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                            + " infinite gas subscription.");
                    haveTrialorSubs();

                }
            };

            @Override
            public void receivedBroadcast() {
                // Received a broadcast notification that the inventory of items has changed
                Log.d("TAG", "Received broadcast notification. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }

            private TrialyCallback mTrialyCallback = new TrialyCallback() {
                @Override
                public void onResult(int status, long timeRemaining, String sku) {
                    switch (status){
                        case STATUS_TRIAL_JUST_STARTED:

                          break;
                        case STATUS_TRIAL_RUNNING:

                          break;
                        case STATUS_TRIAL_JUST_ENDED:
                            havetrial=false;
                            haveTrialorSubs();
                          break;
                        case STATUS_TRIAL_NOT_YET_STARTED:
                            havetrial=false;
                            haveTrialorSubs();
                            break;
                        case STATUS_TRIAL_OVER:
                            havetrial=false;
                            haveTrialorSubs();
                            break;
                    }
                    Log.i("TRIALY", "Returned status: " + Trialy.getStatusMessage(status));
                }

            };

            void complain(String message) {
                Log.e("TAG", "**** TrivialDrive Error: " + message);
                alert("Error: " + message);
            }

            void alert(String message) {
                android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
                bld.setMessage(message);
                bld.setNeutralButton("OK", null);
                Log.d("TAG", "Showing alert dialog: " + message);
                bld.create().show();


            }

            boolean verifyDeveloperPayload(Purchase p) {
                String payload = p.getDeveloperPayload();
                return true;
            }

            void haveTrialorSubs(){
                if(!havetrial && !mSubscribedToInfiniteGas) {
                    startActivity(new Intent(MapActivity.this, Subscription.class));
                    finish();
                }
            }

}
