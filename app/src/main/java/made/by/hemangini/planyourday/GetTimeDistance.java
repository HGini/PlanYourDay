package made.by.hemangini.planyourday;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
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


public class GetTimeDistance {
    private StringBuilder url = new StringBuilder();
    private StringBuilder urlorigin = new StringBuilder();
    private StringBuilder urldest = new StringBuilder();
    private static final String API_KEY = "AIzaSyBrGyGfOjn7VKmaYhkdGcoj98ZKI0mhxbg";
    private JSONMessage timedistance = new JSONMessage();
    private String[][] origindest;
    public AsyncResponse response = null;
    private InstantTask ftsk;
    private int IDofCurrentDurationTask;
    private int IDofLastDurationTask;
    private int finalValidInstantTaskID;
    private boolean LastTaskIsValidInstantTask;


    public GetTimeDistance(Context context, String[][] origindest, InstantTask ftsk, int IDofCurrentDurationTask, int IDofLastDurationTask, int finalValidInstantTaskID, boolean LastTaskIsValidInstantTask){
        this.origindest = origindest;
        response = (AsyncResponse) context;
        this.ftsk = ftsk;
        this.IDofCurrentDurationTask = IDofCurrentDurationTask;
        this.IDofLastDurationTask = IDofLastDurationTask;
        this.finalValidInstantTaskID = finalValidInstantTaskID;
        this.LastTaskIsValidInstantTask = LastTaskIsValidInstantTask;

    //Gets the distance and travel time between (origin, destination) pairs contained in the 'originDest' array
    //'timedistance' is the object with each row containing (time,distance) pair corresponding to each (origin,destination) pair in 'origindest'

        url = url.append("https://maps.googleapis.com/maps/api/distancematrix/json?key=" + API_KEY + "&origins=");
        //url should look like "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC"

        for (String[] row : origindest) {
            String or = row[0];
            urlorigin = urlorigin.append(or.replaceAll("\\s", "+").replaceAll(",",""));
            String des = row[1];
            urldest = urldest.append(des.replaceAll("\\s", "+").replaceAll(",",""));

            if (row != origindest[origindest.length - 1]) {
                //If 'row' is not the last row in the matrix
                urlorigin = urlorigin.append("%7C");
                urldest = urldest.append("%7C");
            }
        }
        url = url.append(urlorigin.append("&destinations=" + urldest.toString()));

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
        ArrayList origin_addresses = null;
        ArrayList destination_addresses = null;
        ArrayList rows = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                status = reader.nextString();
            } else if (name.equals("origin_addresses") && reader.peek() != JsonToken.NULL) {
                origin_addresses = readStringArray(reader);
            } else if (name.equals("destination_addresses") && reader.peek() != JsonToken.NULL) {
                destination_addresses = readStringArray(reader);
            } else if (name.equals("rows")) {
                rows = readRows(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new JSONMessage(status, origin_addresses, destination_addresses, rows);
    }



    public ArrayList readStringArray(JsonReader reader) throws IOException {
        ArrayList s = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            s.add(reader.nextString());
        }
        reader.endArray();
        return s;
    }



    public ArrayList readRows(JsonReader reader) throws IOException {
        ArrayList rows = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            rows.add(readRowsPieces(reader));
        }

        reader.endArray();
        return rows;

    }



    public ArrayList readRowsPieces(JsonReader reader) throws IOException {
        ArrayList rowsPieces = new ArrayList();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("elements")) {
                rowsPieces.add(readElement(reader));
            }
        }
        reader.endObject();
        return rowsPieces;
    }



    public ArrayList readElement(JsonReader reader) throws IOException {
        ArrayList elements = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            elements.add(readElementPieces(reader));
        }

        reader.endArray();
        return elements;
    }



    public ElementsPiece readElementPieces(JsonReader reader) throws IOException {
        String status = "";
        Duration duration = null;
        Distance distance = null;


        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("status")) {
                status = reader.nextString();
            } else if (name.equals("duration")) {
                duration = readDuration(reader);
            } else if (name.equals("distance")) {
                distance = readDistance(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new ElementsPiece(status, duration, distance);
    }



    private Duration readDuration(JsonReader reader) throws IOException {
        int value = 0;
        String text = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("value")) {
                value = reader.nextInt();
            } else if (name.equals("text")) {
                text = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new Duration(value, text);
    }



    private Distance readDistance(JsonReader reader) throws IOException {
        int value = 0;
        String text = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("value")) {
                value = reader.nextInt();
            } else if (name.equals("text")) {
                text = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new Distance(value, text);
    }



    private class JSONMessage {
        String status = "";
        ArrayList origin_addresses = null;
        ArrayList destination_addresses = null;
        ArrayList rows = null;

        JSONMessage() {
        }

        JSONMessage(String status, ArrayList origin_addresses, ArrayList destination_addresses, ArrayList rows) {
            this.status = status;
            this.origin_addresses = origin_addresses;
            this.destination_addresses = destination_addresses;
            this.rows = rows;
        }
    }



    private class ElementsPiece {
        String status = "";
        Duration duration = null;
        Distance distance = null;

        ElementsPiece(String status, Duration duration, Distance distance) {
            this.status = status;
            this.duration = duration;
            this.distance = distance;
        }
    }



    private class Duration {
        int value = 0;
        String text = "";

        Duration(int value, String text) {
            this.value = value;
            this.text = text;
        }
    }



    private class Distance {
        int value = 0;
        String text = "";

        Distance(int value, String text) {
            this.value = value;
            this.text = text;
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
                    timedistance = readJsonStream(inputStream);
                    inputStream.close();
                } else {
                    Log.d("JSON", "Failed to download file");
                }


            } catch (Exception e) {
                Log.d("readJSONFeed" + "", e.getLocalizedMessage());
            }

            return timedistance;
        }

        @Override
        public void onPostExecute(JSONMessage timedistance) throws NullPointerException{
            super.onPostExecute(timedistance);


            ArrayList<ArrayList<ArrayList<ElementsPiece>>> rows = (ArrayList<ArrayList<ArrayList<ElementsPiece>>>) timedistance.rows;
            int distance = rows.get(0).get(0).get(0).distance.value;
            int duration = rows.get(0).get(0).get(0).duration.value;
            ArrayList<Integer> TimeDistance = new ArrayList<Integer>();
            TimeDistance.add(distance);
            TimeDistance.add(duration);
            response.onProcessFinish(origindest, ftsk, TimeDistance, IDofCurrentDurationTask, IDofLastDurationTask, finalValidInstantTaskID, LastTaskIsValidInstantTask);

        }
    }

    public interface AsyncResponse{
        void onProcessFinish(String[][] OriginDestinations, InstantTask ftsk, ArrayList<Integer> td, int id, int id2, int id3, boolean LastTaskIsValidInstantTask);
    }
}

