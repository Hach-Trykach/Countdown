package by.ddrvld.countdowndeathapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class ChatActivity extends AppCompatActivity {

//    private static int SIGN_IN_CODE = 1;
    private RelativeLayout activity_chat;
    private FirebaseListAdapter<Message> adapter;
    private EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    EmojIconActions emojIconActions;

    private Drawer drawerResult;

    //===============================initializeAuthVariables========================================
    final String TAG = "PhoneAuthActivity";
    final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    final int STATE_INITIALIZED = 1;
    final int STATE_CODE_SENT = 2;
    final int STATE_VERIFY_FAILED = 3;
    final int STATE_VERIFY_SUCCESS = 4;
    final int STATE_SIGNIN_FAILED = 5;
    final int STATE_SIGNIN_SUCCESS = 6;

    final int SIGN_IN_CODE = 7;

    final int BTN_EXIT = 10;

    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;

    TextView mStatusText;
    TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    Button mSignOutButton;

    private ListView listOfMessages;
    private ProgressBar circular_progress;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == SIGN_IN_CODE) {
//            if(resultCode == RESULT_OK) {
//                Snackbar.make(activity_chat, "Вы уже авторизованы", Snackbar.LENGTH_LONG).show();
//                displayAllMessages();
//            }
//            else {
//                Snackbar.make(activity_chat, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
//                finish();
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
//            onCreateAuth();
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        initializeDrawer(toolbar);

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
                if(emojiconEditText.getText().toString().equals("")) return;
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), emojiconEditText.getText().toString()));
                emojiconEditText.setText("");
            }
        });
        displayAllMessages();
    }

    @Override
    public void onBackPressed() {
        if(drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void initializeDrawer(Toolbar toolbar) {
        AccountHeader accountHeader = initializeAccountHeader();

        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
//                .withDisplayBelowToolbar(true)
                .withDisplayBelowStatusBar(false) // Походу не работает
                .withActionBarDrawerToggleAnimated(true)
                .addStickyDrawerItems(initializeDrawerItems())
                .withOnDrawerItemClickListener(onClicksLis)
                .build();
    }

    private Drawer.OnDrawerItemClickListener onClicksLis = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if(drawerItem.getIdentifier() == BTN_EXIT)
            {
                FirebaseAuth.getInstance().signOut();
//                onCreateAuth();
                return true;
            }
            return false;
        }
    };

    private IDrawerItem[] initializeDrawerItems() {
        return new IDrawerItem[]{new PrimaryDrawerItem()
                .withName(R.string.sign_out)
//                .withIcon(R.drawable.)
                .withIdentifier(BTN_EXIT),
                new DividerDrawerItem()};
    }

    private AccountHeader initializeAccountHeader() {
        IProfile profile = new ProfileDrawerItem()
                .withName("Dudarev Vlad")
                .withEmail("dudarev.vlad@gmail.com")
                .withIcon((getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_portrait)));

        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(profile)
                .build();
    }

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