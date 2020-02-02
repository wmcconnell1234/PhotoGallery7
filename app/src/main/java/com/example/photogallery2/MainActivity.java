//To get a picture with the GPS info in the Exif Tags, do the following:
//- enable Location permissions for phone
//- enable Location permissions for camera app
//- in the camera app, go to Settings and enable Save Location
//- either go outside and wait until you get a GPS signal, or connect to wifi
//- take a picture using this app
//If a picture does not have the GPS info in the Exif Tags, "no location info" is displayed

package com.example.photogallery2;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList; //WM
import java.util.List; //WM
////////////////////// IL
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
////////////////////// IL
import Util.*; //Utility class containing helpful functions for Photo Gallery app

public class MainActivity extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    static final int BLANK_SCREEN = -1; //used with Go() function to tell it to go to a blank screen
    /////////////////////////////////////IL
    String returnStartTime;     // 2 global variables that stores the time from 2nd activity
    String returnEndTime;
    /////////////////////////////////////IL
    String mCurrentPhotoPath;
    String currentFileName = null; //only used to save the filename of the new picture to add to file name list
    Date CurrentDate = null;       //only used to save the date of the new picture to add to date list
    //Master lists. These are used to keep track of all files, captions, and dates.
    List captionListM = new ArrayList();
    List filenameListM = new ArrayList();
    List dateListM = new ArrayList<Date>();
    //Filtered lists. These are used to keep track of which content is to be displayed.
    List captionListF = new ArrayList();
    List filenameListF = new ArrayList();
    List dateListF = new ArrayList<Date>();
    //The element number of the current image. Refers to the element number in the FILTERED list.
    int currentElement = 0;
    //Instantiate the utility class that provides helpful functions for this app
    private Util U = new Util();

    //============================================================================================================================

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));
    }
    //============================================================================================================================

    private List populateGallery() {         // getting photos from storage on phone, put them in to the photo gallery
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery2/files/Pictures"); // put in our project name then it should work
        File[] fList = file.listFiles();
        List fl = new ArrayList();
        if (fList != null) {
            for (File f : file.listFiles()) {
                fl.add(f.getName());
            }
        }
        return fl;
    }
    //============================================================================================================================

    @Override
    public void onResume() {
        super.onResume();
    }

    //============================================================================================================================

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //On first run, create files to save captions and dates. Also get the filenames.
        File captionFile = U.GetFile(MainActivity.this, "captions");
        File dateFile = U.GetFile(MainActivity.this, "dates");
        //Populate master lists from files
        filenameListM = populateGallery();
        captionListM = U.PopulateList(captionFile);
        dateListM = U.PopulateList(dateFile);
        //Clear filters
        filenameListF = U.copy(filenameListM);
        captionListF = U.copy(captionListM);
        dateListF = U.copy(dateListM);
        //Go to the first picture, if there is one
        if (filenameListF.size() > 0)
            Go(0);
        //Else, go to blank screen
        else
            Go(BLANK_SCREEN);
    }
    //============================================================================================================================

    public void search(View view) {
        Intent intent = new Intent(this, Search.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }
    //============================================================================================================================

    public void takePicture(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.photogallery2.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    //============================================================================================================================

    public File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", mCurrentPhotoPath);
        currentFileName = image.getName(); //Added WM to get the filename, for adding to filenameList.
        CurrentDate = new Date(image.lastModified());//for adding to dateList.
        return image;
    }
    //============================================================================================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Do this if user took a picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            //Update master lists with the new picture
            filenameListM.add(currentFileName);
            captionListM.add("Enter Caption");
            dateListM.add(CurrentDate);
            //Write master caption and date lists to files
            U.SaveToFile(MainActivity.this, captionListM, "captions");
            U.SaveToFile(MainActivity.this, dateListM, "dates");
            //Clear filters
            filenameListF = U.copy(filenameListM);
            captionListF = U.copy(captionListM);
            dateListF = U.copy(dateListM);
            //Go to the new picture
            Go(filenameListF.size()-1);
        }//end do this if user took a picture

        //Do this if user searched
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            // getting the result back from 2nd activity
            String get_caption = data.getStringExtra("CAPTION");
            returnStartTime = data.getStringExtra("STARTDATE");
            returnEndTime = data.getStringExtra("ENDDATE");
            //Initialize date formats
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            SimpleDateFormat sdfUser = new SimpleDateFormat("yyyyMMdd_HHmmss");
            ParsePosition parsepos = new ParsePosition(0);
            //Set up the search criteria for date
            Date dStartTime;
            Date dEndTime;
            //if one or both dates were left empty, set the search criteria for date so that all image dates are accepted
            if (returnStartTime.isEmpty() || returnEndTime.isEmpty())
            {
                String minDate = "00010101_000000";
                String maxDate = "20500505_000000";
                Date minDateD = sdfUser.parse(minDate,parsepos);
                parsepos.setIndex(0);
                Date maxDateD = sdfUser.parse(maxDate,parsepos);
                parsepos.setIndex(0);
                dStartTime = minDateD;
                dEndTime = maxDateD;
            }
            //Else, set the search criteria for date to the user-entered values
            ////////////////////////////////////////////////////////////////////////
            ////////     the user input the date in a simple format that can't be compared with the format of the list
            ////          so we get the "simple format" string,
            else {
                dStartTime = sdfUser.parse(returnStartTime, parsepos);
                parsepos.setIndex(0);
                dEndTime = sdfUser.parse(returnEndTime, parsepos);
                parsepos.setIndex(0);
            }
            //Clear filtered lists in preparation for regenerating them
            filenameListF.clear();
            captionListF.clear();
            dateListF.clear();
            //Loop through all files, add the ones that match the search criteria to the filtered lists
            int cap_index = 0;
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/Android/data/com.example.photogallery2/files/Pictures");
            for (File fwm : file.listFiles()) {
                String str = captionListM.get(cap_index).toString();
                if (str.contains(get_caption)) {       ////////////////   comparsion
                    Date d1 = new Date();
                    d1 = sdf.parse(dateListM.get(cap_index).toString(), parsepos);
                    parsepos.setIndex(0);
                    if (d1.compareTo(dStartTime) > 0 && d1.compareTo(dEndTime) < 0) {
                        filenameListF.add(fwm.getName());
                        captionListF.add(captionListM.get(cap_index).toString());
                        dateListF.add(dateListM.get(cap_index).toString());
                    }
                }
                cap_index++;
            }//end for
            //If the search is cleared, re-enable the snap button
            Button button = findViewById(R.id.btnSnap);
            if(get_caption.isEmpty())
                button.setClickable(true);
            //Otherwise disable the snap button. To prevent crashes
            else
                button.setClickable(false);
            //If the search returned something, go to the first image in the filtered list
            if(captionListF.size() != 0)
                Go(0);
            else //go to a blank screen
                Go(BLANK_SCREEN);
        }//end do this if user searched
    }
    //============================================================================================================================

    public void saveCaption(View view)
    {
        if(mCurrentPhotoPath != null) //If there is an image in the imageview
        {
            //Get the caption
            TextView textView = (TextView) findViewById(R.id.editTextCaption);
            String caption = textView.getText().toString();
            //Change the caption for the current image in the filtered list
            captionListF.set(currentElement, caption);
            //Change the caption for the current image in the master list
            String filename = filenameListF.get(currentElement).toString();
            for(int i = 0; i < filenameListM.size(); i++)
            {
                if(filenameListM.get(i).toString().contains(filename))
                {
                    captionListM.set(i, caption);
                    i = filenameListM.size();//exit loop
                }
            }
            //Update caption file
            U.SaveToFile(MainActivity.this, captionListM, "captions");
        }
    }
    //============================================================================================================================

    //Move to the newer image
    public void Left(View view)
    {
        //See if the number of images is greater than 1.
        int filenameListSize = filenameListF.size();
        if(filenameListSize > 1)
        {
            //See if the current image is an older image.
            //The current image is an older image if currentElement is not the last element number.
            if(currentElement != (filenameListSize - 1))
                Go(currentElement+1); //Go to the newer image
        }
    }
    //============================================================================================================================

    //Display the older image
    public void Right(View view)
    {
        //See if the number of images is greater than 1.
        int filenameListSize = filenameListF.size();
        if(filenameListSize > 1)
        {
            //See if the current image is a newer image.
            //The current image is a newer image if currentElement is not the first element number.
            if(currentElement != 0)
                Go(currentElement-1); //Go to the newer image
        }
    }
    //============================================================================================================================

    //Goes to the specified element in the filtered list. -1 means go to blank screen.
    private void Go(int element)
    {
        if(element != BLANK_SCREEN)
        {
            //1. Specify that the given image is the current image
            currentElement = element;
            //2. Set the current filename to the filename of the given image
            mCurrentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+filenameListF.get(currentElement).toString();
            //3. Display the given image
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            //4. Set the caption to the caption of the given image
            TextView textView = (TextView) findViewById(R.id.editTextCaption);
            textView.setText((CharSequence) captionListF.get(currentElement));
            //5. Set the date to the date of the given image
            TextView textViewforDate = findViewById(R.id.DatetextView);
            textViewforDate.setText((CharSequence) dateListF.get(currentElement).toString());
            //6. Set the location information to the location information of the given image
            float[] f = {0,0};
            boolean result = false;
            try {
                String filename = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+filenameListF.get(currentElement).toString();
                ExifInterface exif = new ExifInterface(filename);
                result = exif.getLatLong(f);
            }
            catch (IOException e) { }
            TextView tv = (TextView) findViewById(R.id.gpsTextView);
            if(result == true)
                tv.setText(String.valueOf(f[0]) + " " + String.valueOf(f[1]));
            else
                tv.setText("No location information");
        }
        else //Go to blank screen
        {
            //1. There is no current element so leave currentElement as is
            //2. Set the current filename to null
            mCurrentPhotoPath = null;
            //3. Display nothing
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageDrawable(null);
            //4. Set the caption to "no files found"
            TextView textView = (TextView) findViewById(R.id.editTextCaption);
            textView.setText("No files found");
            //5. Set the date to "No date information"
            TextView textViewforDate = findViewById(R.id.DatetextView);
            textViewforDate.setText("No date information");
            //6. Set the location information to "No location information"
            TextView textViewforLocation = findViewById(R.id.gpsTextView);
            textViewforLocation.setText("No location information");
        }
    }
    //============================================================================================================================

}//end MainActivity

