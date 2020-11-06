package by.ddrvld.countdowndeathapp;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {

    private RelativeLayout activity_chat;
    private FirebaseListAdapter<Message> adapter;
    private EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    EmojIconActions emojIconActions;

//    private Drawer drawerResult;

    //===============================initializeAuthVariables========================================
    final String TAG = "PhoneAuthActivity";

    final int BTN_EXIT = 10;

    private ListView listOfMessages;
    private ProgressBar circular_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Snackbar.make(activity_chat, "Вы не авторизованы 2", Snackbar.LENGTH_LONG).show();
        }
        else {
            onCreateChat();
        }
    }

//    public boolean ConnectingToInternet(){
//        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivity != null)
//        {
//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
//            if (info != null)
//                for (int i = 0; i < info.length; i++)
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
//                    {
//                        return true;
//                    }
//
//        }
//        return false;
//    }

    private void onCreateChat() {
        setContentView(R.layout.activity_chat);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//        }
//        initializeDrawer(toolbar);

        activity_chat = findViewById(R.id.activity_chat);
        submitButton = findViewById(R.id.submit_button);
        emojiButton = findViewById(R.id.emoji_button);
        emojiconEditText = findViewById(R.id.textField);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_chat, emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();

        listOfMessages = findViewById(R.id.list_of_messages);
        circular_progress = findViewById(R.id.circular_progress);
        //показываем View загрузки
        circular_progress.setVisibility(View.VISIBLE);
        listOfMessages.setVisibility(View.INVISIBLE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                InputFilter filter= new InputFilter() {
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        for (int i = start; i < end; i++) {
//                            String checkMe = String.valueOf(source.charAt(i));
//
//                            Pattern pattern = Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789_абвгдеёжзийклмнопрстуфхцчшщьыъэюя. ]*");
//                            Matcher matcher = pattern.matcher(checkMe);
//                            boolean valid = matcher.matches();
//                            if(!valid){
//                                Log.d("", "invalid");
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//                };
//
//                emojiconEditText.setFilters(new InputFilter[]{filter});

                String text = emojiconEditText.getText().toString();

                if(text.isEmpty()) return;
//                if(text.equals(" ")) return;
//                if(text.equals("\n")) return;
//                if(text.length() < 2) return;
                while (text.charAt(0) == ' ' || text.charAt(0) == '\n') {
                    try {
                        text = text.substring(1); // Удаляем первый символ
                        Log.d("", "SUBSTRING");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while (text.charAt(text.length() -1) == ' ' || text.charAt(text.length() -1) == '\n') {
                    try {
                        text = text.substring(0, text.length() -1); // Удаляем последний  символ
                        Log.d("", "SUBSTRING");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                String msg = "";
//                if (text.indexOf(" ") != -1) {
//                    msg = "элемент есть в тексте";
//                }
//                else {
//                    msg = "элемента в тексте нет";
//                }
//                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().push().setValue(
                        new Message(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), text));
                emojiconEditText.setText("");
            }
        });
        displayAllMessages();
    }

    @Override
    public void onBackPressed() {
//        if(drawerResult != null && drawerResult.isDrawerOpen()) {
//            drawerResult.closeDrawer();
//        } else {
            super.onBackPressed();
//        }
    }

//    private void initializeDrawer(Toolbar toolbar) {
//        AccountHeader accountHeader = initializeAccountHeader();
//
//        drawerResult = new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withAccountHeader(accountHeader)
////                .withDisplayBelowToolbar(true)
//                .withDisplayBelowStatusBar(false) // Походу не работает
//                .withActionBarDrawerToggleAnimated(true)
//                .addStickyDrawerItems(initializeDrawerItems())
//                .withOnDrawerItemClickListener(onClicksLis)
//                .build();
//    }

//    private Drawer.OnDrawerItemClickListener onClicksLis = new Drawer.OnDrawerItemClickListener() {
//        @Override
//        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//            if(drawerItem.getIdentifier() == BTN_EXIT)
//            {
//                FirebaseAuth.getInstance().signOut();
////                onCreateAuth();
//                return true;
//            }
//            return false;
//        }
//    };

//    private IDrawerItem[] initializeDrawerItems() {
//        return new IDrawerItem[]{new PrimaryDrawerItem()
//                .withName(R.string.sign_out)
////                .withIcon(R.drawable.)
//                .withIdentifier(BTN_EXIT),
//                new DividerDrawerItem()};
//    }

//    private AccountHeader initializeAccountHeader() {
//        IProfile profile = new ProfileDrawerItem()
//                .withName("Dudarev Vlad")
//                .withEmail("dudarev.vlad@gmail.com")
//                .withIcon((getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait)));
//
//        return new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(R.color.colorPrimaryDark)
//                .addProfiles(profile)
//                .build();
//    }

    private void displayAllMessages() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                adapter = new FirebaseListAdapter<Message>(ChatActivity.this, Message.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
                    @Override
                    protected void populateView(View v, Message model, int position) {
                        TextView mess_user, mess_time;
                        BubbleTextView mess_text;
                        mess_user = v.findViewById(R.id.item_message_user);
                        mess_time = v.findViewById(R.id.item_message_time);
                        mess_text = v.findViewById(R.id.item_message_text);

                        mess_user.setText(model.getUserName());
                        mess_time.setText(DateFormat.format("dd.MM.yyy\nHH:mm:ss", model.getMessageTime()));
                        mess_text.setText(model.getTextMessage());
                    }
                };
                listOfMessages.setAdapter(adapter);

                //убираем View загрузки
                circular_progress.setVisibility(View.INVISIBLE);
                listOfMessages.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });
    }

//    private void enableViews(View... views) {
//        for (View v : views) {
//            v.setEnabled(true);
//        }
//    }
//
//    private void disableViews(View... views) {
//        for (View v : views) {
//            v.setEnabled(false);
//        }
//    }
}