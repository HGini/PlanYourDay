package made.by.hemangini.planyourday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import made.by.hemangini.planyourday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class PlannedDay extends Activity implements FileSaveFragment.Callbacks {

    private ListView ItemList;
    private Button SaveFile;
    private Button NextTask;
    private BaseAdapter tasklistadapter;
    private ArrayList<InstantTask> FinalTaskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_day);

        SaveFile = (Button) findViewById(R.id.Close);
        NextTask = (Button) findViewById(R.id.NextTask0);
        ItemList = (ListView) findViewById(R.id.nonemptyItemList);
        ItemList.setEmptyView(findViewById(R.id.emptyItemList));

        FinalTaskList = Globals.FinalTasklist;
        tasklistadapter = new DisplayListAdapter(this, FinalTaskList);
        ItemList.setAdapter(tasklistadapter);



        NextTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to the home page to add another task
                Intent k = new Intent(view.getContext(), Main.class);
                startActivity(k);
            }
        });

        SaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save the planned day in a .txt file
                String txt = getResources().getString(R.string.defaultExtension);
                String fragTag = getResources().getString(R.string.tag_fragment_FileSave);

                // Get an instance supplying a default extension, captions and
                // icon appropriate to the calling application/activity.
                FileSaveFragment fsf = FileSaveFragment.newInstance(txt,
                        R.string.resourceID_OK,
                        R.string.resourceID_Cancel,
                        R.string.resourceID_Title,
                        R.string.resourceID_EditHint,
                        R.drawable.ic_launcher);
                fsf.show(getFragmentManager(), fragTag);

            }
        });

    }


    // Example validation showing use of provided helper methods.
    public boolean onCanSave(String absolutePath, String fileName) {

        boolean canSave = true;

        // Catch the really stupid case.
        if (absolutePath == null || absolutePath.length() ==0 ||
                fileName == null || fileName.length() == 0) {
            canSave = false;
            Toast.makeText(this,"Please enter file name", Toast.LENGTH_SHORT).show();
        }

        // Do we have a filename if the extension is thrown away?
        if (canSave) {
            String copyName = FileSaveFragment.NameNoExtension(fileName);
            if (copyName == null || copyName.length() == 0 ) {
                canSave = false;
                Toast.makeText(this,"Please enter file name", Toast.LENGTH_SHORT).show();
            }
        }

        // Allow only alpha-numeric names. Simplify dealing with reserved path
        // characters.
        if (canSave) {
            if (!FileSaveFragment.IsAlphaNumeric(fileName)) {
                canSave = false;
                Toast.makeText(this, "File name contains invalid characters", Toast.LENGTH_SHORT).show();
            }
        }

        // No overwrite of an existing file.
        if (canSave) {
            if (FileSaveFragment.FileExists(absolutePath, fileName)) {
                canSave = false;
                Toast.makeText(this, "File already exists", Toast.LENGTH_SHORT).show();
            }
        }

        return canSave;
    }




    // Act on a validated [positive] button click or a [negative] button
    // click. On [negative] click path and name are both null.
    public void onConfirmSave(String absolutePath, String fileName) {
        if (absolutePath != null && fileName != null) {
            // Recommend that file save for large amounts of data is handled
            // by an AsyncTask.
            try {
                File myFile = new File(absolutePath + File.separator + fileName + ".txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                for(InstantTask tsk : FinalTaskList) {
                    String linetsk = String.valueOf(tsk.getTimeHour()) + ":" + String.valueOf(tsk.getTimeMinute()) + ":" +
                            String.valueOf(tsk.getTimeSecond()) + "      " + tsk.getNote() + "      " + "at" + "      " +  tsk.getPlace().split(",")[0];
                    myOutWriter.append(linetsk);
                    myOutWriter.append("\r\n");
                    myOutWriter.append("\r\n");
                }
                myOutWriter.close();
                fOut.close();
                Toast.makeText(getBaseContext(),
                        "File saved",
                        Toast.LENGTH_SHORT).show();




                //Go to the final activity
                Intent j = new Intent(this, Thankyou.class);
                startActivity(j);


            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }
}
