package com.pmangplus.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.pmangplus.test.result.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;

import org.apache.http.client.HttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PmangPlusTest {

    // application identifier
    private static final String APP_ID = "pmang app id";
    private static final String APP_KEY = "pmang app key";
    private static final String APP_SECRET = "pmang app secret";
    private static final String LOCAL_CD = "KOR";
    private static final String DEVICE_CD = "ANDROID";
    //private static final String DEVICE_CD = "IPHONE";

    // user identifier
    private static final String ID = "pmang id";
    private static final String PW = "pmang pw";

    // payment identifier
    private static final String DEVELOPER_PAYLOAD = "payment developer payload";
    private static final String PURCHASE_TOKEN = "payment purchase token";

    // QA/TQ/Live URL
    private static final String URL = "http://qa.pmangplus.com";
    //private static final String URL = "http://tq.pmangplus.com";
    //private static final String URL = "http://pmangplus.com";

    private HttpClient client = HttpClientBuilder.create().build();

    private String access_token;
    private String member_id;
    private String nickname;

    @Before
    public void setUp() throws Exception {
        // Arrange
        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
            new BasicNameValuePair("local_cd", LOCAL_CD),
            new BasicNameValuePair("app_id", APP_ID),
            new BasicNameValuePair("app_key", APP_KEY),
            new BasicNameValuePair("app_secret", APP_SECRET),
            new BasicNameValuePair("udid", "TestUDID_Java"),
            new BasicNameValuePair("email", ID),
            new BasicNameValuePair("passwd", PW),
            new BasicNameValuePair("device_cd", DEVICE_CD)
        ));

        // Act
        LoginResult result = post("/accounts/2/login", params, LoginResult.class);

        // Assert
        Assert.assertNotNull(result.value.access_token);
        Assert.assertNotNull(result.value.member.member_id);
        Assert.assertNotNull(result.value.member.nickname);

        access_token = result.value.access_token;
        member_id = result.value.member.member_id;
        nickname = result.value.member.nickname;
    }

    @Test
    public void testMembersSelf() throws Exception {
        // Arrange
        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
            new BasicNameValuePair("local_cd", LOCAL_CD),
            new BasicNameValuePair("access_token", access_token)
        ));

        // Act
        MembersSelfResult result = post("/members/@self", params, MembersSelfResult.class);

        // Assert
        Assert.assertEquals(member_id, result.value.member_id);
        Assert.assertEquals(nickname, result.value.nickname);
        Assert.assertNotNull(result.value.crt_dt);
        Assert.assertNotNull(result.value.upd_dt);
        Assert.assertNotNull(result.value.status_cd);
        Assert.assertNotNull(result.value.profile_img_url);
        //Assert.assertNotNull(result.value.profile_img_url_raw);
        //Assert.assertNotNull(result.value.feeling);
        Assert.assertNotNull(result.value.adult_auth_yn);
        //Assert.assertNotNull(result.value.adult_auth_dt);
        Assert.assertNotNull(result.value.recent_app_id);
        //Assert.assertNotNull(result.value.recent_app_title);
        Assert.assertNotNull(result.value.recent_login_dt);
        Assert.assertNotNull(result.value.email);
        //Assert.assertNotNull(result.value.anonymouns_yn);
        //Assert.assertNotNull(result.value.reg_path);
        //Assert.assertNotNull(result.value.last_msg_dt);
        //Assert.assertNotNull(result.value.new_msg_dt);
        Assert.assertNotNull(result.value.friend_accept_cd);
        //Assert.assertNotNull(result.value.achieve_detail_info_list);
        //Assert.assertNotNull(result.value.member_achievement_summary);
        //Assert.assertNotNull(result.value.summray);
        //Assert.assertNotNull(result.value.options);
        //Assert.assertNotNull(result.value.contact);
        Assert.assertNotNull(result.value.contact_friend_mapping);
        Assert.assertNotNull(result.value.contact_require_import);
        Assert.assertNotNull(result.value.contact_require_phone_auth);
        Assert.assertNotNull(result.value.contact_require_phone_auth_confirm);
        Assert.assertNotNull(result.value.pmang_usn);
        //Assert.assertNotNull(result.value.ci);
        Assert.assertNotNull(result.value.sanction);
    }

    @Test
    public void testMarketEffectuate() throws Exception {
        // Arrange
        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
            new BasicNameValuePair("local_cd", LOCAL_CD),
            new BasicNameValuePair("developerPayload", DEVELOPER_PAYLOAD),
            new BasicNameValuePair("purchaseToken", PURCHASE_TOKEN)
        ));

        // Act
        MarketEffectuateResult result = post("/market/v3/effectuate", params, MarketEffectuateResult.class);

        // Assert
        // if success
        //Assert.assertEquals(result.value.result, "SUCCESS");
        //Assert.assertNotNull(result.value.store_product_id);
        //Assert.assertNotNull(result.value.pay_id);

        // if not success (FAILURE / DUPLICATED / NOTEXISTS)
        Assert.assertNotNull(result.value.result);
    }

    @Test
    public void testGcmPublish() throws Exception {
        // Arrange
        String member_ids = member_id.toString() + "|" +
                            member_id.toString() + "|" +
                            member_id.toString();

        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("member_ids", member_ids),
                new BasicNameValuePair("message", "")
        ));

        // Act
        PushResult result = post("/gcm/" + APP_ID + "/publish", params, PushResult.class);

        // Assert
        Assert.assertNotNull(result.value);
    }

    @Test
    public void testGcmGetAllow() throws Exception {
        // Arrange
        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("access_token", access_token)
        ));

        // Act
        PushResult result = post("/gcm/" + APP_ID + "/getAllow", params, PushResult.class);

        // Assert
        Assert.assertNotNull(result.value);
    }

    @Test
    public void testGcmSetAllow() throws Exception {
        Boolean push_allow = false;
        PushResult result;

        // get allow
        List<NameValuePair> getAllowParams = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("access_token", access_token)
        ));

        result = post("/gcm/" + APP_ID + "/getAllow", getAllowParams, PushResult.class);

        Assert.assertNotNull(result.value);
        push_allow = Boolean.valueOf(result.value);

        // set allow
        List<NameValuePair> setAllowParams = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("access_token", access_token),
                new BasicNameValuePair("allow", String.valueOf(!push_allow))
        ));

        result = post("/gcm/" + APP_ID + "/setAllow", setAllowParams, PushResult.class);

        Assert.assertNotNull(result.value);
        Assert.assertEquals(push_allow, Boolean.valueOf(result.value));

        // get allow
        result = post("/gcm/" + APP_ID + "/getAllow", getAllowParams, PushResult.class);

        Assert.assertNotNull(result.value);
        Assert.assertEquals(push_allow, Boolean.valueOf(result.value));
    }

    @Test
    public void testApnsPublish() throws Exception {
        // Arrange
        String member_ids = member_id.toString() + "|" +
                member_id.toString() + "|" +
                member_id.toString();

        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("member_ids", member_ids),
                new BasicNameValuePair("message", "")
        ));

        // Act
        PushResult result = post("/apns/" + APP_ID + "/publish", params, PushResult.class);

        // Assert
        Assert.assertNotNull(result.value);
    }

    @Test
    public void testApnsGetAllow() throws Exception {
        // Arrange
        List<NameValuePair> params = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("access_token", access_token)
        ));

        // Act
        PushResult result = post("/apns/" + APP_ID + "/getAllow", params, PushResult.class);

        // Assert
        Assert.assertNotNull(result.value);
    }

    @Test
    public void testApnsSetAllow() throws Exception {
        Boolean push_allow = false;
        PushResult result;

        // get allow
        List<NameValuePair> getAllowParams = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("access_token", access_token)
        ));

        result = post("/apns/" + APP_ID + "/getAllow", getAllowParams, PushResult.class);

        Assert.assertNotNull(result.value);
        push_allow = Boolean.valueOf(result.value);

        // set allow
        List<NameValuePair> setAllowParams = new ArrayList<NameValuePair>(Arrays.asList(
                new BasicNameValuePair("local_cd", LOCAL_CD),
                new BasicNameValuePair("access_token", access_token),
                new BasicNameValuePair("allow", String.valueOf(!push_allow))
        ));

        result = post("/apns/" + APP_ID + "/setAllow", setAllowParams, PushResult.class);

        Assert.assertNotNull(result.value);
        Assert.assertEquals(push_allow, Boolean.valueOf(result.value));

        // get allow
        result = post("/apns/" + APP_ID + "/getAllow", getAllowParams, PushResult.class);

        Assert.assertNotNull(result.value);
        Assert.assertEquals(push_allow, Boolean.valueOf(result.value));
    }

    private <T extends ResultBase> T post(String prefix, List<NameValuePair> params, Class<T> type) throws Exception {
        String url = URL + prefix;
        HttpPost request = new HttpPost(url);

        for (Map.Entry<String, String> header : makeHeader().entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }

        request.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(request);

        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent())
        );

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        Gson gson = new GsonBuilder().create();
        ResultBase res = gson.fromJson(result.toString(), type);
        Assert.assertEquals(res.result_code, "000");
        Assert.assertEquals(res.result_msg, "API_OK");
        return (T)res;
    }

    private void get(String prefix, List<NameValuePair> params) {

    }

    private HashMap<String, String> makeHeader() throws NoSuchAlgorithmException {
        String now = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("ver", "4");
        headers.put("ts", now);
        headers.put("fp", sha1(now + APP_SECRET));
        headers.put("User-Agent", "PmangPlus for Server");
        return headers;
    }

    private String sha1(String input) throws NoSuchAlgorithmException {
        // Reference : http://www.sha1-online.com/sha1-java/
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}