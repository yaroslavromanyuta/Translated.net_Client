package com.yaroslav.translatednet_client;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import static com.yaroslav.translatednet_client.Constants.AUTHORITY;
import static com.yaroslav.translatednet_client.Constants.LANG_FROM;
import static com.yaroslav.translatednet_client.Constants.LANG_TO;
import static com.yaroslav.translatednet_client.Constants.LOG_TAG;
import static com.yaroslav.translatednet_client.Constants.TRANSL_FROM;
import static com.yaroslav.translatednet_client.Constants.TRANSL_ID;
import static com.yaroslav.translatednet_client.Constants.TRANSL_PATH;
import static com.yaroslav.translatednet_client.Constants.TRANSL_TO;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Translation> {

    ListView listView;
    EditText editText;

    Button button;
    SimpleCursorAdapter cursorAdapter;
    ProgressBar progressBar;
    String currentText;
    Toolbar myToolbar;
    Spinner spinLangFrom;
    Spinner spinLangTo;

    public static final Uri TRANSL_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TRANSL_PATH );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Cursor cursor = getContentResolver().query(TRANSL_CONTENT_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from [] = {TRANSL_FROM, TRANSL_TO, LANG_FROM, LANG_TO};
        int to [] = {R.id.txt_transl_from, R.id.txt_transl_to, R.id.txt_lang_from, R.id.txt_lang_to};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.listview_layout, cursor, from, to);

        listView =(ListView) findViewById(R.id.listView);
        listView.setAdapter(cursorAdapter);

        spinLangFrom = (Spinner) findViewById(R.id.spin_lng_from);
        spinLangTo = (Spinner) findViewById(R.id.spin_lng_to);

        button = (Button)findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Cursor cursor = getContentResolver().query(TRANSL_CONTENT_URI, null, TRANSL_FROM + " LIKE ?",
                        new String[]{"%" + s + "%"}, null);
                cursorAdapter.swapCursor(cursor);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDiatog(getTranslationFromDB(id));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setIcon(R.mipmap.ic_share);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                share();
                return true;

            }
        });

        return true;
    }

    private void share(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, currentText);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_via)));

    }








    public void showDiatog(Translation translation){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.translation)
                .setMessage(translation.getTranslFrom() + "  " +
                translation.getLangFrom() + "->" + translation.getLangTo() + "  " + translation.getTranslatedText())
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        currentText = translation.getTranslatedText();

        dialog.show();
    }

    public Translation getTranslationFromDB (long id){
        Uri newUri = ContentUris.withAppendedId(TRANSL_CONTENT_URI, id);
        Cursor cursor = getContentResolver().query(TRANSL_CONTENT_URI, null, TRANSL_ID + " LIKE ?",
                new String[]{"%" + String.valueOf(id) + "%"}, null);
        Translation translation = null;
        int i = cursor.getCount();

        if (cursor.moveToFirst()){


           String string = cursor.getString(1);


            Log.d(LOG_TAG, " " + i + "  " + "  " + TRANSL_FROM + "=" + string +
                "  " + TRANSL_TO + "=" + cursor.getString(2));
            translation = new Translation(cursor.getString(cursor.getColumnIndex(TRANSL_FROM)),
                cursor.getString(cursor.getColumnIndex(TRANSL_TO)),
                cursor.getString(cursor.getColumnIndex(LANG_FROM)),
                cursor.getString(cursor.getColumnIndex(LANG_TO)));



        }
        cursor.close();
        return translation;
    }

    public void onClick (View view){
        if (isNetworkOnline()){
            if (!(editText.getText().toString().isEmpty())) {
            getLoaderManager().restartLoader(0, null, this);
            getLoaderManager().getLoader(0).forceLoad();
            view.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            }
            else
            Toast.makeText(this,R.string.alert_no_text,Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this,R.string.alert_no_internet,Toast.LENGTH_SHORT).show();


    }



    @Override
    public Loader<Translation> onCreateLoader(int id, Bundle args) {
        String txt = editText.getText().toString();
        editText.setEnabled(false);
        Translation translation = new Translation();
        translation.setTranslFrom(txt);
        translation.setLangFrom(spinLangFrom.getSelectedItem().toString());
        translation.setLangTo(spinLangTo.getSelectedItem().toString());
        MyLoader loader = new MyLoader(this,translation);
        Log.d(LOG_TAG, "Loader oncreated.");

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Translation> loader, Translation data) {
        Log.d(LOG_TAG, "load finished");
        Log.d(LOG_TAG, "transl: " + "orig - " + data.getTranslFrom() + ", to - "
                + data.getTranslatedText() + ", langpair - " + data.getLangFrom() + "|" + data.getLangTo());
        if (!(data.getTranslatedText().equals(data.getTranslFrom()))) {
            showDiatog(data);
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRANSL_FROM, data.getTranslFrom());
            contentValues.put(TRANSL_TO, data.getTranslatedText());
            contentValues.put(LANG_FROM, data.getLangFrom());
            contentValues.put(LANG_TO, data.getLangTo());
            getContentResolver().insert(TRANSL_CONTENT_URI, contentValues);
            editText.setEnabled(true);
            button.setEnabled(true);
            listView.deferNotifyDataSetChanged();
            currentText = data.getTranslatedText();

        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_not_found)
                    .setCancelable(true);
            AlertDialog dialog = builder.create();

            dialog.show();
            editText.setEnabled(true);
            editText.setText("");
            button.setEnabled(true);
        }
        Log.d(LOG_TAG,"titel " + getSupportActionBar().getTitle());

        progressBar.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

    }

    @Override
    public void onLoaderReset(Loader<Translation> loader) {

    }


    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;

            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }
}
