package com.kdkvit.wherewasi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.kdkvit.wherewasi.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FiltersFragment extends Fragment {

    interface FiltersCallback{
        void onClear();
        void onFilter(Date start,Date end,int minTime,boolean onlyInteractions);
    }

    private View rootView;

    private Date startTime;
    private Date endTime;
    private int minDuration;
    private boolean interactions;

    private FiltersCallback callback;


    public FiltersFragment(FiltersCallback callback) {
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_filters, container, false);

        LinearLayout startTimeContainer = rootView.findViewById(R.id.filter_start_time_container);
        final TextView startTimeTV= rootView.findViewById(R.id.filter_start_time);
        startTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                if(startTime!=null) {
                    builder.setSelection(startTime.getTime());
                }
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                builder.setCalendarConstraints(constraintsBuilder.build());

                MaterialDatePicker<Long> picker = builder.build();

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selectedDate) {
                        // user has selected a date
                        // format the date and set the text of the input box to be the selected date
                        // right now this format is hard-coded, this will change
                        ;
                        // Get the offset from our timezone and UTC.
                        startTime = new Date(selectedDate);
                        startTimeTV.setText(new SimpleDateFormat("dd/MM/yyyy").format(startTime));
                    }
                });

                picker.show(getFragmentManager(), picker.toString());

            }
        });

        LinearLayout endTimeContainer = rootView.findViewById(R.id.filter_end_time_container);
        final TextView endTimeTV= rootView.findViewById(R.id.filter_end_time);
        endTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                if(endTime!=null) {
                    builder.setSelection(endTime.getTime());
                }
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                builder.setCalendarConstraints(constraintsBuilder.build());

                MaterialDatePicker<Long> picker = builder.build();

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selectedDate) {
                        // user has selected a date
                        // format the date and set the text of the input box to be the selected date
                        // right now this format is hard-coded, this will change
                        ;
                        // Get the offset from our timezone and UTC.
                        endTime = new Date(selectedDate);
                        endTimeTV.setText(new SimpleDateFormat("dd/MM/yyyy").format(endTime));
                    }
                });

                picker.show(getFragmentManager(), picker.toString());
            }
        });

        final EditText durationEditText = rootView.findViewById(R.id.duration_input);
        final CheckBox interactionsCB = rootView.findViewById(R.id.interactions_checkbox);

        Button filterBtn = rootView.findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String duration = durationEditText.getText().toString().trim();
                if(!duration.isEmpty()){
                    try{
                        minDuration = Integer.parseInt(duration);
                    }catch (NumberFormatException e){
                        durationEditText.setText("");
                    }
                }

                interactions = interactionsCB.isChecked();

                callback.onFilter(startTime, endTime, minDuration, interactions);
            }
        });

        Button clearBtn = rootView.findViewById(R.id.filter_clear_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = null;
                endTime = null;
                minDuration = 0;
                interactions = false;

                //reset UI
                callback.onClear();
            }
        });

        return rootView;
    }
}