package made.by.hemangini.planyourday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class AutocompleteAdapter extends ArrayAdapter<String> implements Filterable{

    private List<String> resultList = new ArrayList<String>();
    private String API_KEY = "AIzaSyBrGyGfOjn7VKmaYhkdGcoj98ZKI0mhxbg";
    private String Type;
    private String CountryCode;
    private GetAddressFromAdapter response;
    private String city = "";
    private String locationNdradius = "";
    private Context ActivityContext;



    public AutocompleteAdapter(Context context, int t) {
        super(context, t);
        this.Type = "country";
        response = (GetAddressFromAdapter) context;
        this.ActivityContext = context;
    }


    public AutocompleteAdapter(Context context, int t, String type, String CountryCode, String city) {
        super(context, t);
        this.CountryCode = CountryCode;
        this.Type = type;   //type = "sublocality_level_" ;  you need to add '1' & '2' to search for the type
        response = (GetAddressFromAdapter) context;
        this.city = city;
        this.ActivityContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        ((TextView) convertView).setText(getItem(position));
        return convertView;
    }



    @Override
    public Filter getFilter() {
        Filter filter = new SubFilter();
        return filter;
    }



    public class SubFilter extends Filter implements GetAutocompleteList.AutocompResponse{

        private SubFilter FilterContext = this;
        private ArrayList<String> Strings = new ArrayList<String>();


        SubFilter(){};

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();


            if (constraint != null) {
                StringBuilder url = new StringBuilder();
                if(Type.equals("country")) {
                    url = url.append("https://maps.googleapis.com/maps/api/place/autocomplete/json?types=geocode&key=" + API_KEY + "&input=" + constraint.toString());
                    GetAutocompleteList getAutocompleteList = new GetAutocompleteList( url.toString(), Type, FilterContext, "");
                }
                else if(Type.equals("locality") ){
                    url = url.append("https://maps.googleapis.com/maps/api/place/autocomplete/json?types=(cities)&key=" + API_KEY + "&components=country:" + CountryCode + "&input=" + constraint.toString());
                    GetAutocompleteList getAutocompleteList = new GetAutocompleteList(url.toString(), Type, FilterContext, city);

                }else{
                    locationNdradius = ((Main) ActivityContext).getLatLongFromMain();
                    url = url.append("https://maps.googleapis.com/maps/api/place/autocomplete/json?types=(regions)&key=" + API_KEY + locationNdradius
                            + "&components=country:" + CountryCode + "&input=" + constraint.toString());
                    GetAutocompleteList getAutocompleteList = new GetAutocompleteList( url.toString(), Type, FilterContext, city);


                }


                filterResults.values = Strings;
                filterResults.count = Strings.size();

            }
            return filterResults;
        }


        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if (results != null && results.count > 0) {
                resultList = (ArrayList<String>) results.values;

                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }


        public void onAutocompFinish(ArrayList<String> td){
            Strings = td;
        }


        public void getAddressFromGetAutoCompleteList(ArrayList<String> addrs){
            response.getAddressFromAdapter(addrs);
        }

    }



    public interface GetAddressFromAdapter{
        void getAddressFromAdapter(ArrayList<String> address);
    }

}