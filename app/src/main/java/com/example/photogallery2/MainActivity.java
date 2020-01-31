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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
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

public class MainActivity extends AppCompatActivity
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    String mCurrentPhotoPath;
    /////////////////////////////////////IL
    String returnStartTime;     // 2 global variables that stores the time from 2nd activity
    String returnEndTime;
    int a =0;
    /////////////////////////////////////IL
    String currentFileName = null; //similar to mCurrentPhotoPath, but the filename only. WM
    List captionList = new ArrayList(); //contains all the captions. WM
    List filenameList = new ArrayList(); //contains all the filenames. WM
    int currentElement = 0; //The element number of the current image. WM
    Date CurrentDate = null;
    List dateList = new ArrayList<Date>();

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    private List populateGallery(Date minDate, Date maxDate) {         // getting photos from storage on phone, put them in to the photo gallery
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery2/files/Pictures"); // put in our project name then it should work
        filenameList = new ArrayList();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : file.listFiles()) {
                filenameList.add(f.getName());
            }
        }
        return filenameList;
    }//end populateGallery

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date minDate = new Date(Long.MIN_VALUE); // show all the photo, from min date to max date
        Date maxDate = new Date(Long.MAX_VALUE);
        filenameList = populateGallery(minDate, maxDate);  // pupolateGallery take all the pictures, put into photoGallery ( array of filenames)

        //Initialize textViews for date and location
        TextView tv = (TextView) findViewById(R.id.DatetextView);
        tv.setText("No date information");
        tv = (TextView) findViewById(R.id.gpsTextView);
        tv.setText("No location information");

        //See if the caption file exists. If not, create a blank caption file and verify that it was created.
        boolean captionFileExists = false;
        int dummy1 = 0;
        File captionFile = null;
        captionFile = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//so far have not created the caption file. This is only the document directory
        File[] williamsfList;
        if(captionFile != null)
        {
            williamsfList = captionFile.listFiles();
            if (williamsfList.length == 0) //If caption file does not exist (I am assuming caption file would be the only one here)
            {
                //Create the caption file
                try{
                    captionFile = File.createTempFile("captions", ".txt", captionFile);}
                catch (IOException e) {/*could not create captionFile*/}
                //Verify that the file was created ***************(for testing only)*****************
                File testCaptionFile = null;
                testCaptionFile = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File[] williamsTestfList;
                williamsTestfList = testCaptionFile.listFiles();
                if(williamsTestfList.length == 0) {/*could not list files in document directory*/}
                else//found some file(s)
                {
                    String testReadingCaptionFileName = null;
                    testReadingCaptionFileName = williamsTestfList[0].getAbsolutePath();//Assuming there is only one file here!
                    if(testReadingCaptionFileName.contains("captions"))
                    {
                        captionFileExists = true;//found the caption file! All good!
                    }
                }
            }//end create / verify caption file
            else
            {
                captionFileExists = true; //Caption file exists already! Do not create a new one!
                //But do save the filename!
                String fileName = williamsfList[0].getAbsolutePath(); //Assuming there is only one file here
                File file = new File(fileName);
                captionFile = file;
            }
            dummy1 = 3;
        }
        else{/*could not get document directory*/}
        //if there were no errors, captionFileExists should be true by this point!
        dummy1 = 10;

        //Populate the captionList.
        //The caption file should exist by now! If it is the first run, it will be blank - captionList will remain blank.
        if(captionFileExists)
        {
            //A. Create bufferedReader
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            String ret = null;
            try {
                fileReader = new FileReader(captionFile);//captionFile should exist by this point
                bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
            } catch (IOException e) { /*could not create bufferedReader*/ e.printStackTrace(); }
            dummy1 = 6;
            //B. Populate captionList
            if(bufferedReader != null)
            {
                try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                while (ret != null)
                {
                    captionList.add(ret);
                    try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                }
            }
            //C. Close bufferedReader if it was opened
            if(bufferedReader != null)
                try { bufferedReader.close(); } catch (IOException e) { /*could not close bufferedReader*/ e.printStackTrace(); }
            dummy1++;
        }//end populating captionList

        //************************************************************************************************************************
        //NEW: need to do the same thing for the date list.
        //See if the date file exists. If not, create a blank date file and verify that it was created.
        boolean dateFileExists = false;
        int dummy2 = 0;
        File dateFile = null;
        dateFile = getExternalFilesDir(Environment.DIRECTORY_MUSIC);//so far have not created the caption file.
        File[] williamsfList2;
        if(dateFile != null)
        {
            williamsfList2 = dateFile.listFiles();
            if (williamsfList2.length == 0) //If caption file does not exist (I am assuming caption file would be the only one here)
            {
                //Create the date file
                try{
                    dateFile = File.createTempFile("dates", ".txt", dateFile);}
                catch (IOException e) {/*could not create dateFile*/}
            }//end create dateFile
            else
            {
                dateFileExists = true; //date file exists already! Do not create a new one!
                //But do save the filename!
                String fileName = williamsfList2[0].getAbsolutePath(); //Assuming there is only one file here
                File file = new File(fileName);
                dateFile = file;
            }
        }
        else{/*could not get "music" directory*/}
        //if there were no errors, dateFileExists should be true by this point!
        dummy1 = 10;

        //Populate the dateList.
        //The date file should exist by now! If it is the first run, it will be blank - dateList will remain blank.
        if(dateFileExists)
        {
            //A. Create bufferedReader
            BufferedReader bufferedReader = null;
            FileReader fileReader = null;
            String ret = null;
            try {
                fileReader = new FileReader(dateFile);//dateFile should exist by this point
                bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
            } catch (IOException e) { /*could not create bufferedReader*/ e.printStackTrace(); }
            dummy1 = 6;
            //B. Populate dateList
            if(bufferedReader != null)
            {
                try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                while (ret != null)
                {
                    dateList.add(ret);
                    try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                }
            }
            //C. Close bufferedReader if it was opened
            if(bufferedReader != null)
                try { bufferedReader.close(); } catch (IOException e) { /*could not close bufferedReader*/ e.printStackTrace(); }
        }//end populating dateList
        //*****************************************************************************************************************************

        //Display the first picture, if there is one
        if (filenameList.size() > 0)
        {
            //1. Specify that the image #0 is the current image
            int currentElement = 0;
            //2. Set the filename to the filename of image #0
            //   No need to set currentFileName because there are no images yet
            mCurrentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+"/"+filenameList.get(currentElement).toString();
            //3. Display image #0
            displayPhoto(mCurrentPhotoPath);
            //4. Set the caption to the caption of image #0
            TextView textView = (TextView) findViewById(R.id.editTextCaption);
            textView.setText((CharSequence) captionList.get(currentElement));
            //5. Set the date to the date of image #0
            TextView textViewforDate = findViewById(R.id.DatetextView);
            textViewforDate.setText((CharSequence) dateList.get(currentElement).toString());
            //6. Set the location information to the location information of image #0
            File newFile;
            newFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File[] myfList;
            myfList = newFile.listFiles();
            float[] f = {0,0};
            boolean result = false;
            try {
                ExifInterface exif = new ExifInterface(myfList[0].toString());
                result = exif.getLatLong(f);
            }
            catch (IOException e) {}
            tv = (TextView) findViewById(R.id.gpsTextView);
            if(result == true)
                tv.setText(String.valueOf(f[0]) + " " + String.valueOf(f[1]));
            else
                tv.setText("No location information");
        }
    }//end onCreate

    public void search(View view) {
        Intent intent = new Intent(this, Search.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

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
    }//end takePicture

    public File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", mCurrentPhotoPath);
        currentFileName = image.getName(); //Added WM to get the filename
        CurrentDate = new Date(image.lastModified());
        return image;
    }//end createImageFile


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Do this if user took a picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            //1. Specify that the new image is the current image
            currentElement = filenameList.size();
            //2. Set the current filename to the filename of the new image, adding the new file to the list
            //   Setting the current filename to the filename of the new image was done already in createImageFile
            filenameList.add(currentFileName);
            //3. Display the new image
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            //4. Set the caption to the caption of the new image, adding the new caption to the list
            captionList.add("Enter Caption");
            TextView textView = (TextView) findViewById(R.id.editTextCaption);
            textView.setText((CharSequence) captionList.get(currentElement));
            //5. Set the date to the date of the new image, adding the new date to the list
            dateList.add(CurrentDate);
            TextView textViewforDate = findViewById(R.id.DatetextView);
            textViewforDate.setText((CharSequence) dateList.get(currentElement).toString());
            //6. Set the location information to the location information of the new image
            float[] f = {0,0};
            boolean result = false;
            try {
                String filename = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+filenameList.get(currentElement).toString();
                ExifInterface exif = new ExifInterface(filename);
                result = exif.getLatLong(f);
            }
            catch (IOException e) {}
            TextView tv = (TextView) findViewById(R.id.gpsTextView);
            if(result == true)
                tv.setText(String.valueOf(f[0]) + " " + String.valueOf(f[1]));
            else
                tv.setText("No location information");
            //7. Update caption file
            //Need to delete caption file and rewrite the caption list to file.
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Delete entire caption file (I couldn't see how to delete only the contents)
            //and write entire contents of captionList into the file.
            int dummy = 1;
            //1. Find the caption file.
            File storageDir = null;
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File[] storageDirFlist;
            if(storageDir != null) {
                storageDirFlist = storageDir.listFiles();
                String fileName = storageDirFlist[0].getAbsolutePath(); //Assuming there is only one file here
                File file = new File(fileName);
                //2. Delete the caption file.
                file.delete();
                //2B. Verify that the caption file was deleted (for testing only)
                storageDirFlist = storageDir.listFiles();
                storageDirFlist = storageDir.listFiles();
                //3. Create a new caption file.
                File captionFile = null;
                if (storageDirFlist.length == 0)//It should be 0, I just deleted the file
                {
                    //Create the caption file
                    captionFile = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    try{
                        captionFile = File.createTempFile("captions", ".txt", captionFile);}
                    catch (IOException e) {/*could not create captionFile*/}
                    //Verify that the file was created ***************(for testing only)*****************
                    File testCaptionFile = null;
                    testCaptionFile = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File[] williamsTestfList;
                    williamsTestfList = testCaptionFile.listFiles();
                    if(williamsTestfList.length == 0) {/*could not list files in document directory*/}
                    else//found some file(s)
                    {
                        String testReadingCaptionFileName = null;
                        testReadingCaptionFileName = williamsTestfList[0].getAbsolutePath();//Assuming there is only one file here!
                        if(testReadingCaptionFileName.contains("captions"))
                        {
                            //found the caption file all good

                        }
                    }
                }//end create / verify caption file
                else{ //the file wasn't deleted or there were multiple files there???? This should never happen
                }
                //4. Write the entire contents of captionList to the file.
                FileWriter fileWriter = null;
                BufferedWriter bufferedWriter = null;
                int captionListSize = captionList.size();
                try {
                    //trying to make the fileWriter and bufferedWriter outside the for loop
                    fileWriter = new FileWriter(captionFile/*, true*/);
                    //public FileWriter(File file, boolean append)
                    //Constructs a FileWriter object given a File object. If the second argument is true, then bytes
                    //will be written to the end of the file rather than the beginning.
                    bufferedWriter = new BufferedWriter(fileWriter);//source: https://www.baeldung.com/java-write-to-file
                    for (int i = 0; i < captionListSize; i++)
                    {
                        String stringToWrite = captionList.get(i).toString() + "\n";
                        bufferedWriter.write(stringToWrite);
                    }
                }
                catch (IOException e) {e.printStackTrace();}
                if(bufferedWriter != null)
                {
                    try{
                        bufferedWriter.close();
                    }
                    catch (IOException e) {e.printStackTrace();}
                }
                dummy = 2;
                //Verify some of the contents of the file (for testing only)
                BufferedReader bufferedReader = null;
                FileReader fileReader = null;
                String stringFromFile1 = null;
                String stringFromFile2 = null;
                String stringFromFile3 = null;
                try {
                    fileReader = new FileReader(captionFile);//giving it the file object hope this works
                    bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
                    stringFromFile1 = bufferedReader.readLine();
                    stringFromFile2 = bufferedReader.readLine();
                    stringFromFile3 = bufferedReader.readLine();
                    bufferedReader.close();
                } catch (IOException e) {
                    //could not read from test file
                    e.printStackTrace();
                }
                dummy = 6;
            }//end if storageDir != null
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //8. Update date file
            //*********************************************************************************************************************
            //NEW: Need to do the same for the date file
            //Need to delete date file and rewrite the date list to file.
            //Delete entire date file (I couldn't see how to delete only the contents)
            //and write entire contents of dateList into the file.
            int dummy3 = 1;
            //1. Find the date file.
            File storageDir2 = null;
            storageDir2 = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            File[] storageDirFlist2;
            if(storageDir2 != null) {
                storageDirFlist2 = storageDir2.listFiles();
                String fileName = storageDirFlist2[0].getAbsolutePath(); //Assuming there is only one file here
                File file = new File(fileName);
                //2. Delete the date file.
                file.delete();
                //3. Create a new date file.
                File dateFile = null;
                storageDirFlist2 = storageDir2.listFiles();
                if (storageDirFlist2.length == 0)//It should be 0, I just deleted the file
                {
                    //Create the date file
                    dateFile = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                    try{
                        dateFile = File.createTempFile("dates", ".txt", dateFile);}
                    catch (IOException e) {/*could not create captionFile*/}
                }//end create datefile
                else{ //the file wasn't deleted or there were multiple files there???? This should never happen
                }
                //4. Write the entire contents of dateList to the file.
                FileWriter fileWriter = null;
                BufferedWriter bufferedWriter = null;
                int dateListSize = dateList.size();
                try {
                    //trying to make the fileWriter and bufferedWriter outside the for loop
                    fileWriter = new FileWriter(dateFile);
                    bufferedWriter = new BufferedWriter(fileWriter);//source: https://www.baeldung.com/java-write-to-file
                    for (int i = 0; i < dateListSize; i++)
                    {
                        String stringToWrite = dateList.get(i).toString() + "\n";
                        bufferedWriter.write(stringToWrite);
                    }
                }
                catch (IOException e) {e.printStackTrace();}
                if(bufferedWriter != null)
                {
                    try{
                        bufferedWriter.close();
                    }
                    catch (IOException e) {e.printStackTrace();}
                }
                dummy3 = 2;
                //Verify some of the contents of the file (for testing only)
                BufferedReader bufferedReader = null;
                FileReader fileReader = null;
                String stringFromFile1 = null;
                String stringFromFile2 = null;
                String stringFromFile3 = null;
                try {
                    fileReader = new FileReader(dateFile);//giving it the file object hope this works
                    bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
                    stringFromFile1 = bufferedReader.readLine();
                    stringFromFile2 = bufferedReader.readLine();
                    stringFromFile3 = bufferedReader.readLine();
                    bufferedReader.close();
                } catch (IOException e) {
                    //could not read from test file
                    e.printStackTrace();
                }
                dummy = 6;
            }//end if storageDir != null
            //*********************************************************************************************************************
        }//end do this if user took a picture

        //Do this if user searched
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath(), "/Android/data/com.example.photogallery2/files/Pictures");
            String get_caption = data.getStringExtra("CAPTION");
            returnStartTime = data.getStringExtra("STARTDATE");  // getting the result back from 2nd activity
            returnEndTime = data.getStringExtra("ENDDATE");
            int cap_index = 0;
            filenameList.clear();

            //============================================================================
            //=== need to regenerate the caption list from the caption file. Otherwise fileList and captionList
            //=== have unequal numbers of elements the second search
            //=== (because captionList gets cleared)
            //============================================================================
            //write entire contents of caption file into caption list
            //1. Find the caption file.
            File storageDir = null;
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File[] storageDirFlist;
            if(storageDir != null) {
                storageDirFlist = storageDir.listFiles();
                String fileName = storageDirFlist[0].getAbsolutePath(); //Assuming there is only one file here
                //2. Write the entire contents of caption file into caption list
                int captionListSize = captionList.size();//not sure if needed here
                //A. Create bufferedReader
                BufferedReader bufferedReader = null;
                FileReader fileReader = null;
                String ret = null;
                try {
                    fileReader = new FileReader(fileName);//captionFile should exist by this point
                    bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
                } catch (IOException e) { /*could not create bufferedReader*/ e.printStackTrace(); }
                //B. Populate captionList from file
                captionList.clear();
                if(bufferedReader != null)
                {
                    try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                    while (ret != null)
                    {
                        captionList.add(ret);
                        try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                    }
                }
                //C. Close bufferedReader if it was opened
                if(bufferedReader != null)
                    try { bufferedReader.close(); } catch (IOException e) { /*could not close bufferedReader*/ e.printStackTrace(); }
            }//end if storageDir != null
            //=======================================================================================
            //=======================================================================================

            //******************************************************************************************************
            //*** need to regenerate the date list from the date file. Otherwise fileList and captionList and dateList
            //*** have unequal numbers of elements the second search
            //*** (because dateList gets cleared)
            //*********************************************************************************************************
            //write entire contents of date file into date list
            //1. Find the date file.
            File storageDir2 = null;
            storageDir2 = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            File[] storageDirFlist2;
            if(storageDir2 != null) {
                storageDirFlist2 = storageDir2.listFiles();
                String fileName = storageDirFlist2[0].getAbsolutePath(); //Assuming there is only one file here
                //2. Write the entire contents of caption file into caption list
                int dateListSize = dateList.size();//not sure if needed here
                //A. Create bufferedReader
                BufferedReader bufferedReader = null;
                FileReader fileReader = null;
                String ret = null;
                try {
                    fileReader = new FileReader(fileName);//captionFile should exist by this point
                    bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
                } catch (IOException e) { /*could not create bufferedReader*/ e.printStackTrace(); }
                //B. Populate dateList from file
                dateList.clear();
                if(bufferedReader != null)
                {
                    try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                    while (ret != null)
                    {
                        dateList.add(ret);
                        try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ e.printStackTrace(); }
                    }
                }
                //C. Close bufferedReader if it was opened
                if(bufferedReader != null)
                    try { bufferedReader.close(); } catch (IOException e) { /*could not close bufferedReader*/ e.printStackTrace(); }
            }//end if storageDir != null
            //*****************************************************************************************************
            //*****************************************************************************************************

            List filter_captionList = new ArrayList();
            List filter_dateList = new ArrayList<Date>();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");  //
            SimpleDateFormat sdfUser = new SimpleDateFormat("yyyyMMdd_HHmmss");
            ParsePosition test = new ParsePosition(0);
            Date dStartTime;
            Date dEndTime;
            String minDate = "00010101_000000";
            String maxDate = "20500505_000000";

            Date minDateD = sdfUser.parse(minDate,test);
            test.setIndex(0);
            Date maxDateD = sdfUser.parse(maxDate,test);
            test.setIndex(0);

            if (returnStartTime.isEmpty() || returnEndTime.isEmpty())
            {
                dStartTime = minDateD;
                dEndTime = maxDateD;
            }
            ////////////////////////////////////////////////////////////////////////
            ////////     the user input the date in a simple format that can't be compared with the format of the list
            ////          so we get the "simple format" string,
            else {
                dStartTime = sdfUser.parse(returnStartTime, test);
                test.setIndex(0);
                dEndTime = sdfUser.parse(returnEndTime, test);
                test.setIndex(0);
            }
            a = 0;
            for (File fwm : file.listFiles()) {
                String str = captionList.get(cap_index).toString();
                if (str.contains(get_caption)  ) {       ////////////////   comparsion
                    Date d1 = new Date();
                    d1 = sdf.parse(dateList.get(cap_index).toString(), test);
                    test.setIndex(0);

                    if (d1.compareTo(dStartTime) > 0 && d1.compareTo(dEndTime) < 0) {
                        a = 1;
                        filenameList.add(fwm.getName());
                        filter_captionList.add(captionList.get(cap_index).toString());
                        filter_dateList.add(dateList.get(cap_index).toString());
                    } else
                        a = 0;
                }
                cap_index++;
            }

            captionList.clear();
            captionList = filter_captionList;
            dateList.clear();
            dateList = filter_dateList;

            //If the search is cleared, re-enable the snap button
            Button button = findViewById(R.id.btnSnap);
            if(get_caption.isEmpty())
                button.setClickable(true);
                //Otherwise disable the snap button. To prevent crashes
            else
                button.setClickable(false);

            //If the search returned something, go to the first image in the filtered list
            if(captionList.size() != 0)
            {
                //1. Specify that the first image in the filtered list is the current image
                currentElement = 0;
                //2. Set the current filename to the filename of the new image
                mCurrentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + filenameList.get(currentElement).toString();
                currentFileName = filenameList.get(currentElement).toString();
                //3. Display the new image
                ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                //4. Set the caption to the caption of the new image
                TextView textView = (TextView) findViewById(R.id.editTextCaption);
                textView.setText((CharSequence) captionList.get(currentElement));
                //5. Set the date to the date of the new image
                TextView textViewforDate = findViewById(R.id.DatetextView);
                textViewforDate.setText((CharSequence) dateList.get(currentElement).toString());
                //6. Set the location information to the location information of the new image
                float[] f = {0,0};
                boolean result = false;
                try {
                    String filename = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+filenameList.get(currentElement).toString();
                    ExifInterface exif = new ExifInterface(filename);
                    result = exif.getLatLong(f);
                } catch (IOException e) {}
                TextView tv = (TextView) findViewById(R.id.gpsTextView);
                if(result == true)
                    tv.setText(String.valueOf(f[0]) + " " + String.valueOf(f[1]));
                else
                    tv.setText("No location information");
            }
            //Else, go back to the photo gallery, but display no image
            else
            {
                //1. Specify that the first image in the photo gallery is the current image
                //   Because the photo gallery will be empty, this doesn't matter.
                //2. Set the current filename to null
                mCurrentPhotoPath = null;
                currentFileName = null;
                //3. Display nothing
                ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
                mImageView.setImageDrawable(null);
                //4. Set the caption to "no files found"
                TextView textView = (TextView) findViewById(R.id.editTextCaption);
                textView.setText("no files found");
                //5. Set the date to "No date information"
                TextView textViewforDate = findViewById(R.id.DatetextView);
                textViewforDate.setText("No date information");
                //6. Set the location information to "No location information"
                TextView textViewforLocation = findViewById(R.id.gpsTextView);
                textViewforLocation.setText("No location information");
            }
        }//end do this if user searched
    }//end onActivityResult

    //Saves the caption. WM
    public void saveCaption(View view)
        {

        //Algorithm for saving captions:
        //1. See if there is an image in the imageView.
        //   I will do this indirectly by seeing if currentFileName is not null.
        //2. If not, do nothing.
        //3. If yes, change the caption for the image.

        if(currentFileName != null)
        {
            TextView textView = (TextView) findViewById(R.id.editTextCaption);
            String caption = textView.getText().toString();
            captionList.set(currentElement, caption);

            //Need to delete caption file and rewrite the caption list to file.
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Delete entire caption file (I couldn't see how to delete only the contents)
            //and write entire contents of captionList into the file.
            int dummy = 1;
            //1. Find the caption file.
            File storageDir = null;
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File[] storageDirFlist;
            if(storageDir != null) {
                storageDirFlist = storageDir.listFiles();
                String fileName = storageDirFlist[0].getAbsolutePath(); //Assuming there is only one file here
                File file = new File(fileName);
                //2. Delete the caption file.
                file.delete();
                //2B. Verify that the caption file was deleted (for testing only)
                storageDirFlist = storageDir.listFiles();
                storageDirFlist = storageDir.listFiles();
                //3. Create a new caption file.
                File captionFile = null;
                if (storageDirFlist.length == 0)//It should be 0, I just deleted the file
                {
                    //Create the caption file
                    captionFile = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    try{
                        captionFile = File.createTempFile("captions", ".txt", captionFile);}
                    catch (IOException e) {/*could not create captionFile*/}
                    //Verify that the file was created ***************(for testing only)*****************
                    File testCaptionFile = null;
                    testCaptionFile = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File[] williamsTestfList;
                    williamsTestfList = testCaptionFile.listFiles();
                    if(williamsTestfList.length == 0) {/*could not list files in document directory*/}
                    else//found some file(s)
                    {
                        String testReadingCaptionFileName = null;
                        testReadingCaptionFileName = williamsTestfList[0].getAbsolutePath();//Assuming there is only one file here!
                        if(testReadingCaptionFileName.contains("captions"))
                        {
                            //found the caption file all good

                        }
                    }
                }//end create / verify caption file
                else{ //the file wasn't deleted or there were multiple files there???? This should never happen
                }
                //4. Write the entire contents of captionList to the file.
                FileWriter fileWriter = null;
                BufferedWriter bufferedWriter = null;
                int captionListSize = captionList.size();
                try {
                    //trying to make the fileWriter and bufferedWriter outside the for loop
                    fileWriter = new FileWriter(captionFile/*, true*/);
                    //public FileWriter(File file, boolean append)
                    //Constructs a FileWriter object given a File object. If the second argument is true, then bytes
                    //will be written to the end of the file rather than the beginning.
                    bufferedWriter = new BufferedWriter(fileWriter);//source: https://www.baeldung.com/java-write-to-file
                    for (int i = 0; i < captionListSize; i++)
                    {
                        String stringToWrite = captionList.get(i).toString() + "\n";
                        bufferedWriter.write(stringToWrite);
                    }
                }
                catch (IOException e) {e.printStackTrace();}
                if(bufferedWriter != null)
                {
                    try{
                        bufferedWriter.close();
                    }
                    catch (IOException e) {e.printStackTrace();}
                }
                dummy = 2;
                //Verify some of the contents of the file (for testing only)
                BufferedReader bufferedReader = null;
                FileReader fileReader = null;
                String stringFromFile1 = null;
                String stringFromFile2 = null;
                String stringFromFile3 = null;
                try {
                    fileReader = new FileReader(captionFile);//giving it the file object hope this works
                    bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
                    stringFromFile1 = bufferedReader.readLine();
                    stringFromFile2 = bufferedReader.readLine();
                    stringFromFile3 = bufferedReader.readLine();
                    bufferedReader.close();
                } catch (IOException e) {
                    //could not read from test file
                    e.printStackTrace();
                }
                dummy = 6;
            }//end if storageDir != null
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }//end saveCaption

    //Move to the newer image. WM
    public void Left(View view)
    {
        //Other commands that might be useful
        //textView.onCommitCompletion();

        //Algorithm:
        //1. See if the number of images is greater than 1.
        //2. If not, do nothing.
        //3. If yes, continue with the next steps.
        //4. See if the current image is an older image.
        //   I will do this by checking the currentElement.
        //5. If not, do nothing.
        //6. If yes, continue with the next steps.
        //7. Set the current image to the newer image.

        //See if the number of images is greater than 1.
        int filenameListSize = filenameList.size();
        if(filenameListSize > 1)
        {
            //See if the current image is an older image.
            //The current image is an older image if currentElement is not the last element number.
            if(currentElement != (filenameListSize - 1))
            {
                //1. Specify that the current element is the newer image
                currentElement++;
                //2. Set the current filename to the filename of the newer image
                mCurrentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + filenameList.get(currentElement).toString();
                currentFileName = filenameList.get(currentElement).toString();
                //3. Display the newer image
                ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                //4. Set the caption to the caption of the newer image
                TextView textView = (TextView) findViewById(R.id.editTextCaption);
                textView.setText((CharSequence) captionList.get(currentElement));
                //5. Set the date to the date of the newer image
                TextView textViewforDate = findViewById(R.id.DatetextView);
                textViewforDate.setText((CharSequence) dateList.get(currentElement).toString());
                //6. Set the location information to the location information of the newer image
                float[] f = {0,0};
                boolean result = false;
                try {
                    String filename = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+filenameList.get(currentElement).toString();
                    ExifInterface exif = new ExifInterface(filename);
                    result = exif.getLatLong(f);
                } catch (IOException e) {}
                TextView tv = (TextView) findViewById(R.id.gpsTextView);
                if(result == true)
                    tv.setText(String.valueOf(f[0]) + " " + String.valueOf(f[1]));
                else
                    tv.setText("No location information");
            }
        }
    }//end Left

    //Display the older image. WM
    public void Right(View view)
    {
        //Algorithm:
        //1. See if the number of images is greater than 1.
        //2. If not, do nothing.
        //3. If yes, continue with the next steps.
        //4. See if the current image is a newer image.
        //   I will do this by checking the currentElement.
        //5. If not, do nothing.
        //6. If yes, continue with the next steps.
        //7. Set the current image to the older image.

        //See if the number of images is greater than 1.
        int filenameListSize = filenameList.size();
        if(filenameListSize > 1)
        {
            //See if the current image is a newer image.
            //The current image is a newer image if currentElement is not the first element number.
            if(currentElement != 0)
            {
                //1. Specify that the current element is the older image
                currentElement--;
                //2. Set the current filename to the filename of the older image
                mCurrentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + filenameList.get(currentElement).toString();
                currentFileName = filenameList.get(currentElement).toString();
                //3. Display the older image
                ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
                mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                //4. Set the caption to the caption of the older image
                TextView textView = (TextView) findViewById(R.id.editTextCaption);
                textView.setText((CharSequence) captionList.get(currentElement));
                //5. Set the date to the date of the older image
                TextView textViewforDate = findViewById(R.id.DatetextView);
                textViewforDate.setText((CharSequence) dateList.get(currentElement).toString());
                //6. Set the location information to the location information of the older image
                float[] f = {0,0};
                boolean result = false;
                try {
                    String filename = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+filenameList.get(currentElement).toString();
                    ExifInterface exif = new ExifInterface(filename);
                    result = exif.getLatLong(f);
                } catch (IOException e) {}
                TextView tv = (TextView) findViewById(R.id.gpsTextView);
                if(result == true)
                    tv.setText(String.valueOf(f[0]) + " " + String.valueOf(f[1]));
                else
                    tv.setText("No location information");
            }
        }
    }//end Right
}//end MainActivity

