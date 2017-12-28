package mad9132.doo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import mad9132.doo.model.BuildingPOJO;
import mad9132.doo.services.MyService;
import mad9132.doo.utils.NetworkHelper;
import mad9132.doo.utils.RequestPackage;

public class MainActivity extends Activity {

    //FIXME :: LOCALHOST
    //private static final String JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";
    private static final String JSON_URL = "http://10.0.2.2:3000/buildings";

    private static final int    NO_SELECTED_CATEGORY_ID = -1;
    private static final String REMEMBER_SELECTED_CATEGORY_ID = "lastSelectedCategoryId";

    private BuildingAdapter    mBuildingAdapter;
    private List<BuildingPOJO> mBuildingsList;
    private String[]           mCategories;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private int                mRememberSelectedCategoryId;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (intent.hasExtra(MyService.MY_SERVICE_PAYLOAD)) {
                BuildingPOJO[] buildingsArray = (BuildingPOJO[]) intent
                        .getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
                Toast.makeText(MainActivity.this,
                        "Received " + buildingsArray.length + " buildings from service",
                        Toast.LENGTH_SHORT).show();

                mBuildingsList = Arrays.asList(buildingsArray);
                displayBuildings();
            } else if (intent.hasExtra(MyService.MY_SERVICE_EXCEPTION)) {
                String message = intent.getStringExtra(MyService.MY_SERVICE_EXCEPTION);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pbNetwork);
        mProgressBar.setVisibility(View.INVISIBLE);

//      Code to manage sliding navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mCategories = getResources().getStringArray(R.array.categories);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_category_row, mCategories));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Category: " + mCategories[position], Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(mDrawerList);
                fetchBuildings(position);
            }
        });
//      end of navigation drawer

        mRecyclerView = (RecyclerView) findViewById(R.id.rvBuildings);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
        mRememberSelectedCategoryId = settings.getInt(REMEMBER_SELECTED_CATEGORY_ID, NO_SELECTED_CATEGORY_ID);
        fetchBuildings(mRememberSelectedCategoryId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt( REMEMBER_SELECTED_CATEGORY_ID, mRememberSelectedCategoryId );
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_all_items:
                // fetch and display all buildings
                fetchBuildings(NO_SELECTED_CATEGORY_ID);
                return true;
            case R.id.action_choose_category:
                //open the drawer
                mDrawerLayout.openDrawer(mDrawerList);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayBuildings() {
        if (mBuildingsList != null) {
            mBuildingAdapter = new BuildingAdapter(this, mBuildingsList);
            mRecyclerView.setAdapter(mBuildingAdapter);
        }
    }

    private void fetchBuildings(int selectedCategoryId) {
        mRememberSelectedCategoryId = selectedCategoryId;
        if (NetworkHelper.hasNetworkAccess(this)) {
            mProgressBar.setVisibility(View.VISIBLE);
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setEndPoint(JSON_URL);
            if (selectedCategoryId != NO_SELECTED_CATEGORY_ID) {
                requestPackage.setParam("categoryId", selectedCategoryId + "");
            }

            Intent intent = new Intent(this, MyService.class);
            intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }
}
