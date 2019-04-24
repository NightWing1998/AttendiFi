package edu.somaiya.attendifi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class student_form extends AppCompatActivity {

    public void student(View v){
        EditText et = (EditText) findViewById(R.id.edit1);
        Intent i = new Intent(this.getApplicationContext(),student.class);
        i.putExtra("Name",et.getText());
        et= (EditText) findViewById(R.id.edit2);
        i.putExtra("Class",et.getText());
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_form);
    }
}
