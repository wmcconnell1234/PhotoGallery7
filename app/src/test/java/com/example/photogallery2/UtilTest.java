package com.example.photogallery2;
import android.content.Context;
import android.os.Environment;
import org.junit.Test;
import org.junit.runner.RunWith;                //ADDED FOR ROBOLECTRIC (alt-enter)
import org.robolectric.RobolectricTestRunner;   //ADDED FOR ROBOLECTRIC (alt-enter)
import org.robolectric.annotation.Config;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Utils2.*;

@RunWith(RobolectricTestRunner.class)       //ADDED FOR ROBOLECTRIC http://robolectric.org/getting-started/
@Config(sdk = 27)                           //added https://stackoverflow.com/questions/56821193/does-robolectric-require-java-9
public class UtilTest
{
    @Test //Utils.GetFile() test 1
    public void CanCreateFile()
    {
        //Given the caption file has not been created yet...

        //...When I call Utils.GetFile() with "captions"...
        Utils2 U = new Utils2();
        String s = "captions";
        Context c = getApplicationContext();
        File f = null;
        f = U.GetFile(c,s);

        //...Does the filename contain "captions"?
        assertEquals(true, f.toString().contains("captions"));
    }//end CanCreateCaptionFile()

    @Test //Utils.GetFile() test 2
    public void CanDetectFile()
    {
        //Given the caption file exists already...
        Context c = getApplicationContext();
        File f = null;
        f = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File captionFile = null;
        try{
            captionFile = File.createTempFile("captions", ".txt", f);
        } catch (IOException e) {}

        //...When I call Utils.GetFile()...
        Utils2 U = new Utils2();
        f = null;
        f = U.GetFile(c, "captions");

        //...Does it detect the file?
        assertEquals(captionFile.toString(), f.toString());
    }//end CanDetectFile

    @Test //Utils.PopulateList() test
    public void CanPopulateList()
    {
        //Given that a caption file containing two captions exists already...
        Context c = getApplicationContext();
        File f = null;
        f = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File captionFile = null;
        try{
            captionFile = File.createTempFile("captions", ".txt", f);
        } catch (IOException e) {}
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(captionFile);
            bufferedWriter = new BufferedWriter(fileWriter);//source: https://www.baeldung.com/java-write-to-file
            bufferedWriter.write("Caption 1" + "\n");
            bufferedWriter.write("Caption 2" + "\n");
            bufferedWriter.close();
        } catch (IOException e) {}

        //...When I call Utils.PopulateList...
        Utils2 U = new Utils2();
        List result;
        result = U.PopulateList(captionFile);

        //...Did it populate the list?
        assertEquals("Caption 1", result.get(0));
        assertEquals("Caption 2", result.get(1));
    }//end CanPopulateList

    @Test //Utils.SaveToFile() test
    public void CanSaveToFile()
    {
        //Given that a caption list exists, and a blank caption file exists...
        List captionList = new ArrayList();
        captionList.add("Caption 1");
        captionList.add("Caption 2");
        Context c = getApplicationContext();
        File f = null;
        f = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File captionFile = null;
        try{
            captionFile = File.createTempFile("captions", ".txt", f);
        } catch (IOException e) {}

        //...When I call Utils.SaveToFile() with "captions"...
        Utils2 U = new Utils2();
        U.SaveToFile(c, captionList, "captions"); //deletes the file and creates a new one.

        //...Did it save the caption list to the file?
        //I need to find the new file, since the original captionFile is no more.
        File[] storageDirFlist;
        storageDirFlist = f.listFiles();
        String fileName = storageDirFlist[0].getAbsolutePath(); //Assuming there is only one file here
        List result = new ArrayList();
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String ret = null;
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);//source: https://www.journaldev.com/709/java-read-file-line-by-line
            ret = bufferedReader.readLine();
            result.add(ret);
            ret = bufferedReader.readLine();
            result.add(ret);
            bufferedReader.close();
        } catch (IOException e) { }
        assertEquals("Caption 1", result.get(0));
        assertEquals("Caption 2", result.get(1));
    }//end CanSaveToFile

    @Test //Utils.copy() test
    public void CanCopyList()
    {
        //Given that I have a populated list and a blank list...
        List l1 = new ArrayList();
        l1.add("Caption 1");
        l1.add("Caption 2");
        List l2 = new ArrayList();

        //...When I call Utils.copy...
        Utils2 U = new Utils2();
        l2 = U.copy(l1);

        //...Did it make a true copy of the list?
        assertEquals("Caption 1", l2.get(0));
        assertEquals("Caption 2", l2.get(1));
        //Make sure it's not a shallow copy
        l1.clear();
        assertEquals("Caption 1", l2.get(0));
        assertEquals("Caption 2", l2.get(1));
    }
}//end UtilTest


