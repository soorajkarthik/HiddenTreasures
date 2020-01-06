package com.example.hiddentreasures;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity {

  // Fields
  private FirebaseDatabase database;
  private DatabaseReference users;
  private SharedPreferences pref;
  private EditText editUsername, editPassword;
  private Button btnLogIn, btnToSignUp;
  private CheckBox checkStaySignedIn;

  /**
   * If a user is currently signed on, go directly to MainActivity If no user is currently signed on, stay on current
   * screen Get reference to Firebase Database, and the "Users" node Get reference to all components of the activity's
   * view
   *
   * @param savedInstanceState the last saved state of the application
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    pref = getApplicationContext().getSharedPreferences("MyPref", 0);

    // Check to see if user is currently logged on, if true, start MainActivity
    if (pref.getString("username", null) != null) {

      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      intent.putExtra("username", pref.getString("username", null));

      startActivity(intent);
    }

    database = FirebaseDatabase.getInstance();
    users = database.getReference("Users");

    setContentView(R.layout.activity_login);

    editUsername = findViewById(R.id.editUsername);
    editPassword = findViewById(R.id.editPassword);

    btnLogIn = findViewById(R.id.btnLogIn);
    btnToSignUp = findViewById(R.id.btnToSignUp);
    checkStaySignedIn = findViewById(R.id.checkStaySignedIn);

    btnLogIn.setOnClickListener(
        view -> logIn(editUsername.getText().toString(), editPassword.getText().toString()));

    btnToSignUp.setOnClickListener(view -> {

      Intent myIntent = new Intent(getApplicationContext(), SignUpActivity.class);
      startActivity(myIntent);
    });
  }

  /**
   * Check to see if there is a user in Firebase with the entered username and password combination If there is, start
   * MainActivity
   *
   * @param username entered username
   * @param password entered password
   */
  private void logIn(final String username, final String password) {

    users.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.child(username).exists() && !username.isEmpty()) {

              User login = dataSnapshot.child(username).getValue(User.class);

              if (login.getPassword().equals(password)) {

                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                /*
                 * If stay signed in box is checked, store username on
                 * device so login is no longer necessary until user logs out
                 */
                if (checkStaySignedIn.isChecked()) {

                  SharedPreferences.Editor editor = pref.edit();
                  editor.putString("username", username);
                  editor.commit();
                }

                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("username", username);
                startActivity(myIntent);
              } else {

                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
              }
            } else {

              Toast.makeText(LoginActivity.this, "Username not registered", Toast.LENGTH_SHORT).show();
            }
          }

          // Method required by ValueEventListener
          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
          }
        });
  }
}
