package made.by.hemangini.planyourday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import made.by.hemangini.planyourday.R;

public class NoTasksForToday extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_tasks_for_today);

        Button NewTask = (Button) findViewById(R.id.Nexttask);
        NewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the main activity to set a new task
                Intent k = new Intent(view.getContext(), Main.class);
                startActivity(k);
            }
        });

    }



}
