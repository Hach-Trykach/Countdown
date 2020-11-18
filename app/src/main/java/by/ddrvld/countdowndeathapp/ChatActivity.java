package by.ddrvld.countdowndeathapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private List<Message> list_message = new ArrayList<>();
    private EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    EmojIconActions emojIconActions;

//    private Drawer drawerResult;

    private Message selectedListItem; //hold selected item
    private boolean selectMode = false;
    private SparseBooleanArray chosen;
    private int selectItemSize;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        //показываем View загрузки
        circular_progress.setVisibility(View.VISIBLE);
        listOfMessages.setVisibility(View.INVISIBLE);

        registerForContextMenu(listOfMessages);
        displayAllMessages();

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

//                if(text.length() < 2) return;
                if(text.equals(" ")) return;
                if(text.equals("\n")) return;
                if(text.isEmpty()) return;
                else {
                    while (text.charAt(0) == ' ' || text.charAt(0) == '\n') {
                        try {
                            text = text.substring(1); // Удаляем первый символ
                            Log.d("", "SUBSTRING");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    while (text.charAt(text.length() - 1) == ' ' || text.charAt(text.length() - 1) == '\n') {
                        try {
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
            }
        });

        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectMode) {
                    if(listOfMessages.isItemChecked(i) /*&& itemOldClickId != i*/) {
                        view.setBackgroundResource(R.color.white);

                        view.setSelected(true);
                        selectItemSize++;

                        selectedListItem = (Message) adapterView.getItemAtPosition(i);

                        toolbar.setTitle("" + selectItemSize);
                    }
                    else {
                        view.setBackgroundResource(R.color.black);
                        view.setSelected(false);
                        if(selectItemSize > 0) {
                            selectItemSize--;
                            toolbar.setTitle("" + selectItemSize);
                        }
                        if (selectItemSize == 0) {
                            deselectAllItems();
                        }
                    }
                }
                else {
//                    Message message = (Message) adapterView.getItemAtPosition(i);
//                    openInWindow(message);
                    listOfMessages.setItemChecked(i, false);
                }
            }
        });
        listOfMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!selectMode) {
                    createVibration(30);
//                    Message message = (Message) adapterView.getItemAtPosition(i);
//                    selectedListItem = message;
//                    adapterView.setSelected(true);
                    selectMode = true;
//                    view.setSelected(true);
//                    listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    return false;
                }
                return true;
            }
        });
        listOfMessages.setOnCreateContextMenuListener(this);
    }

    String messageID;
    private void createMessage(String text) {
        if(System.currentTimeMillis() < lastShareTime) {
//            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.new_message_after) + " " + (lastShareTime-System.currentTimeMillis())/1000 + " " + getResources().getString(R.string.seconds), Snackbar.LENGTH_SHORT).show();
//            return;
        }
        messageID = mDatabaseReference.child("chats").child("general").push().getKey();
        Message message = new Message(user.getEmail(), user.getDisplayName(), text, messageID);
        mDatabaseReference.child("chats")
                .child("general")
                .child(messageID)
                .setValue(message);
        lastShareTime = System.currentTimeMillis() + 60 * 1000;
        clearEditText();
    }

//    private void updateMessage(Message selectedListItem) {
//        mDatabaseReference.child("chats")
//                .child("general")
//                .push()
//                .child(mAuth.getUid())
//                .child(selectedListItem.getEmail())
//                .child("message")
//                .setValue(selectedListItem.getTextMessage());
////        selectedListItem = null;
//        clearEditText();
//    }

    private void deleteMessage() {
        if(selectedListItem != null) {
            chosen = listOfMessages.getCheckedItemPositions();
            for (int i = 0; i < chosen.size(); i++) {
                // если пользователь выбрал пункт списка,
                // то выводим его в TextView.
                selectedListItem = (Message) listOfMessages.getItemAtPosition(chosen.keyAt(i));
                if (chosen.valueAt(i)) {
                    mDatabaseReference.child("chats")
                            .child("general")
                            .child(selectedListItem.getMessageID())
                            .removeValue();
//                    list_message.remove(selectedListItem);
                    Snackbar deleteMesSnackbar = Snackbar.make(findViewById(android.R.id.content), "Сообщение удалено", Snackbar.LENGTH_LONG);
                    deleteMesSnackbar.setAction(R.string.undo, new MyUndoListener());
                    deleteMesSnackbar.show();
                }
            }
            deselectAllItems();
            displayAllMessages();
        }
        else Snackbar.make(findViewById(android.R.id.content), "Выберите сообщение", Snackbar.LENGTH_SHORT).show();
    }

    public class MyUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Code to undo the user's last action
            list_message.add(selectedListItem);
        }
    }

    void clearEditText() {
        emojiconEditText.setText("");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_1:
                deleteMessage();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deselectAllItems() {
        createVibration(15);
        selectItemSize = 0;
        selectedListItem = null;
//        adapterView.setSelected(false);
        selectMode = false;
        toolbar.setTitle(R.string.app_name);
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

    private void displayAllMessages() {
        //показываем View загрузки
        circular_progress.setVisibility(View.VISIBLE);
        listOfMessages.setVisibility(View.INVISIBLE);

        mDatabaseReference.child("chats").child("general").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (list_message.size() > 0)
                    list_message.clear();
//                проходим по всем записям и помещаем их в list_users в виде класса Message
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Message uMessage = postSnapshot.getValue(Message.class);
                    list_message.add(uMessage);
                }

                ListViewAdapter adapter = new ListViewAdapter(ChatActivity.this, list_message, mAuth);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.menu_add:
                createVibration(50);
                return true;
            case R.id.menu_remove:
                createVibration(50);
                deleteMessage();
                return true;
            case R.id.menu_share:
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
                    lastShareTime = System.currentTimeMillis() + 60 * 1000;
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.new_message_after) + " " + (lastShareTime-System.currentTimeMillis())/1000 + " " + getResources().getString(R.string.seconds), Snackbar.LENGTH_SHORT).show();
                }
                return true;
        }
        return true;
    }

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }
}