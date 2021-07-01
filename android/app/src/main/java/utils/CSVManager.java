package utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import models.Interaction;
import models.MyLocation;

public class CSVManager {

    Context context;

    public CSVManager(Context context) {
        this.context = context;
    }

    public void exportLocations(List<MyLocation> locationList) {

        StringBuilder data = new StringBuilder();
        // Adding File Header
        data.append("ID,Latitude,Longitude,Provider,startTime,endTime,accuracy,adminArea,countryCode,featureName,Locality,subAdminArea,addressLine,Interactions");

        // Adding Data
        for(MyLocation location : locationList) {
           data.append("\n"
           +location.getId()+","
           +location.getLatitude()+","
           +location.getLongitude()+","
           +location.getProvider()+","
           +location.getStartTime()+","
           +location.getEndTime()+","
           +location.getAccuracy()+","
           +location.getAdminArea()+","
           +location.getCountryCode()+","
           +location.getFeatureName()+","
           +location.getLocality()+","
           +location.getSubAdminArea()+","
           +location.getAddressLine()+","
           +location.getNumOfInteractions());
        }

        try{
            // Saving file onto device
            FileOutputStream out = context.openFileOutput("locations_export.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            // Exporting
            File fileLocation = new File(context.getFilesDir(), "locations_export.csv");
            Uri path = FileProvider.getUriForFile(context, "com.kdkvit.wherewasi.provider", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            context.startActivity(Intent.createChooser(fileIntent, "Export CSV"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
