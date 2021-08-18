package by.ddrvld.countdowndeathapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatKitActivity extends AppCompatActivity {

    private com.stfalcon.chatkit.messages.MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        messagesList = findViewById(R.id.messagesList);

        String senderId = "Sender Name";
        ImageLoader imageLoader = null;
        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesList.setAdapter(adapter);
    }
}
