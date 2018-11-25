package com.MsoftTexas.WeatherOnMyTripRoute;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.MsoftTexas.WeatherOnMyTripRoute.Adapters.AdapterList;
import com.MsoftTexas.WeatherOnMyTripRoute.util.IabBroadcastReceiver;
import com.crashlytics.android.Crashlytics;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import io.fabric.sdk.android.Fabric;


public class TravelWithActivity extends BaseActivity
       {

    final TravelWithActivity cont=TravelWithActivity.this;
    int tempYear,tempMonth,tempDay;
    public static int  DistanceUnit = 0;
    static DirectionsResult directionapi;
    static public int selectedroute=0;
    static int mYear, mMonth, mDay, mHour, mMinute;
    static int travelmode=0;
    static boolean HIGHWAYS=false;
    static boolean TOLLS=false;
    static boolean FERRIES=false;
    static long jstart_date_millis, jstart_time_millis;
    static String timezone;
    static LatLng origin = null;
    static LatLng destination = null;

    static String googleAPIkey="AIzaSyCv_imK5ydtkdWnGJP1Dbt-DT07UdvyDeo";
    static String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    static RecyclerView recyclerView;
    static TextView departAt;
    static CardView date_holder;
    
    static TextView tv_source, tv_dstn;
    static Button go;
   
    static Context context;

    static ProgressDialog progress;

    static android.app.AlertDialog.Builder bld;

    static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_travel_with_activity);

        recyclerView = findViewById(R.id.recycler);
        Fabric.with(this, new Crashlytics());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        progress=new ProgressDialog(this);


        tv_source = findViewById(R.id.source);
        tv_dstn = findViewById(R.id.destination);
 //       requestDirection = findViewById(R.id.submit);
        go=findViewById(R.id.submit);
        //Custom radio button...............................................................................
