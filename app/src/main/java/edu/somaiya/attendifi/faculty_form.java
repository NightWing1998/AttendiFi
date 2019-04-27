package edu.somaiya.attendifi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class faculty_form extends AppCompatActivity {

    public void faculty(View v){
        EditText et = (EditText) findViewById(R.id.name);
        Intent i = new Intent(this.getApplicationContext(),faculty.class);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid name!Please enter a valid name", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Name",et.getText());
        et= (EditText) findViewById(R.id.department);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid department!Please enter a valid department", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Department",et.getText());
        et= (EditText) findViewById(R.id.division);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid division!Please enter a valid division", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Division",et.getText());
        et= (EditText) findViewById(R.id.subject);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid subject!Please enter a valid subject", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Subject",et.getText());
        et= (EditText) findViewById(R.id.topic);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid topic!Please enter a valid topic", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Topic",et.getText());
        et= (EditText) findViewById(R.id.timing);
        if(et.getText().length() == 0){
            Toast.makeText(this, "Invalid time!Please enter a valid time", Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("Timing",et.getText());
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_form);
    }
}
