package made.by.hemangini.planyourday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import made.by.hemangini.planyourday.R;


public class Thankyou extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);

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
