package hapkiduki.net.empresis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import hapkiduki.net.empresis.Activities.EmpresisActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){

            case R.id.btnTerce:
                intent = new Intent(MainActivity.this, TerceroActivity.class);
                break;
            case R.id.btnPer:
                intent = new Intent(MainActivity.this, EmpresisActivity.class);
                break;
        }
        startActivity(intent);
    }
}
