package com.example.agromate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class home_ extends Fragment {

    private TextView tempTextView;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            getCityName(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_, container, false);

        checkLocationPermission();

        return view;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    private void getLocation() {
        locationManager = (LocationManager) requireActivity().getSystemService(requireActivity().LOCATION_SERVICE);
        if (locationManager != null) {
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    getCityName(latitude, longitude);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                String cityName = null;
                for (Address address : addresses) {
                    if (address.getLocality() != null) {
                        cityName = address.getLocality();
                        break;
                    } else if (address.getSubAdminArea() != null) {
                        cityName = address.getSubAdminArea();
                        break;
                    }
                }
                if (cityName != null) {
                    callOpenWeatherMapAPI(cityName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double currentTemperature = 0.0;
    private double feelslike = 0.0;
    private String sky;
    private int pressure;
    private int humidity;
    private double speed;
    private String city;

    private void callOpenWeatherMapAPI(String cityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String apiKey = "917bc24786cd9164b0f7c9b51bcada44";
                    String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey;

                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject main = jsonResponse.getJSONObject("main");
                        JSONObject wind = jsonResponse.getJSONObject("wind");
                        city = jsonResponse.getString("name");

                        sky = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("main");
                        feelslike = Math.round(main.getDouble("feels_like") - 273.15);
                        currentTemperature = Math.round(main.getDouble("temp") - 273.15);
                        pressure = main.getInt("pressure");
                        humidity = main.getInt("humidity");
                        speed = wind.getDouble("speed");
                        Log.d("city",city);
                        if (getActivity() != null && getView() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView skyView = getView().findViewById(R.id.main);
                                    TextView tempTextView = getView().findViewById(R.id.temp);
                                    TextView feelLikeView = getView().findViewById(R.id.feellike);
                                    TextView pressureview = getView().findViewById(R.id.pressure);
                                    TextView humidityview = getView().findViewById(R.id.humidity);
                                    TextView windtext = getView().findViewById(R.id.wind);
                                    TextView cityview = getView().findViewById(R.id.name);

                                    tempTextView.setText(currentTemperature + "°");
                                    feelLikeView.setText("Feels like " + feelslike + "°");
                                    skyView.setText(sky);
                                    pressureview.setText("Pressure: " + pressure + " hPa");
                                    humidityview.setText("Humidity: " + humidity + "%");
                                    windtext.setText("Wind: " + speed + " km/h");
                                    cityview.setText(city);
                                }
                            });
                        }
                    } else {
                        showToast("Failed to fetch weather data: " + connection.getResponseMessage());
                    }
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    showToast("Error: " + e.getMessage());
                }
            }
        }).start();
    }




    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
