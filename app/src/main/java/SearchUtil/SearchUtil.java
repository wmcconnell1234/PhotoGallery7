//Utility class for the search function
package SearchUtil;
import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchUtil
{
    //Given the search criteria, master caption list, and master date list, returns a list of lists
    //containing the filtered filename list, caption list, and date list,
    //containing the information of only the pictures that match the search criteria.
    //"Context c" has been added for testability.
    public List Search(Context c, String get_caption, String returnStartTime, String returnEndTime,
                       String lat1, String lng1, String lat2, String lng2, List captionListM, List dateListM)
    {
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
        //Loop through all files, add the ones that match the search criteria to the filtered lists
        List filenameListF = new ArrayList();
        List captionListF = new ArrayList();
        List dateListF = new ArrayList();
        int cap_index = 0;
        File file = null;
        file = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES); //using Context c for testability
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
        //Pack the filtered lists into a list of lists and return it
        List result = new ArrayList();
        result.add(filenameListF);
        result.add(captionListF);
        result.add(dateListF);
        return result;
    }//end Search()
}//end SearchUtil
