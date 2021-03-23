package com.kdkvit.wherewasi.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import actions.actions;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import models.User;

import android.transition.CircularPropagation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.kdkvit.wherewasi.R;
import com.kdkvit.wherewasi.utils.SharedPreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static actions.actions.sendPositive;


public class ExportFragment extends Fragment {


    private View rootView;

    public ExportFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_export, container, false);

        CircularProgressButton exportBtn = rootView.findViewById(R.id.export_data);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable d = getContext().getDrawable(R.drawable.ic_done);

                Bitmap icon = drawableToBitmap(d);

                exportBtn.startAnimation();
                exportBtn.doneLoadingAnimation(Color.parseColor("#249ff0"),icon);
            }
        });

        CircularProgressButton markPositiveBtn = rootView.findViewById(R.id.mark_positive_btn);
        markPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();

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
                        markPositiveBtn.startAnimation();
                        sendPositive(getContext(), selectedDate, new actions.ActionsCallback() {
                            @Override
                            public void onSuccess() {
                                if(getContext()!=null) {
                                    Toast.makeText(getContext(), "Thank you for reporting!", Toast.LENGTH_SHORT).show();
                                    Drawable d = getContext().getDrawable(R.drawable.ic_done);

                                    Bitmap icon = drawableToBitmap(d);

                                    markPositiveBtn.doneLoadingAnimation(Color.parseColor("#249ff0"),icon);
                                }
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContext(), "Something went wrong, please try again...", Toast.LENGTH_SHORT).show();
                                markPositiveBtn.revertAnimation();
                            }
                        });
                    }
                });

                picker.show(getFragmentManager(), picker.toString());
            }
        });

        return rootView;
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);



        return bitmap;
    }
}