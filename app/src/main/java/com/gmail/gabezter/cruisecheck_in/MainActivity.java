package com.gmail.gabezter.cruisecheck_in;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public File passengersFile;
    public Uri uri;

    public ArrayList<String> listPassengersName = new ArrayList<>();
    public ArrayList<String> listPassengersDeck = new ArrayList<>();
    public ArrayList<String> listPassengersNumB = new ArrayList<>();
    public ArrayList<String> listPassengersDay1 = new ArrayList<>();
    public ArrayList<String> listPassengersDay2 = new ArrayList<>();

    String trip = "No Trip Selected";
    ArrayList<Integer> countIDa1 = new ArrayList<>();
    ArrayList<Integer> countIDa2 = new ArrayList<>();
    final static String TAG = "CruiseCheckIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException (Thread thread, Throwable e) {
                        handleUncaughtException (thread, e);
                        Log.e("ME", "ME");
                    }
                });
        setContentView(R.layout.activity_main);

        checkPerms(this);
        passengersFile = new File(getFilesDir() + "/passengers.csv");

        if(!passengersFile.exists()){
            importFile();
        }else{
            if(passengersFile.exists()){
                readPassengers(readFromFile());
            }else{
                showFileChooser();
            }
        }


        TextView currentMeet = findViewById(R.id.selectedTrip);
        currentMeet.setText(trip);

        final TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Day 1");
        spec.setContent(R.id.Day_1);
        spec.setIndicator("Day 1");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Day 2");
        spec.setContent(R.id.Day_2);
        spec.setIndicator("Day 2");
        tabHost.addTab(spec);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem mi = menu.findItem(R.id.fileMenu);
        getMenuInflater().inflate(R.menu.sub_file_menu, mi.getSubMenu());

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                TableLayout tableLayout;
                TabHost tabHost = findViewById(R.id.tabHost);
                if (tabHost.getCurrentTab() == 0) {
                    tableLayout = findViewById(R.id.passengerTableD1);
                } else {
                    tableLayout = findViewById(R.id.passengerTableD2);
                }
                    for (int i = 1, j = tableLayout.getChildCount(); i < j; i++) {
                        View view = tableLayout.getChildAt(i);
                        if (view instanceof TableRow) {
                            TableRow row = (TableRow) view;
                            TextView me = (TextView) row.getChildAt(0);
                            String text = me.getText().toString();
                            if (!text.contains(newText)) {
                                row.setVisibility(View.GONE);
                            }
                        }
                    }
                if(newText.equals("")){
                    for (int i = 1, j = tableLayout.getChildCount(); i < j; i++) {
                        View view = tableLayout.getChildAt(i);
                        if (view instanceof TableRow) {
                            TableRow row = (TableRow) view;
                            row.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent help = new Intent(this, HowToUse.class);
        Intent deck = new Intent(this, DeckAssign.class);
        switch (item.getItemId()) {
            /*case R.id.howToUse:
                startActivity(help);
                return true; */
            case R.id.importFile:
                importFile();
                return true;
            case R.id.deckAssign:
                startActivity(deck);
                return true;
            case R.id.exportFile:
                exportFile();
                return true;
            case R.id.submit:
                TabHost tabHost = findViewById(R.id.tabHost);
                readCheckIn(tabHost.getCurrentTab());
                alterDocument();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readCheckIn(int currentTab) {
        Log.e(TAG, "readCheckIn: " + currentTab);
        if(currentTab == 0){
            int total = 0;
            int t;
            int id;
            String c;
            listPassengersDay1.clear();
            for(int i = 0; i < countIDa1.size(); i++){
                t = total;
                RadioGroup radioGroup = findViewById(countIDa1.get(i));
                id = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(id);
                if(rb.getText() == "Y"){
                    listPassengersDay1.add("Y");
                    total = t + Integer.parseInt(listPassengersNumB.get(i));
                }else{
                    listPassengersDay1.add("N");
                }
            }
            new AlertDialog.Builder(this).setMessage("There are " + total + " on board today.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        }else if(currentTab == 1){
            int total = 0;
            int t;
            int id;
            listPassengersDay2.clear();
            for(int i = 0; i < countIDa2.size(); i++){
                t = total;
                RadioGroup radioGroup = findViewById(countIDa2.get(i));
                id = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(id);
                if(rb.getText() == "Y"){
                    listPassengersDay2.add("Y");
                    total = t + Integer.parseInt(listPassengersNumB.get(i));
                }else{
                    listPassengersDay2.add("N");
                }
            }
            new AlertDialog.Builder(this).setMessage("There are " + total + " on board today.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        }else{
            Log.e(TAG,"Invalid tab");
        }
    }

    public void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType("*/*");      //all files
        intent.setType("text/comma-separated-values");
        String[] mimetypes = {"text/csv", "text/comma-separated-values", "application/csv"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 1);
            Log.e("Clerk", "File Pick");
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            try{
                readPassengers(readTextFromUri(uri));
                setPassengerFile(uri);
                Log.e("Clerk", uri.toString());
            }catch (IOException e){e.printStackTrace();}
        }
    }

    public void appendLog(StackTraceElement[] text){
        File logFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();

            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            Log.e("Create", logFile.getAbsolutePath());
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            for (StackTraceElement s : text){
                buf.append(s.toString());
                buf.newLine();
            }
            buf.close();
            Toast.makeText(this, "Clerk Check crashed. Created log file in downloads folder." + logFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void handleUncaughtException (Thread thread, Throwable e) {

        // The following shows what I'd like, though it won't work like this.
        appendLog(e.getStackTrace());
        appendLog(e.getCause().getStackTrace());
        thread.interrupt();
        // Add some code logic if needed based on your requirement
    }

    static void checkPerms(final Context context){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(context)
                        .setMessage("This app needs permission to write a file.")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        }).show();

                Log.e(TAG, "requestWritePermission: "+ "229" );
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                new AlertDialog.Builder(context)
                        .setMessage("This app needs permission to write a file.")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        }).show();

                Log.e(TAG, "requestWritePermission: "+ "244" );
                // No explanation needed; request the permission

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
    }

}

    public String[] readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        String line;
        ArrayList<String> text = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            text.add(line);
        }
        inputStream.close();
        reader.close();
        return text.toArray(new String[0]);
    }

    public void importFile(){
        showFileChooser();
    }

    public void exportFile(){

        String[] mFile = readFromFile();
        String name = mFile[0].replace(",","");
        name = name.replace("/", "-");
        File toFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + name +".csv");
        if(!(toFile.exists())) {
            try {
                FileWriter fw = new FileWriter(toFile);
                PrintWriter writer = new PrintWriter(fw);
                for(String text : mFile){
                    writer.println(text);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            toFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + name +".csv");
            try {
                FileWriter fw = new FileWriter(toFile);
                PrintWriter writer = new PrintWriter(fw);
                for(String text : mFile){
                    writer.println(text);
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        goToFile(toFile.toString() , name);
    }

    public void alterDocument() {

        String[] name = listPassengersName.toArray(new String[0]);
        String[] number = listPassengersNumB.toArray(new String[0]);
        String[] deck = listPassengersDeck.toArray(new String[0]);
        String[] day1 = listPassengersDay1.toArray(new String[0]);
        String[] day2 = listPassengersDay2.toArray(new String[0]);
        File file = passengersFile;

        try {
            String[] reply = readFromFile();
            ArrayList<String> list = new ArrayList<>();

            list.add(reply[0]);
            list.add(reply[1]);
            for (int i = 0; i < name.length; i++){
                Log.e(Integer.toString(i), name[i]+","+number[i]+","+deck[i]+","+day1[i]+","+day2[i]);
                list.add(name[i]+","+number[i]+","+deck[i]+","+day1[i]+","+day2[i]);
            }
            OutputStream outputStream = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            for(String line: list){
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readPassengers(String[] schools){
        listPassengersDay1.clear();
        listPassengersName.clear();
        listPassengersDay2.clear();
        listPassengersNumB.clear();
        listPassengersDeck.clear();
        trip = schools[0];
        trip = trip.replace("," ," ");
        TextView currentMeet = findViewById(R.id.selectedTrip);
        currentMeet.setText(trip);
        for(int i = 2; i < schools.length; i++){
            Log.i(Integer.toString(i), schools[i]);
            String[] tokens = schools[i].split(",");

            listPassengersName.add(tokens[0]);
            if(tokens.length >= 2 && tokens[1].length() >0){
                listPassengersNumB.add(tokens[1]);
            }else{
                listPassengersNumB.add("0");
            }
            if(tokens.length >= 3 && tokens[2].length() > 0){
                listPassengersDeck.add(tokens[2]);
            }else{
                listPassengersDeck.add("0");
            }
            if(tokens.length >= 4 && tokens[3].length() >0 ){
                listPassengersDay1.add(tokens[3]);
            }else{
                listPassengersDay1.add("N");
            }
            if(tokens.length >= 5 && tokens[4].length() >0){
                listPassengersDay2.add(tokens[4]);
            }else{
                listPassengersDay2.add("N");
            }
        }
        dayOneCreate(listPassengersName.toArray(new String[0]), listPassengersNumB.toArray(new String[0]), listPassengersDeck.toArray(new String[0]), listPassengersDay1.toArray(new String[0]));
        dayTwoCreate(listPassengersName.toArray(new String[0]), listPassengersNumB.toArray(new String[0]), listPassengersDeck.toArray(new String[0]), listPassengersDay2.toArray(new String[0]));
    }

    public void dayOneCreate(String[] name, String[] number, String[] deck, String[] check){
        TableLayout ll = findViewById(R.id.passengerTableD1);
        TextView colName;
        TextView colNum;
        TextView colDeck;
        TextView colCheckTxt;
        RadioGroup radioGroup;
        RadioButton radioYes;
        RadioButton radioNo;

        TableRow row = new TableRow(this);
        colName = new TextView(this);
        colNum = new TextView(this);
        colDeck = new TextView(this);
        colCheckTxt = new TextView(this);

        colName.setText("Passenger");
        colNum.setText("Number of \n Passengers");
        colDeck.setText("Assigned \n Deck");
        colCheckTxt.setText("Checked In \n Y | N");

        colName.setGravity(Gravity.CENTER);
        colNum.setGravity(Gravity.CENTER);
        colDeck.setGravity(Gravity.CENTER);
        colCheckTxt.setGravity(Gravity.CENTER);

        ll.removeAllViewsInLayout();

        row.addView(colName);
        row.addView(colNum);
        row.addView(colDeck);
        row.addView(colCheckTxt);
        ll.addView(row);

        int i = 0;
        countIDa1.clear();
        for (String school : name) {
            TableRow ros = new TableRow(this);
            colName = new TextView(this);
            colNum = new TextView(this);
            colDeck = new TextView(this);
            radioGroup = new RadioGroup(this);
            radioNo = new RadioButton(this);
            radioYes = new RadioButton(this);

            radioGroup.setOrientation(LinearLayout.HORIZONTAL);

            radioNo.setText("N");
            radioYes.setText("Y");

            radioGroup.addView(radioYes);
            radioGroup.addView(radioNo);

            switch (check[i]){
                case "Y":
                    radioGroup.check(radioYes.getId());
                    break;
                case "N":
                    radioGroup.check(radioNo.getId());
            }

            colName.setText(school);
            colNum.setText(number[i]);
            colDeck.setText(deck[i]);
            i++;

            colName.setGravity(Gravity.CENTER);
            colNum.setGravity(Gravity.CENTER);
            colDeck.setGravity(Gravity.CENTER);
            radioGroup.setGravity(Gravity.CENTER);
            int id;
                id = View.generateViewId();
                countIDa1.add(id);

            radioGroup.setId(id);

            ros.addView(colName);
            ros.addView(colNum);
            ros.addView(colDeck);
            ros.addView(radioGroup);
            ll.addView(ros);
        }
    }

    public void dayTwoCreate(String[] name, String[] number, String[] deck, String[] check){
        TableLayout ll = findViewById(R.id.passengerTableD2);
        TextView colName;
        TextView colNum;
        TextView colDeck;
        TextView colCheckTxt;
        RadioGroup radioGroup;
        RadioButton radioYes;
        RadioButton radioNo;

        TableRow row = new TableRow(this);
        colName = new TextView(this);
        colNum = new TextView(this);
        colDeck = new TextView(this);
        colCheckTxt = new TextView(this);

        colName.setText("Passenger");
        colNum.setText("Number of \n Passengers");
        colDeck.setText("Assigned \n Deck");
        colCheckTxt.setText("Checked In \n Y | N");

        colName.setGravity(Gravity.CENTER);
        colNum.setGravity(Gravity.CENTER);
        colDeck.setGravity(Gravity.CENTER);
        colCheckTxt.setGravity(Gravity.CENTER);

        ll.removeAllViewsInLayout();

        row.addView(colName);
        row.addView(colNum);
        row.addView(colDeck);
        row.addView(colCheckTxt);
        ll.addView(row);

        int i = 0;
        countIDa2.clear();
        for (String school : name) {
            TableRow ros = new TableRow(this);
            colName = new TextView(this);
            colNum = new TextView(this);
            colDeck = new TextView(this);
            radioGroup = new RadioGroup(this);
            radioNo = new RadioButton(this);
            radioYes = new RadioButton(this);
            radioGroup.setOrientation(LinearLayout.HORIZONTAL);

            radioNo.setText("N");
            radioYes.setText("Y");

            radioGroup.addView(radioYes);
            radioGroup.addView(radioNo);

            switch (check[i]){
                case "Y":
                    radioGroup.check(radioYes.getId());
                    break;
                case "N":
                    radioGroup.check(radioNo.getId());
            }

            colName.setText(school);
            colNum.setText(number[i]);
            colDeck.setText(deck[i]);
            i++;

            colName.setGravity(Gravity.CENTER);
            colNum.setGravity(Gravity.CENTER);
            colDeck.setGravity(Gravity.CENTER);
            radioGroup.setGravity(Gravity.CENTER);
            int id;
            id = View.generateViewId();
            countIDa2.add(id);

            radioGroup.setId(id);

            ros.addView(colName);
            ros.addView(colNum);
            ros.addView(colDeck);
            ros.addView(radioGroup);
            ll.addView(ros);
        }
    }

    public String[] readFromFile() {

        ArrayList<String> text = new ArrayList<>();

        try {
            InputStream inputStream = new FileInputStream(passengersFile);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    text.add(receiveString);
                }

                inputStream.close();
            }
        }catch (IOException e){}
        return text.toArray(new String[0]);
    }

    public void setPassengerFile(Uri uriF) {
        File file = passengersFile;
        try {
            String[] texts = readTextFromUri(uriF);
            FileWriter fw = new FileWriter(file);
            PrintWriter writer = new PrintWriter(fw);
            for(String text : texts){
                writer.println(text);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToFile(final String loc, String name){
        AlertDialog.Builder toFile = new AlertDialog.Builder(MainActivity.this);
        toFile.setTitle("Exported");
        toFile.setMessage("This meet has been exported to the Downloads folder and is named: " + name);
       /* toFile.setPositiveButton("Go to File", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri.Builder uriFile = new Uri.Builder();
                uriFile.scheme("content");
                uriFile.path(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/");
                Uri file = uriFile.build();
                Intent goToFile = new Intent(Intent.ACTION_VIEW);
                goToFile.setDataAndType(file, "resource/folder");
                startActivity(goToFile);
            }
        });*/
        toFile.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        toFile.setCancelable(false);
        toFile.show();

    }
}