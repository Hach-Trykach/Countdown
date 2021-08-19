package by.ddrvld.countdowndeathapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatKitActivity extends AppCompatActivity {

    private final List<Message> list_message = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatkit_message_list);

        com.stfalcon.chatkit.messages.MessagesList messagesList = findViewById(R.id.messagesList);

        Message message = new Message("dudarev.vlad@gmail.com", "Dudarev Vlad", "myChatKitText", "0");

        ImageLoader imageLoader = null;
        MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(message.getId(), imageLoader);
        messagesList.setAdapter(adapter);

        adapter.addToStart(message, false);
        adapter.addToEnd(list_message, false);

//        inputView.setInputListener(new MessageInput.InputListener() {
//            @Override
//            public boolean onSubmit(CharSequence input) {
//                //validate and send message
//                adapter.addToStart(message, true);
//                return true;
//            }
//        });

//        messageInput.setAttachmentsListener(new MessageInput.AttachmentsListener() {
//            @Override
//            public void onAddAttachments() {
//                //select attachments
//            }
//        });

    }
}
