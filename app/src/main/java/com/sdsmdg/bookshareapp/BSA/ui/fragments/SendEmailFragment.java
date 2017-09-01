package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.ui.CustomProgressDialog;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendEmailFragment extends Fragment {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";

    private TextInputLayout emailInputLayout;
    private TextInputEditText emailInputEditText;
    private TextView titleTextView, descriptionTextView;
    private CustomProgressDialog customProgressDialog;
    private Button submitEmailButton;
    private String title;
    private String description;
    private String type;
    private boolean isTypeForgotPassword;

    private OnFragmentInteractionListener mListener;

    public SendEmailFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Title.
     * @param description Description.
     * @param type Type
     * @return A new instance of fragment SendEmailFragment.
     */
    public static SendEmailFragment newInstance(String title, String description, String type) {
        SendEmailFragment fragment = new SendEmailFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESCRIPTION, description);
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            description = getArguments().getString(DESCRIPTION);
            type = getArguments().getString(TYPE);
            isTypeForgotPassword = type.equals("forgot_password_email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_email, container, false);
        initViews(view);
        setTitle();
        regListeners();
        return view;
    }

    private void initViews(View view) {
        customProgressDialog = new CustomProgressDialog(getActivity());
        emailInputLayout = (TextInputLayout) view.findViewById(R.id.email_edit_text);
        emailInputEditText = (TextInputEditText) view.findViewById(R.id.txt_email_address);
        titleTextView = (TextView) view.findViewById(R.id.txt_send_email_heading);
        descriptionTextView = (TextView) view.findViewById(R.id.txt_send_email_message);
        submitEmailButton = (Button) view.findViewById(R.id.submit_email_button1);
    }

    private void regListeners() {
        submitEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailInputEditText.getText().toString().trim().length() != 0){
                    emailInputLayout.setErrorEnabled(false);
                    sendEmail(emailInputEditText.getText().toString());
                } else{
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError("Please enter an email address!!");
                }
            }
        });
    }

    private void setTitle() {
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    private void sendEmail(String email) {
        customProgressDialog.show();
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<ResponseBody> sendEmailResponse;
        if (isTypeForgotPassword) {
             sendEmailResponse = usersAPI.sendForgotPasswordMail(email);
        }else {
             sendEmailResponse = usersAPI.sendNewActivationMail(email);
        }
        sendEmailResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                customProgressDialog.dismiss();
                if (response.isSuccessful()){
                    emailInputLayout.setEnabled(false);
                    Toast.makeText(getActivity(), "An E-mail has been sent to you", Toast.LENGTH_SHORT).show();
                    mListener.onFragmentInteraction("success");
                } else{
                    emailInputLayout.setEnabled(true);
                    Toast.makeText(getActivity(), "Invalid email address!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                customProgressDialog.dismiss();
                emailInputLayout.setEnabled(true);
                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        void onFragmentInteraction(String status);
    }
}