//        radioGroup = findViewById(R.id.test_radio);
//        radioGroup.check(R.id._1);
        context=getApplicationContext();
        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_on);

        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
        ((ImageView) (findViewById(R.id.d))).setImageResource(R.drawable.bike_off);


        findViewById(R.id.swap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(origin!=null && destination!=null) {
                    resetresult();
                    LatLng temp = origin;
                    origin = destination;
                    destination = temp;

                    new TimezoneOfOrigin(Calendar.getInstance().getTimeInMillis(), origin).execute();
                    String srctemp = tv_source.getText().toString();
                    tv_source.setText(tv_dstn.getText());
                    tv_dstn.setText(srctemp);

                }else{
                    displayError("Start/Destination Address no filled","Please fill Start and Destination to find routes");
                    //Toast.makeText(context,"start or end Address is null",Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  radioGroup.check(R.id._1);
                ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_on);
                ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                ((ImageView) (findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
                ((CheckBox) findViewById(R.id.highway)).setVisibility(View.VISIBLE);
                ((CheckBox) findViewById(R.id.tolls)).setVisibility(View.VISIBLE);
                travelmode=0;
                resetresult();
                System.out.println("travelmode :"+travelmode);
            }
        });

        findViewById(R.id.c).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   radioGroup.check(R.id._3);
                ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_on);
                ((ImageView) (findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
                ((CheckBox) findViewById(R.id.highway)).setVisibility(View.GONE);
                ((CheckBox) findViewById(R.id.tolls)).setVisibility(View.GONE);
                travelmode=2;
                resetresult();
                System.out.println("travelmode :"+travelmode);
            }
        });
        findViewById(R.id.d).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   radioGroup.check(R.id._4);

                ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                ((ImageView) (findViewById(R.id.d))).setImageResource(R.drawable.bike_on);
                ((CheckBox) findViewById(R.id.highway)).setVisibility(View.GONE);
                ((CheckBox) findViewById(R.id.tolls)).setVisibility(View.GONE);
                travelmode=1;
                resetresult();
                System.out.println("travelmode :"+travelmode);
            }
        });

        findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = findViewById(R.id.option_list).getVisibility();
                if (a == 0) {
                    findViewById(R.id.option_list).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.option_list).setVisibility(View.VISIBLE);
                }

            }
        });

        ((RadioGroup) findViewById(R.id.distance_unit)).check(R.id.automatic);
        ((RadioGroup) findViewById(R.id.distance_unit)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                DistanceUnit = Integer.parseInt(findViewById(selectedId).getTag().toString());
                System.out.println(DistanceUnit);
                resetresult();
            }
        });



        ((CheckBox) findViewById(R.id.highway))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        HIGHWAYS=isChecked;
                        resetresult();
                    }
                });
        ((CheckBox) findViewById(R.id.tolls))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        TOLLS=isChecked;
                        resetresult();
                    }
                });

        ((CheckBox) findViewById(R.id.ferries))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        FERRIES=isChecked;
                        resetresult();
                    }
                });



        date_holder = findViewById(R.id.card_date);
        departAt = findViewById(R.id.date1);
        date_holder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePicker();

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

        (findViewById(R.id.source_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetresult();
                Intent intent=new Intent(TravelWithActivity.this,SearchPlace.class);
                intent.putExtra("SrcOrDstn","Src");
                startActivity(intent);
            }
        });

        (findViewById(R.id.dstn_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetresult();
                Intent intent=new Intent(TravelWithActivity.this,SearchPlace.class);
                intent.putExtra("SrcOrDstn","Dstn");
                startActivity(intent);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   findViewById(R.id.option_list).setVisibility(View.GONE);

                requestDirection();
            }
        });




        bld = new AlertDialog.Builder(cont);

    }

    public void requestDirection() {

        if(origin!=null && destination!=null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            new RouteApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            displayError("Start/Destination Address no filled","Please fill Start and Destination to find routes");
            //Toast.makeText(getApplicationContext(),"origin or destination null", Toast.LENGTH_LONG).show();
        }
    }

    //..................................................................................................
    private void datePicker() {

        // Get Current Date


        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        departAt.setText("00:00,"+dayOfMonth + " " + month[monthOfYear] + " " + String.valueOf(year).substring(2));
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeZone(TimeZone.getTimeZone(timezone));
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        tempDay=dayOfMonth;
                        tempMonth=monthOfYear;
                        tempYear=year;

                        jstart_date_millis = cal.getTimeInMillis();

                        timePicker();

                        //*************Call Time Picker Here ********************

                        resetresult();
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void timePicker() {
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

                        departAt.setText(set_time + "," +tempDay + " " + month[tempMonth] + " " + String.valueOf(tempYear).substring(2));

                        jstart_time_millis = (mHour * 60 + mMinute) * 60 * 1000;

                    }
                }, mHour, mMinute, true);

        timePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.travel_with_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_retry:
                directionapi=null;
                recyclerView.setAdapter(null);
                requestDirection();
                Toast.makeText(this, "Retrying...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Subscription:
                Intent intent=new Intent(getApplicationContext(), Subscription.class);
                startActivity(intent);

                //               Toast.makeText(this, "Retrying...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_clr:
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();

                   directionapi=null;
                   selectedroute=0;
                   DistanceUnit = 0;
                   travelmode=0;
                   HIGHWAYS=false;TOLLS=false;FERRIES=false;
                   origin = null;
                   destination = null;
                  
                  finish();
                  startActivity(getIntent());

                  return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    static void resetresult(){
        directionapi=null;
        recyclerView.setAdapter(null);
    };

     static void displayError(String title, String msg){

         bld.setMessage(msg);
         bld.setNeutralButton("OK", null);
         bld.setTitle(title);
         Log.d("TAG", "Showing alert dialog: " + msg);
         Dialog dialog=bld.create();
      //   dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
         dialog.show();
     };
         class RouteApi extends AsyncTask<Object,Object,DirectionsResult> {
               String emsgHead="error";
               String emsg="";

               @Override
               protected void onPreExecute() {


                   progress.setTitle("Loading Routes...");
                   progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                   progress.setIndeterminate(true);
                   progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                   getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                           WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                   progress.show();
               }

               @Override
               protected void onPostExecute(DirectionsResult apidata) {


                   progress.dismiss();
                   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                   if(apidata !=null) {

                       if (apidata.routes != null && apidata.routes.length > 0) {

                           TravelWithActivity.directionapi = apidata;
                           LinearLayoutManager manager = new LinearLayoutManager(context);
                           recyclerView.setLayoutManager(manager);
                           recyclerView.setHasFixedSize(true);

                           AdapterList adapterList = new AdapterList(TravelWithActivity.context, apidata);

                           recyclerView.setAdapter(adapterList);
                       } else {
                         //  Toast.makeText(context, "No Routes Available", Toast.LENGTH_SHORT).show();
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

       }