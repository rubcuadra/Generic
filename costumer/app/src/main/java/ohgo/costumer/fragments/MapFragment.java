package ohgo.costumer.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import ohgo.costumer.R;
import ohgo.costumer.Service;


/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends Fragment
{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Context CONTEXT;

    Bundle mBundle;
    LatLng myPosition;

    boolean initPos = true;
    int jobs ;

    ImageView mNewWork;

    public MapFragment(){}

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        CONTEXT = activity;
    }
    public static MapFragment getInstance(Bundle bundle)
    {
        MapFragment map = new MapFragment();
        map.setArguments(bundle);
        return map;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        setUpMapIfNeeded();
        ObtainServicesFromUser(ParseUser.getCurrentUser());
        mNewWork = (ImageView) rootView.findViewById(R.id.confirm);
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (initPos == true)
                    MoveCameraTo(new LatLng(location.getLatitude(), location.getLongitude()));

                initPos = false;
            }
        });

        mNewWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (initPos == false && jobs<1) //If we have user position as well he has not exceed the jobs allowed
                {
                    double lat = mMap.getCameraPosition().target.latitude;
                    double lng = mMap.getCameraPosition().target.longitude;
                    ++jobs;
                    CreateService(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("This is your new job"));
                    //sendPush(lat, lng);
                }
            }
        });
        return rootView;
    }

    private void CreateService(final double lat,final double lng)
    {
        Service trabajo = new Service();
        trabajo.setlocEnd(lat, lng);
        trabajo.setStatus(0); //Status 0 means Pending, 1 in Progress, 2 Done;

        ParseRelation relation = trabajo.getUserRelation();
        relation.add(ParseUser.getCurrentUser());

        trabajo.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                Toast.makeText(CONTEXT,"Your Job is Online!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void ObtainServicesFromUser(ParseUser user)
    {
        ParseQuery<Service> services = ParseQuery.getQuery("Service");
        services.whereEqualTo("userId", user);
        services.findInBackground(new FindCallback<Service>() {
            @Override
            public void done(List<Service> list, ParseException e) {
                if (e == null)
                {
                    jobs = list.size();
                    if (jobs==1) //You can modify if you want that user only have 1 job or < 5, etc
                    {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(list.get(0).getlocEnd().getLatitude(), list.get(0).getlocEnd().getLongitude())))
                                .setTitle("Your job ");
                    } else
                    {
                        Log.d("Parse", "User has " + jobs + " jobs pending");
                    }
                } else {
                    Log.wtf("Parse", "ERROR ON OBTAIN-SERVICE-FROM-USER");
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded()
    {
        if (mMap == null)
        {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if(mMap != null)
            {
                setUpPosition();
            }
        }
    }

    private void setUpPosition()
    {
        if (myPosition!=null)
        MoveCameraTo(myPosition);
    }
    private void MoveCameraTo(LatLng position)
    {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(position, 15)));
    }

    void sendPush(double l,double g)
    {
        ParsePush push = new ParsePush();
        push.setChannel("company"); //Maybe just to deliveryguys near the area
        push.setMessage("There is a new Job near you in "+String.valueOf(l)+","+String.valueOf(g));
        push.sendInBackground();
    }

}
