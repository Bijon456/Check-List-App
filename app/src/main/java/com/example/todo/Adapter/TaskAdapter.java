package com.example.todo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.AddTask;
import com.example.todo.MainActivity;
import com.example.todo.Model.TaskModel;
import com.example.todo.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<TaskModel> todoList;
    private MainActivity activity;
    private FirebaseFirestore firebaseFirestore;

    public  TaskAdapter(MainActivity mainActivity,List<TaskModel> todoList)
    {
        this.todoList=todoList;
        activity=mainActivity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.item_todo,parent,false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position)
    {
        TaskModel taskModel=todoList.get(position);
        firebaseFirestore.collection("task").document(taskModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editTask(int position)
    {
        TaskModel taskModel=todoList.get(position);
        Bundle bundle=new Bundle();
        bundle.putString("task",taskModel.getTask());
        bundle.putString("due",taskModel.getDue());
        bundle.putString("id",taskModel.TaskId);

        AddTask addTask=new AddTask();
        addTask.setArguments(bundle);
        addTask.show(activity.getSupportFragmentManager(),addTask.getTag());
    }

    public Context getContext()
    {
        return activity;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final TaskModel taskModel=todoList.get(position);
        holder.title.setText(taskModel.getTask());
        if(taskModel.getDue().toString().isEmpty()) {
            holder.dueDate.setText(taskModel.getDue());
        }
        else
            holder.dueDate.setText("Due on "+taskModel.getDue());
        holder.checkBox.setChecked(toBoolean(taskModel.getStatus()));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    firebaseFirestore.collection("task").document(taskModel.TaskId).update("status",1);
                }
                else
                {
                    firebaseFirestore.collection("task").document(taskModel.TaskId).update("status",0);
                }
            }
        });
    }
    private  boolean toBoolean(int s)
    {
        return s!=0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView dueDate;
        CheckBox checkBox;
        TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dueDate=itemView.findViewById(R.id.date);
            title=itemView.findViewById(R.id.title);
            checkBox=itemView.findViewById(R.id.checkbox);
        }
    }
}
