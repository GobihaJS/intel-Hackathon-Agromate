package com.example.agromate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class process extends Fragment {

    private FrameLayout frameLayout;
    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process, container, false);

        // Initialize views
        frameLayout = view.findViewById(R.id.frame);
        calendarView = view.findViewById(R.id.calendarView);

        // Set date change listener for CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (dayOfMonth == 30) {
                    Log.d("TAG", "Day 22 is selected");
                    showAlertDialog();
                }
            }
        });

        // Find all ProgressBar views
        ProgressBar progressBar1 = view.findViewById(R.id.progressBar);
        ProgressBar progressBar2 = view.findViewById(R.id.progressBar1);
        ProgressBar progressBar3 = view.findViewById(R.id.progressBar2);
        ProgressBar progressBar4 = view.findViewById(R.id.progressBar3);

        // Check the progress values
        int progress1 = progressBar1.getProgress();
        int progress2 = progressBar2.getProgress();
        int progress3 = progressBar3.getProgress();
        int progress4 = progressBar4.getProgress();

        // Create a StringBuilder to build the alert message
        StringBuilder messageBuilder = new StringBuilder();

        // Check each progress value and append relevant message to the StringBuilder
        if (progress1 < 20) {
            messageBuilder.append("Water needed.\n");
        }
        if (progress2 < 20) {
            messageBuilder.append("Nitrogen needed.\n");
        }
        if (progress3 < 20) {
            messageBuilder.append("Phosphorus needed.\n");
        }
        if (progress4 < 20) {
            messageBuilder.append("Potassium needed.\n");
        }

        // Check if any alert message is created
        if (messageBuilder.length() > 0) {
            // Create an AlertDialog for showing the alert message
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Fertilizer Needed");
            builder.setMessage(messageBuilder.toString().trim());

            // Add an OK button to dismiss the dialog
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return view;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Alert")
                .setMessage("Small, water-soaked spots on leaves, stems, and fruits. Spots may become brown and scabby.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Show solution
                        showSolutionDialog();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSolutionDialog() {
        // Create a new AlertDialog for showing the solution
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Solution")
                .setMessage("- Planting resistant tomato varieties. - Cultural practices: Proper spacing, avoid overhead watering,aqs")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog solutionDialog = builder.create();
        solutionDialog.show();
    }

    private void showSolution() {
        // Implement your logic to show the solution here
        Toast.makeText(requireContext(), "Showing solution...", Toast.LENGTH_SHORT).show();
        // You can replace the Toast with the actual solution display logic
    }
}
