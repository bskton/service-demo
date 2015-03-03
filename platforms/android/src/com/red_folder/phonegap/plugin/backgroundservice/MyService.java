package com.red_folder.phonegap.plugin.backgroundservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

import android.location.LocationManager;
import android.content.Context;
import android.location.LocationListener;
import android.location.Location;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MyService extends BackgroundService {
	
	private final static String TAG = MyService.class.getSimpleName();
	
	private String mHelloTo = "Service Demo";

    private String interval = "1000";

    private String host = "localhost";

    protected LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {}

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

	@Override
	protected JSONObject doWork() {
		JSONObject result = new JSONObject();
		
		try {
            Location location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            
            String altitude, UTC, latitude, longitude, HDOP, COG, speedKm, speedKnots, date, satellites;
            
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator('.');
            DecimalFormat decim = new DecimalFormat("#####0.00", otherSymbols);
            DecimalFormat decim1 = new DecimalFormat("#####0.0", otherSymbols);
            // TODO: move to separate method
            if (location != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhmmss.SSS");
                UTC = simpleDateFormat.format(location.getTime());
                latitude = this.convertLatitude(location.getLatitude());
                longitude = this.convertLongitude(location.getLongitude());
                HDOP = decim.format(location.getAccuracy() / 5.0f);
                altitude = decim1.format(location.getAltitude());
                COG = decim.format(location.getBearing());
                speedKm = decim.format(location.getSpeed() * 3.6f);
                speedKnots = decim.format(location.getSpeed() * 1.94384f);
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyy");
                date = dateFormat.format(new Date());
                satellites = String.format("%02d", location.getExtras().getInt("satellites"));
            } else {
                // for avd tests 
                UTC = "ThisIsATest";
                latitude = this.convertLatitude(43.17612478);
                longitude = this.convertLongitude(31.93167789);
                HDOP = decim.format(68.0f / 5.0f);
                altitude = decim1.format(123.45d);
                COG = String.format("%.2f", 234.234f);
                speedKm = decim.format(10.0f * 3.6f);
                speedKnots = decim.format(10.0f * 1.94384f);
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
                date = dateFormat.format(new Date());
                satellites = String.format("%02d", 5);
                throw new Exception("Have no known location.");
            }

            HttpGet httpget = new HttpGet();
            // TODO: Move address to config
            httpget.setURI(new URI("http://" + this.host +"/index.php?imei=312345006395040&key=test&data="
                        + UTC + "," + latitude + "," + longitude + "," + HDOP + "," + altitude + ",3," 
                        + COG + "," + speedKm + "," + speedKnots + "," + date + "," + satellites));
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpget);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
			String now = df.format(new Date(System.currentTimeMillis())); 

			String msg = "Hello " + this.mHelloTo + " - its currently " + now;
			result.put("Message", msg);

			Log.d(TAG, msg);
		} catch (JSONException e) {
            Log.d(TAG, e.toString());
		} catch (URISyntaxException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
		
		return result;
    }

	@Override
	protected JSONObject getConfig() {
		JSONObject result = new JSONObject();
		
		try {
			result.put("HelloTo", this.mHelloTo);
            result.put("host", this.host);
            result.put("interval", this.interval);
		} catch (JSONException e) {
		}
		
		return result;
	}

	@Override
	protected void setConfig(JSONObject config) {
		try {
			if (config.has("HelloTo"))
				this.mHelloTo = config.getString("HelloTo");
            if (config.has("interval"))
				this.interval = config.getString("interval");
            if (config.has("host"))
				this.host = config.getString("host");
		} catch (JSONException e) {
		}
		
	}     

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onTimerEnabled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTimerDisabled() {
		// TODO Auto-generated method stub
		
	}

    // TODO: move to separete class
    private String convertCoordinate(double lat, String format) {
        String result;

        int degree = (int) lat;
        result = String.valueOf(degree);

        double minute = (lat - degree)*60;
        result += String.valueOf(minute);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat decim = new DecimalFormat(format, otherSymbols);
        result = decim.format(Math.round(Double.parseDouble(result) * 10000.0) / 10000.0);

        return result;
    }

    private String convertLongitude(double lon) {
        String result = this.convertCoordinate(lon, "00000.0000");

        if (lon > 0) {
            result += "E";
        } else {
            result += "W";
        }
        //TODO: What about zero?

        return result;
    }

    private String convertLatitude(double lat) {
        String result = this.convertCoordinate(lat, "0000.0000");

        if (lat > 0) {
            result += "N";
        } else {
            result += "S";
        }
        //TODO: What about zero?

        return result;
    }
}
