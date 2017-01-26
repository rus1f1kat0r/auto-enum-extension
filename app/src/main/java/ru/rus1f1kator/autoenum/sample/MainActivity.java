package ru.rus1f1kator.autoenum.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Count> list = getCounts();
        Count.CountVisitor<String, Integer> visitor = new CounterVisitor();
        for (Count each : list) {
            Log.d("VISITOR", each.accept(visitor, 2));
        }
    }

    @NonNull
    private List<Count> getCounts() {
        return Arrays.asList(Count.FOURTH, Count.THIRD, Count.SECOND, Count.FIRST);
    }

    private static class CounterVisitor implements Count.CountVisitor<String, Integer> {
        @Override
        public String first(Integer param) {
            return "" + 1 * param;
        }

        @Override
        public String second(Integer param) {
            return "" + 2 * param;
        }

        @Override
        public String third(Integer param) {
            return "" + 3 * param;
        }

        @Override
        public String fourth(Integer param) {
            return "" + 4 * param;
        }
    }
}
