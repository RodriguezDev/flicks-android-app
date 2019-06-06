package com.example.flicks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {

    EditText searchBox;
    ListView resultList;

    String email;
    ArrayList<String> moviesList;
    ArrayList<String> movieIds;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        moviesList = new ArrayList<>();
        movieIds = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        searchBox = findViewById(R.id.searchBox);
        resultList = findViewById(R.id.resultList);

        adapter = new ArrayAdapter<String>(this, R.layout.movieitem, moviesList);

        resultList.setAdapter(adapter);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchMovies(searchBox.getText().toString());
                    return true;
                }
                return false;
            }
        });

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String movieId= movieIds.get(position);

                Intent i = new Intent(getApplicationContext(), viewMovie.class);
                i.putExtra("email", email);
                i.putExtra("movieID", movieId);
                startActivity(i);
            }
        });
    }

    private void searchMovies(String title) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://10.0.2.2:3311/api/movies/search?title=" + title;

        CustomStringRequest request = new CustomStringRequest(Request.Method.GET, url, new Response.Listener<CustomStringRequest.ResponseM>() {
            @Override
            public void onResponse(CustomStringRequest.ResponseM result) {
                System.out.println(result.headers);
                System.out.println(result.response);


                try {
                    JSONObject object = (JSONObject) new JSONTokener(result.response).nextValue();

                    int resultCode = object.getInt("resultCode");
                    if (resultCode == 210) {
                        JSONArray movies = object.getJSONArray("movies");

                        moviesList.clear();

                        for (int i = 0; i < movies.length(); i++) {
                            JSONObject currentMovie = (JSONObject)movies.get(i);
                            System.out.println(currentMovie.getString("title"));
                            moviesList.add(currentMovie.getString("title"));
                            movieIds.add(currentMovie.getString("movieId"));
                        }
                        System.out.println(moviesList);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(request);
    }
}
