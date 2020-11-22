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
    List<Message> list_message;
    LayoutInflater inflater;
    FirebaseAuth mAuth;

    public ListViewAdapter(Activity activity, List<Message> list_message, FirebaseAuth mAuth) {
        this.activity = activity;
        this.list_message = list_message;
        this.mAuth = mAuth;
    }

    @Override
    public int getCount() {
        return list_message.size();
    }

    @Override
    public Object getItem(int i) {
        return list_message.get(i);
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
        if(list_message.get(i).getEmail().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())) {
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

        mess_user.setText(list_message.get(i).getUserName());
//        mess_time.setText(DateFormat.format("dd.MM.yyy\nHH:mm:ss", listMessage.get(i).getMessageTime()));
        mess_time.setText(DateFormat.format("HH:mm", list_message.get(i).getMessageTime()));
        mess_text.setText(list_message.get(i).getTextMessage());

        return  itemView;
    }

    public void removeItem(int position) {
        list_message.remove(position);
        notifyDataSetChanged();
    }
}
