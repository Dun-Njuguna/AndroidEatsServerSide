package com.dunk.androideatsserverside;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView (R.id.btnSignin) Button btnSignIn;
    @BindView (R.id.textSlogan) Button textSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Nabila.ttf");
    }
}
