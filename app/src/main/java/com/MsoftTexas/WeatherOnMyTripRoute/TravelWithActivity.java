package com.MsoftTexas.WeatherOnMyTripRoute;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import java.util.Calendar;


public class TravelWithActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    boolean flag;
    String selectedText="one",selectedUnit="automatic";
    RecyclerView recyclerView;
    static TextView departAt;
    static int mYear,mMonth,mDay, mHour, mMinute;
    static long jstart_date_millis, jstart_time_millis;
    String[] month={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    static CardView date_holder;
    ImageView requestDirection;

    TextView autoCompleteSource,autoCompleteDstn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_with);
        recyclerView = findViewById(R.id.recycler);


        autoCompleteSource= findViewById(R.id.source);
        autoCompleteDstn=findViewById(R.id.destination);
        requestDirection=findViewById(R.id.submit);

        //Custom radio button...............................................................................
        radioGroup = findViewById(R.id.test_radio);
        radioGroup.check(R.id._1);
        //  Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_on);
//        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                selectedText = ((RadioButton) (findViewById(selectedId))).getText().toString();
                System.out.println(selectedText);
                switch (selectedText) {
                    case "one":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_on);
//                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "two":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
//                       ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_on);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "three":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
//                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_on);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "four":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
//                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_on);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "five":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
//                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_on);
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
//        findViewById(R.id.b).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                radioGroup.check(R.id._2);
//            }
//        });
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
//        findViewById(R.id.e).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                radioGroup.check(R.id._5);
//            }
//        });
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
                selectedUnit = ((RadioButton) (findViewById(selectedId))).getText().toString();
                System.out.println(selectedUnit);
            }
        });

        boolean highway = ((CheckBox) findViewById(R.id.highway)).isChecked();
        boolean tolls = ((CheckBox) findViewById(R.id.tolls)).isChecked();
        boolean ferries = ((CheckBox) findViewById(R.id.ferries)).isChecked();

        Switch weatherSwitch=findViewById(R.id.weather_switch);
        weatherSwitch.setChecked(false);

        flag=false;
        ((ImageView)(findViewById(R.id.weather_switch_icon))).setImageResource(R.drawable.weather_off);
        weatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b==true){
                    Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    ((ImageView)(findViewById(R.id.weather_switch_icon))).setImageResource(R.drawable.weather_on);
                    flag=true;
                }else {
                    flag=false;
                    ((ImageView)(findViewById(R.id.weather_switch_icon))).setImageResource(R.drawable.weather_off);
                    Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
                }
            }
        });

        date_holder = findViewById(R.id.card_date);
        departAt=findViewById(R.id.date1);
        date_holder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePicker();

            }
        });
    }
//..................................................................................................
private void datePicker(){

    // Get Current Date


    final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

try{
    departAt.setText(dayOfMonth + " " + month[monthOfYear] + " " + String.valueOf(year).substring(2));
}catch (Exception e){
    System.out.println(e);
}
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
}
