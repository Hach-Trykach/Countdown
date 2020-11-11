package by.ddrvld.countdowndeathapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.DateFormat;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private DatabaseReference mDatabaseReference;

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
                        text = text.substring(0, text.length() -1); // Удаляем последний символ
                        Log.d("", "SUBSTRING");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while (text.contains("\n\n\n")) {
                    try {
                        text = text.replaceAll("\n\n\n", "\n\n");
//                        Snackbar.make(findViewById(android.R.id.content), "Слишком много отступов", Snackbar.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                if(text.equals(" ")) return;
//                if(text.equals("\n")) return;
//                if(text.length() < 2) return;
                if(text.isEmpty()) {
                }
                else {
                    FirebaseDatabase.getInstance().getReference().push().setValue(
                            new Message(user.getEmail(), user.getDisplayName(), text));
                    clearEditText();
                }
            }
        });
        displayAllMessages();

        circular_progress = findViewById(R.id.circular_progress);
        listOfMessages = findViewById(R.id.list_of_messages);

//        initFirebase();

        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selectMode) {
                    if(listOfMessages.isItemChecked(i) /*&& itemOldClickId != i*/) {
                        view.setBackground(getResources().getDrawable(R.drawable.yes_no_buttons));

                        view.setSelected(true);
                        selectItemSize++;

                        Message message = (Message) adapterView.getItemAtPosition(i);
                        selectedListItem = message;

                        toolbar.setTitle("" + selectItemSize);
                    }
                    else {
                        view.setBackground(getResources().getDrawable(R.drawable.yes_no_buttons));
                        view.setSelected(false);
                        if(selectItemSize > 0) {
                            selectItemSize--;
                            toolbar.setTitle("" + selectItemSize);
                        }
                        if (selectItemSize == 0){
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
//                    Notes note = (Notes) adapterView.getItemAtPosition(i);
//                    selectedListItem = note;
//                    adapterView.setSelected(true);
                    selectMode = true;
//                    view.setSelected(true);
//                    listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    return false;
                }
                return true;
            }
        });
    }

    private void createMessage() {
        //создаем элемент класса Notes
        Message message = new Message(user.getEmail(), String.valueOf(System.currentTimeMillis() / 1000), emojiconEditText.getText().toString());
        mDatabaseReference.child("users")
//                .child(mAuth.getUid() != null ? mAuth.getUid() : "CgPHNky1EFRqBSCvpp1HgJNZ3U")
                .child(mAuth.getUid())
                .child(message.getEmail())
                .setValue(message);
        //очищаем поля ввода
        clearEditText();
    }

    private void updateMessage(Message selectedListItem) {
        mDatabaseReference.child("users")
                .child(mAuth.getUid())
                .child(selectedListItem.getEmail())
                .child("message")
                .setValue(selectedListItem.getTextMessage());
//        selectedListItem = null;
        //очищаем поля ввода
        clearEditText();
    }

    private void deleteMessage() {
        if(selectedListItem != null) {
            chosen = listOfMessages.getCheckedItemPositions();
            for (int i = 0; i < chosen.size(); i++) {
                // если пользователь выбрал пункт списка,
                // то выводим его в TextView.
                Message message = (Message) listOfMessages.getItemAtPosition(chosen.keyAt(i));
                selectedListItem = message;
                if (chosen.valueAt(i)) {
                    mDatabaseReference.child("users")
                            .child(mAuth.getUid())
                            .child(selectedListItem.getEmail())
                            .removeValue();
                }
            }
            deselectAllItems();
        }
        else Snackbar.make(findViewById(android.R.id.content), "Выберите сообщение", Snackbar.LENGTH_SHORT).show();
    }

    void clearEditText() {
        emojiconEditText.setText("");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_1:
                deleteMessage();
                Snackbar.make(findViewById(android.R.id.content), "Выбран Option_1", Snackbar.LENGTH_SHORT).show();
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
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//            connectedRef.child("OOC").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
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