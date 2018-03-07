package com.gmail.gabezter.cruisecheck_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DeckAssign extends AppCompatActivity {

    private ArrayList<Integer> countIDa = new ArrayList<>();
    MainActivity main = new MainActivity();

    ArrayList<String> listPassengersName = main.listPassengersName;
    ArrayList<String> listPassengersNumB = main.listPassengersNumB;
    ArrayList<String> listPassengersDeck = main.listPassengersDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_assign);
        readPassengers(readFromFile());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deck_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent main = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.submitDeck:
                readDeckAssign();
                alterDocument();
                startActivity(main);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void alterDocument() {

        String[] name = listPassengersName.toArray(new String[0]);
        String[] number = listPassengersNumB.toArray(new String[0]);
        String[] deck = listPassengersDeck.toArray(new String[0]);
        File file = new File(getFilesDir() + "/passengers.csv");

        try {
            String[] reply = readFromFile();
            ArrayList<String> list = new ArrayList<>();

            list.add(reply[0]);
            list.add(reply[1]);
            for (int i = 0; i < name.length; i++){
                Log.e(Integer.toString(i), name[i]+","+number[i]+","+deck[i]+",N,N");
                list.add(name[i]+","+number[i]+","+deck[i]+",N,N");
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

    private void readDeckAssign() {
        int id;
        listPassengersDeck.clear();
        for(int i = 0; i < countIDa.size(); i++){
            RadioGroup radioGroup = findViewById(countIDa.get(i));
            id = radioGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(id);
            switch (rb.getText().toString()){
                case "1":
                    listPassengersDeck.add("1");
                    break;
                case "2":
                    listPassengersDeck.add("2");
                    break;
                case "3":
                    listPassengersDeck.add("3");
                    break;
            }
        }
    }

    public String[] readFromFile() {

        ArrayList<String> text = new ArrayList<>();

        try {
            InputStream inputStream = new FileInputStream(new File(getFilesDir() + "/passengers.csv"));

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


    public void readPassengers(String[] schools){
        listPassengersName.clear();
        listPassengersNumB.clear();
        listPassengersDeck.clear();
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
        }
        setDeck(listPassengersName.toArray(new String[0]), listPassengersNumB.toArray(new String[0]));
    }

    public void setDeck(String[] name, String[] number){
        TableLayout ll = findViewById(R.id.deckTable);
        TextView colName;
        TextView colNum;
        TextView colDeck;
        RadioGroup radioGroup;
        RadioButton radio1;
        RadioButton radio2;
        RadioButton radio3;

        TableRow row = new TableRow(this);
        colName = new TextView(this);
        colNum = new TextView(this);
        colDeck = new TextView(this);

        colName.setText("Passenger");
        colNum.setText("Number of \n Passengers");
        colDeck.setText("Assigned \n Deck");

        colName.setGravity(Gravity.CENTER);
        colNum.setGravity(Gravity.CENTER);
        colDeck.setGravity(Gravity.CENTER);

        ll.removeAllViewsInLayout();

        row.addView(colName);
        row.addView(colNum);
        row.addView(colDeck);
        ll.addView(row);

        int i = 0;
        countIDa.clear();
        for (String school : name) {
            TableRow ros = new TableRow(this);
            colName = new TextView(this);
            colNum = new TextView(this);
            radioGroup = new RadioGroup(this);
            radio1 = new RadioButton(this);
            radio2 = new RadioButton(this);
            radio3 = new RadioButton(this);

            radioGroup.setOrientation(LinearLayout.HORIZONTAL);

            radio1.setText("1");
            radio2.setText("2");
            radio3.setText("3");

            radioGroup.addView(radio1);
            radioGroup.addView(radio2);
            radioGroup.addView(radio3);

            radioGroup.check(radio1.getId());

            colName.setText(school);
            colNum.setText(number[i]);
            i++;

            colName.setGravity(Gravity.CENTER);
            colNum.setGravity(Gravity.CENTER);
            colDeck.setGravity(Gravity.CENTER);
            radioGroup.setGravity(Gravity.CENTER);
            int id;
            id = View.generateViewId();
            countIDa.add(id);

            radioGroup.setId(id);

            ros.addView(colName);
            ros.addView(colNum);
            ros.addView(radioGroup);
            ll.addView(ros);
        }
    }
}
