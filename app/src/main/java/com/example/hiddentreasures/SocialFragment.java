package com.example.hiddentreasures;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.FriendRequest;
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
        friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList()));

        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 100000, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 1000000, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 10000000, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 100000000, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 1000000000, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 10000000000L, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 100000000000L, "Test123"));
        user.addFriendRequest(new FriendRequest(System.currentTimeMillis() - 1000000000000L, "Test123"));

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
            requestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));

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

    public void updateViews() {
        friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList()));
        requestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));
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

            if (view != null)
                return view;

            final int index = i;
            final View thisView = layoutInflater.inflate(R.layout.friends_list_element, null);
            final FriendListHolder holder = new FriendListHolder();

            holder.lastSeenText = thisView.findViewById(R.id.lastSeenText);
            holder.usernameText = thisView.findViewById(R.id.usernameText);


            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User friend = dataSnapshot.child(friends.get(index)).getValue(User.class);

                    String lastSeenText = Util.getTimeDifferenceText(friend.getLastSeen());

                    holder.usernameText.setText(friend.getUsername());
                    holder.lastSeenText.setText(lastSeenText);

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
            TextView lastSeenText;
        }
    }

    /**
     * Structure to hold all of the components of a list element
     */
    class FriendRequestAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<FriendRequest> friendRequests = new ArrayList<>();

        /**
         * Constructor
         *
         * @param context        current application context
         * @param friendRequests list of friend requests the user has received
         */
        public FriendRequestAdapter(Context context, ArrayList<FriendRequest> friendRequests) {

            this.context = context;
            this.friendRequests.addAll(friendRequests);
            layoutInflater = LayoutInflater.from(context);
            count = friendRequests.size();
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
            return friendRequests.get(i);
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

            if (view != null)
                return view;

            final View thisView = layoutInflater.inflate(R.layout.requests_list_element, null);
            final FriendRequestHolder holder = new FriendRequestHolder();
            final FriendRequest request = friendRequests.get(i);

            holder.usernameText = thisView.findViewById(R.id.usernameText);
            holder.timeReceivedText = thisView.findViewById(R.id.timeReceivedText);
            holder.btnAccept = thisView.findViewById(R.id.btnAccept);
            holder.btnDelete = thisView.findViewById(R.id.btnDelete);

            holder.usernameText.setText(request.getUsername());
            holder.timeReceivedText.setText(Util.getTimeDifferenceText(request.getTime()));

            users.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User requestSender = dataSnapshot.child(request.getUsername()).getValue(User.class);

                    holder.btnAccept.setOnClickListener(v -> {

                        user.acceptFriendRequest(friendRequests.get(i));
                        requestSender.addFriend(user.getUsername());

                        users.child(user.getUsername()).setValue(user);
                        users.child(requestSender.getUsername()).setValue(requestSender);

                        updateViews();

                    });

                    holder.btnDelete.setOnClickListener(v -> {

                        user.removeFriendRequest(friendRequests.get(i));
                        users.child(user.getUsername()).setValue(user);

                        updateViews();

                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            thisView.setTag(holder);
            view = thisView;

            return view;
        }

        /**
         * Structure for holding all components of a list element
         */
        class FriendRequestHolder {
            TextView usernameText;
            TextView timeReceivedText;
            Button btnAccept;
            Button btnDelete;
        }
    }


}