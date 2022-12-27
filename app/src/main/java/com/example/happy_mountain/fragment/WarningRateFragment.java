package com.example.happy_mountain.fragment;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happy_mountain.adapter.WarningRateAdapter;
import com.example.happy_mountain.R;
import com.example.happy_mountain.databinding.FragmentWarningRateBinding;
import com.example.happy_mountain.item.WarningRateItem;
import com.example.happy_mountain.model.WarningRateModel;
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
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WarningRateFragment extends Fragment {

    private TextView dateView;
    private RecyclerView recyclerView;
    ArrayAdapter<String> spinnerAdapter;

    WarningRateModel warningRateModel;
    List<WarningRateItem> warningRateItems;

    public WarningRateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        warningRateModel = new ViewModelProvider(requireActivity()).get(WarningRateModel.class);
        spinnerAdapter =
                new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner));
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.example.happy_mountain.databinding.FragmentWarningRateBinding binding = FragmentWarningRateBinding.inflate(inflater, container, false);
        Spinner areaSpinner = binding.areaSpinner;
        dateView = binding.dateView;
        recyclerView = binding.warningRateRecycler;

        warningRateModel.getWarningRateList().observe(requireActivity(), items -> {
            warningRateItems = items;
            WarningRateAdapter warningRateAdapter = new WarningRateAdapter(items);
            requireActivity().runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(warningRateAdapter);
            });
        });
        warningRateModel.getDateInfo().observe(requireActivity(), item -> dateView.setText(item));

        updateArea("전국");
        areaSpinner.setAdapter(spinnerAdapter);
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String area;
                switch (position) {
                    case 0:
                        area = "전국";
                        break;
                    case 1:
                        area = "서울";
                        break;
                    case 2:
                        area = "부산";
                        break;
                    case 3:
                        area = "대구";
                        break;
                    case 4:
                        area = "인천";
                        break;
                    case 5:
                        area = "광주";
                        break;
                    case 6:
                        area = "대전";
                        break;
                    case 7:
                        area = "울산";
                        break;
                    case 8:
                        area = "세종";
                        break;
                    case 9:
                        area = "경기";
                        break;
                    case 10:
                        area = "강원";
                        break;
                    case 11:
                        area = "충북";
                        break;
                    case 12:
                        area = "충남";
                        break;
                    case 13:
                        area = "전북";
                        break;
                    case 14:
                        area = "전남";
                        break;
                    case 15:
                        area = "경북";
                        break;
                    case 16:
                        area = "경남";
                        break;
                    default:
                        area = "제주";
                        break;
                }

               updateArea(area);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateArea(String area) {
        if (warningRateItems != null) {
            List<WarningRateItem> areaWarningRateItems = new ArrayList<>();

            for (int i = 0; i < warningRateItems.size(); i++) {
                if (warningRateItems.get(i).getArea().equals(area) || area.equals("전국")) {
                    areaWarningRateItems.add(warningRateItems.get(i));
                }
            }
            Log.i("WarningRate", "size : " + areaWarningRateItems.size());
            WarningRateAdapter warningRateAdapter = new WarningRateAdapter(areaWarningRateItems);
            requireActivity().runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(warningRateAdapter);
            });
        }
    }
}