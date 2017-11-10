
/*
 * The MIT License
 *
 * Copyright 2017 SpyderText Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.spydertext.SpyderText;
import com.spydertext.account.sdk.AccountSDKException;
import com.spydertext.authentication.sdk.AuthenticationSDKException;
import com.spydertext.base.BaseApiException;
import com.spydertext.message.sdk.Message;
import com.spydertext.message.sdk.MessageSDKException;
import com.spydertext.message.sdk.Recipient;
import com.spydertext.message.sdk.Response;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * Example usages of the SDK. You will need to get your API Key 
 * from the API tab in your account settings.
 * 
 * You will need to call the createDeviceToken endpoint ONCE
 * to generate a device token. All API Endpoints (with the exception
 * of the createDeviceToken) require both an API Key and a Device
 * Token
 * @author mkramer
 */
public class App 
{
    public static final String SPYDERTEXT_API_KEY = "CHANGE_ME";
    public static final String SPYDERTEXT_DEVICE_TOKEN = "CHANGE_ME";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        SpyderText.init(SPYDERTEXT_API_KEY, SPYDERTEXT_DEVICE_TOKEN);
        AuthenticationAPI();
        AccountAPI();
        MessageAPI();
    }
    
    public static void AuthenticationAPI()
    {
        try {
        /**
         * Create Device Token
         * https://api.spydertext.com/docs/authentication/api-key
         */
        JSONObject result = SpyderText.Authentication().createDeviceToken();
        //store this in a file, env variable, or wherever you store secrets
        // this SHOULD NOT be called for every request, only once to generate the token
        String token = result.getString("device_token");
        } catch(AuthenticationSDKException ex) {
            System.out.println(ex.getMessage());
        }
    }   
    
    public static void AccountAPI()
    {
        try {
            
            /**
             * Get an Account Collection
             * https://api.spydertext.com/docs/accounts/collection
             */
            JSONObject collection = SpyderText.Account().collection(null);
            if(!collection.isNull("next")) {
                JSONObject nextPage = SpyderText.Account().next(collection.getString("next"));
            }
            
            //search
            JSONObject filters = new JSONObject();
            filters.put("q", "doe");
            collection = SpyderText.Account().collection(filters);
            
            /**
             * Get a single account
             * https://api.spydertext.com/docs/accounts/get
             */
            JSONObject account = SpyderText.Account().get(1235);
            
            //Load by phone
            account = SpyderText.Account().getByPhone("555-123-1234");
            
            //Load by email
            account = SpyderText.Account().getByEmail("noreply@spydertext.com");
            
            /**
             * Save Account (create new, or save existing. Will OVERWRITE existing account if sent an id)
             * https://api.spydertext.com/docs/save-account
             */
            JSONObject newUser = new JSONObject();
            newUser.put("mobile_phone", "15551251256");
            newUser.put("name", "Billy Bob");
            newUser.put("email", "kramer+billybob@spydertext.com");
            newUser.put("sms_opt_in", true);
            newUser.put("email_opt_in", true);
            newUser.put("active", true);
            
            JSONObject newAccount = SpyderText.Account().save(newUser);
            System.out.println(newAccount);
            
            /**
             * Delete an account
             * https://api.spydertext.com/docs/account/delete
             */
            JSONObject result = SpyderText.Account().delete(1242);
            System.out.println(result);
            
        } catch(AccountSDKException | BaseApiException | IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Example Message Usage of the Message API
     */
    public static void MessageAPI()
    {
        try {
            
            /**
             * Get a message collection
             * https://api.spydertext.com/docs/message/collection
             */
            JSONObject collection = SpyderText.Message().collection(null);
            
            //You can use the "next" property to fetch the next page of results
            if(!collection.isNull("next")) {
                JSONObject nextPage = SpyderText.Message().next(collection.getString("next"));
            }
            
            /**
             * Get a single message
             * https://api.spydertext.com/docs/message/get
             */
            
            Message testMessage = SpyderText.Message().get(1);
            
            /**
             * Send a message
             * https://api.spydertext.com/docs/message/send
             */
            Message message = new Message();
            message.put("text", "This is a test message");
            message.put("type", Message.TYPE_YES_NO);
            
            /**
             * Create the message responses (not needed for OneWay messages)
             */
            ArrayList<Response> responses = new ArrayList();
            Response yes = new Response(Response.YES, "You replied yes", 0, null);
            Response no = new Response(Response.NO, "You replied no...", 0, null);
            responses.add(yes);
            responses.add(no);
            
            message.put("responses", responses);
            
            /**
             * Add the recipients. Need the AccountID or the GroupID
             */
            Integer accountId = 1;
            Recipient me = new Recipient(accountId, null);
            
            ArrayList<Recipient> recipients = new ArrayList();
            recipients.add(me);
            message.put("recipients", recipients);
            
            //Send the message
            JSONObject result = SpyderText.Message().send(message);
            System.out.println(result);
            
        } catch(MessageSDKException | BaseApiException | IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
