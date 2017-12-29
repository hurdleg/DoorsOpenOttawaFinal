package mad9132.doo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import mad9132.doo.model.BuildingPOJO;

/**
 * BuildingAdapter.
 *
 */
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    public static final String BUILDING_KEY = "building_key";

    private Context            mContext;
    private List<BuildingPOJO> mBuildings;

    public BuildingAdapter(Context context, List<BuildingPOJO> buildings) {
        this.mContext = context;
        this.mBuildings = buildings;
    }

    @Override
    public BuildingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View planetView = inflater.inflate(R.layout.list_building, parent, false);
        ViewHolder viewHolder = new ViewHolder(planetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuildingAdapter.ViewHolder holder, int position) {
        final BuildingPOJO aBuilding = mBuildings.get(position);

        holder.tvName.setText(aBuilding.getNameEN());

        //FIXME :: LOCALHOST
        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + aBuilding.getBuildingId() + "/image";
        //url = "http://10.0.2.2:3000/buildings/" + aBuilding.getBuildingId() + "/image";
        Picasso.with(mContext)
                .load(Uri.parse(url))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .resize(96, 96)
                .into(holder.imageView);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailBuildingActivity.class);
                intent.putExtra(BUILDING_KEY, aBuilding);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imageView;
        public View mView;

        public ViewHolder(View buildingView) {
            super(buildingView);

            tvName = (TextView) buildingView.findViewById(R.id.buildingNameText);
            imageView = (ImageView) buildingView.findViewById(R.id.imageView);
            mView = buildingView;
        }
    }
}
