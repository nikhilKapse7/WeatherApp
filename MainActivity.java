package com.example.apidemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ImageButton imgBtn;
    String toastDate;
    TextView textF;
    TextView textC;
    TextView feelLike;
    Button go;
    Switch aSwitch;
    Spinner spinner;
    boolean isZip;
    boolean isYourLocation;
    boolean isPlaceName;
    boolean fahrenheit = true;
    EditText editText;
    String where;
    ListView weatherPredictions;
    LocationManager locationManager;
    TextView currentTemp;
    ImageView currentWeatherImage;
    TextView currentDescription;
    TextView currentDate;
    TextView feelsLikeTemp;
    Location location;
    TextView dateTitle;
    TextView minTitle;
    TextView maxTitle;

    ArrayList<weatherPrediction> predictionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent homeScreen = new Intent(MainActivity.this, HomeScreen.class);
//                startActivity(homeScreen);
//                finish();
//            }
//        }, 2000);

        textF = findViewById(R.id.textF);
        textC = findViewById(R.id.textC);
        go = findViewById(R.id.button);

        dateTitle = findViewById(R.id.dateTitle);
        minTitle = findViewById(R.id.minTitle);
        maxTitle = findViewById(R.id.maxTitle);

        feelLike = findViewById(R.id.feelsLike);
        feelsLikeTemp = findViewById(R.id.feelsLikeTemp);
        currentTemp = findViewById(R.id.currentTemp);
        currentDate = findViewById(R.id.currentDate);
        currentWeatherImage = findViewById(R.id.imageView2);
        currentDescription = findViewById(R.id.currentDescription);

        spinner = findViewById(R.id.spinner);
        editText = findViewById(R.id.editText);
        weatherPredictions = findViewById(R.id.listview);

        aSwitch = findViewById(R.id.switch1);
        imgBtn = findViewById(R.id.imageButton);

        currentTemp.bringToFront();
        //filling spinner with array from string resource file
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncThread thread = new AsyncThread();
                thread.execute(where);
                Log.d("TAG", "button click listener is running");
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    editText.setVisibility(View.VISIBLE);
                    editText.setClickable(true);
                    isZip = true;
                    isYourLocation = false;
                    isPlaceName = false;
                }
                else if(position == 1) {

                    if((ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    if(location == null) {
                        return;
                    }
                    editText.setText("");
                    editText.setVisibility(View.GONE);
                    editText.setClickable(false);
                    if(location != null) {
                        where = location.getLatitude() + ", " + location.getLongitude();
                    }
                    isZip = false;
                    isYourLocation = true;
                    isPlaceName = false;
                }
                else if (position == 2){
                    editText.setVisibility(View.VISIBLE);
                    editText.setClickable(true);
                    isZip = false;
                    isYourLocation = false;
                    isPlaceName = true;
                    Log.d("tag", "" + isPlaceName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged()", "afterTextChanged is running");
                where = s.toString();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    fahrenheit = false;
//                    textF.setTextColor(Color.WHITE);
//                    textF.setTypeface(Typeface.DEFAULT);
//                    textC.setTextColor(Color.parseColor("#FFA549"));
//                    textC.setTypeface(Typeface.DEFAULT_BOLD);
                }
                else {
                    fahrenheit = true;
//                    textC.setTextColor(Color.WHITE);
//                    textC.setTypeface(Typeface.DEFAULT);
//                    textF.setTextColor(Color.parseColor("#FFA549"));
//                    textF.setTypeface(Typeface.DEFAULT_BOLD);
                }
                AsyncThread thread = new AsyncThread();
                thread.execute(where);
            }
        });

        final CustomAdapter customAdapter = new CustomAdapter(this, R.layout.activity_custom, predictionsList);
        weatherPredictions.setAdapter(customAdapter);
    }
    public class AsyncThread extends AsyncTask<String, Integer, ArrayList<JSONObject>>{
        @Override
        protected ArrayList<JSONObject> doInBackground(String... strings) {
            JSONObject file = null;
            JSONObject forecast = null;
            ArrayList<JSONObject> JSONFiles = new ArrayList<>();
            String units = "";
            try {
                URL url;
                URL forecastUrl;
                String key = "&APPID=a906a2cd6d8ef7b48b304c056f642ed2";
                if(fahrenheit) {
                    units = "&units=imperial";
                }
                else { // if celsius
                    units = "&units=metric";
                }
                // Interpret information from link as a string and pass it to a JSONObject
                // key = a906a2cd6d8ef7b48b304c056f642ed2

                //CURRENT WEATHER
                if(isZip) {
                    Log.d("TAG", "isZip entered");
                    String forecastWeatherLink = "https://api.openweathermap.org/data/2.5/forecast?zip=";
                    String currentWeatherLink = "https://api.openweathermap.org/data/2.5/weather?zip=";
                    String zip = strings[0];
                    url = new URL(currentWeatherLink + zip + key + units);

                    forecastUrl = new URL(forecastWeatherLink + zip + key + units);

                    Log.d("tag", url.toString());
                    Log.d("tag", forecastUrl.toString());
                }
                else if(isYourLocation) {
                    String forecastWeatherLink = "https://api.openweathermap.org/data/2.5/forecast?";
                    String currentWeatherLink = "https://api.openweathermap.org/data/2.5/weather?";
                    //separating string into latitude and longitude
                    Pattern pattern = Pattern.compile(", *");
                    Matcher matcher = pattern.matcher(strings[0]);
                    String latitude = "";
                    String longitude = "";
                    if (matcher.find()) {
                        latitude = strings[0].substring(0, matcher.start());
                        longitude = where.substring(matcher.end());
                    }

                    url = new URL(currentWeatherLink + "lat=" + latitude + "&lon=" + longitude + key + units);
                    forecastUrl = new URL(forecastWeatherLink + "lat=" + latitude + "&lon=" + longitude + key + units);
                }
                else {//(isPlaceName)
                    String forecastWeatherLink = "https://api.openweathermap.org/data/2.5/forecast?q=";
                    String currentWeatherLink = "http://api.openweathermap.org/data/2.5/weather?q=";
                    String place = strings[0];
                    url = new URL(currentWeatherLink + place + "," + key + units);
                    forecastUrl = new URL(forecastWeatherLink + place + "," + key + units);
                }
                //url = new URL("http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=a906a2cd6d8ef7b48b304c056f642ed2");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                HttpURLConnection forecastUrlConnection = (HttpURLConnection) forecastUrl.openConnection();
                forecastUrlConnection.setRequestMethod("GET");

                StringBuffer URLText = new StringBuffer();
                String textLine;
                BufferedReader JSONReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                while((textLine = JSONReader.readLine()) != null) {
                    URLText.append(textLine);
                }

                StringBuffer forecastURLText = new StringBuffer();
                String forecastTextLine;
                BufferedReader JSONForecastReader = new BufferedReader(new InputStreamReader(forecastUrlConnection.getInputStream()));
                while((forecastTextLine = JSONForecastReader.readLine()) != null) {
                    forecastURLText.append(forecastTextLine);
                }


                JSONForecastReader.close();
                forecastUrlConnection.disconnect();
                forecast = new JSONObject(forecastURLText.toString());

                JSONReader.close();
                urlConnection.disconnect();

                //Log.d("TAG current", URLText.toString());
                //Log.d("TAG forecast", forecastURLText.toString());

                file = new JSONObject(URLText.toString());




                JSONFiles.add(file); //current weather JSON file
                JSONFiles.add(forecast); //forecast weather JSON file

            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("TAG", "IOException");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return JSONFiles;
        }

        @Override
        protected void onPreExecute() {
            predictionsList.clear();
        }

        //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(ArrayList<JSONObject> JSONFiles) {
            if(JSONFiles.size() > 0) {
                JSONObject currentJSON = JSONFiles.get(0);
                JSONObject forecastJSON = JSONFiles.get(1);
                Log.d("JSON", currentJSON.toString());
                //CURRENT WEATHER
                try {
                    //if(fahrenheit) {
                    JSONObject JSONtemp = new JSONObject(currentJSON.getJSONObject("main").toString());

                    String temp = JSONtemp.get("temp").toString();
                    String feelsLike = JSONtemp.get("feels_like").toString();

                    JSONArray weatherJSON = currentJSON.getJSONArray("weather");

                    JSONObject weatherObject = weatherJSON.getJSONObject(0);
                    String weather = weatherObject.get("main").toString();
                    //Log.d("weather", weather);
                    //possible weather situations: Snow, Clouds, Clear, Drizzle, Rain, Extreme
                    String imageURL = ("http://openweathermap.org/img/w/");
                    String urlEnd = (".png");


                    JSONObject iconObject = weatherJSON.getJSONObject(0);
                    String icon = iconObject.get("icon").toString();

                    String main = iconObject.get("main").toString();

                    switch (main) {
                        case "Rain":
                            currentDescription.setText("It's raining like it does on Hateno's coast");
                            break;
                        case "Clear":
                            currentDescription.setText("It's as clear as the skies of Gerudo");
                            break;
                        case "Snow":
                            currentDescription.setText("It's snowing like the Hebra mountains");
                            break;
                        case "Clouds":
                            currentDescription.setText("The skies are cloudy like in the Great Plains");
                            break;
                        case "Drizzle":
                            currentDescription.setText("It's drizzling like in the Lanayru Wetlands");
                            break;
                        case "Thunderstorm":
                            currentDescription.setText("It's thunderstorming like on the Lanayru Seas");
                            break;
                        case "Haze":
                            currentDescription.setText("It's as hazy as Korok forest");
                            break;
                        case "Fog":
                            currentDescription.setText("it's as foggy as the Great Springs");
                            break;
                        case "Sand":
                            currentDescription.setText("It's sandier than the Gerudo desert");
                            break;
                        case "Dust":
                            currentDescription.setText("It's as dusty as Hyrule Castle");
                            break;
                        case "Ash":
                            currentDescription.setText("It's as ashy as Eldin volcano");
                            break;
                        case "Mist":
                            currentDescription.setText("It's as misty as the deep Faron woods");
                        default:
                            currentDescription.setText("Weather unknown");
                    }

                    URL conditionURL = new URL(imageURL + icon + urlEnd);
                    //Log.d("link", conditionURL.toString());
                    Picasso.with(MainActivity.this).load(conditionURL.toString()).into(currentWeatherImage);

                    Log.d("DoubleTemp", temp);
                    Log.d("FeelsLike", feelsLike);

                    JSONArray list = forecastJSON.getJSONArray("list");

                    ArrayList<String> allDates = new ArrayList<>();
                    ArrayList<String> dates = new ArrayList<>();

                    ArrayList<Double> allMins = new ArrayList<>();
                    ArrayList<Double> mins = new ArrayList<>();

                    ArrayList<Double> allMaxes = new ArrayList<>();
                    ArrayList<Double> maxes = new ArrayList<>();

                    ArrayList<String> allIcons = new ArrayList<>();
                    ArrayList<String> icons = new ArrayList<>();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject obj = list.getJSONObject(i);

                        JSONArray array = obj.getJSONArray("weather");
                        JSONObject object = array.getJSONObject(0);
                        String iconString = object.get("icon").toString();

                        JSONObject mainWeather = obj.getJSONObject("main");

                        String date = obj.get("dt_txt").toString();

                        allDates.add(date);
                        allMaxes.add(new Double((mainWeather.get("temp_min")).toString()));
                        allMins.add(new Double((mainWeather.get("temp_max")).toString()));
                        allIcons.add(iconString);
                    }


                    int start = 0;
                    boolean keepRunning = true;
                    int n = 0;


                    while (keepRunning) {
                        String date = list.getJSONObject(n).get("dt_txt").toString();
                        if (date.contains("12:00:00")) {
                            start = n;
                            keepRunning = false;
                        }
                        n++;
                    }

                    Log.d("allStats", allMins + " + " + allMaxes);
                    for (int i = start; i < 40; i += 8) {
                        icons.add(allIcons.get(i));
                        dates.add(allDates.get(i));
                        maxes.add(allMaxes.get(i));
                        mins.add(allMins.get(i));
                    }
                    // Making sure only the list is filled with only dates, not times as well
                    for (int i = 0; i < dates.size(); i++) {
                        String[] commaSplitter = dates.get(i).split(" ");
                        dates.set(i, commaSplitter[0]);
                    }
                    Log.d("Dates", dates.get(0));

                    Log.d("TEMP", dates.toString());
                    Log.d("MAX", maxes.toString());
                    Log.d("MIN", mins.toString());

                    for (int i = 0; i < 5; i++) {

                        weatherPrediction prediction = new weatherPrediction(dates.get(i), icons.get(i), (int) Math.round(maxes.get(i)), (int) Math.round(mins.get(i)));
                        Log.d("weatherpredictionlist", prediction.getDate() + " " + prediction.getHigh() + " " + prediction.getLow());
                        predictionsList.add(prediction);
                    }

                    Log.d("size", predictionsList.size() + "");

                    Date date = new Date();
                    String dateFormat = "dd-MMM-yyyy";
                    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                    String dateString = format.format(date);

                    //WIDGET ASSIGNMENT
                    currentTemp.setText( Math.round(new Double(temp)) + "");
                    feelsLikeTemp.setText(Math.round(new Double(feelsLike)) + "");
                    currentDate.setText(reformat(dateString));
                    dateTitle.setText(getMonthAndYear(reformat(dateString)));
                    Log.d("Date", reformat(dateString));

                    JSONArray weatherArr = currentJSON.getJSONArray("weather");
                    JSONObject weatherDescription = weatherArr.getJSONObject(0);
                    String description = weatherDescription.get("description").toString();
                    String capitalDescription = description.substring(0, 1).toUpperCase() + description.substring(1);
                    //currentDescription.setText(capitalDescription);

                    ArrayAdapter adapter = (ArrayAdapter) weatherPredictions.getAdapter();
                    adapter.notifyDataSetChanged();

                    //set visibility back
                    imgBtn.setVisibility(View.VISIBLE);
                    textF.setVisibility(View.VISIBLE);
                    textC.setVisibility(View.VISIBLE);
                    aSwitch.setVisibility(View.VISIBLE);
                    currentTemp.setVisibility(View.VISIBLE);
                    currentDate.setVisibility(View.VISIBLE);
                    feelsLikeTemp.setVisibility(View.VISIBLE);
                    feelLike.setVisibility(View.VISIBLE);
                    currentDescription.setVisibility(View.VISIBLE);
                    dateTitle.setVisibility(View.VISIBLE);
                    minTitle.setVisibility(View.VISIBLE);
                    maxTitle.setVisibility(View.VISIBLE);

                }
                catch (JSONException e) { e.printStackTrace();
                }
                catch(NetworkOnMainThreadException e) {
                    Log.d("NetworkOnMainException", e.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else { //Call was invalid
                Toast toast = Toast.makeText(MainActivity.this, "Please enter a valid location", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
    public class weatherPrediction {
        String date;
        String iconCode;
        int high;
        int low;
        public weatherPrediction(String date, String iconCode, int high, int low) {
            this.date = date;
            this.iconCode = iconCode;
            this.high = high;
            this.low = low;
        }
        public String getDate() {
            return date;
        }
        public String getIconCode() {
            return iconCode;
        }
        public int getHigh() {
            return high;
        }
        public int getLow() {
            return low;
        }

    }


    public class CustomAdapter extends ArrayAdapter<weatherPrediction> {
        List<weatherPrediction> list;
        Context context;
        int xmlResource;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<weatherPrediction> objects) {
            super(context, resource, objects);
            this.context = context;
            xmlResource = resource;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final View adapterView = inflater.inflate(xmlResource, null);

            TextView date = adapterView.findViewById(R.id.date);
            TextView highTemp = adapterView.findViewById(R.id.highTemp);
            TextView lowTemp = adapterView.findViewById(R.id.lowTemp);
            ImageView imageView = adapterView.findViewById(R.id.imageView);


            weatherPrediction currentObject = list.get(position);

            //FUNCTIONALITY

           // date.setText(reformat(dateStr));
            date.setText(getDay(currentObject.getDate()));
            highTemp.setText(currentObject.getHigh() + "");
            lowTemp.setText(currentObject.getLow() + "");
            URL conditionURL = null;
            try {
                conditionURL = new URL("http://openweathermap.org/img/w/" + currentObject.getIconCode() + ".png");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Picasso.with(MainActivity.this).load(conditionURL.toString()).into(imageView);
            return adapterView;
        }
    }
    public static double kelvinToFahrenheit(double temperature){
        temperature = ((temperature - 273.15) * (9/5) + 32);
        return temperature;
    }
    public static double kelvinToCelsius(double temperature) {
        temperature = temperature - 273.15;
        return temperature;
    }
    public static double celsiusToFahrenheit(double temperature) {
        temperature = temperature;
        return temperature;
    }
    public static double fahrenheitToCelsius(double temperature) {
        temperature = temperature;
        return temperature;
    }

    //LOCATION METHODS

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    public String reformat(String dateStr) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;

        try {
            date = initialFormat.parse(dateStr);
        }
        catch (ParseException e) {
            Log.d("exception", "reformat method parsing error");
        }
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        String newDate = format.format(date);
        return newDate;

        //return format.format();
    }
    public String getDay(String date) {
        String[] arr = date.split("-");
        return arr[2];
    }
    public String getYear(String date) {
        String[] arr = date.split("-");
        return arr[0];
    }
    public String getMonthAndYear(String date) {
        String[] arr = date.split(" ");
        String month = arr[0];
        String year = arr[2];
        return (month.toUpperCase() + " " + year);
    }
}
