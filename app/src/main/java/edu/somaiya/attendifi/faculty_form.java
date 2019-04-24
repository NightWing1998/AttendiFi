package edu.somaiya.attendifi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class faculty_form extends AppCompatActivity {

    public void faculty(View v){
        EditText et = (EditText) findViewById(R.id.edit1);
        Intent i = new Intent(this.getApplicationContext(),faculty.class);
        i.putExtra("Name",et.getText());
        et= (EditText) findViewById(R.id.edit2);
        i.putExtra("Class",et.getText());
        et= (EditText) findViewById(R.id.edit3);
        i.putExtra("Division",et.getText());
        et= (EditText) findViewById(R.id.edit4);
        i.putExtra("Subject",et.getText());
        et= (EditText) findViewById(R.id.edit5);
        i.putExtra("Topic",et.getText());
        et= (EditText) findViewById(R.id.edit6);
        i.putExtra("Timing",et.getText());
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_form);
    }
}
