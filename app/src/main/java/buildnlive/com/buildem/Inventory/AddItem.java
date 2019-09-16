package buildnlive.com.buildem.Inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.elements.Item;
import buildnlive.com.buildem.utils.UtilityofActivity;

public class AddItem extends AppCompatActivity {
    private List<Item> itemsList = new ArrayList<>();
    private App app;
    private TextView edit, view;
    private Fragment fragment;
    private ImageButton back;

    private UtilityofActivity utilityofActivity;
    private AppCompatActivity appCompatActivity=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        app = (App) getApplication();
        edit = findViewById(R.id.edit);
        view = findViewById(R.id.view);

//        utilityofActivity=new UtilityofActivity(appCompatActivity);
//        utilityofActivity.configureToolbar(appCompatActivity);
//
//        TextView toolbar_title=findViewById(R.id.title);
//        TextView toolbar_subtitle=findViewById(R.id.subtitle);
//        toolbar_title.setText("Inventory");
//        toolbar_subtitle.setText(App.projectName);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText("Inventory");

        fragment = ViewItemFragment.newInstance(app);
        changeScreen();
        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit();
                disableView();
                fragment = ViewItemFragment.newInstance(app);
                changeScreen();
            }
        });
        view = findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableView();
                disableEdit();
//                fragment = AddItemFragment.newInstance();
                fragment = AddItemFragment.newInstance();
                changeScreen();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.attendance_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id){
//            case R.id.nav_home:
//                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
//                return true;
//            case R.id.nav_profile:
//                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void changeScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.attendance_content, fragment)
                .commit();
    }

    private void disableView() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_left, null));
        } else {
            view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_left));
        }
        view.setTextColor(getResources().getColor(R.color.color2));
    }

    private void enableView() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_left_selected, null));
        } else {
            view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_left_selected));
        }
        view.setTextColor(getResources().getColor(R.color.white));
    }

    private void disableEdit() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            edit.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_right, null));
        } else {
            edit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_right));
        }
        edit.setTextColor(getResources().getColor(R.color.color2));
    }

    private void enableEdit() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            edit.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_right_selected, null));
        } else {
            edit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_right_selected));
        }
        edit.setTextColor(getResources().getColor(R.color.white));
    }
}
