package made.by.hemangini.planyourday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import made.by.hemangini.planyourday.R;

import java.util.ArrayList;

/**
 * Created by Hemangini on 1/19/2015.
 */
public class DisplayListAdapter extends BaseAdapter{

    private Context fContext;
    protected LayoutInflater fInflater;
    protected ArrayList<InstantTask> FinalTaskList;


    public DisplayListAdapter(Context aContext, ArrayList<InstantTask> FinalTaskList) {
        fContext = aContext;
        this.FinalTaskList = FinalTaskList;
        fInflater = (LayoutInflater) fContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public int getCount() {
        return FinalTaskList.size();
    }



    public Object getItem(int i) {
        return FinalTaskList.get(i);
    }



    public long getItemId(int i) {
        return (long) i;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        View lView = convertView;
        if (lView == null) {
            lView = fInflater.inflate(R.layout.list_item, parent, false);
        }

        String TaskTime = String.valueOf(FinalTaskList.get(position).getTimeHour()) + ":" + String.valueOf(FinalTaskList.get(position).getTimeMinute())
                + ":" + String.valueOf(FinalTaskList.get(position).getTimeSecond());
        String TaskNote = FinalTaskList.get(position).getNote();
        String TaskLocation = FinalTaskList.get(position).getPlace().split(",")[0];

        TextView Time = (TextView) lView.findViewById(R.id.ItemTime);
        TextView Note = (TextView) lView.findViewById(R.id.ItemNote);
        TextView Place = (TextView) lView.findViewById(R.id.ItemPlace);


        Time.setText(TaskTime);
        Note.setText(TaskNote);
        Place.setText(TaskLocation);

        if(position == FinalTaskList.size()-1){
            //if this is the last item in the listview, do not display the arrow
        }else{
        }


        return lView;
    }


}