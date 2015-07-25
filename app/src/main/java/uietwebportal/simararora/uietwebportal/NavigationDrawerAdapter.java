package uietwebportal.simararora.uietwebportal;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationDrawerViewHolder> {

    private LayoutInflater inflater;
    private List<NavigationDrawerItem> data = Collections.emptyList();
    private ArrayList<NavigationDrawerIndividualView> allViews;
    private int noOfViews;
    private int currentFragment;

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerItem> data, int currentFragment) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        allViews = new ArrayList<>();
        this.currentFragment = currentFragment;
        noOfViews = 0;
    }

    @Override
    public NavigationDrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new NavigationDrawerViewHolder(inflater.inflate(R.layout.row_naviigation_drawer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(NavigationDrawerViewHolder navigationDrawerViewHolder, int i) {
        NavigationDrawerItem current = data.get(i);
        navigationDrawerViewHolder.textView.setText(current.getTitle());
        navigationDrawerViewHolder.imageView.setImageResource(current.getIconId());
        allViews.add(new NavigationDrawerIndividualView(navigationDrawerViewHolder.textView, navigationDrawerViewHolder.imageView));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<NavigationDrawerIndividualView> getAllViews() {
        return allViews;
    }

    class NavigationDrawerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public NavigationDrawerViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tvListText);
            imageView = (ImageView) itemView.findViewById(R.id.ivListIcon);
            if (noOfViews == currentFragment) {
                imageView.setColorFilter(Color.parseColor("#F44336"));
                textView.setTextColor(Color.parseColor("#F44336"));
            }
            noOfViews++;

        }
    }


}