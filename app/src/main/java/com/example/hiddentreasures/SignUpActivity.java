package com.example.hiddentreasures;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hiddentreasures.Model.User;
import com.google.firebase.database.*;

public class SignUpActivity extends AppCompatActivity {


    //Fields
    private FirebaseDatabase database;
    private DatabaseReference users;
    private EditText editMail, editUsername, editPassword, editConfirmPassword;
    private Button btnSignUp, btnToLogIn;

    /**
     * Get reference to Firebase Database, and the "Users" node
     * Get reference to all components of the activity's view
     *
     * @param savedInstanceState the last saved state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editMail = findViewById(R.id.editMail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnToLogIn = findViewById(R.id.btnToLogIn);
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            /**
             * When sign-up button is pressed, goes through sign-up process
             * @param view view of the clicked button
             */
            @Override
            public void onClick(View view) {

                final User temp = new User(editMail.getText().toString(),
                        editUsername.getText().toString(),
                        editPassword.getText().toString(),
                        System.currentTimeMillis());

                users.addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * Creates and adds new user in Firebase if entered information is valid and username is unique
                     * @param dataSnapshot a snapshot of the current state of the node in Firebase
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Check to make sure no user already exists with entered username
                        if (dataSnapshot.child(temp.getUsername()).exists()) {

                            Toast.makeText(SignUpActivity.this,
                                    "This Username is Taken!",
                                    Toast.LENGTH_SHORT).show();

                            editUsername.setText("");
                        }

                        //Check to see if username is valid
                        else if (temp.getUsername().isEmpty() ||
                                temp.getUsername().contains(" ") ||
                                temp.getUsername().length() < 6) {

                            Toast.makeText(SignUpActivity.this,
                                    "That username is not valid",
                                    Toast.LENGTH_SHORT).show();


                            Toast.makeText(SignUpActivity.this,
                                    "Please select a username without any spaces with at least six characters",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            //Check to make sure entered password and password confirmation are exactly the same
                            if (editConfirmPassword.getText().toString().
                                    equals(editPassword.getText().toString())) {

                                users.child(temp.getUsername()).setValue(temp); //Adds a new user to Firebase with the entered email, username, and password

                                Toast.makeText(SignUpActivity.this,
                                        "Registered Successfully!",
                                        Toast.LENGTH_SHORT).show();

                                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                                myIntent.putExtra("username", temp.getUsername()); //Passes username to next activity
                                startActivity(myIntent);
                            } else {

                                Toast.makeText(SignUpActivity.this,
                                        "Please ensure the password you have entered is consistent",
                                        Toast.LENGTH_SHORT).show();

                                editConfirmPassword.setText("");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        btnToLogIn.setOnClickListener(new View.OnClickListener() {

            /**
             * Send user to Login screen
             * @param view view of the clicked button
             */
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);
            }
        });
    }
}