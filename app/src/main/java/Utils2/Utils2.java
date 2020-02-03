//Utility class for Photo Gallery app
package Utils2;
import android.content.Context;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils2
{
    //This function gets the filename of a file containing the given text in the appropriate directory of this app.
    //If the string is "captions", the file is in the Documents directory of this app.
    //If the string if "dates", the file is in the Music directory of this app.
    //If the file does not exist, it creates one.
    //As for why Context c is required, see https://stackoverflow.com/questions/34638931/android-says-cannot-resolve-method-getexternalfilesdirnull
    public File GetFile(Context c, String s)
    {
        File f = null;
        if(s.contains("captions"))
            f = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        else if(s.contains("dates"))
            f = c.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        else
        {/*invalid string was passed*/}
        File[] fl;
        if(f != null)
        {
            fl = f.listFiles();
            if (fl.length == 0) //If file does not exist (I am assuming file would be the only one here)
            {
                //Create the file
                try{
                    f = File.createTempFile(s, ".txt", f);}
                catch (IOException e) {/*could not create file*/}
            }
            else
            {
                //File exists already, find the filename
                String fileName = fl[0].getAbsolutePath(); //Assuming there is only one file here
                File file = new File(fileName);
                f = file;
            }
        }
        else{/*could not get directory*/}
        return f;
    }
    //============================================================================================================================

    //This function populates a list from the given file.
    public List PopulateList(File file)
    {
        //Create bufferedReader
        BufferedReader bufferedReader = null;
        FileReader fileReader2 = null;
        String ret = null;
        try {
            fileReader2 = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader2);//source: https://www.journaldev.com/709/java-read-file-line-by-line
        } catch (IOException e) { /*could not create bufferedReader*/ }
        //Populate list
        List list = new ArrayList();
        if(bufferedReader != null)
        {
            try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ }
            while (ret != null)
            {
                list.add(ret);
                try { ret = bufferedReader.readLine(); } catch (IOException e) {/*could not read line*/ }
            }
        }
        //Close bufferedReader if it was opened
        if(bufferedReader != null)
            try { bufferedReader.close(); } catch (IOException e) { /*could not close bufferedReader*/ }
        return list;
    }
    //============================================================================================================================

    //This function saves the given list to the file specified by s ("captions" or "dates").
    public void SaveToFile(Context c, List list, String s)
    {
        //Need to delete the file and rewrite the list to file.
        //(I couldn't see how to delete only the contents)
        //1. Find the file.
        File storageDir = null;
        if(s.contains("captions"))
            storageDir = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        else if(s.contains("dates"))
            storageDir = c.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File[] storageDirFlist;
        if(storageDir != null)
        {
            storageDirFlist = storageDir.listFiles();
            String fileName = storageDirFlist[0].getAbsolutePath(); //Assuming there is only one file here
            File file = new File(fileName);
            //2. Delete the file.
            file.delete();
            //3. Create a new file.
            File newFile = null;
            if(s.contains("captions"))
            {
                newFile = c.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                try{
                    newFile = File.createTempFile("captions", ".txt", newFile);}
                catch (IOException e) {/*could not create newFile*/}
            }
            else if(s.contains("dates"))
            {
                newFile = c.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                try{
                    newFile = File.createTempFile("dates", ".txt", newFile);}
                catch (IOException e) {/*could not create newFile*/}
            }
            //4. Write the entire contents of the list to the file.
            FileWriter fileWriter = null;
            BufferedWriter bufferedWriter = null;
            int listSize = list.size();
            try {
                fileWriter = new FileWriter(newFile);
                bufferedWriter = new BufferedWriter(fileWriter);//source: https://www.baeldung.com/java-write-to-file
                for (int i = 0; i < listSize; i++)
                    bufferedWriter.write(list.get(i).toString() + "\n");
            }
            catch (IOException e) {e.printStackTrace();}
            if(bufferedWriter != null)
            {
                try{
                    bufferedWriter.close();
                }
                catch (IOException e) {e.printStackTrace();}
            }
        }
        else {}//storageDir == null
    }
    //============================================================================================================================

    //This function clears the given filter, i.e. returns a list equal to the given master list
    //(There is no copy function for lists. And using the equals operator does not make a true copy.)
    public List copy(List masterList)
    {
        List l = new ArrayList();
        for(int i = 0; i < masterList.size(); i++)
            l.add(masterList.get(i));
        return l;
    }
    //============================================================================================================================
}//end Class
