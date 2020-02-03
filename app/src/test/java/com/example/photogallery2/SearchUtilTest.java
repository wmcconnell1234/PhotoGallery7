package com.example.photogallery2;
import android.content.Context;
import android.os.Environment;
import org.junit.Test;
import org.junit.runner.RunWith;                //ADDED FOR ROBOLECTRIC (alt-enter)
import org.robolectric.RobolectricTestRunner;   //ADDED FOR ROBOLECTRIC (alt-enter)
import org.robolectric.annotation.Config;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import SearchUtil.*;

@RunWith(RobolectricTestRunner.class)       //ADDED FOR ROBOLECTRIC http://robolectric.org/getting-started/
@Config(sdk = 27)                           //added https://stackoverflow.com/questions/56821193/does-robolectric-require-java-9
public class SearchUtilTest
{
    @Test
    public void BlankSearch()
    {
        //Given that there are two pictures, and the user did a blank search...
        Context c = getApplicationContext();
        File f = null;
        f = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            File.createTempFile("img1", ".JPEG", f);
            File.createTempFile("img2", ".JPEG", f);
        } catch (IOException e) {}
        List captionListM = new ArrayList();
        captionListM.add("Caption 1");
        captionListM.add("Caption 2");
        List dateListM = new ArrayList();
        dateListM.add("Sat Feb 01 08:00:00 PST 2020");
        dateListM.add("Sat Feb 01 08:30:00 PST 2020");
        String get_caption = "";
        String returnStartTime = ""; String returnEndTime = "";
        String lat1 = ""; String lng1 = ""; String lat2 = ""; String lng2 = "";

        //When Search() is called...
        SearchUtil S = new SearchUtil();
        Context context = getApplicationContext();
        List result = S.Search(context,get_caption,returnStartTime,returnEndTime,lat1,lng1,lat2,lng2,captionListM,dateListM);

        //Does it return both pictures?
        List filenameListF = (List) result.get(0);
        List captionListF = (List) result.get(1);
        List dateListF = (List) result.get(2);
        assertEquals(2, filenameListF.size()); //should be 2 files in the list
        assertEquals(2, captionListF.size()); //should be 2 captions in the list
        assertEquals(2, dateListF.size()); //should be 2 dates in the list
        //Verify content of lists is correct
        assertEquals(true, filenameListF.get(0).toString().contains("img1"));
        assertEquals(true, filenameListF.get(1).toString().contains("img2"));
        assertEquals("Caption 1", captionListF.get(0));
        assertEquals("Caption 2", captionListF.get(1));
        assertEquals("Sat Feb 01 08:00:00 PST 2020", dateListF.get(0));
        assertEquals("Sat Feb 01 08:30:00 PST 2020", dateListF.get(1));
    }//end blank search

    @Test
    public void CaptionSearch()
    {
        //Given that there are two pictures called "Caption 1" and "Caption 2",
        //and the user searched for "1"...
        Context c = getApplicationContext();
        File f = null;
        f = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            File.createTempFile("img1", ".JPEG", f);
            File.createTempFile("img2", ".JPEG", f);
        } catch (IOException e) {}
        List captionListM = new ArrayList();
        captionListM.add("Caption 1");
        captionListM.add("Caption 2");
        List dateListM = new ArrayList();
        dateListM.add("Sat Feb 01 08:00:00 PST 2020");
        dateListM.add("Sat Feb 01 08:30:00 PST 2020");
        String get_caption = "1";
        String returnStartTime = ""; String returnEndTime = "";
        String lat1 = ""; String lng1 = ""; String lat2 = ""; String lng2 = "";

        //When Search() is called...
        SearchUtil S = new SearchUtil();
        Context context = getApplicationContext();
        List result = S.Search(context,get_caption,returnStartTime,returnEndTime,lat1,lng1,lat2,lng2,captionListM,dateListM);

        //Does it return only the picture called "Caption 1"?
        List filenameListF = (List) result.get(0);
        List captionListF = (List) result.get(1);
        List dateListF = (List) result.get(2);
        assertEquals(1, filenameListF.size()); //should be 1 file in the list
        assertEquals(1, captionListF.size()); //should be 1 caption in the list
        assertEquals(1, dateListF.size()); //should be 1 date in the list
        //Verify content of lists is correct
        assertEquals(true, filenameListF.get(0).toString().contains("img1"));
        assertEquals("Caption 1", captionListF.get(0));
        assertEquals("Sat Feb 01 08:00:00 PST 2020", dateListF.get(0));
    }//end caption search

    @Test
    public void DateSearch()
    {
        //Given that there are two pictures, one taken on January 20 2020, and one
        //taken on January 22 2020, and the user searched for pictures between
        //January 21 2020 and January 23 2020 (leaving the caption blank)...
        Context c = getApplicationContext();
        File f = null;
        f = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            File.createTempFile("img1", ".JPEG", f);
            File.createTempFile("img2", ".JPEG", f);
        } catch (IOException e) {}
        List captionListM = new ArrayList();
        captionListM.add("Caption 1");
        captionListM.add("Caption 2");
        List dateListM = new ArrayList();
        dateListM.add("Mon Jan 20 08:00:00 PST 2020");
        dateListM.add("Wed Jan 22 08:30:00 PST 2020");
        String get_caption = "";
        String returnStartTime = "20200121_090909"; String returnEndTime = "20200123_090909";
        String lat1 = ""; String lng1 = ""; String lat2 = ""; String lng2 = "";

        //When Search() is called...
        SearchUtil S = new SearchUtil();
        Context context = getApplicationContext();
        List result = S.Search(context,get_caption,returnStartTime,returnEndTime,lat1,lng1,lat2,lng2,captionListM,dateListM);

        //Does it return only the picture taken on January 22?
        List filenameListF = (List) result.get(0);
        List captionListF = (List) result.get(1);
        List dateListF = (List) result.get(2);
        assertEquals(1, filenameListF.size()); //should be 1 file in the list
        assertEquals(1, captionListF.size()); //should be 1 caption in the list
        assertEquals(1, dateListF.size()); //should be 1 date in the list
        //Verify content of lists is correct
        assertEquals(true, filenameListF.get(0).toString().contains("img2"));
        assertEquals("Caption 2", captionListF.get(0));
        assertEquals("Wed Jan 22 08:30:00 PST 2020", dateListF.get(0));
    }//end date search


}//end SearchUtilTest
