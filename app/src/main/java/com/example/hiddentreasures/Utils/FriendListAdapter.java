package com.example.hiddentreasures.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.hiddentreasures.Model.User;
import com.example.hiddentreasures.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Custom adapter for friendList ListView
 */
public class FriendListAdapter extends BaseAdapter {

    int count;
    Context context;
    private DatabaseReference users;
    private LayoutInflater layoutInflater;
    private ArrayList<String> friends = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context current application context
     * @param friends user's friend list
     */
    public FriendListAdapter(Context context, ArrayList<String> friends, DatabaseReference users) {
        layoutInflater = LayoutInflater.from(context);
        this.friends.addAll(friends);
        count = friends.size();
        this.context = context;
        this.users = users;
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
