package hapkiduki.net.empresis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btnRefe:
                intent = new Intent(MainActivity.this, ReferenciaActivity.class);
                break;

            case R.id.btnTerce:
                intent = new Intent(MainActivity.this, TerceroActivity.class);
        }
        startActivity(intent);
    }
}
