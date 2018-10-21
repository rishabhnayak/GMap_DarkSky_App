package com.MsoftTexas.WeatherOnMyTripRoute.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.MsoftTexas.WeatherOnMyTripRoute.R;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.Arrays;


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
        View view=inflater.inflate(R.layout.list_view_row_item,parent,false);
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


            }
        });

        System.out.println("AdapterList,on bind view ");
        holder.route.setText(route.summary);
        holder.distance.setText(route.legs[0].distance.humanReadable);
        holder.time.setText(route.legs[0].duration.humanReadable);



    }

    @Override
    public int getItemCount() {

        return data.routes.length;
    }



    public class AdapterAllHolder extends RecyclerView.ViewHolder {

        TextView route,route_detail,time,distance;
        ImageView icon;CardView item;

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
}
