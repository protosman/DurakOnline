package ru.tmkstd.cardgamedurakonline.ChatsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ru.tmkstd.cardgamedurakonline.R;

public class ChatsChatsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats_chats, container, false);
        return rootView;
    }
    public ChatsChatsFragment() {

    }
}
