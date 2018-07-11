package com.example.appideas_user4.facebookdigit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LoginButton loginButton1;
    CallbackManager callbackManager;
    public static int APP_REQUEST_CODE = 99;
    public static final String TAG = "MainActivity";
    private Button login,logout;
    public static int REQUEST_CODE=999;
    String toastMessage;
    private String facebook_id,f_name, m_name, l_name, gender, profile_image, full_name, email_id;


    TextView tv_name;
    ImageView profileimage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountKit.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.appideas_user4.facebookdigit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        login=(Button)findViewById(R.id.login);
        tv_name=(TextView) findViewById(R.id.tv_name);
        profileimage=(ImageView) findViewById(R.id.profileimage);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StartLoginPage(LoginType.PHONE);
            }
        });


        callbackManager = CallbackManager.Factory.create();
        loginButton1 = (LoginButton) findViewById(R.id.login_button1);
        loginButton1.setReadPermissions();
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton1.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        facebook_id = profile.getId();
                        Log.e("facebook_id", facebook_id);
                        f_name = profile.getFirstName();
                        Log.e("f_name", f_name);
                        m_name = profile.getMiddleName();
                        Log.e("m_name", m_name);
                        l_name = profile.getLastName();
                        Log.e("l_name", l_name);
                        full_name = profile.getName();
                        Log.e("full_name", full_name);
                        profile_image = profile.getProfilePictureUri(400, 400).toString();
                        Log.e("profile_image", profile_image);
                    }
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
/*

        Home fragment2 = new Home();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment2);
        fragmentTransaction.commit();
*/

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ImageView btnLinkedinMain = (ImageView) findViewById(R.id.Linked);

        btnLinkedinMain.setOnClickListener(this);

    }


    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                JSONObject json = response.getJSONObject();
                System.out.println("Json data :"+json);
                try {
                    if(json != null){
                        String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        //details_txt.setText(Html.fromHtml(text));
                       // profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void StartLoginPage(LoginType loginType) {
        if (loginType == LoginType.EMAIL) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.EMAIL,
                            AccountKitActivity.ResponseType.CODE); // Use token when 'Enable client Access Token Flow' is YES
            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        } else if (loginType == LoginType.PHONE) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.CODE); // Use token when 'Enable client Access Token Flow' is YES
            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void getCurrentAccount()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {

                @Override
                public void onSuccess(final Account account) {

                    // Get Account Kit ID
                    String accountKitId = account.getId();
                    Log.e("Account Kit Id", accountKitId);

                    if(account.getPhoneNumber()!=null) {
                        Log.e("CountryCode", "" + account.getPhoneNumber().getCountryCode());
                        Log.e("PhoneNumber", "" + account.getPhoneNumber().getPhoneNumber());

                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String phoneNumberString = phoneNumber.toString();
                     //   logout.setVisibility(View.VISIBLE);
                      //  login.setVisibility(View.GONE);
                        Log.e("NumberString", phoneNumberString);


                    }

                    if(account.getEmail()!=null)
                        Log.e("Email",account.getEmail());
                }

                @Override
                public void onError(final AccountKitError error) {
                    // Handle Error
                  //  Log.e(TAG,error.toString());
                }
            });

        } else {
            //Handle new or logged out user
           // Log.e(TAG,"Logged Out");
            Toast.makeText(this,"Logged Out User",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        getCurrentAccount();
        if (requestCode == REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();

                return;

            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                return;
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }

                //  Intent intent=new Intent(this,SignedIn.class);
                //  StartActivity(intent);
            }
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();

        }
    }

    public void onClick(View v) {

        if (v.getId() == R.id.Linked) {

            LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
                @Override
                public void onAuthSuccess() {

                  //  getPrsonalInfo();
                    SharePost();
                }

                @Override
                public void onAuthError(LIAuthError error) {
                    // Handle authentication errors
                    Log.e("responceerror",error.toString());
                }
            }, true);

        }
    }



    private void getPrsonalInfo() {
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                try {
                    System.out.println("responcedata"+apiResponse.getResponseDataAsJson());
                JSONObject jsonObject=apiResponse.getResponseDataAsJson();
                String emailAddress=jsonObject.getString("emailAddress");
                String firstName=jsonObject.getString("firstName");
                String formattedName=jsonObject.getString("formattedName");
                String lastName=jsonObject.getString("lastName");
                String id=jsonObject.getString("id");
                tv_name.setText(formattedName);
                String pictureUrl=jsonObject.getString("pictureUrl");
                Glide.with(MainActivity.this).load(pictureUrl).into(profileimage);
                JSONObject jsonObject1=jsonObject.getJSONObject("pictureUrls");
                    String _total=jsonObject1.getString("_total");
                    String publicProfileUrl=jsonObject1.getString("publicProfileUrl");
                    JSONArray jsonArray=jsonObject1.getJSONArray("values");
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject jsonObject2=jsonArray.getJSONObject(i);
                        String value1=jsonObject2.getString("0");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Success!
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
            }
        });


    }


    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE,Scope.R_EMAILADDRESS);
    }


    private void SharePost() {
        String shareUrl = "https://api.linkedin.com/v1/people/~/shares";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.postRequest(MainActivity.this, shareUrl, buildShareMessage("Hello World", "Hello Title", "Hello Descriptions", "http://ankitthakkar90.blogspot.in/", "http://1.bp.blogspot.com/-qffW4zPyThI/VkCSLongZbI/AAAAAAAAC88/oGxWnHRwzBk/s320/10333099_1408666882743423_2079696723_n.png"), new ApiListener() {
            @Override


            public void onApiSuccess(ApiResponse apiResponse) {
                // ((TextView) findViewById(R.id.response)).setText(apiResponse.toString());
                try {
                Toast.makeText(getApplicationContext(), "Share success:  " + apiResponse.toString(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "share success" + apiResponse.getResponseDataAsJson());
                JSONObject jsonObject=apiResponse.getResponseDataAsJson();

                  String updateKey=jsonObject.getString("updateKey");
                  String updateUrl=jsonObject.getString("updateUrl");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override


            public void onApiError(LIApiError error) {
                //   ((TextView) findViewById(R.id.response)).setText(error.toString());
                Log.e(TAG, "share error" + error.toString());
                   Toast.makeText(getApplicationContext(), "Share failed " + error.toString(),
                Toast.LENGTH_LONG).show();
            }
        });
    }


    public String buildShareMessage(String comment,String title,String descriptions,String linkUrl,String imageUrl  ){
        String shareJsonText = "{ \n" +
                "   \"comment\":\"" + comment + "\"," +
                "   \"visibility\":{ " +
                "      \"code\":\"anyone\"" +
                "   }," +
                "   \"content\":{ " +
                "      \"title\":\""+title+"\"," +
                "      \"description\":\""+descriptions+"\"," +
                "      \"submitted-url\":\""+linkUrl+"\"," +
                "      \"submitted-image-url\":\""+imageUrl+"\"" +
                "   }" +
                "}";
        return shareJsonText;
    }

}
