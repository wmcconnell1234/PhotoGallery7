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
import java.io.File;
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
import Utils2.*; //Utility class containing helpful functions for Photo Gallery app
import SearchUtil.*; //Utility class containing search function for Photo Gallery app

public class MainActivity extends AppCompatActivity
{
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    private static final int BLANK_SCREEN = -1; //used with Go() function to tell it to go to a blank screen
    /////////////////////////////////////IL
    private String returnStartTime;     // 2 global variables that stores the time from 2nd activity
    private String returnEndTime;
    /////////////////////////////////////IL
    private String mCurrentPhotoPath;
    private String currentFileName = null; //only used to save the filename of the new picture to add to file name list
    private Date CurrentDate = null;       //only used to save the date of the new picture to add to date list
    //Master lists. These are used to keep track of all files, captions, and dates.
    private List captionListM = new ArrayList();
    private List filenameListM = new ArrayList();
    private List dateListM = new ArrayList<Date>();
    //Filtered lists. These are used to keep track of which content is to be displayed.
    private List captionListF = new ArrayList();
    private List filenameListF = new ArrayList();
    private List dateListF = new ArrayList<Date>();
    //The element number of the current image. Refers to the element number in the FILTERED list.
    private int currentElement = 0;
    //Instantiate the utility classes that provide helpful functions for this app
    private Utils2 U = new Utils2();
    private SearchUtil S = new SearchUtil();
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
            String lat1 = null;
            String lng1 = null; //these will hold the user-entered latitude and longitude
            String lat2 = null; String lng2 = null; //depending on the search logic these two might not be needed
            //Clear filtered lists in preparation for receiving the results from Search()
            filenameListF.clear();
            captionListF.clear();
            dateListF.clear();
            //Search!!
            List result = new ArrayList();
            result = S.Search(MainActivity.this,get_caption,returnStartTime,returnEndTime,lat1,lng1,lat2,lng2,captionListM,dateListM);
            //Unpack result
            filenameListF = (List) result.get(0);
            captionListF = (List) result.get(1);
            dateListF = (List) result.get(2);
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
    public void Go(int element)
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

