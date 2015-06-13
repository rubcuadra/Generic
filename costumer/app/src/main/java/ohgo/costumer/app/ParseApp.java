package ohgo.costumer.app;

import android.app.Application;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import ohgo.costumer.Service;

/**
 * Created by Ruben on 6/7/15.
 */
public class ParseApp extends Application
{
    public void onCreate()
    {
        ParseObject.registerSubclass(Service.class);
        Parse.initialize(this, "GbhsPA5Oh2yu2voFbxo45iJgFqPoJWd3kzZnqBZi", "diMBtwqe0Ysm0XXp2wb5fD5qpKusC1pkPeEgKDIQ");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        //ParseSignUp("ParisTest", "test", "test@ohgo.me");

        if(ParseUser.getCurrentUser() == null)
            ParseLogIn("ParisTest","test");
        else
        {
            //Ya esta Logeado alguien
        }

    }

    void ParseSignUp(final String userName, final String password,String mail)
    {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(mail);

        user.signUpInBackground(new SignUpCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e == null)
                { //NO ERROS
                    ParseLogIn(userName, password);
                } else
                {
                    Log.d("Log",e.toString());
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    void ParseLogIn(String userName,String password)
    {
        ParseUser.logInInBackground(userName, password, new LogInCallback()
        {
            @Override
            public void done(ParseUser parseUser, ParseException e)
            {
                if (parseUser != null)
                {
                    // Hooray! The user is logged in.
                    Log.d("Log",parseUser.getObjectId().toString());
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });
    }

}
