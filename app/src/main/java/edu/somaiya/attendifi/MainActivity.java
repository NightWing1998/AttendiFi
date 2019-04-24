package edu.somaiya.attendifi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public void faculty(View v){
        Intent i = new Intent(this.getApplicationContext(),faculty_form.class);
        startActivity(i);
    }

    public void student(View v){
        Intent i = new Intent(this.getApplicationContext(),student_form.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
