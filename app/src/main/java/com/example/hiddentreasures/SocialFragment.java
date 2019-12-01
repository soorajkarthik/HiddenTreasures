package com.example.hiddentreasures;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.User;
import com.example.hiddentreasures.Utils.Util;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class SocialFragment extends Fragment {


    private FirebaseDatabase database;
    private DatabaseReference users;
    private String username;
    private User user;
    private View view;
    private ListView friendList, searchList;
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
        friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_social, menu);

    }

    /**
     * @return the view corresponding to this fragment
     */
    public View getMyView() {
        return view;
    }

    /**
     * Custom adapter for friendList ListView
     */
    class FriendListAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<String> friends = new ArrayList<>();

        /**
         * Constructor
         *
         * @param context current application context
         * @param friends user's friend list
         */
        public FriendListAdapter(Context context, ArrayList<String> friends) {
            layoutInflater = LayoutInflater.from(context);
            this.friends.addAll(friends);
            count = friends.size();
            this.context = context;
        }

        /**
         * @return number of elements there will be in ListView
         */
        @Override
        public int getCount() {
            return count;
        }

        /**
         * @param i index of object
         * @return username at index i
         */
        @Override
        public Object getItem(int i) {
            return friends.get(i);
        }

        /**
         * Required method in order to be subclass of BaseAdapter
         */
        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Configures element of ListView
         *
         * @param i         index of element in ListView
         * @param view      view of the element in index i
         * @param viewGroup the ListView
         * @return the view of the configured element in the ListView
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final int index = i;
            final View thisView = layoutInflater.inflate(R.layout.friends_view, null);
            final FriendListHolder holder = new FriendListHolder();

            holder.lastSeen = thisView.findViewById(R.id.lastSeenText);
            holder.usernameText = thisView.findViewById(R.id.usernameText);


            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User friend = dataSnapshot.child(friends.get(index)).getValue(User.class);

                    String lastSeenText = Util.getTimeDifferenceText(friend.getLastSeen());

                    holder.usernameText.setText(friend.getUsername());
                    holder.lastSeen.setText(lastSeenText);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            view = thisView;
            view.setTag(holder);

            return view;
        }


        /**
         * Structure to hold all of the components of a list element
         */
        class FriendListHolder {

            TextView usernameText;
            TextView lastSeen;
        }
    }

}