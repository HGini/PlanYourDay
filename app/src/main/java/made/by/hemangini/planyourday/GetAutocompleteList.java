package made.by.hemangini.planyourday;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class GetAutocompleteList {


    private JSONMessage jsonMessage = new JSONMessage();
    private String Type = "";
    private AutocompResponse response;
    private ArrayList<String> address = new ArrayList<String>();
    private String city = "";
    private Context ActivityContext;



    public GetAutocompleteList(String Input, String Type, AutocompleteAdapter.SubFilter filter, String city) {

        this.Type = Type;
        response =  filter;
        this.city = city;
        this.ConnectHttp(Input);

    }


    public JSONMessage readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }



    public JSONMessage readMessage(JsonReader reader) throws IOException {
        String status = "";
        ArrayList predictions = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                status = reader.nextString();
            } else if (name.equals("predictions")) {
                predictions = readPredictions(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new JSONMessage(status, predictions);
    }



    public ArrayList<PredictionPiece> readPredictions(JsonReader reader) throws IOException {
        ArrayList<PredictionPiece> rows = new ArrayList<PredictionPiece>();

        reader.beginArray();
        while (reader.hasNext()) {
            rows.add(readPredictionsPieces(reader));
        }

        reader.endArray();
        return rows;

    }



    public PredictionPiece readPredictionsPieces(JsonReader reader) throws IOException {
        PredictionPiece predictionPiece = new PredictionPiece();


        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("description")) {
                predictionPiece.description = reader.nextString();
            } else if (name.equals("types")) {
                predictionPiece.types = readTypes(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return predictionPiece;
    }



    public ArrayList<String> readTypes(JsonReader reader) throws IOException {
        ArrayList<String> result = new ArrayList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            result.add(reader.nextString());
        }
        reader.endArray();
        return result;

    }



    public class PredictionPiece {
        String description = "";
        ArrayList<String> types = null;

        PredictionPiece() {
        }

        PredictionPiece(String description, ArrayList<String> types) {
            this.description = description;
            this.types = types;
        }

        //get method
        private boolean isThisPPAtType(String type) {
            if (this.types.get(0).equals(type)) {
                return true;
            }else if(this.types.get(0).equals(type + "1")){
                return true;
            }else if(this.types.get(0).equals(type + "2")) {
                return true;
            }else {
                return false;
            }

        }

        public String returnFirstString(){
            return this.description.split(",")[0];
        }


        public String returnSecondString(){
            if(this.description.split((",")).length > 1){
                return this.description.split(",")[1];}
            else{
                return "";
            }
        }


    }


    private class JSONMessage {
        String status = "";
        ArrayList<PredictionPiece> predictions = null;

        JSONMessage() {
        }

        JSONMessage(String status, ArrayList<PredictionPiece> predictions) {
            this.status = status;
            this.predictions = predictions;
        }

        private ArrayList<String> getStringsOfType(String type) {
            ArrayList<String> result = new ArrayList<String>();

            for (PredictionPiece piece : this.predictions) {
                if ((Type.equals("country") && piece.isThisPPAtType("country"))) {
                    result.add(piece.returnFirstString());
                    address.add(piece.description);
                }
                else if((Type.equals("sublocality_level_") && piece.isThisPPAtType("sublocality_level_") && (piece.returnSecondString().replaceAll("\\s+","").
                        equals(city.replaceAll("\\s+",""))))  || (Type.equals("locality") && piece.isThisPPAtType("locality"))) {
                    result.add(piece.description);
                    address.add(piece.description);

                }
            }


            return result;
        }


    }

    public void ConnectHttp(String url){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                jsonMessage = readJsonStream(inputStream);
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed" + "", e.getLocalizedMessage());
        }

        ArrayList<String> StringList = jsonMessage.getStringsOfType(Type);
        response.onAutocompFinish(StringList);
        response.getAddressFromGetAutoCompleteList(address);
        httpClient.getConnectionManager().shutdown();

    }





    public interface AutocompResponse {
        void onAutocompFinish(ArrayList<String> td);

        void getAddressFromGetAutoCompleteList(ArrayList<String> addrs);

    }


}

