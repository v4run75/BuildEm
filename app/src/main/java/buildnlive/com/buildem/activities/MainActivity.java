package buildnlive.com.buildem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import buildnlive.com.buildem.Inventory.AddItem;
import buildnlive.com.buildem.MarkAttendance.MarkAttendance;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.RequestItem.RequestItems;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView markAttendance, managInventory, issuedItems, requestItems, workProgress;
//    private AlarmManager alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        markAttendance = findViewById(R.id.mark_attendance);
        managInventory = findViewById(R.id.manage_inventory);
        issuedItems = findViewById(R.id.issued_items);
        requestItems = findViewById(R.id.request_items);
        workProgress = findViewById(R.id.work_progress);
        markAttendance.setOnClickListener(this);
        managInventory.setOnClickListener(this);
        issuedItems.setOnClickListener(this);
        requestItems.setOnClickListener(this);
        workProgress.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mark_attendance:
                startActivity(new Intent(getApplicationContext(), MarkAttendance.class));
                break;
            case R.id.manage_inventory:
                startActivity(new Intent(getApplicationContext(), AddItem.class));
                break;
            case R.id.issued_items:
                //startActivity(new Intent(getApplicationContext(), IssuedItems.class));
                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
                break;
            case R.id.request_items:
                startActivity(new Intent(getApplicationContext(), RequestItems.class));
                break;
            case R.id.work_progress:
                //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
