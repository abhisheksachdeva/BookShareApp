package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdsmdg.bookshareapp.BSA.Listeners.EndlessScrollListener;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notification_Model;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.NotificationAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationFragment extends Fragment {

    RecyclerView notificationsListView;
    LinearLayoutManager nLinearLayoutManager;
    NotificationAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    List<Notifications> notificationsList = new ArrayList<>();
    TextView noNotificationTextView;
    SharedPreferences prefs;

    private OnFragmentInteractionListener mListener;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        prefs = getContext().getSharedPreferences("Token", Context.MODE_PRIVATE);
        noNotificationTextView = (TextView) v.findViewById(R.id.no_notification_text);

        nLinearLayoutManager = new LinearLayoutManager(getActivity());


        notificationsListView = (RecyclerView) v.findViewById(R.id.notifications_list);
        notificationsListView.setLayoutManager(nLinearLayoutManager);

        adapter = new NotificationAdapter(getActivity(), notificationsList);
        notificationsListView.setAdapter(adapter);
        getNotifications("1");

        final EndlessScrollListener endlessScrollListener = new EndlessScrollListener((LinearLayoutManager) nLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getNotifications(String.valueOf(page + 1));
            }
        };

        notificationsListView.addOnScrollListener(endlessScrollListener);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.notif_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.reset();
                getNotifications("1");
            }
        });
        return v;
    }

    public void getNotifications(final String page) {
        Helper.setOld_total(Helper.getNew_total());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtilities.local_books_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);
        Call<Notification_Model> call = usersAPI.getNotifs(page, "Token " + prefs.getString("token", null));
        call.enqueue(new Callback<Notification_Model>() {

            @Override
            public void onResponse(Call<Notification_Model> call, Response<Notification_Model> response) {
                if (response.body() != null) {

                    List<Notifications> notifList = response.body().getNotificationsList();
                    if (notifList.size() == 0) {
                        noNotificationTextView.setText("No new notifications");
                        noNotificationTextView.setVisibility(View.VISIBLE);
                    } else {
                        noNotificationTextView.setVisibility(View.GONE);
                    }

                    if (page.equals("1")) {
                        notificationsList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    Helper.setNew_total(notifList.size());
                    notificationsList.addAll(notifList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Notification_Model> call, Throwable t) {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.setText("You are offline");
                refreshLayout.setRefreshing(false);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
