package com.MsoftTexas.WeatherOnMyTripRoute;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


import com.MsoftTexas.WeatherOnMyTripRoute.util.IabBroadcastReceiver;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;

import java.util.Calendar;
import java.util.TimeZone;


public class TravelWithActivity extends BaseActivity
       {


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

 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_travel_with_activity);
        recyclerView = findViewById(R.id.recycler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    Toast.makeText(context,"start or end Address is null",Toast.LENGTH_LONG).show();
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
                Intent intent=new Intent(TravelWithActivity.this,SearchPlace.class);
                intent.putExtra("SrcOrDstn","Src");
                startActivity(intent);
            }
        });

        (findViewById(R.id.dstn_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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





    }

    public void requestDirection() {

        if(origin!=null && destination!=null) {
            new RouteApi().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            Toast.makeText(getApplicationContext(),"origin or destination null", Toast.LENGTH_LONG).show();
        }
    }

    //..................................................................................................
    private void datePicker() {

        // Get Current Date


        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        departAt.setText(dayOfMonth + " " + month[monthOfYear] + " " + String.valueOf(year).substring(2)+" "+timezone);
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

                        departAt.setText(set_time + "," +tempDay + " " + month[tempMonth] + " " + String.valueOf(tempYear).substring(2)+" "+timezone);

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
                   origin = null;destination = null;
                  
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
}