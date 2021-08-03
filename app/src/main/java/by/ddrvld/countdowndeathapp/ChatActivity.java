package by.ddrvld.countdowndeathapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static by.ddrvld.countdowndeathapp.MainActivity.days;
import static by.ddrvld.countdowndeathapp.MainActivity.hours;
import static by.ddrvld.countdowndeathapp.MainActivity.lastShareTime;
import static by.ddrvld.countdowndeathapp.MainActivity.mins;
import static by.ddrvld.countdowndeathapp.MainActivity.years;

public class ChatActivity extends AppCompatActivity {

    private RelativeLayout activity_chat;
    //    private FirebaseListAdapter<Message> adapter;
    private final List<Message> list_message = new ArrayList<>();
    private EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton, shareDateOfDeath;
    EmojIconActions emojIconActions;

//    private Drawer drawerResult;

    private Message selectedListItem; //hold selected item
    private Message savedForDeletingListItem;
    private boolean selectMode = false;
    private SparseBooleanArray chosen;
    private int selectedItem;
    private Toolbar toolbar;

    //===============================initializeAuthVariables========================================
    final String TAG = "PhoneAuthActivity";

    final int BTN_EXIT = 10;

    private ListView listOfMessages;
    private ProgressBar circular_progress;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    Activity activity = null;

    String locale = Locale.getDefault().getDisplayLanguage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //получаем точку входа для базы данных
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //получаем ссылку для работы с базой данных
        mDatabaseReference = mFirebaseDatabase.getReference();

        if(user == null) {
            Snackbar.make(activity_chat, "Вы не авторизованы..", Snackbar.LENGTH_LONG).show();
        }
        else {
            onCreateChat();
        }
    }

//    public boolean ConnectingToInternet() {
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

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getResources().getString(R.string.chat_language) + " «" + locale + "»");
        }
