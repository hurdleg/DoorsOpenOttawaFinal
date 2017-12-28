package mad9132.doo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import mad9132.doo.model.BuildingPOJO;

/**
 * Detailed Activity of a Building.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class DetailBuildingActivity extends Activity {

    private TextView  tvName;
    private TextView  tvCategory;
    private TextView  tvDescription;
    private ImageView buildingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        BuildingPOJO selectedBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (selectedBuilding == null) {
            throw new AssertionError("Null data item received!");
        }

        tvName = (TextView) findViewById(R.id.tvName);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        buildingImage = (ImageView) findViewById(R.id.buildingImage);

        tvName.setText(selectedBuilding.getNameEN());
        tvCategory.setText(selectedBuilding.getCategoryEN());
        tvDescription.setText(selectedBuilding.getDescriptionEN());

        //FIXME :: LOCALHOST
        //String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + selectedBuilding.getBuildingId() + "/image";
        String url = "http://10.0.2.2:3000/buildings/" + selectedBuilding.getBuildingId() + "/image";
        Picasso.with(this)
                .load(Uri.parse(url))
                .error(R.drawable.noimagefound)
                .fit()
                .into(buildingImage);
    }
}