package made.by.hemangini.planyourday;

import android.content.Context;
import android.os.AsyncTask;
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


public class GetLatLong {
    private String[] LatLong = new String[2];  //Stores the output latitude and longitude values for the input address
    private String API_KEY = "AIzaSyBrGyGfOjn7VKmaYhkdGcoj98ZKI0mhxbg";
    private StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?key=" + API_KEY + "&address=");
    private JSONMessage jsonMessage;
    private GetLatLongInterface Response;


    public GetLatLong(String address, Context context){
        Response = (GetLatLongInterface) context;
        String fullAddress  = address.replaceAll("\\s","+").replaceAll(",","");
        url = url.append(fullAddress);
        ConnectHttp connectHttp = new ConnectHttp();
        connectHttp.execute(url.toString());

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
        ArrayList<resultsPiece> results = new ArrayList<resultsPiece>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                status = reader.nextString();
            } else if (name.equals("results")) {
                results = readResults(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new JSONMessage(status, results);
    }


    public ArrayList<resultsPiece> readResults(JsonReader reader) throws IOException {
        ArrayList<resultsPiece> rows = new ArrayList<resultsPiece>();

        reader.beginArray();
        while (reader.hasNext()) {
            rows.add(readresultsPieces(reader));
        }

        reader.endArray();
        return rows;

    }


    public resultsPiece readresultsPieces(JsonReader reader) throws IOException {
        resultsPiece Resultspiece = new resultsPiece();


        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("geometry")) {
                Resultspiece.Geometry = readGeometry(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return Resultspiece;
    }


    private geometry readGeometry(JsonReader reader) throws IOException {
        geometry Geometry = new geometry();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("location")) {
                Geometry.Location = readLocation(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return Geometry;

    }


    private location readLocation(JsonReader reader) throws IOException {
        location Location = new location();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("lat")) {
                Location.latlong[0] = reader.nextString();
            }else if(name.equals("lng")){
                Location.latlong[1] = reader.nextString();
            }   else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return Location;

    }


    private class resultsPiece {
        private geometry Geometry = new geometry();

        resultsPiece(){}

        resultsPiece(geometry Geometry) {
            this.Geometry = Geometry;
        }
    }



    private class geometry {
        private location Location = new location();

        geometry(){};

        geometry(location Location) {
            this.Location = Location;
        }

    }


    private class location {
        private String[] latlong = new String[2];

        location(){}

        location(String[] latlong) {
            this.latlong = latlong;
        }
    }



    private class JSONMessage {
        String status = "";
        ArrayList<resultsPiece> results = new ArrayList<resultsPiece>();

        JSONMessage() {
        }

        JSONMessage(String status, ArrayList<resultsPiece> results) {
            this.status = status;
            this.results = results;
        }

    }




    public class ConnectHttp extends AsyncTask<String, Void, JSONMessage> {

        @Override
        public JSONMessage doInBackground(String... url) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url[0]);
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

            httpClient.getConnectionManager().shutdown();

            return jsonMessage;
        }

        @Override
        public void onPostExecute(JSONMessage jsonMessage1) throws NullPointerException{
            super.onPostExecute(jsonMessage1);

            LatLong = jsonMessage1.results.get(0).Geometry.Location.latlong;
            Response.getLatLongInterfaceMethod(LatLong);
        }
    }






    public interface GetLatLongInterface{
        void getLatLongInterfaceMethod(String[] latlong);

    }



}
