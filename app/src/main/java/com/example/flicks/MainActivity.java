package com.example.flicks;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    public void attemptRegistration() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://10.0.2.2:3312/api/idm/register";

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", emailEditText.getText().toString());
            jsonObject.put("password", passwordEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        CustomStringRequest request = new CustomStringRequest(Request.Method.POST, url, new Response.Listener<CustomStringRequest.ResponseM>() {
            @Override
            public void onResponse(CustomStringRequest.ResponseM result) {
                System.out.println(result.headers);
                System.out.println(result.response);

                if (result.response.contains("110")) {
                    Toast.makeText(getApplicationContext(), "Registered successfully.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                    intent.putExtra("email", emailEditText.getText().toString());
                    startActivity(intent);
                    finish();
                } else if (result.response.contains("-11")) {
                    Toast.makeText(getApplicationContext(), "Invalid email format.", Toast.LENGTH_LONG).show();
                } else if (result.response.contains("16")) {
                    Toast.makeText(getApplicationContext(), "Email in use.", Toast.LENGTH_LONG).show();
                } else if (result.response.contains("12") || result.response.contains("13")) {
                    Toast.makeText(getApplicationContext(), "Invalid password.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(getApplicationContext(), "Invalid email format.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(request);

    }

    public void attemptLogin() {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://10.0.2.2:3312/api/idm/login";

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", emailEditText.getText().toString());
            jsonObject.put("password", passwordEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        CustomStringRequest request = new CustomStringRequest(Request.Method.POST, url, new Response.Listener<CustomStringRequest.ResponseM>() {
            @Override
            public void onResponse(CustomStringRequest.ResponseM result) {
                System.out.println(result.headers);
                System.out.println(result.response);

                if (result.response.contains("120")) {
                    Toast.makeText(getApplicationContext(), "Logged in successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                    intent.putExtra("email", emailEditText.getText().toString());
                    startActivity(intent);
                    finish();
                } else if (result.response.contains("14")) {
                    Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();
                } else if (result.response.contains("11")) {
                    Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(getApplicationContext(), "Invalid email format.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(request);
    }
}
