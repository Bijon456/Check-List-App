package com.example.todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.todo.Adapter.TaskAdapter;
import com.example.todo.Model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private FirebaseFirestore firebaseFirestore;
    private TaskAdapter adapter;
    private List<TaskModel> list;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private ImageButton toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerView);
        fab=findViewById(R.id.fab);
        toast=findViewById(R.id.Toast);
        firebaseFirestore=FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "- Swipe the task to the right to delete the task, or to the left to update it.", Toast.LENGTH_LONG).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTask.newInstance().show(getSupportFragmentManager(),AddTask.TAG);
            }
        });
        list=new ArrayList<>();
        adapter=new TaskAdapter(MainActivity.this,list);

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new Touch(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        showData();
    }
    private void showData()
    {
        query=firebaseFirestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
               listenerRegistration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange:value.getDocumentChanges())
                {
                    if(documentChange.getType()==DocumentChange.Type.ADDED)
                    {
                        String id=documentChange.getDocument().getId();
                        TaskModel taskModel=documentChange.getDocument().toObject(TaskModel.class).withId(id);

                        list.add(taskModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        list.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}