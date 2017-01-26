package ru.rus1f1kator.autoenum.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Count.FIRST.accept(new Count.CountVisitor<String, Integer>() {
            @Override
            public String first(Integer param) {
                return "" + param;
            }

            @Override
            public String second(Integer param) {
                return "" + param;
            }

            @Override
            public String third(Integer param) {
                return null;
            }

            @Override
            public String fourth(Integer param) {
                return null;
            }
        }, 0);
    }
}
