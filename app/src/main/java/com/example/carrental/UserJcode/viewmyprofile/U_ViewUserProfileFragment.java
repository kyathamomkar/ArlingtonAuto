package com.example.carrental.UserJcode.viewmyprofile;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.carrental.DatabaseHelper;
import com.example.carrental.R;
import com.example.carrental.RegistrationActivity;
import com.example.carrental.UserJcode.MainActivityUser;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class U_ViewUserProfileFragment extends Fragment {
    Spinner spinner;
    Switch membership;
    EditText username;
    EditText password;
    EditText email;
    EditText lastname;
    EditText firstname;
    EditText studentid;
    EditText phonenumber;
    EditText address;
    EditText city;
    EditText state;
    EditText zipcode;
    Button updatebutton;
    Button ignorebutton;
    LinearLayout linearlayout;
    TableLayout newmaintable;
    //db related
    SharedPreferences sharedpreferences;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    
    String sessionUsername = null;
      String userType= null;
    private U_ViewUserProfileViewModel UViewUserProfileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UViewUserProfileViewModel = ViewModelProviders.of(this).get(U_ViewUserProfileViewModel.class);
        View root = inflater.inflate(R.layout.u_viewmyprofile, container, false);

        sharedpreferences  = this.getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        sessionUsername= sharedpreferences.getString("username",null);
       userType= sharedpreferences.getString("userType",null);
        //db related
        mDBHelper = new DatabaseHelper(getActivity());
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        username = (EditText)root.findViewById(R.id.newusername);
        password = (EditText)root.findViewById(R.id.newpassword);
        email = (EditText)root.findViewById(R.id.newemail);
        lastname = (EditText)root.findViewById(R.id.newlastname);
        firstname = (EditText)root.findViewById(R.id.newfirstname);
        studentid = (EditText)root.findViewById(R.id.newstudentid);
        phonenumber = (EditText)root.findViewById(R.id.newphonenumber);
        address = (EditText)root.findViewById(R.id.newaddress);
        city = (EditText)root.findViewById(R.id.newcity);
        state = (EditText)root.findViewById(R.id.newstate);
        zipcode = (EditText)root.findViewById(R.id.newzipcode);
        spinner = (Spinner) root.findViewById(R.id.newrole);
        membership = (Switch)root.findViewById(R.id.newmembership);
        updatebutton = (Button)root.findViewById(R.id.updateprofilebutton);
        ignorebutton = (Button)root.findViewById(R.id.ignorechanges);
        linearlayout = (LinearLayout)root.findViewById(R.id.llviewprofile);
        newmaintable = (TableLayout)root.findViewById(R.id.newmaintable);


        //spinner code

        //spinner.setOnItemSelectedListener(getActivity());

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("User");
        categories.add("Admin");
        categories.add("Rental Manager");

        // Creating adapter for spinner
        // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),  R.layout.spinner_blacktext, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.dropdown);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);



        if(userType.equalsIgnoreCase("user"))
        {
            spinner.setEnabled(false);
            spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition("User"));
        }
        else if(userType.equalsIgnoreCase("rental manager"))
        {
            spinner.setEnabled(false);
            spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition("Rental Manager"));
            membership.setEnabled(false);
            membership.setVisibility(View.GONE);
        }
        else
        {
            spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition("Admin"));
            spinner.setEnabled(false);
            spinner.setVisibility(View.GONE);
            membership.setEnabled(true);
            membership.setVisibility(View.GONE);
        }
        newmaintable.setVisibility(View.VISIBLE);
        newmaintable.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_up));
        updatebutton.setVisibility(View.VISIBLE);
        updatebutton.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_left));
        ignorebutton.setVisibility(View.VISIBLE);
        ignorebutton.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.slide_right));
        //linear layout click event
        linearlayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

            }
        } );


    //fetch initial data from db
        GetUserDetailsFromDb(sessionUsername);
        //ignore button clicked
        ignorebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ignorebutton.getCurrentTextColor() == Color.parseColor("#020202"))
                {
                    ignorebutton.setTextColor(Color.parseColor("#ffffff"));
                }
                else{
                    ignorebutton.setTextColor(Color.parseColor("#020202"));
                }
                GetUserDetailsFromDb(sessionUsername);
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
            }
        });

        //update profile click listener
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatebutton.getCurrentTextColor() == Color.parseColor("#020202")) {
                    updatebutton.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    updatebutton.setTextColor(Color.parseColor("#020202"));
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                if (username.getText().toString().trim().equals("")) {
                    username.setError("Username is required!");
                } else if (password.getText().toString().trim().equals("")) {
                    password.setError("Password is required!");
                } else if (email.getText().toString().trim().equals("")) {
                    email.setError("email is required!");
                } else if (lastname.getText().toString().trim().equals("")) {
                    lastname.setError("lastname is required!");
                } else if (firstname.getText().toString().trim().equals("")) {
                    firstname.setError("firstname is required!");
                } else if (studentid.getText().toString().trim().equals("")) {
                    studentid.setError("studentid is required!");
                } else if (phonenumber.getText().toString().trim().equals("")) {
                    phonenumber.setError("phonenumber is required!");
                } else if (address.getText().toString().trim().equals("")) {
                    address.setError("address is required!");
                } else if (city.getText().toString().trim().equals("")) {
                    city.setError("city is required!");
                } else if (state.getText().toString().trim().equals("")) {
                    state.setError("state is required!");
                } else if (zipcode.getText().toString().trim().equals("")) {
                    zipcode.setError("zipcode is required!");
                } else {

                    UpdateProfileinDB(sessionUsername);
                }
            }
        });

        return root;
    }

    private void UpdateProfileinDB(String sUsername) {

        ContentValues cv = new ContentValues();
        cv.put("username",username.getText().toString().trim()); //These Fields should be your String values of actual column names
        cv.put("password",password.getText().toString().trim());
        cv.put("uta_id",studentid.getText().toString().trim());
        cv.put("last_name", lastname.getText().toString().trim());
        cv.put("first_name",firstname.getText().toString().trim());
        cv.put("phone",phonenumber.getText().toString().trim());
        cv.put("email",email.getText().toString().trim());
        cv.put("address",address.getText().toString().trim());
        cv.put("city",city.getText().toString().trim());
        cv.put("state",state.getText().toString().trim());
        cv.put("zip",zipcode.getText().toString().trim());
        cv.put("role",spinner.getSelectedItem().toString().trim());
        cv.put("club_membership", getMembership());   //1 - true, 0 -false
        cv.put("is_revoked", 0);


        mDb.update("user", cv, "username='"+sUsername+"'", null);

        //Toast.makeText(getActivity(), "Account Details Updated", Toast.LENGTH_LONG).show();
        Snackbar.make(getView(), "Account Details Updated", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        GetUserDetailsFromDb(username.getText().toString().trim());
        //saving a session for a logged in user in the form of (key,value) pair (username, "")
        SharedPreferences.Editor session = sharedpreferences.edit();
        session.putString("username", username.getText().toString().trim());
        session.putString("userType", spinner.getSelectedItem().toString().trim());
        session.commit();
        sharedpreferences  = this.getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        sessionUsername= sharedpreferences.getString("username",null);
        userType= sharedpreferences.getString("userType",null);
    }

    //fetch initial data from db
    public void GetUserDetailsFromDb(String sessionUsername) {
        Cursor cursor = mDb.rawQuery("select username FROM user", null);
        if (cursor.getCount() > 0) {
            String query = "Select * from user where username = '" + sessionUsername.toString().trim()+"'";
            cursor = mDb.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                Intent loginIntent = new Intent(getActivity(), RegistrationActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(loginIntent, options.toBundle());
                cursor.close();
            } else {

                if (cursor.moveToFirst()) {
                    username.setText(cursor.getString(cursor.getColumnIndex("username")));
                    password.setText(cursor.getString(cursor.getColumnIndex("password")));
                    studentid.setText(cursor.getString(cursor.getColumnIndex("uta_id")));
                    lastname.setText(cursor.getString(cursor.getColumnIndex("last_name")));
                    firstname.setText(cursor.getString(cursor.getColumnIndex("first_name")));
                    address.setText(cursor.getString(cursor.getColumnIndex("address")));
                    phonenumber.setText(cursor.getString(cursor.getColumnIndex("phone")));
                    email.setText(cursor.getString(cursor.getColumnIndex("email")));
                    city.setText(cursor.getString(cursor.getColumnIndex("city")));
                    state.setText(cursor.getString(cursor.getColumnIndex("state")));
                    zipcode.setText(cursor.getString(cursor.getColumnIndex("zip")));
                    spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(cursor.getString(cursor.getColumnIndex("role"))));
                    membership.setChecked(cursor.getInt(cursor.getColumnIndex("club_membership"))==1);

                }
                cursor.close();

            }
        }

    }
    public String getMembership() {
        return membership.isChecked()?"1":"0";

    }

}