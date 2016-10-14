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
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.Notification.Notifications;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.NotificationAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

        prefs = getContext().getSharedPreferences("Token",Context.MODE_PRIVATE);
        noNotificationTextView = (TextView)v.findViewById(R.id.no_notification_text);

        nLinearLayoutManager = new LinearLayoutManager(getActivity());
        nLinearLayoutManager.setReverseLayout(true);
        nLinearLayoutManager.setStackFromEnd(true);

        notificationsListView = (RecyclerView) v.findViewById(R.id.notifications_list);
        notificationsListView.setLayoutManager(nLinearLayoutManager);

        adapter = new NotificationAdapter(getActivity(), notificationsList);
        notificationsListView.setAdapter(adapter);
        getNotifications();

        refreshLayout =(SwipeRefreshLayout)v.findViewById(R.id.notif_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });
        return v;
    }

    public void getNotifications() {
        Helper.setOld_total(Helper.getNew_total());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();

        httpclient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonUtilities.local_books_api_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpclient.build())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);
        Call<List<Notifications>> call = usersAPI.getNotifs(Helper.getUserId(),"Token "+prefs.getString("token",null));
        call.enqueue(new Callback<List<Notifications>>() {
            @Override
            public void onResponse(Call<List<Notifications>> call, Response<List<Notifications>> response) {
                if (response.body() != null) {
                    List<Notifications> notifList = response.body();
                    if(notifList.size() == 0) {
                        noNotificationTextView.setVisibility(View.VISIBLE);
                    }
                    notificationsList.clear();
                    Helper.setNew_total(notifList.size());
                    notificationsList.addAll(notifList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Notifications>> call, Throwable t) {
                Toast.makeText(getActivity(), "Check your internet connection and try again!", Toast.LENGTH_SHORT).show();
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
