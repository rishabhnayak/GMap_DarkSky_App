package com.MsoftTexas.WeatherOnMyTripRoute.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.MsoftTexas.WeatherOnMyTripRoute.R;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;


/**
 * Created by RAJA on 18-12-2017.
 */

public class DragupListAdapter_route extends RecyclerView.Adapter<DragupListAdapter_route.PnrViewHolder>{

    private Context context;
    private DirectionsRoute route;
    public DragupListAdapter_route(Context context, DirectionsRoute route){
        this.context=context;
        this.route=route;
    }



    @Override
    public PnrViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.dragup_list_route,parent,false);
        return new PnrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PnrViewHolder holder, int position) {
        DirectionsStep mStep =route.legs[0].steps[position];

        setScaleAnimation(holder.itemView);
       // Glide.with(holder.image.getContext()).load(passengerList.getLink()).into(holder.image);
try {
    String maneuvers=mStep.maneuver;

    switch (maneuvers){
        case "turn-sharp-left":
//                holder.directionImage.setImageResource(R.id);
            holder.directionImage.setImageResource(R.drawable.turn_sharp_left);
            break;
        case "uturn-right":
            holder.directionImage.setImageResource(R.drawable.utern_right);
            break;
        case "turn-slight-right":
            holder.directionImage.setImageResource(R.drawable.uturn_slight_right);
            break;
        case "merge":
            holder.directionImage.setImageResource(R.drawable.merge);
            break;
        case "roundabout-left":
            holder.directionImage.setImageResource(R.drawable.rounded_about_left);
            break;
        case "roundabout-right":
            holder.directionImage.setImageResource(R.drawable.rounded_about_right);
            break;
        case "uturn-left":
            holder.directionImage.setImageResource(R.drawable.uturn_left);
            break;
        case "turn-slight-left":
            holder.directionImage.setImageResource(R.drawable.utern_right);
            break;
        case "turn-left":
            holder.directionImage.setImageResource(R.drawable.turn_left);
            break;
        case "ramp-right":
            holder.directionImage.setImageResource(R.drawable.ramp_right);
            break;
        case "turn-right":
            holder.directionImage.setImageResource(R.drawable.turn_right);
            break;
        case "fork-right":
            holder.directionImage.setImageResource(R.drawable.fork_right);
            break;
        case "straight":
            holder.directionImage.setImageResource(R.drawable.straight);
            break;
        case "fork-left":
            holder.directionImage.setImageResource(R.drawable.fork_left);
            break;
        case "ferry-train":
            holder.directionImage.setImageResource(R.drawable.ferry_train);
            break;
        case "turn-sharp-right":
            holder.directionImage.setImageResource(R.drawable.turn_sharp_right);
            break;
        case "ramp-left":
            holder.directionImage.setImageResource(R.drawable.ramp_left);
            break;
        case "ferry":
            holder.directionImage.setImageResource(R.drawable.ferry);
            break;
            case "keep-left":
                holder.directionImage.setImageResource(R.drawable.keep_left);
                break;
            case "keep-right":
                holder.directionImage.setImageResource(R.drawable.keep_right);
                break;
        default:
    }
}catch (Exception e){

}


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.instr.setText(Html.fromHtml(mStep.htmlInstructions, Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.instr.setText(Html.fromHtml(mStep.htmlInstructions));
        }
        //     holder.instr.setText(mStep.getStep().getHtml_instructions());
//        holder.distance.setText("Traveled : "+mStep.getAft_distance()/(long)1000+" km");
//        holder.arrtime.setText("Start time:"+mStep.getArrtime());
        try {
            String dist= mStep.distance.humanReadable;
            holder.stepLength.setText(dist);
      //      holder.arrtime.setText(mStep.getArrtime());
      //      holder.weather.setText(mStep.getWlist().getSummary());
      //      holder.temp.setText(mStep.getWlist().getTemperature() + "°F");
       //     holder.stepLength.setText(String.format("%.2f", (float) mStep.getStep().getDistance().getValue() / (float) 1000 * (0.621371)) + " miles");
         //   StorageReference storageRef = storage.getReference(mStep.getWlist().getIcon()+".png");
//            Drawable icon = context.getResources().getDrawable( R.drawable.clear_day );
//
//
//            switch (mStep.getWlist().getIcon()){
//                case "clear-day":icon = context.getResources().getDrawable(R.drawable.clear_day);
//                    break;
//                case "cloudy":icon = context.getResources().getDrawable(R.drawable.cloudy);
//                    break;
//                case "clear-night":icon = context.getResources().getDrawable(R.drawable.clear_night);
//                    break;
//                case "fog":icon = context.getResources().getDrawable(R.drawable.fog);
//                    break;
//                case "hail":icon = context.getResources().getDrawable(R.drawable.hail);
//                    break;
//                case "partly-cloudy-day":icon = context.getResources().getDrawable(R.drawable.partly_cloudy_day);
//                    break;
//                case "partly-cloudy-night":icon = context.getResources().getDrawable(R.drawable.partly_cloudy_night);
//                    break;
//                case "rain":icon = context.getResources().getDrawable(R.drawable.rain);
//                    break;
//                case "sleet":icon = context.getResources().getDrawable(R.drawable.sleet);
//                    break;
//                case "snow":icon = context.getResources().getDrawable(R.drawable.snow);
//                    break;
//                case "thunderstorm":icon = context.getResources().getDrawable(R.drawable.thunderstorm);
//                    break;
//                case "tornado":icon = context.getResources().getDrawable(R.drawable.tornado);
//                    break;
//                case "wind":icon = context.getResources().getDrawable(R.drawable.wind);
//                    break;
//            }
//            Glide.with(context)
//                    //  .load("http://openweathermap.org/img/w/"+itemslist.get(position).weather.get(0).icon+".png")
//                    .load(icon)
//                    //     .override(100,100)
//                    .into(holder.weatherimg);
        //    System.out.println(mStep.getStep().getStart_location().getLat());
         //   System.out.println(mStep.getStep().getStart_location().getLng());


  //          String address = new Geocoder(context, Locale.ENGLISH).getFromLocation(mStep.getStep().getStart_location().getLat(), mStep.getStep().getStart_location().getLng(), 1).get(0).getAddressLine(0);
            //  System.out.println("hre is address :"+address);
  //          holder.address.setText(address);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

   @Override
    public int getItemCount() {
      return route.legs[0].steps.length;
    }

    public class PnrViewHolder extends RecyclerView.ViewHolder {



        TextView instr,stepLength;
        ImageView directionImage;
        public PnrViewHolder(View itemView) {
            super(itemView);
            instr= (TextView) itemView.findViewById(R.id.desc);
            stepLength=itemView.findViewById(R.id.stepLength);
            directionImage=itemView.findViewById(R.id.direction_image);
        }
    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
