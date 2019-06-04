package com.example.lux.mapsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Matrix;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.lang.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //initialize things for running purpose
    private GoogleMap mMap;
    private ArrayList<POI> pois;//this i a list of all the pois that we recomended to the user
    private double longitude = 0;
    private double latitude = 0;
    private String category = ""; // this i for the categories of pois that we have (Food, Bars. Arts & Entertainment
    private Circle circle = null; //we use this for the range
    private Marker posMarker = null; //marker for users position
    private String IP;

    Bitmap bmp;
    private ArrayList<Marker> markers = new ArrayList<Marker>(); //list of markers
    private ArrayList<LatLng> latLngs = new ArrayList<LatLng>(); //list of LatLngs
    private ArrayList<Boolean> changedMarker = new ArrayList<Boolean>(); //list boolean to known if a marker has or doesn't have it's original image
    // we used that because there wasn't a function like marker.getIcon()
    private int range = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //we've edited nothing in here, it's the default onCreate
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int i = (int) marker.getTag();
                if (i != -1) {
                    if (changedMarker.get(i) == false) {

                        Image im=new Image();
                        try {
                            im.execute(i).get(); //we use .get() in order to make sure that we have downloaded the image before the marker icon changed
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(i)));

                        //
                        markers.get(i).setIcon((BitmapDescriptorFactory.fromBitmap(getResizedBitmap(bmp,750,750))));
                        changedMarker.set(i, true);
                        for (int j = 0; j < markers.size(); j++) {
                            if (i != j) markers.get(j).setVisible(false);
                        }

                        //set visibility of the layouts elements. in this case we want to make them invisible to the user in order to see the pois displayed to him better.
                        EditText id_t = (EditText) findViewById(R.id.id_f);
                        EditText poi_num_t = (EditText) findViewById(R.id.poi_num_f);
                        EditText long_t = (EditText) findViewById(R.id.longitude_f);
                        EditText lat_t = (EditText) findViewById(R.id.latitude_f);
                        EditText cat_t = (EditText) findViewById(R.id.category_f);
                        EditText range_t = (EditText) findViewById(R.id.range_f);
                        CheckBox cb=(CheckBox) findViewById(R.id.checkBox);
                        EditText ip = (EditText) findViewById(R.id.IP);
                        Button b = (Button) findViewById(R.id.send_b);

                        id_t.setVisibility(View.INVISIBLE);
                        poi_num_t.setVisibility(View.INVISIBLE);
                        long_t.setVisibility(View.INVISIBLE);
                        lat_t.setVisibility(View.INVISIBLE);
                        cat_t.setVisibility(View.INVISIBLE);
                        range_t.setVisibility(View.INVISIBLE);
                        b.setVisibility(View.INVISIBLE);
                        cb.setVisibility(View.INVISIBLE);
                        ip.setVisibility(View.INVISIBLE);

                        //its a toast to inform the user to click on the info window in order to make the other markers appear
                        Context context = getApplicationContext();
                        CharSequence text = "In order to make the other markers appear again,tap on the info window !";
                        short duration = (short)85000;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    } else {
                        //sets the marker's icon to default, but changed the color in green so that
                        // it can inform the user that the place's photos have been viewed
                        markers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        changedMarker.set(i, false);//changed the changed marker's value in order to know if it's changed or not
                        for (int j = 0; j < markers.size(); j++) {
                            if (i != j) markers.get(j).setVisible(true);//makes the markers reappear
                        }
                        //set visibility of the layouts elements.
                        EditText id_t = (EditText) findViewById(R.id.id_f);
                        EditText poi_num_t = (EditText) findViewById(R.id.poi_num_f);
                        EditText long_t = (EditText) findViewById(R.id.longitude_f);
                        EditText lat_t = (EditText) findViewById(R.id.latitude_f);
                        EditText cat_t = (EditText) findViewById(R.id.category_f);
                        EditText range_t = (EditText) findViewById(R.id.range_f);
                        CheckBox cb=(CheckBox) findViewById(R.id.checkBox);
                        EditText ip = (EditText) findViewById(R.id.IP);
                        Button b = (Button) findViewById(R.id.send_b);

                        id_t.setVisibility(View.VISIBLE);
                        poi_num_t.setVisibility(View.VISIBLE);
                        long_t.setVisibility(View.VISIBLE);
                        lat_t.setVisibility(View.VISIBLE);
                        cat_t.setVisibility(View.VISIBLE);
                        range_t.setVisibility(View.VISIBLE);
                        b.setVisibility(View.VISIBLE);
                        cb.setVisibility(View.VISIBLE);
                        ip.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        //This button does everything that out app needs to do, since
        //we had trouble synchronising things, we thought that getting
        //everything inside this button would work, and it did.
        final Button button = (Button) findViewById(R.id.send_b);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //In here it reads from the texts,in case a field is empty we have
                //assigned some default values so that our app won't crash.
                int id;
                EditText id_t = (EditText) findViewById(R.id.id_f);
                String id_temp = id_t.getText().toString().trim();
                if (!id_temp.equals("")) id = Integer.parseInt(id_temp);
                else id = 130;

                int poi_num;
                EditText poi_num_t = (EditText) findViewById(R.id.poi_num_f);
                String poi_num_temp = poi_num_t.getText().toString().trim();
                if (!poi_num_temp.equals("")) poi_num = Integer.parseInt(poi_num_temp);
                else poi_num = 10;

                EditText long_t = (EditText) findViewById(R.id.longitude_f);
                String long_temp = long_t.getText().toString().trim();
                if (!long_temp.equals("")) longitude = Double.parseDouble(long_temp);
                else longitude=-73.935242;

                EditText lat_t = (EditText) findViewById(R.id.latitude_f);
                String lat_temp = lat_t.getText().toString().trim();
                if (!lat_temp.equals("")) latitude = Double.parseDouble(lat_temp);
                else latitude = 40.730610;

                EditText cat_t = (EditText) findViewById(R.id.category_f);
                category = cat_t.getText().toString();

                EditText range_t = (EditText) findViewById(R.id.range_f);
                String range_temp = range_t.getText().toString().trim();
                if (!range_temp.equals("")) range = Integer.parseInt(range_temp);
                else range = 0;

                EditText ip = (EditText) findViewById(R.id.IP);
                IP = ip.getText().toString();
                if(IP.equals("")) IP="10.0.2.2";

                CheckBox check = (CheckBox) findViewById(R.id.checkBox);
                boolean filter = check.isChecked();

                //All the fields have been read..

                //Checks if we already have a marker that shows our position,if it has it deletes it.
                //This is used for new queries
                if (posMarker != null) posMarker.remove();
                //Creates a marker on the map by using our long and lat.
                //Also it moves the camera to the new marker.
                LatLng pos = new LatLng(latitude, longitude);
                MarkerOptions mo1 = new MarkerOptions().position(pos).title("User's Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                posMarker = mMap.addMarker(mo1);
                posMarker.setTag(-1);//tag -1 is used in order to avoid downloading a non-existend image when we click on the info window,so we use this tag to basically render it useless.
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null); // zooms to the users position
                mMap.setTrafficEnabled(true); //displays the traffic

                //this is used to display the range.
                if(circle!=null)//and this is used in order to remove the range,if we want to do another query.
                {
                    circle.setVisible(false);
                    circle.remove();
                }
                circle = mMap.addCircle(new CircleOptions()
                        .center(pos)
                        .radius(range * 1000)
                        .strokeColor(Color.argb(20, 0, 51, 255))
                        .fillColor(Color.argb(200, 141, 230, 243)));

                //Now the client starts running,by giving it id,poi_num,category it finds
                //The best pois suited to our likings.
                Client cl = new Client();
                try {
                    //We use .get on .execute because this will force the android app to wait for the client
                    //to finish getting the recommendations.If it finishes we can move freely to the next stage.
                    cl.execute(id, poi_num).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                //Once again,if we want another query the map finds and deletes
                //any markers previously used for new recommendations
                if (markers.size() != 0) {
                    for (int j = 0; j < markers.size(); j++) {
                        markers.get(j).remove();
                    }
                    for (int i = 0; i < markers.size(); i++) markers.remove(i);
                    for(int i=0;i<changedMarker.size();i++)
                    {
                        changedMarker.remove(i);
                    }
                }
                if (latLngs.size() != 0) {
                    for (int j = 0; j < latLngs.size(); j++) {
                        latLngs.remove(j);
                    }
                }

                //and it places the new markers.
                markers=new ArrayList<Marker>();
                changedMarker = new ArrayList<Boolean>();
                latLngs = new ArrayList<LatLng>();
                int pointer=0;
                for (int i = 0; i < pois.size(); i++) {
                    latLngs.add(new LatLng(pois.get(i).getLatitude(), pois.get(i).getLongitude()));
                    //we give the positions of the poi and the user to compute the distance
                    float dist = getDistancBetweenTwoPoints(pos.latitude,pos.longitude,latLngs.get(i).latitude,latLngs.get(i).longitude);
                    //we fix the distance in km
                    dist=dist/1000;

                    //we use a filter with a check box in order to diplay pois only in the range that the user asked
                    if(filter==true&&dist<=range) {

                        //adds a marker for each poi according to users wishes
                        markers.add(mMap.addMarker(new MarkerOptions()
                                //gets the position of a poi
                                .position(latLngs.get(i))
                                //displays the name of the poi
                                .title(pois.get(i).getName())
                                //displays the category of the poi and the distance between the poi and the user in Km
                                .snippet(pois.get(i).getCategory() + " " + dist + "Km")));
                        //we use the tag in oder to remember for which poi we display his info
                        markers.get(pointer).setTag(pointer);
                        pointer++;
                        //
                        changedMarker.add(false);
                    }
                    //if the user doent want the feature of the chechbox it simple displays the pois that the user asked
                    else if(filter==false)
                    {
                        markers.add(mMap.addMarker(new MarkerOptions()
                                .position(latLngs.get(i))
                                .title(pois.get(i).getName())
                                .snippet(pois.get(i).getCategory() + " " + dist + "Km")));
                        markers.get(i).setTag(i);
                        changedMarker.add(false);
                    }
                }

                Context context = getApplicationContext();
                CharSequence text = "If you want to see a photo of a POI,just click on the marker and then on the info window.";
                short duration = (short)85000;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        });

    }

    //we use this method to calculate the distance between every poi and the users position
    private float getDistancBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        //distance is a float array that has 2 spaces. this is for overflow purpose
        // If result has length >= 2, is stored in distance[1] and if the result has length >=3, is stored in distance[2]
        float[] distance = new float[2];
        //Computes the approximate distance in meters between two locations, and optionally the initial and final bearings of the shortest path between them.
        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);
        //returns the distance in the first space
        return distance[0];
    }

    //resize the photos matrix in oder to make sure that all the images has the same dimensions
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        //makes sure that photo can fit the screen
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    // this is the connection between the app and the server.
    public class Client extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... ints) {

            int id = ints[0];
            int poi_num = ints[1];

            //
            pois = new ArrayList<POI>();

            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            Socket requestSocket = null;
            if (poi_num != 0) {//For debugging purposes.
                try {
                    //Client then has to connect to the serverv
                    requestSocket = new Socket(IP, 4200);
                    out = new ObjectOutputStream(requestSocket.getOutputStream());
                    in = new ObjectInputStream(requestSocket.getInputStream());
                    //Client sends hid id and K
                    out.writeInt(id);
                    out.flush();
                    out.writeInt(poi_num);
                    out.flush();
                    out.writeObject(category);
                    out.flush();

                    poi_num = in.readInt();
                    //Finally he receives the results from the server
                    for (int i = 0; i < poi_num; i++) {
                        //Print the returned POI's data
                        try {
                            pois.add((POI) in.readObject());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    out.writeInt(poi_num);
                    out.flush();
                } catch (UnknownHostException unknownHost) {
                    System.err.println("You are trying to connect to an unknown host!");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    try {
                        in.close();
                        out.close();
                        requestSocket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    //we download the images links that we need for the pois
    public class Image extends AsyncTask<Integer,Void,Void>
    {

        int ktop;

        @Override
        protected Void doInBackground(Integer... ints) {

            int i=ints[0];
            String link=pois.get(i).getPhoto();
            //we noticed that some pois didn't have links for photos so we use the link below to display a photo that notifies the user that a photo doesnt exits yet
            //without a link the pois that didn't have a photo will crash our app
            if(link.equals("Not exists"))link="https://upload.wikimedia.org/wikipedia/commons/b/ba/No_image_yet2.jpg";
            URL url= null;
            try {
                url = new URL(link);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                bmp=BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
