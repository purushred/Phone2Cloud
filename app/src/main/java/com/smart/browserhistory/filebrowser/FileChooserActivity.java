package com.smart.browserhistory.filebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.smart.browserhistory.R;
import com.smart.browserhistory.fragment.dialog.CreateFolderFragment;

import java.io.File;
import java.io.FileFilter;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Purushotham on 20-11-2014.
 */

public class FileChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, FolderCreationListener {

    private File currentDir;
    private FileArrayAdapter adapter;
    private ListView dirListView;
    private FloatingActionButton selectFolderButton;
    private FloatingActionButton createFolderButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileexplorer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dirListView = (ListView) findViewById(R.id.dirListView);
        dirListView = (ListView) findViewById(R.id.dirListView);
        selectFolderButton = (FloatingActionButton) findViewById(R.id.selectFolderButton);
        selectFolderButton.setOnClickListener(this);

        createFolderButton = (FloatingActionButton) findViewById(R.id.createFolderButton);
        createFolderButton.setOnClickListener(this);

        dirListView.setOnItemClickListener(this);
        String dirPath = getIntent().getStringExtra("dirPath");
        currentDir = new File(dirPath);
        fill(currentDir);
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        this.setTitle(f.getName());
        getSupportActionBar().setSubtitle(f.getAbsolutePath());
        List<Item> items = new ArrayList<Item>();
        try {
            for (File file : dirs) {
                Date lastModDate = new Date(file.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (file.isDirectory()) {

                    File[] subDirs = file.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.isDirectory();
                        }
                    });
                    String num_item = "no items";
                    if (subDirs != null) {
                        num_item = subDirs.length + " items";
                    }
                    items.add(new Item(file.getName(), num_item,
                            date_modify, file.getAbsolutePath(), "directory_icon"));
                }
            }
        } catch (Exception e) {
        }
        Collections.sort(items);
        if (!f.getName().toLowerCase().contains("sdcard"))
            items.add(0, new Item("..", "Parent Directory", "", f.getParent(), "directory_icon"));
        adapter = new FileArrayAdapter(FileChooserActivity.this, R.layout.file_view_item, items);
        dirListView.setAdapter(adapter);
    }

    private void onDirSelected() {
        Intent intent = new Intent();
        intent.putExtra("DirPath", currentDir.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = adapter.getItem(position);
        if (item.getPath().equals("/")) {
            return;
        } else {
            currentDir = new File(item.getPath());
            fill(currentDir);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == selectFolderButton.getId()) {
            if (!currentDir.canWrite()) {
                Toast.makeText(this, "You don't have permissions to write here. Please select another folder.", Toast.LENGTH_LONG).show();
            } else {
                onDirSelected();
            }
        } else if (v.getId() == createFolderButton.getId()) {

            FragmentManager manager = getSupportFragmentManager();

            Fragment prevFragment = getSupportFragmentManager().findFragmentByTag("Dialog");
            FragmentTransaction ft = manager.beginTransaction();
            if (prevFragment != null) {
                ft.remove(prevFragment);
            }
            CreateFolderFragment fragment = new CreateFolderFragment();
            fragment.show(ft, "Dialog");

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFolderCreated(String folderName) {
        File file = new File(currentDir, folderName);
        boolean result = file.mkdir();
        if (result) {
            fill(file);
            currentDir = file;
            Toast.makeText(getApplicationContext(), "New directory created.", Toast.LENGTH_SHORT).show();
        } else if (file.isDirectory()) {
            Toast.makeText(getApplicationContext(), "Directory already exists.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to create new directory.", Toast.LENGTH_LONG).show();
        }
    }
}