//        initializeDrawer(toolbar);

        activity_chat = findViewById(R.id.activity_chat);
        submitButton = findViewById(R.id.submit_button);
        emojiButton = findViewById(R.id.emoji_button);
        emojiconEditText = findViewById(R.id.textField);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_chat, emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();

        listOfMessages = findViewById(R.id.list_of_messages);
        circular_progress = findViewById(R.id.circular_progress);
        shareDateOfDeath = findViewById(R.id.shareDateOfDeath);

        //показываем View загрузки
        circular_progress.setVisibility(View.VISIBLE);
        listOfMessages.setVisibility(View.INVISIBLE);

        loadSettingsFromDatabase();

        displayAllMessages();

        shareDateOfDeath.setOnClickListener(view -> {
            registerForContextMenu(shareDateOfDeath);
            openContextMenu(shareDateOfDeath);
            unregisterForContextMenu(shareDateOfDeath);
        });

        submitButton.setOnClickListener(view -> {
//                InputFilter filter= new InputFilter() {
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        for (int i = start; i < end; i++) {
//                            String checkMe = String.valueOf(source.charAt(i));
//
//                            Pattern pattern = Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789_абвгдеёжзийклмнопрстуфхцчшщьыъэюя. ]*");
//                            Matcher matcher = pattern.matcher(checkMe);
//                            boolean valid = matcher.matches();
//                            if(!valid) {
//                                Log.d("", "invalid");
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//                };
//                emojiconEditText.setFilters(new InputFilter[]{filter});

            String text = emojiconEditText.getText().toString();

            if(text.isEmpty()) return;
            else {
                while (text.charAt(0) == ' ' || text.charAt(0) == '\n') {
                    try {
                        if(text.equals(" ")) return;
                        if(text.equals("\n")) return;
                        text = text.substring(1); // Удаляем первый символ
                        Log.d("", "SUBSTRING");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while (text.charAt(text.length() - 1) == ' ' || text.charAt(text.length() - 1) == '\n') {
                    try {
                        if(text.equals(" ")) return;
                        if(text.equals("\n")) return;
                        text = text.substring(0, text.length() - 1); // Удаляем последний символ
                        Log.d("", "SUBSTRING");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while (text.contains("\n\n\n")) {
                    try {
                        text = text.replaceAll("\n\n\n", "\n\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            createMessage(text);
        });

        listOfMessages.setOnItemClickListener((adapterView, view, i, l) -> {
//            if(listOfMessages.isItemChecked(i) /*&& itemOldClickId != i*/) {
//                selectedItem = i;
//                selectedListItem = (Message) adapterView.getItemAtPosition(i);
//            }
            selectedItem = i;
            selectedListItem = (Message) adapterView.getItemAtPosition(i);

            registerForContextMenu(listOfMessages);
            openContextMenu(listOfMessages);
            unregisterForContextMenu(listOfMessages);
            createVibration(30);
        });

        listOfMessages.setOnItemLongClickListener((adapterView, view, i, l) -> {
//            activity.closeContextMenu();
            return false;
        });
    }

    String messageID;
    private void createMessage(String text) {
        if(System.currentTimeMillis() < lastShareTime) {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.new_message_after) + " " + (lastShareTime-System.currentTimeMillis())/1000 + " " + getResources().getString(R.string.seconds), Snackbar.LENGTH_SHORT).show();
            return;
        }
        messageID = mDatabaseReference.child("chats").child(locale).push().getKey();
        Message message = new Message(user.getEmail(), user.getDisplayName(), text, messageID);
        mDatabaseReference.child("chats")
                .child(locale)
                .child(messageID)
                .setValue(message);

        lastShareTime = System.currentTimeMillis() + sixtySeconds * 1000;
        clearEditText();
    }

//    private void updateMessage(Message selectedListItem) {
//        mDatabaseReference.child("chats")
//                .child(locale)
//                .push()
//                .child(mAuth.getUid())
//                .child(selectedListItem.getEmail())
//                .child("message")
//                .setValue(selectedListItem.getTextMessage());
////        selectedListItem = null;
//        clearEditText();
//    }

    private void deleteMessage(Message listItem) {
        if(listItem != null) {
            mDatabaseReference.child("chats")
                    .child(locale)
                    .child(listItem.getMessageID())
                    .removeValue();
            deselectAllItems();
            displayAllMessages();
        }
//        else Snackbar.make(findViewById(android.R.id.content), "Выберите сообщение", Snackbar.LENGTH_SHORT).show();
    }

    Thread deleteMessageTimer = null;
    public void Timer() {
        deleteMessageTimer = new Thread() {
            int deleteMessageTime = 0;
            public void run() {
                while (deleteMessageTime < 3) {
                    try {
                        sleep(1000);
                        deleteMessageTime ++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> {
                    deleteMessage(savedForDeletingListItem);
                    deleteMessageTimer = null;
                });
            }
        };
        deleteMessageTimer.start();
    }

    private void deleteOneMessage() {
        if(deleteMessageTimer != null) {
            savedForDeletingListItem = selectedListItem;
            deleteMessage(savedForDeletingListItem);
            deleteMessageTimer = null;
        }
        if(selectedItem != -1) {
            selectedListItem = (Message) listOfMessages.getItemAtPosition(selectedItem);
            Timer();
            adapter.removeItem(selectedItem);

            Snackbar deleteMesSnackbar = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.one_message_deleted), Snackbar.LENGTH_LONG);
            deleteMesSnackbar.setAction(R.string.undo, new UndoDeleteMessage());
            deleteMesSnackbar.show();
        }
//        else Snackbar.make(findViewById(android.R.id.content), "Выберите сообщение", Snackbar.LENGTH_SHORT).show();
    }

    public class UndoDeleteMessage implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Code to undo the user's last action
            if(deleteMessageTimer != null) {
                deleteMessageTimer.interrupt();
            }
            deselectAllItems();
            displayAllMessages();
        }
    }

    private void editMessage(Message selectedListItem) {
        if(selectedListItem != null) {
            emojiconEditText.setText(selectedListItem.getTextMessage());
//            emojiconEditText.setFocusable(true);
//            deselectAllItems();
//            displayAllMessages();
        }
//        else Snackbar.make(findViewById(android.R.id.content), "Выберите сообщение", Snackbar.LENGTH_SHORT).show();
    }

    void clearEditText() {
        emojiconEditText.setText("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.list_of_messages) {
            getMenuInflater().inflate(R.menu.context_menu, menu);

            if(selectedListItem.getEmail().equals(user.getEmail())) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);
            }
        }
        else if(v.getId() == R.id.shareDateOfDeath) {
            getMenuInflater().inflate(R.menu.share_date_of_death, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_item:
                deleteOneMessage();
                return true;
            case R.id.edit_item:
                editMessage(selectedListItem);
//                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.edit), Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.item_shareDateOfDeath:
                if(System.currentTimeMillis() > lastShareTime) {
                    createMessage(getResources().getString(R.string.share_text_1) +
                            (years > 0 ? " " + years + " " + GetWord(years,
                                    getResources().getString(R.string.text_yrs1),
                                    getResources().getString(R.string.text_yrs2),
                                    getResources().getString(R.string.text_yrs3)) : "") +
                            (days > 0 ? " " + days + " " + GetWord(days,
                                    getResources().getString(R.string.text_day1),
                                    getResources().getString(R.string.text_day2),
                                    getResources().getString(R.string.text_day3)) : "") +
                            (hours > 0 ? " " + hours + " " + GetWord(hours,
                                    getResources().getString(R.string.text_hrs1),
                                    getResources().getString(R.string.text_hrs2),
                                    getResources().getString(R.string.text_hrs3)) : "") +
                            (mins > 0 ? " " + mins + " " + GetWord(mins,
                                    getResources().getString(R.string.text_min1),
                                    getResources().getString(R.string.text_min2),
                                    getResources().getString(R.string.text_min3)) : ""));
                    lastShareTime = System.currentTimeMillis() + sixtySeconds * 1000;
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.new_message_after) + " " + (lastShareTime-System.currentTimeMillis())/1000 + " " + getResources().getString(R.string.seconds), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deselectAllItems() {
//        createVibration(15);
        selectedItem = -1;
        selectedListItem = null;
//        adapterView.setSelected(false);
        selectMode = false;
        toolbar.setTitle(getResources().getString(R.string.chat_language) + " «" + locale + "»");
    }

    private void createVibration(int duration) {
        Vibrator vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibration.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibration.vibrate(duration);
        }
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
//                mAuth.signOut();
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

    private ListViewAdapter adapter;
    private void displayAllMessages() {
        //показываем View загрузки
        circular_progress.setVisibility(View.VISIBLE);
        listOfMessages.setVisibility(View.INVISIBLE);

        mDatabaseReference.child("chats").child(locale).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (list_message.size() > 0)
                    list_message.clear();

//                проходим по всем записям и помещаем их в list_message в виде класса Message
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Message uMessage = postSnapshot.getValue(Message.class);
                    list_message.add(uMessage);
                }

                adapter = new ListViewAdapter(ChatActivity.this, list_message, mAuth);
                listOfMessages.setAdapter(adapter);

                //убираем View загрузки
                circular_progress.setVisibility(View.INVISIBLE);
                listOfMessages.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "displayAllMessages was cancelled");
            }
        });
    }

    private int sixtySeconds;
    private void loadSettingsFromDatabase() {
        mDatabaseReference.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                if (list_settings.size() > 0)
//                    list_settings.clear();

                for (DataSnapshot settingsSnapshot : snapshot.getChildren()) {
//                    AppSettings uSettings = settingsSnapshot.getValue(AppSettings.class);
                    sixtySeconds = Integer.parseInt(settingsSnapshot.getValue(String.class));
                }
//                settingsAdapter = new ListViewAdapter(ChatActivity.this, list_settings, mAuth);
//                listOfSettings.setAdapter(settingsAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadSettingsFromDatabase was cancelled");
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_share:
//                return true;
//        }
//        return true;
//    }

    private String GetWord(Long value, String one, String before_five, String after_five)
    {
        value %= 100;
        if(10 < value && value < 20) return after_five;
        switch(Integer.parseInt(value.toString())%10)
        {
            case 1: return one;
            case 2:
            case 3:
            case 4: return before_five;
            default: return after_five;
        }
    }

    @Override
    public void onBackPressed() {
//        if(drawerResult != null && drawerResult.isDrawerOpen()) {
//            drawerResult.closeDrawer();
//        } else {
        super.onBackPressed();
//        }
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