package com.wpavelev.museneurofeedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.choosemuse.libmuse.Muse;

import java.util.List;


public class MuseConnectFragment extends Fragment implements View.OnClickListener{

    private ArrayAdapter<String> spinnerAdapter;

    View view;

    public MuseConnectFragment() {
        // Required empty public constructor
    }


    public void museListChanged() {
        final List<Muse> list = manager.getMuses();
        spinnerAdapter.clear();
        for (Muse m : list) {
            spinnerAdapter.add(m.getName());
        }
        ImageButton button = view.findViewById(R.id.connect);
        button.setColorFilter(getResources().getColor(R.color.lightgreen));
    }


    private void addAdapterItem(String s) {
        spinnerAdapter.add(s);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_muse_connect, container, false);

        ImageButton refreshImageButton = view.findViewById(R.id.refresh);
        refreshImageButton.setOnClickListener(this);
        ImageButton connectImageButton = view.findViewById(R.id.connect);
        connectImageButton.setOnClickListener(this);


        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        Spinner musesSpinner = view.findViewById(R.id.muse_spinner);
        musesSpinner.setAdapter(spinnerAdapter);

        return view;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.refresh:

                break;

            case R.id.connect:

                break;

        }


    }
}
