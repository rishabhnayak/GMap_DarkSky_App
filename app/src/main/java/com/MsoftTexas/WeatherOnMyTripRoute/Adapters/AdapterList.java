package com.MsoftTexas.WeatherOnMyTripRoute.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MsoftTexas.WeatherOnMyTripRoute.MapActivity;
import com.MsoftTexas.WeatherOnMyTripRoute.R;
import com.MsoftTexas.WeatherOnMyTripRoute.TravelWithActivity;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;


/**
 * Created by RAJA on 18-07-2018.
 */

public class AdapterList extends RecyclerView.Adapter<AdapterList.AdapterAllHolder> {
    private Context context;
    private DirectionsResult data;
    public AdapterList(Context context, DirectionsResult data){
        this.context=context;
        this.data=data;
    }

    @Override
    public AdapterAllHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.listview_item_travelwithactivity,parent,false);
        return new AdapterAllHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterAllHolder holder, final int position) {

        final DirectionsRoute route=data.routes[position];
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("hre is position "+position);
//                System.out.println(route.summary);
                Intent intent=new Intent(context,MapActivity.class);
                TravelWithActivity.selectedroute=position;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        setScaleAnimation(holder.itemView);

        System.out.println("AdapterList,on bind view ");
        holder.route.setText(route.summary);
        holder.distance.setText(route.legs[0].distance.humanReadable);
        holder.time.setText(route.legs[0].durationInTraffic!=null?route.legs[0].durationInTraffic.humanReadable:
        route.legs[0].duration.humanReadable);
        if(position==0)
        holder.route_detail.setText(R.string.FasttestRoute);

        switch (route.legs[0].steps[0].travelMode.toString()) {

            case "driving": holder.icon.setImageResource(R.drawable.car_on);break;
            case "bicycling": holder.icon.setImageResource(R.drawable.bike_on);break;
            case "walking": holder.icon.setImageResource(R.drawable.walk_on);break;
            default:holder.icon.setImageResource(R.drawable.car_on);
        }


    }

    @Override
    public int getItemCount() {

        return data.routes.length;
    }



    public class AdapterAllHolder extends RecyclerView.ViewHolder {

        TextView route,route_detail,time,distance;
        ImageView icon;LinearLayout item;

        public AdapterAllHolder(View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.itemContainer);
            route=(TextView) itemView.findViewById(R.id.route);
            route_detail=(TextView) itemView.findViewById(R.id.route_detail);
            time=(TextView) itemView.findViewById(R.id.time);
            distance=(TextView) itemView.findViewById(R.id.distance);
            icon=itemView.findViewById(R.id.icon);
        }

    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
