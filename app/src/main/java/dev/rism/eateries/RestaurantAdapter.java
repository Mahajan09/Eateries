package dev.rism.eateries;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by risha on 18-04-2018.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ListHolder> implements Filterable {
    ArrayList<RestaurantModel>list;
     ArrayList<RestaurantModel>listFiltered;
    public RestaurantAdapter(ArrayList<RestaurantModel> list)
    {
        this.list=list;
        this.listFiltered=list;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_list_item,parent,false);

        return new ListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
    RestaurantModel model=listFiltered.get(position);
    holder.tvtitle.setText(model.getName());
    holder.tvadd.setText(model.getAdd());
    Double rating=model.getRating();
    String s=getCode(rating);
    holder.tvrating.setText(rating+"");
    holder.tvrating.setBackgroundColor(Color.parseColor(s));
    holder.tvcuisine.setText(model.getCuisine());
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String s=constraint.toString();
                if (s.isEmpty())
                {
                    listFiltered=list;
                }
                else {
                    try {
                        JSONObject jsonObject=new JSONObject(s);
                        String city=jsonObject.getString("city").trim();
                        String cuisine=jsonObject.getString("cuisine").trim();
                        if (city.compareTo("All")==0 && cuisine.compareTo("All")==0)
                        {
                            listFiltered=list;
                        }
                        else if (city.compareTo("All")==0)
                        {
                            ArrayList<RestaurantModel> diff=new ArrayList<>();
                            for (RestaurantModel model:list)
                            {
                                if (model.getCuisine().contains(cuisine))
                                {
                                    diff.add(model);
                                }
                            }
                            listFiltered=diff;
                        }
                        else if (cuisine.compareTo("All")==0)
                        {
                            ArrayList<RestaurantModel> diff=new ArrayList<>();
                            for (RestaurantModel model:list)
                            {
                                if (model.getLocation().contains(city) || model.getAdd().contains(city))
                                {
                                    diff.add(model);
                                }
                            }
                            listFiltered=diff;
                        }
                        else {
                            ArrayList<RestaurantModel> diff=new ArrayList<>();
                            for (RestaurantModel model:list)
                            {
                                if ((model.getLocation().contains(city)||model.getAdd().contains(city)) && model.getCuisine().contains(cuisine))
                                {
                                    diff.add(model);
                                }
                            }
                            listFiltered=diff;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                FilterResults results=new FilterResults();
                results.values=listFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
              listFiltered= (ArrayList<RestaurantModel>) results.values;
              notifyDataSetChanged();
            }
        };
    }

    public class ListHolder extends RecyclerView.ViewHolder
    {
       CanaroTextView tvtitle;
       GothamTextView tvrating;
       TextView tvadd,tvcuisine;
        public ListHolder(View itemView) {
            super(itemView);
            tvtitle=itemView.findViewById(R.id.tv_item_name);
            tvrating=itemView.findViewById(R.id.tv_item_rating);
            tvadd=itemView.findViewById(R.id.tv_item_address);
            tvcuisine=itemView.findViewById(R.id.tv_item_cuisine);
        }
    }
    private String getCode(Double rating)
    {
        int val=new Double(rating*10.0).intValue();
        if (val>=0 && val<10)
            return Constants.COLOR[0];
        else if (val>=10 && val<20)
            return Constants.COLOR[1];
        else if (val>=20 && val<=30)
            return Constants.COLOR[2];
        else if (val>30 && val <40)
            return Constants.COLOR[3];
        return Constants.COLOR[4];
    }
}
