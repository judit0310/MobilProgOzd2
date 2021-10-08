package hu.uni.miskolc.mobilprogozd2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import hu.uni.miskolc.mobilprogozd2.dao.UserDAO;
import hu.uni.miskolc.mobilprogozd2.dao.UserDatabase;
import hu.uni.miskolc.mobilprogozd2.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("mobilprog", MODE_PRIVATE);
        if (prefs.contains("nev")) {
            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.udvtoast) + " " + prefs.getString("nev", ""), Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
            toast.show();
        }
        setContentView(R.layout.activity_main);
        EditText nevMezo = findViewById(R.id.nev);
        nevMezo.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().isEmpty()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("nev", editable.toString());
                    editor.apply();
                    try {
                        FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("nevek.txt", MODE_APPEND);
                        fileOutputStream.write(editable.toString().getBytes());
                        fileOutputStream.write("\n".getBytes());
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        nevMezo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = getApplicationContext().openFileOutput("csaknevek.txt", MODE_APPEND);
                        String text = ((EditText) view).getText().toString();
                        fileOutputStream.write(text.getBytes());
                        fileOutputStream.write("\n".getBytes());
                        fileOutputStream.close();
                        File file = new File(getExternalFilesDir(null),"mentettNevek.txt");
                        FileOutputStream stream = new FileOutputStream(file,true);
                        stream.write(text.getBytes());
                        stream.write("\n".getBytes());
                        stream.close();
                        FileInputStream input = new FileInputStream(file);
                        StringBuilder sb = new StringBuilder();
                        int data = input.read();
                        while (data != -1){
                            sb.append((char)data);
                            data = input.read();
                        }
                        System.out.println(sb.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        UserDatabase db = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "users").build();
        UserDAO dao = db.getUserDao();

        Button hozzaadgomb = findViewById(R.id.hozzaadasgomb);
        hozzaadgomb.setEnabled(false);
        EditText felhasznalonevMezo = findViewById(R.id.felhasznalonev);
        EditText csaladnevMezo = findViewById(R.id.csaladnev);
        EditText keresztnevMezo = findViewById(R.id.keresztnev);
        felhasznalonevMezo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    String felhasznalonev = felhasznalonevMezo.getText().toString();
                    if (felhasznalonev.trim().isEmpty()){
                        hozzaadgomb.setEnabled(false);
                    }else{
                        hozzaadgomb.setEnabled(true);
                    }
                }
            }
        });
        hozzaadgomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        User user = new User();
                        user.setUsername(felhasznalonevMezo.getText().toString());
                        user.setLastName(csaladnevMezo.getText().toString());
                        user.setFirstName(keresztnevMezo.getText().toString());
                        try{
                            dao.insert(user);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    felhasznalonevMezo.setText("");
                                    csaladnevMezo.setText("");
                                    keresztnevMezo.setText("");
                                }
                            });

                        }
                        catch (SQLiteConstraintException e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    felhasznalonevMezo.setError("Már foglalt");
                                }
                            });


                        }
                    }
                });

            }
        });
        
        Button mentettgomb = findViewById(R.id.visszaolvasasgomb);
        mentettgomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<User> users = dao.getAll();
                        System.out.println(users);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, users.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

    }
};
