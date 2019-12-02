package com.example.hiddentreasures;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.User;
import com.example.hiddentreasures.Utils.FriendListAdapter;
import com.example.hiddentreasures.Utils.FriendRequestAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SocialFragment extends Fragment {


    private FirebaseDatabase database;
    private DatabaseReference users;
    private String username;
    private User user;
    private View view;
    private ListView friendList, requestList, searchList;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_social, container, false);

        friendList = view.findViewById(R.id.friendList);
        friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList(), users));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_social, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.friendRequests) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Friend Requests");
            View inflatedView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_view,
                            (ViewGroup) this.view,
                            false);

            requestList = inflatedView.findViewById(R.id.listView);
            requestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests(), users));

            builder.setView(inflatedView);
            builder.setNegativeButton("Done", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else if (item.getItemId() == R.id.userSearch) {

        }

        return true;

    }

    /**
     * @return the view corresponding to this fragment
     */
    public View getMyView() {
        return view;
    }



}