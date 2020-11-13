package by.ddrvld.countdowndeathapp;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class ListViewAdapter extends BaseAdapter {

    Activity activity;
    List<Message> listMessage;
    LayoutInflater inflater;
    FirebaseAuth mAuth;

    public ListViewAdapter(Activity activity, List<Message> listMessage, FirebaseAuth mAuth) {
        this.activity = activity;
        this.listMessage = listMessage;
        this.mAuth = mAuth;
    }

    @Override
    public int getCount() {
        return listMessage.size();
    }

    @Override
    public Object getItem(int i) {
        return listMessage.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) activity
                .getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView;
        if(listMessage.get(i).getEmail().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())) {
            itemView = inflater.inflate(R.layout.list_item_right, null);
        }
        else {
            itemView = inflater.inflate(R.layout.list_item_left, null);
        }

        TextView mess_user, mess_time;
        TextView mess_text;
        mess_user = itemView.findViewById(R.id.item_message_user);
        mess_time = itemView.findViewById(R.id.item_message_time);
        mess_text = itemView.findViewById(R.id.item_message_text);

        mess_user.setText(listMessage.get(i).getUserName());
//        mess_time.setText(DateFormat.format("dd.MM.yyy\nHH:mm:ss", listMessage.get(i).getMessageTime()));
        mess_time.setText(DateFormat.format("HH:mm", listMessage.get(i).getMessageTime()));
        mess_text.setText(listMessage.get(i).getTextMessage());

        return  itemView;
    }
}
