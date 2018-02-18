package mad9132.doo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import mad9132.doo.model.BuildingPOJO;

/**
 * Detailed Activity of a Building.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 */
public class DetailBuildingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView  tvName;
    private TextView  tvCategory;
    private TextView  tvDescription;
    private ImageView buildingImage;

    private BuildingPOJO selectedBuilding;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);

        selectedBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (selectedBuilding == null) {
            throw new AssertionError("Null data item received!");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        tvName = (TextView) findViewById(R.id.tvName);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        buildingImage = (ImageView) findViewById(R.id.buildingImage);

        tvName.setText(selectedBuilding.getNameEN());
        tvCategory.setText(selectedBuilding.getCategoryEN());
        tvDescription.setText(selectedBuilding.getDescriptionEN());

        //FIXME :: LOCALHOST
        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + selectedBuilding.getBuildingId() + "/image";
        //String url = "http://10.0.2.2:3000/buildings/" + selectedBuilding.getBuildingId() + "/image";
        Picasso.with(this)
                .load(Uri.parse(url))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .fit()
                .into(buildingImage);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker, and move the camera
        LatLng latLng = new LatLng(selectedBuilding.getLatitude(), selectedBuilding.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(selectedBuilding.getAddressEN()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.F));
    }
}