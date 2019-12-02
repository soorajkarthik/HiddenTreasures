package com.example.hiddentreasures.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.hiddentreasures.Model.FriendRequest;
import com.example.hiddentreasures.Model.User;
import com.example.hiddentreasures.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Structure to hold all of the components of a list element
 */
public class FriendRequestAdapter extends BaseAdapter {

    int count;
    Context context;
    private DatabaseReference users;
    private LayoutInflater layoutInflater;
    private ArrayList<FriendRequest> friendRequests = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context        current application context
     * @param friendRequests list of friend requests the user has received
     */
    public FriendRequestAdapter(Context context, ArrayList<FriendRequest> friendRequests, DatabaseReference users) {

        this.context = context;
        this.friendRequests.addAll(friendRequests);
        layoutInflater = LayoutInflater.from(context);
        count = friendRequests.size();
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

        holder.usernameText.setText(request.getUsername());
        holder.timeReceivedText.setText(Util.getTimeDifferenceText(request.getTime()));

        users.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final User requestSender = dataSnapshot.child(request.getUsername()).getValue(User.class);
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
    }
}

