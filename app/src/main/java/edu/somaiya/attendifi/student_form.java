package edu.somaiya.attendifi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class student_form extends AppCompatActivity {

    public void student(View v){
        EditText et = (EditText) findViewById(R.id.name);
        Intent i = new Intent(this.getApplicationContext(),student.class);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid name field!Please enter a valid name", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Name",et.getText());
        et= (EditText) findViewById(R.id.roll_number);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid roll number!Please enter a valid roll number", Toast.LENGTH_SHORT).show();
            return;
        } else{
            i.putExtra("Class",et.getText());
        }
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_form);
    }
}
