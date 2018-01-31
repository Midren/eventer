package ucu.in.ua.mapstest;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.LinkAddress;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by momka45 on 31.01.18.
 */

public class FragmentProfile extends Fragment implements View.OnClickListener{

    private static final String TAG = "TAG";
    final int RC_SIGN_IN = 9876;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton sgnInBtn;
    Button sgnOutBtn;
    ImageView acntImgView;
    TextView acntName;
    TextView acntEmail;
    LinearLayout acntInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        sgnInBtn = (SignInButton) v.findViewById(R.id.sign_in_button);
        sgnOutBtn = (Button) v.findViewById(R.id.sign_out_button);
        acntImgView = (ImageView) v.findViewById(R.id.account_image);
        acntEmail = (TextView) v.findViewById(R.id.account_email);
        acntName = (TextView) v.findViewById(R.id.account_name);
        acntInfo = (LinearLayout) v.findViewById(R.id.account_info);

        sgnInBtn.setOnClickListener(this);
        sgnOutBtn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(v.getContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(v.getContext());
        updateUI(account);
        return v;
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
            // Signed in successfully, show authenticated UI.
            Log.v(TAG, "Succesfully logged in");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
        updateUI(null);
    }

    private void updateUI(GoogleSignInAccount account) {

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.username_text);
        TextView navEmail = (TextView) headerView.findViewById(R.id.email_text);
        ImageView navPicture = (ImageView) headerView.findViewById(R.id.nav_picture);
        if (account != null) {
            navUsername.setText(account.getDisplayName());
            navEmail.setText(account.getEmail());
            Picasso.with(navPicture.getContext()).load(account.getPhotoUrl()).transform(new BorderedCircleTransformation()).into(navPicture);

            acntInfo.setVisibility(View.VISIBLE);
            sgnInBtn.setVisibility(View.GONE);
            sgnOutBtn.setVisibility(View.VISIBLE);
            acntName.setText(account.getDisplayName());
            acntEmail.setText(account.getEmail());
            Picasso.with(acntImgView.getContext()).load(account.getPhotoUrl()).transform(new BorderedCircleTransformation()).into(acntImgView);
        }
        else {
            navUsername.setText("Eventer");
            navEmail.setText("");
            Picasso.with(navPicture.getContext()).load(R.mipmap.ic_launcher_round).into(navPicture);

            acntInfo.setVisibility(View.GONE);
            sgnInBtn.setVisibility(View.VISIBLE);
            sgnOutBtn.setVisibility(View.GONE);
        }
    }
}
