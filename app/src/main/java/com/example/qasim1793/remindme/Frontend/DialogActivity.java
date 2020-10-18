package com.example.qasim1793.remindme.Frontend;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qasim1793.remindme.R;

public class DialogActivity extends AppCompatActivity {
    public MediaPlayer catSoundMediaPlayer;
    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Intent intent = getIntent();
        id = intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(intent.getStringExtra("name"));

         catSoundMediaPlayer = MediaPlayer.create(this, R.raw.alarmsound);

        catSoundMediaPlayer.start();

        Button show = (Button) findViewById(R.id.viewButton);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DialogActivity.this,ReminderEditActivity.class);
                intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, id);
                startActivity(intent);
                finish();
            }
        });




    }
}
