package com.practice.venues;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_activity);

        // Get the Intent that started this activity and extract the string
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        String address = extras.getString("address");
        address = "Address: " + address;
        String imageUrl = extras.getString("imageUrl");

        TextView textView = findViewById(R.id.textView);
        textView.setText(title);

        TextView addressView = findViewById(R.id.AddressTextView);
        addressView.setText(address);
        if(imageUrl != "") {
            ImageView imageView = findViewById(R.id.ImageView);
            Picasso.get().load(imageUrl).into(imageView);
        }
     }
}
