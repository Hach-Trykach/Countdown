package by.ddrvld.countdowndeathapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
public class LeftFragment extends Fragment {

    static LeftFragment newInstance(int page) {
        LeftFragment leftFragment = new LeftFragment();
        Bundle arguments = new Bundle();
        leftFragment.setArguments(arguments);
        return leftFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_date, null);
    }
}
