package com.example.hiddentreasures;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.example.hiddentreasures.Model.FriendRequest;
import com.example.hiddentreasures.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class SocialFragment extends Fragment {

  private FirebaseDatabase database;
  private DatabaseReference users;
  private String username;
  private User user;
  private ArrayList<User> userList, friendList;
  private View view;
  private ListView friendListView, requestListView, leaderboardListView, searchListView;
  private SearchView searchView;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    database = FirebaseDatabase.getInstance();
    users = database.getReference("Users");
    user = ((MainActivity) getActivity()).getUser();
    userList = ((MainActivity) getActivity()).getUserList();
    friendList = ((MainActivity) getActivity()).getFriendList();
    username = user.getUsername();

    users.addValueEventListener(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            friendList.clear();
            user.getFriendList()
                .forEach(
                    friendName ->
                        friendList.add(dataSnapshot.child(friendName).getValue(User.class)));
            friendList.add(user.placeHolderUser());
            friendList.sort(User::compareTo);
            friendListView.setAdapter(new FriendListAdapter(getContext(), friendList));
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
          }
        });

    view = inflater.inflate(R.layout.fragment_social, container, false);
    friendListView = view.findViewById(R.id.friendList);
    friendListView.setAdapter(new FriendListAdapter(getActivity(), friendList));
    friendListView.setOnItemClickListener(
        (parent, clickedView, position, id) -> {
          User friend = friendList.get(position);
          new AlertDialog.Builder(getContext())
              .setTitle(friend.getUsername())
              .setMessage(friend.scoreSummary())
              .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
              .create()
              .show();
        });

    searchListView = view.findViewById(R.id.userSearchList);
    setHasOptionsMenu(true);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    inflater.inflate(R.menu.menu_social, menu);

    searchView = (SearchView) menu.getItem(2).getActionView();
    searchView.setQueryHint("Start typing to search");
    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          /** Required method, no action necessary */
          @Override
          public boolean onQueryTextSubmit(String query) {

            return false;
          }

          /**
           * Refreshes search results every time user types/deletes a letter
           *
           * @param newText the string currently in the search bar
           * @return false since listener does not show any suggestions
           */
          @Override
          public boolean onQueryTextChange(String newText) {

            if (newText.length() > 0) {

              updateSearchResults(newText);
              setFriendListInvisible();
            } else {
              setFriendListVisible();
            }

            return false;
          }
        });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.optFriendRequests) {

      View inflatedView =
          LayoutInflater.from(getContext())
              .inflate(R.layout.list_view, (ViewGroup) this.view, false);

      requestListView = inflatedView.findViewById(R.id.listView);
      requestListView.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));

      new AlertDialog.Builder(getContext())
          .setTitle("Friend Requests")
          .setView(inflatedView)
          .setNegativeButton("Done", (dialog, which) -> dialog.dismiss())
          .create()
          .show();
    } else if (item.getItemId() == R.id.optLeaderBoard) {

      View inflatedView =
          LayoutInflater.from(getContext())
              .inflate(R.layout.list_view, (ViewGroup) this.view, false);

      leaderboardListView = inflatedView.findViewById(R.id.listView);
      leaderboardListView.setAdapter(new LeaderboardListAdapter(getContext(), userList));

      leaderboardListView.setOnItemClickListener(
          (parent, view1, position, id) ->
              new AlertDialog.Builder(getContext())
                  .setTitle(userList.get(position).getUsername())
                  .setMessage(userList.get(position).scoreSummary())
                  .setNegativeButton("Ok", (dialog, which) -> dialog.dismiss())
                  .create()
                  .show());

      new AlertDialog.Builder(getContext())
          .setTitle("Global Leaderboard")
          .setView(inflatedView)
          .setNegativeButton("Ok", ((dialog, which) -> dialog.dismiss()))
          .create()
          .show();
    }

    return true;
  }

  /**
   * Filters usernames based on user's input Stores filtered usernames in a new ArrayList Updates display with search
   * results
   *
   * @param searchText the user's input
   */
  private void updateSearchResults(String searchText) {

    ArrayList<User> filteredUserList = userList.stream()
        .filter(u -> u.getUsername().toLowerCase().contains(searchText.toLowerCase()))
        .collect(Collectors.toCollection(ArrayList::new));

    filteredUserList.remove(user);
    searchListView.setAdapter(new UserSearchAdapter(getActivity(), filteredUserList));
  }

  /**
   * Sets friend list visible Sets search results invisible
   */
  private void setFriendListVisible() {
    friendListView.setVisibility(View.VISIBLE);
    searchListView.setVisibility(View.INVISIBLE);
  }

  /**
   * Sets friend list invisible Sets search results visible
   */
  private void setFriendListInvisible() {
    friendListView.setVisibility(View.INVISIBLE);
    searchListView.setVisibility(View.VISIBLE);
  }

  private String getTimeDifferenceText(long oldTime) {

    long currentTime = System.currentTimeMillis();
    long difference = currentTime - oldTime;

    long years = difference / ((long) 365 * 24 * 60 * 60 * 1000);
    long months = difference / ((long) 30 * 24 * 60 * 60 * 1000);
    long weeks = difference / (7 * 24 * 60 * 60 * 1000);
    long days = difference / (24 * 60 * 60 * 1000);
    long hours = difference / (60 * 60 * 1000);
    long minutes = difference / (60 * 1000);

    if (years > 0) {

      return years + " years(s) ago";
    } else if (months > 0) {

      return months + " month(s) ago";
    } else if (weeks > 0) {

      return weeks + " week(s) ago";
    } else if (days > 0) {

      return days + " day(s) ago";
    } else if (hours > 0) {

      return hours + " hour(s) ago";
    } else if (minutes > 0) {

      return minutes + " minute(s) ago";
    } else {

      return "Online";
    }
  }

  enum RequestType {
    SENT,
    ACCEPTED,
    NONE
  }

  /**
   * Custom adapter for friendList ListView
   */
  class FriendListAdapter extends BaseAdapter {

    int count;

    Context context;

    private LayoutInflater layoutInflater;

    private ArrayList<User> friends = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context current application context
     * @param friends user's friend list
     */
    public FriendListAdapter(Context context, ArrayList<User> friends) {
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

      final View thisView = layoutInflater.inflate(R.layout.friends_list_element, null);
      final FriendListHolder holder = new FriendListHolder();
      final User friend = friends.get(i);
      String lastSeenText = getTimeDifferenceText(friend.getLastSeen());

      holder.usernameText = thisView.findViewById(R.id.usernameText);
      holder.rankText = thisView.findViewById(R.id.rankText);
      holder.scoreText = thisView.findViewById(R.id.scoreText);
      holder.lastSeenText = thisView.findViewById(R.id.lastSeenText);

      holder.usernameText.setText(friend.getUsername());
      holder.rankText.setText((i + 1) + "");
      holder.scoreText.setText(friend.calculateScore() + "");
      holder.lastSeenText.setText(lastSeenText);

      view = thisView;
      view.setTag(holder);

      return view;
    }

    /**
     * Structure to hold all of the components of a list element
     */
    class FriendListHolder {

      TextView usernameText;
      TextView rankText;
      TextView scoreText;
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

      final View thisView = layoutInflater.inflate(R.layout.requests_list_element, null);
      final FriendRequestHolder holder = new FriendRequestHolder();
      final FriendRequest request = friendRequests.get(i);

      holder.usernameText = thisView.findViewById(R.id.usernameText);
      holder.timeReceivedText = thisView.findViewById(R.id.timeReceivedText);
      holder.btnAccept = thisView.findViewById(R.id.btnAccept);
      holder.btnDelete = thisView.findViewById(R.id.btnDelete);

      holder.usernameText.setText(request.getUsername());
      holder.timeReceivedText.setText(getTimeDifferenceText(request.getTime()));

      users.addListenerForSingleValueEvent(
          new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              User requestSender = dataSnapshot.child(request.getUsername()).getValue(User.class);

              holder.btnAccept.setOnClickListener(
                  v -> {
                    user.acceptFriendRequest(friendRequests.get(i));
                    requestSender.addFriend(user.getUsername());

                    users.child(user.getUsername()).setValue(user);
                    users.child(requestSender.getUsername()).setValue(requestSender);
                    requestListView.setAdapter(
                        new FriendRequestAdapter(getContext(), user.getFriendRequests()));
                  });

              holder.btnDelete.setOnClickListener(
                  v -> {
                    user.removeFriendRequest(friendRequests.get(i));
                    users.child(user.getUsername()).setValue(user);
                    requestListView.setAdapter(
                        new FriendRequestAdapter(getContext(), user.getFriendRequests()));
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

  /**
   * Structure to hold all of the components of a list element
   */
  class LeaderboardListAdapter extends BaseAdapter {

    int count;

    Context context;

    private LayoutInflater layoutInflater;

    private ArrayList<User> userList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context  current application context
     * @param userList list of all users in database
     */
    public LeaderboardListAdapter(Context context, ArrayList<User> userList) {

      this.context = context;
      this.userList.addAll(userList);
      layoutInflater = LayoutInflater.from(context);
      count = userList.size();
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
      return userList.get(i);
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

      final View thisView = layoutInflater.inflate(R.layout.leaderboard_list_element, null);
      final LeaderboardListHolder holder = new LeaderboardListHolder();
      final User thisUser = userList.get(i);

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
      Date date = new Date(thisUser.getDateJoined());

      holder.usernameText = thisView.findViewById(R.id.usernameText);
      holder.rankText = thisView.findViewById(R.id.rankText);
      holder.scoreText = thisView.findViewById(R.id.scoreText);
      holder.dateJoinedText = thisView.findViewById(R.id.dateJoinedText);
      holder.usernameText.setText(thisUser.getUsername());
      holder.rankText.setText((i + 1) + "");
      holder.scoreText.setText(thisUser.calculateScore() + "");
      holder.dateJoinedText.setText(simpleDateFormat.format(date));

      thisView.setTag(holder);
      view = thisView;

      return view;
    }

    /**
     * Structure for holding all components of a list element
     */
    class LeaderboardListHolder {

      TextView usernameText;
      TextView scoreText;
      TextView rankText;
      TextView dateJoinedText;
    }
  }

  /**
   * Custom adapter for searchResults ListView
   */
  class UserSearchAdapter extends BaseAdapter {

    int count;

    Context context;

    private LayoutInflater layoutInflater;

    private ArrayList<User> filterUserList;

    /**
     * Constructor
     *
     * @param context        current application context
     * @param filterUserList filtered search results
     */
    public UserSearchAdapter(Context context, ArrayList<User> filterUserList) {

      layoutInflater = LayoutInflater.from(context);
      this.filterUserList = new ArrayList<>();
      this.filterUserList.addAll(filterUserList);
      this.count = filterUserList.size();
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
      return filterUserList.get(i);
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

      View thisView = layoutInflater.inflate(R.layout.user_search_list_element, null);
      FriendSearchHolder holder = new FriendSearchHolder();
      holder.usernameText = thisView.findViewById(R.id.usernameText);
      holder.requestStatusText = thisView.findViewById(R.id.requestStatusText);
      holder.updateFriendStatus = thisView.findViewById(R.id.updateFriendStatus);

      final User searchedUser = filterUserList.get(i);
      final RequestType requestType;

      holder.usernameText.setText(searchedUser.getUsername());

      if (searchedUser.hasFriendRequestFromUser(username)) {

        holder.requestStatusText.setText("Requested");
        holder.updateFriendStatus.setText("Cancel");
        requestType = RequestType.SENT;
      } else if (user.isFriendOfUser(searchedUser.getUsername())) {

        holder.requestStatusText.setText("Accepted");
        holder.updateFriendStatus.setText("Remove");
        requestType = RequestType.ACCEPTED;
      } else {

        holder.requestStatusText.setText("None");
        holder.updateFriendStatus.setText("Request");
        requestType = RequestType.NONE;
      }

      users.addListenerForSingleValueEvent(
          new ValueEventListener() {
            /**
             * Checks to see if user has SENT/is a friend of searchedUser If user is a friend, allow
             * them to remove the searchedUser from their friend list If user has already SENT a
             * friend request, allow them to cancel the request If neither, allow user to send the
             * searchedUser a friend request
             *
             * @param dataSnapshot snapshot of the Users node in its current state
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              switch (requestType) {
                case SENT:
                  holder.updateFriendStatus.setOnClickListener(
                      (View view) -> {
                        searchedUser.removeFriendRequestFromUser(username);
                        users.child(searchedUser.getUsername()).setValue(searchedUser);
                        updateSearchResults(searchView.getQuery().toString());
                      });

                  break;

                case ACCEPTED:
                  holder.updateFriendStatus.setOnClickListener(
                      (View view) -> {
                        user.removeFriend(searchedUser.getUsername());
                        searchedUser.removeFriend(username);

                        users.child(searchedUser.getUsername()).setValue(searchedUser);
                        users.child(username).setValue(user);
                        updateSearchResults(searchView.getQuery().toString());
                      });

                  break;

                case NONE:
                  holder.updateFriendStatus.setOnClickListener(
                      (View view) -> {
                        searchedUser.addFriendRequest(
                            new FriendRequest(System.currentTimeMillis(), username));
                        users.child(searchedUser.getUsername()).setValue(searchedUser);
                        updateSearchResults(searchView.getQuery().toString());
                      });

                  break;
              }
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
    class FriendSearchHolder {

      TextView usernameText;
      Button updateFriendStatus;
      TextView requestStatusText;
    }
  }
}
