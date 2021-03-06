package com.example.hiddentreasures;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hiddentreasures.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

  // Fields
  private FirebaseDatabase database;
  private DatabaseReference users;
  private EditText editMail, editUsername, editPassword, editConfirmPassword;
  private Button btnSignUp, btnToLogIn;

  /**
   * Get reference to Firebase Database, and the "Users" node Get reference to all components of the
   * activity's view
   *
   * @param savedInstanceState The last saved state of the application
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

    //When signUp button is pressed, try signing up user with the values they entered. If there is a problem, show error messages.
    btnSignUp.setOnClickListener(view -> {

      final User temp =
          new User(
              editMail.getText().toString(),
              editUsername.getText().toString(),
              editPassword.getText().toString(),
              System.currentTimeMillis());

      users.addListenerForSingleValueEvent(
          new ValueEventListener() {
            /**
             * Creates and adds new user in Firebase if entered information is valid and username is unique
             *
             * @param dataSnapshot Snapshot of the Users node in its current state
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              // Check to make sure no user already exists with entered username
              if (dataSnapshot.child(temp.getUsername()).exists()) {

                Toast.makeText(
                    getApplicationContext(),
                    "This Username is Taken!",
                    Toast.LENGTH_SHORT)
                    .show();

                editUsername.setText("");
              }

              // Check to see if username is valid
              else if (temp.getUsername().isEmpty()
                  || temp.getUsername().contains(" ")
                  || temp.getUsername().length() < 6) {

                Toast.makeText(
                    getApplicationContext(), "That username is not valid",
                    Toast.LENGTH_SHORT)
                    .show();

                Toast.makeText(
                    getApplicationContext(),
                    "Please select a username without any spaces with at least six characters",
                    Toast.LENGTH_SHORT)
                    .show();
              } else {

                // Check to make sure password and password confirmation are exactly the same
                if (editConfirmPassword.getText().toString()
                    .equals(editPassword.getText().toString())) {

                  // Adds a new user to Firebase with the entered email, username, and password
                  users.child(temp.getUsername()).setValue(temp);

                  Toast.makeText(
                      getApplicationContext(),
                      "Registered Successfully!",
                      Toast.LENGTH_SHORT)
                      .show();

                  // Sends user to MainActivity and passes username to the activity
                  Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                  myIntent.putExtra("username", temp.getUsername());
                  startActivity(myIntent);
                } else {

                  Toast.makeText(
                      getApplicationContext(),
                      "Please ensure the password you have entered is consistent",
                      Toast.LENGTH_SHORT)
                      .show();

                  editConfirmPassword.setText("");
                }
              }
            }

            //Method required by ValueEventListener
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
          });
    });

    //If toLogIn button is clicked, send user to LoginActivity
    btnToLogIn.setOnClickListener(view -> {
      Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
      startActivity(myIntent);
    });
  }
}
