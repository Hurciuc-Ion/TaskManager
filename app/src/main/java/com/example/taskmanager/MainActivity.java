package com.example.taskmanager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView userList;
    EditText userFilterEditText; // Added EditText for filtering
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = findViewById(R.id.list);
        userFilterEditText = findViewById(R.id.userFilter); // Initialize EditText

        userFilterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String filterText = charSequence.toString().trim();
                filterDatabase(filterText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
    }

    public void deleteCompletedItems(View view) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Assuming your completion column is named "completed" in your database
        db.delete(DatabaseHelper.TABLE, DatabaseHelper.COLUMN_COMPLETED + " = 1", null);

        // Refresh the list after deletion
        refreshList();

        // Close the database
        db.close();
    }

    private void refreshList() {
        db = databaseHelper.getReadableDatabase();
        userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);
        userAdapter.changeCursor(userCursor);
    }

    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();
        userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);

        String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_DEADLINE, DatabaseHelper.COLUMN_COMPLETED};
        int[] views = new int[]{android.R.id.text1, android.R.id.text2};

        userAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                userCursor,
                headers,
                views,
                0
        );

        userAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text2) {
                    TextView detailsTextView = (TextView) view;

                    // Check if COLUMN_COMPLETED and COLUMN_DEADLINE exist in the cursor
                    int completedIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_COMPLETED);
                    int deadlineIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DEADLINE);

                    if (completedIndex != -1 && deadlineIndex != -1) {
                        int completedValue = cursor.getInt(completedIndex);
                        String deadline = cursor.getString(deadlineIndex);

                        String statusText;
                        if (completedValue == 1) {
                            statusText = "Îndeplinit!";
                        } else {
                            statusText = "În proces...";
                        }

                        // Include deadline information
                        String details = String.format(Locale.getDefault(), "Deadline: %s\nStatus: %s", deadline, statusText);
                        detailsTextView.setText(details);
                    }

                    return true;
                }

                return false;
            }

        });

        userList.setAdapter(userAdapter);
    }


    public void add(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }

    private void filterDatabase(String filterText) {
        db = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + filterText + "%"};
        userCursor = db.query(DatabaseHelper.TABLE, null, selection, selectionArgs, null, null, null);
        userAdapter.changeCursor(userCursor);
    }
}