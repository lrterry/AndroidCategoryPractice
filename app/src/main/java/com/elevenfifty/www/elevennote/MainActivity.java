package com.elevenfifty.www.elevennote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String NOTE_INDEX = "com.elevenfifty.www.elevennote.NOTE_INDEX";
    public static final String NOTE_TITLE = "com.elevenfifty.www.elevennote.NOTE_TITLE";
    public static final String NOTE_TEXT = "com.elevenfifty.www.elevennote.NOTE_TEXT";
    public static final String NOTE_CATEGORY = "com.elevenfifty.www.elevennote.NOTE_CATEGORY";

    // Filename for our persistence
    String filename = "CategoryItems";


    private ListView notesList;
    // Holds all categories in our app
    private List<Category> categories = new ArrayList<>();
    // Stores categories + notes to pass to our custom adapter
    private ArrayList<Object> allItems = new ArrayList<>();
    // Custom adapter to display categories and notes to list view
    private CategoryAdapter categoryAdapter;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesList = (ListView)findViewById(R.id.list_view);

        // Create a file to save data to
        File filesDir = this.getFilesDir();
        final File categoryFile = new File(filesDir + File.separator + filename);

        // If we have save data load it, else setup default notes
        if(categoryFile.exists()) {
            readFile(categoryFile);
        } else {
            setupNotes();
            writeFile();
        }

        // Create our listview and update our items array
        notesList = (ListView)findViewById(R.id.list_view);
        updateAllItems();

        // Set our custom adapter to handle our notesList
        categoryAdapter = new CategoryAdapter(this, allItems);
        notesList.setAdapter(categoryAdapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            // Create a new note from the data passed from our detail activity
            Note note = new Note(data.getStringExtra(NOTE_TITLE), data.getStringExtra(NOTE_TEXT), new Date(), Integer.parseInt(data.getStringExtra(NOTE_CATEGORY)));

            // Add the new note to the defined category, write to files, update our adapter
            categories.get(note.getCategoryID()).notes.add(note);
            writeFile();
            updateAllItems();
            categoryAdapter.notifyDataSetChanged();
        }
    }

    private void setupNotes() {
        categories.add(new Category("Home", new ArrayList<Note>()));
        categories.add(new Category("Work", new ArrayList<Note>()));
        categories.add(new Category("Misc", new ArrayList<Note>()));

        for(int i = 0; i < categories.size(); i++) {
            categories.get(i).notes.add(new Note("Test", "Test", new Date(), i));
            categories.get(i).notes.add(new Note("Test", "Test", new Date(), i));
            categories.get(i).notes.add(new Note("Test", "Test", new Date(), i));
        }
    }

    /*
    JSON/GSON persistence method. Reads back from our saved file an array
    of our categories with our notes held inside
     */
    private void readFile(File categoryFile) {
        FileInputStream inputStream = null;
        String categoryText ="";
        try {
            inputStream = openFileInput(categoryFile.getName());
            byte[] input = new byte[inputStream.available()];
            while(inputStream.read(input) != -1) {}
            categoryText = new String(input);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Determine type of our collection
            Type collectionType = new TypeToken<List<Category>>(){}.getType();
            // Pull out our categories in a list
            List<Category> categoryList = gson.fromJson(categoryText, collectionType);
            // Create a LinkedList that we can edit from our categories list and save it
            // to our global categories
            categories = new LinkedList(categoryList);
        }
    }

    /*
    Persistence method to save our categories array to a JSON/GSON file
     */
    private void writeFile() {
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            String json = gson.toJson(categories);
            byte[] bytes = json.getBytes();
            outputStream.write(bytes);

            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (Exception ignored) {}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, NoteDetailActivity.class);

            intent.putExtra(NOTE_TITLE, "");
            intent.putExtra(NOTE_TEXT, "");
            intent.putExtra(NOTE_CATEGORY, "");

            startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateAllItems() {
        allItems.clear();
        for(int i = 0; i < categories.size(); i++) {
            allItems.add(categories.get(i).getName());
            for(int j = 0; j < categories.get(i).notes.size(); j++) {
                allItems.add(categories.get(i).notes.get(j));
            }
        }
    }
}
