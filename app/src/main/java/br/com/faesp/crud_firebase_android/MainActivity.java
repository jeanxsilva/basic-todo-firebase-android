package br.com.faesp.crud_firebase_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

   public class MainActivity extends AppCompatActivity {

    Button addButton;
    RecyclerView mRecyclerView;
    EditText title;
    EditText description;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private int id = 0;
    private LineAdapter lineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.buttonSave);
        mRecyclerView = findViewById(R.id.recyclerView);

        database = FirebaseDatabase.getInstance();;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myRef = database.getReference("Tasks");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TaskModel> tmList = new ArrayList<TaskModel>();

                for(DataSnapshot ds : snapshot.getChildren()){
                     TaskModel tm = ds.getValue(TaskModel.class);
                     tmList.add(tm);
                }

                if(tmList.size() > 0) {
                    id = tmList.get(tmList.size() - 1).getId() + 1;
                }

                lineAdapter.updateList(tmList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        setupView();
        setupRecycler();
    }

    private void setupView() {

    }

   // Método re sponsável por atualizar um usuário já existente na lista.
   public void taskDetails(TaskModel taskModel) {
       Intent intent = new Intent(this, TaskDetailsActivity.class);
       intent.putExtra("TaskModel", taskModel);
       startActivity(intent);
   }
    private void setupRecycler() {
        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        List<TaskModel> taskModelList = new ArrayList<TaskModel>();
        lineAdapter = new LineAdapter(taskModelList);
        mRecyclerView.setAdapter(lineAdapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void addTask(View v){

        title = findViewById(R.id.editTextTitle);
        description = findViewById(R.id.editTextDescription);

        myRef.child(String.valueOf(id)).child("Id").setValue(id);
        myRef.child(String.valueOf(id)).child("Title").setValue(title.getText().toString());
        myRef.child(String.valueOf(id)).child("Description").setValue(description.getText().toString());
    }


       class LineHolder extends RecyclerView.ViewHolder {
           public TextView title;
           public RelativeLayout line;
           public ImageButton deleteButton;

           public LineHolder(View view) {
               super(view);
               title = view.findViewById(R.id.main_line_title);
               deleteButton = view.findViewById(R.id.main_line_delete);
               line = view.findViewById(R.id.line);
           }
       }

       class LineAdapter extends RecyclerView.Adapter<LineHolder> {
           private List<TaskModel> taskModels;

           public LineAdapter(List<TaskModel> tasks) {
               taskModels = tasks;
           }

           @Override
           public LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
               return new LineHolder(LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.linear_vertical, parent, false));
           }

           @Override
           public void onBindViewHolder(LineHolder holder, int position) {
               holder.title.setText(String.format(Locale.getDefault(), "%s",
                       taskModels.get(position).getTitle()
               ));

               //holder.moreButton.setOnClickListener(view -> updateItem(position));
               holder.line.setOnClickListener(view -> { MainActivity.this.taskDetails(taskModels.get(position)); });
               holder.deleteButton.setOnClickListener(view -> removerItem(position));
           }

           @Override
           public int getItemCount() {
               return taskModels != null ? taskModels.size() : 0;
           }

           public void updateList(List<TaskModel> tasks) {
               this.taskModels = tasks;
               notifyDataSetChanged();
           }

           // Método responsável por remover um usuário da lista.
           private void removerItem(int position) {
               TaskModel taskToRemove = taskModels.get(position);
               int id = taskToRemove.getId();

               DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");
               Query taskQuery = ref.child(String.valueOf(id));

               taskQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                           taskSnapshot.getRef().removeValue();
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {
                       Log.e(TAG, "onCancelled", databaseError.toException());
                   }
               });

               notifyItemRemoved(position);
               notifyItemRangeChanged(position, taskModels.size());
           }
       }
   }