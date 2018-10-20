package com.MsoftTexas.WeatherOnMyTripRoute;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class TravelWithActivity extends AppCompatActivity {
    RadioGroup radioGroup;

    String selectedText="one",selectedUnit="automatic";
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_with);
        recyclerView = findViewById(R.id.recycler);
        //Custom radio button...............................................................................
        radioGroup = findViewById(R.id.test_radio);
        radioGroup.check(R.id._1);
        //  Toast.makeText(getApplicationContext(), selectedText, Toast.LENGTH_SHORT).show();
        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_on);
        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
//        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
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
                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
//                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "two":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_on);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
//                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "three":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_on);
//                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "four":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
//                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_on);
//                        ((ImageView)(findViewById(R.id.e))).setImageResource(R.drawable.plane_off);
                        break;
                    case "five":
                        ((ImageView) (findViewById(R.id.a))).setImageResource(R.drawable.car_off);
                        ((ImageView) (findViewById(R.id.b))).setImageResource(R.drawable.train_off);
                        ((ImageView) (findViewById(R.id.c))).setImageResource(R.drawable.walk_off);
//                        ((ImageView)(findViewById(R.id.d))).setImageResource(R.drawable.bike_off);
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
//        findViewById(R.id.d).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                radioGroup.check(R.id._4);
//            }
//        });
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
    }
//..................................................................................................

}
