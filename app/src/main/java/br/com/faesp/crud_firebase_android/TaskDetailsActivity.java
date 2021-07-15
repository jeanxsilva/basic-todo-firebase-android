package br.com.faesp.crud_firebase_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TaskDetailsActivity extends AppCompatActivity {
    TaskModel taskModel;
    TextView textId;
    TextView textTitle;
    TextView textDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskModel = (TaskModel) getIntent().getSerializableExtra("TaskModel");
        textId = (TextView) findViewById(R.id.textId);
        textTitle = (TextView) findViewById(R.id.textTitle);
        textDescription = (TextView) findViewById(R.id.textDescription);

        Log.d("Task", taskModel.getDescription());
        if(taskModel != null){
            textId.setText(String.valueOf(taskModel.getId()));
            textTitle.setText(taskModel.getTitle());
            textDescription.setText(taskModel.getDescription());
        }
    }

    public void returnHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}