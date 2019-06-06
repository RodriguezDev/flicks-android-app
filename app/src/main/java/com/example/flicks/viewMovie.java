package com.example.flicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class viewMovie extends AppCompatActivity {

    TextView title, year, director, overview, rating, genres, stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie);

        title = findViewById(R.id.movieViewTitle);
        year = findViewById(R.id.movieViewYear);
        director = findViewById(R.id.movieViewDirector);
        overview = findViewById(R.id.movieViewOverview);
        rating = findViewById(R.id.movieViewRating);
        genres = findViewById(R.id.movieViewGenres);
        stars = findViewById(R.id.movieViewStars);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String movieID = extras.getString("movieID");
            String email = extras.getString("email");
            getMovie(movieID, email);

        } else {
            Toast.makeText(this, "Error loading movie", Toast.LENGTH_LONG).show();
        }
    }

    private void getMovie(String id, final String email) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://10.0.2.2:3311/api/movies/get/" + id;

        CustomStringRequest request = new CustomStringRequest(Request.Method.GET, url, new Response.Listener<CustomStringRequest.ResponseM>() {
            @Override
            public void onResponse(CustomStringRequest.ResponseM result) {
                System.out.println(result.headers);
                System.out.println(result.response);


                try {
                    JSONObject object = (JSONObject) new JSONTokener(result.response).nextValue();

                    int resultCode = object.getInt("resultCode");
                    if (resultCode == 210) {
                        JSONObject movie = (JSONObject)object.get("movie");

                        try {
                            title.setText(movie.getString("title"));
                        } catch (JSONException e) {}
                        try {
                            year.setText("Year: " + movie.getInt("year"));
                        } catch (JSONException e) {}
                        try {
                            overview.setText("Overview: " + movie.getString("overview"));
                        } catch (JSONException e) {}
                        try {
                            director.setText("Director: " + movie.getString("director"));
                        } catch (JSONException e) {}
                        try {
                            rating.setText("Rating: " + movie.getDouble("rating") + " (" + movie.getInt("numVotes") + " votes)");
                        } catch (JSONException e) {}

                        try {
                            JSONArray genresList = movie.getJSONArray("genres");
                            String genreString = "Genres: ";
                            for (int i = 0; i < genresList.length(); i++) {
                                JSONObject j = (JSONObject)genresList.get(i);
                                genreString += j.getString("name") + ", ";
                            }

                            genres.setText(genreString);
                        } catch (JSONException e) {}


                        try {
                            JSONArray starsList = movie.getJSONArray("stars");

                            String starsString = "Stars: ";

                            for (int i = 0; i < starsList.length(); i++) {
                                JSONObject j = (JSONObject)starsList.get(i);
                                starsString += j.getString("name") + ", ";
                            }

                            stars.setText(starsString);
                        } catch (JSONException e) {}

                    } else {
                        Toast.makeText(getApplicationContext(), "Error getting movie", Toast.LENGTH_LONG).show();
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
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email);
                params.put("sessionID", "x");
                return params;
            }
        };
        requestQueue.add(request);
    }
}
