package com.example.happy_mountain.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happy_mountain.adapter.ForestPointAdapter;
import com.example.happy_mountain.BuildConfig;
import com.example.happy_mountain.R;
import com.example.happy_mountain.databinding.FragmentForestPointBinding;
import com.example.happy_mountain.item.ForestPointItem;
import com.example.happy_mountain.model.ForestPointModel;
import com.example.happy_mountain.model.WeatherModel;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ForestPointFragment extends Fragment {
    private String baseURL = "https://apis.data.go.kr/1400377/forestPoint/forestPointListSidoSearch";
    private final String serviceKey = BuildConfig.MOUNTAIN_POINT_API_KEY;

    private Spinner areaSpinner;
    private TextView yearMonth;
    private String localAreas;
    private RecyclerView recyclerView;
    private ForestPointModel forestPointModel;

    public ForestPointFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forestPointModel = new ViewModelProvider(requireActivity()).get(ForestPointModel.class);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentForestPointBinding binding = FragmentForestPointBinding.inflate(inflater, container, false);
        areaSpinner = binding.areaSpinner;
        yearMonth = binding.yearMonth;
        recyclerView = binding.forestPointRecycler;
        recyclerView.setHasFixedSize(true);
        ForestPointAdapter forestPointAdapter = new ForestPointAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(forestPointAdapter);

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner));
        areaSpinner.setAdapter(spinnerAdapter);

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        localAreas = "11";
                        break;
                    case 1:
                        localAreas = "26";
                        break;
                    case 2:
                        localAreas = "27";
                        break;
                    case 3:
                        localAreas = "28";
                        break;
                    case 4:
                        localAreas = "29";
                        break;
                    case 5:
                        localAreas = "30";
                        break;
                    case 6:
                        localAreas = "31";
                        break;
                    case 7:
                        localAreas = "36";
                        break;
                    case 8:
                        localAreas = "41";
                        break;
                    case 9:
                        localAreas = "42";
                        break;
                    case 10:
                        localAreas = "43";
                        break;
                    case 11:
                        localAreas = "44";
                        break;
                    case 12:
                        localAreas = "45";
                        break;
                    case 13:
                        localAreas = "46";
                        break;
                    case 14:
                        localAreas = "47";
                        break;
                    case 15:
                        localAreas = "48";
                        break;
                    case 16:
                        localAreas = "50";
                        break;
                }

                loadPointAPI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                areaSpinner.setVerticalScrollbarPosition(0);
                localAreas = "11";
            }
        });

        return binding.getRoot();
    }

    public void loadPointAPI() {
        new Thread(() -> {
            try {
                String urlBuilder = "https://www.data.go.kr/catalog/15092027/fileData.json";
                Log.i("[ForestPoint]", "URL: " + urlBuilder);

                URL url = new URL(urlBuilder);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                Log.i("[ForestPoint]", "Response code: " + conn.getResponseCode());

                BufferedReader bufferedReader;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                conn.disconnect();
                jsonParsing(stringBuilder.toString());
            } catch (IOException e) {
                Log.i("[ForestPoint] ERROR : ", e.toString());
            }
        }).start();
    }

    public void jsonParsing(String jsonString) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -2);
            Date checkDate = cal.getTime();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray distributionArray = jsonObject.getJSONArray("distribution");
            JSONObject distributionObject = distributionArray.getJSONObject(0);
            String contentUrl = distributionObject.getString("contentUrl");
            Log.i("ForestPoint", contentUrl);

            URL url = new URL(contentUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream, "EUC-KR"));
            List<String[]> contents = reader.readAll();
            for (int i = 1; i < contents.size(); i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Date compDate = dateFormat.parse(contents.get(i)[0]);
                    if (Objects.requireNonNull(compDate).compareTo(checkDate) > 0) {
                        Log.i("ForestPoint", "content : " + Arrays.toString(contents.get(i)));
                        forestPointModel.add(new ForestPointItem(contents.get(i)[0], contents.get(i)[1],
                                contents.get(i)[1] + contents.get(i)[2] + contents.get(i)[3],
                                contents.get(i)[4], contents.get(i)[5], contents.get(i)[6]));
                    }
                }
            }

        } catch (JSONException | IOException | CsvException | ParseException e) {
            e.printStackTrace();
        }
    }
}