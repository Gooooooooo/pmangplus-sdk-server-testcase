using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Security.Cryptography;
using System.IO;
using System.Text;
using System.Net;
using Facebook.MiniJSON;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace ASP.NET.Tests
{    
    [TestClass]
    public class PmangPlusTests
    {
        // application identifier
        static string APP_ID = "pmang app id";
        static string APP_KEY = "pmang app key";
        static string APP_SECRET = "pmang app secret";
        static string LOCAL_CD = "KOR";
        static string DEVICE_CD = "ANDROID";
        //static string DEVICE_CD = "IPHONE";

        // user identifier
        static string ID = "pmang id";
        static string PW = "pmang pw";
        static string UDID = "UnityUDID_" + APP_ID;

        // payment identifier
        static string DEVELOPER_PAYLOAD = "payment developer payload";
        static string PURCHASE_TOKEN = "payment purchase token";

        // QA/TQ/Live URL
        static string URL = "http://qa.pmangplus.com";
        //static string URL = "http://tq.pmangplus.com";
        //static string URL = "http://pmangplus.com";

        string accessToken;
        long memberId;
        string nickname;

        [TestInitialize]
        public void SetUp()
        {
            // Arrange
            // Act
            var response = post("/accounts/2/login", new Dictionary<string, string>
            {
                { "app_id", APP_ID },
                { "app_key", APP_KEY },
                { "app_secret", APP_SECRET },
                { "udid", UDID },
                { "email", ID },
                { "passwd", PW },
                { "local_cd", LOCAL_CD },
                { "device_cd", DEVICE_CD }
            });

            // Assert
            Assert.IsNotNull(response["value"]);

            var value = response["value"] as Dictionary<string, object>;
            Assert.IsNotNull(value["access_token"]);

            var member = value["member"] as Dictionary<string, object>;
            Assert.IsNotNull(member["member_id"]);
            Assert.IsNotNull(member["nickname"]);

            accessToken = value["access_token"] as string;
            memberId = (long)member["member_id"];
            nickname = member["nickname"] as string;
        }
        
        [TestMethod]
        public void TestMembersSelf()
        {
            // Arrange            
            // Act
            var response = post("/members/@self", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            // Assert
            Assert.IsNotNull(response["value"]);
            var value = response["value"] as Dictionary<string, object>;

            Assert.AreEqual(memberId, value["member_id"]);
            Assert.AreEqual(nickname, value["nickname"]);
            Assert.IsTrue(value.ContainsKey("crt_dt"));
            Assert.IsTrue(value.ContainsKey("upd_dt"));
            Assert.IsTrue(value.ContainsKey("status_cd"));
            Assert.IsTrue(value.ContainsKey("profile_img_url"));
            Assert.IsTrue(value.ContainsKey("feeling"));
            Assert.IsTrue(value.ContainsKey("adult_auth_yn"));
            Assert.IsTrue(value.ContainsKey("adult_auth_dt"));
            Assert.IsTrue(value.ContainsKey("recent_login_dt"));
            Assert.IsTrue(value.ContainsKey("recent_app_id"));
            Assert.IsTrue(value.ContainsKey("email"));
            Assert.IsTrue(value.ContainsKey("anonymous_yn"));
            Assert.IsTrue(value.ContainsKey("reg_path"));
            Assert.IsTrue(value.ContainsKey("recent_app_title"));
            Assert.IsTrue(value.ContainsKey("last_msg_dt"));
            Assert.IsTrue(value.ContainsKey("new_msg_yn"));
            Assert.IsTrue(value.ContainsKey("friend_accept_cd"));
            Assert.IsTrue(value.ContainsKey("achieve_detail_info_list"));
            Assert.IsTrue(value.ContainsKey("member_achievement_summary"));
            Assert.IsTrue(value.ContainsKey("summary"));
            Assert.IsTrue(value.ContainsKey("options"));
            Assert.IsTrue(value.ContainsKey("contact_require_phone_auth"));
            Assert.IsTrue(value.ContainsKey("contact_require_import"));
            Assert.IsTrue(value.ContainsKey("contact_friend_mapping"));
            Assert.IsTrue(value.ContainsKey("contact_require_phone_auth_confirm"));
            Assert.IsTrue(value.ContainsKey("contact"));
            Assert.IsTrue(value.ContainsKey("pmang_usn"));
            Assert.IsTrue(value.ContainsKey("ci"));
            Assert.IsTrue(value.ContainsKey("sanction"));
            Assert.IsTrue(value.ContainsKey("profile_img_url_raw"));
        }

        [TestMethod]
        public void TestMarketEffectuate()
        {
            // Arrange
            // Act
            var response = post("/market/v3/effectuate", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "developerPayload", DEVELOPER_PAYLOAD },
                { "purchaseToken", PURCHASE_TOKEN }
            });

            // Assert
            Assert.IsNotNull(response["value"]);
            var value = response["value"] as Dictionary<string, object>;

            // if Success
            //Assert.Equals(value["result"], "SUCCESS");
            //Assert.IsTrue(value.ContainsKey("store_product_id"));
            //Assert.IsTrue(value.ContainsKey("pay_id"));

            // if not success(FAILURE / DUPLICATED / NOTEXISTS)
            Assert.IsTrue(value.ContainsKey("result"));
        }

        [TestMethod]
        public void TestGcmPublish()
        {
            // Arrange
            var memberIds = new string[] { memberId.ToString(), memberId.ToString(), memberId.ToString() };

            // Act
            var response = post("/gcm/" + APP_ID + "/publish", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "member_ids", String.Join("|", memberIds) },
                { "message", "{\"content\":\"내용\", \"title\":\"제목\", \"type\":\"ad\", \"url\":\"http://ppl.cm/abcdef\"}" }
            });

            // Assert
            Assert.IsTrue(response.ContainsKey("value"));
        }

        [TestMethod]
        public void TestGcmGetAllow()
        {
            // Arrange
            // Act
            var response = get("/gcm/" + APP_ID + "/getAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            // Assert
            Assert.IsTrue(response.ContainsKey("value"));
        }

        [TestMethod]
        public void TestGcmSetAllow()
        {
            bool pushAllow = false;
            IDictionary<string, object> response;

            // get allow
            response = post("/gcm/" + APP_ID + "/getAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            Assert.IsTrue(response.ContainsKey("value"));
            pushAllow = (bool)response["value"];

            // set allow
            response = post("/gcm/" + APP_ID + "/setAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken },
                { "allow", (!pushAllow).ToString() }
            });

            Assert.IsTrue(response.ContainsKey("value"));
            var setAllow = (bool)response["value"];
            Assert.AreEqual(pushAllow, setAllow);

            // get allow
            response = post("/gcm/" + APP_ID + "/getAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            Assert.IsTrue(response.ContainsKey("value"));
            setAllow = (bool)response["value"];
            Assert.AreEqual(pushAllow, setAllow);
        }

        [TestMethod]
        public void TestApnsPublish()
        {
            // Arrange
            var memberIds = new string[] { memberId.ToString(), memberId.ToString(), memberId.ToString() };

            // Act
            var response = post("/apns/" + APP_ID + "/publish", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "member_ids", String.Join("|", memberIds) },
                { "message", "내용" }
            });

            // Assert
            Assert.IsTrue(response.ContainsKey("value"));
        }

        [TestMethod]
        public void TestApnsGetAllow()
        {
            // Arrange
            // Act
            var response = get("/apns/" + APP_ID + "/getAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            // Assert
            Assert.IsTrue(response.ContainsKey("value"));
        }

        [TestMethod]
        public void TestApnsSetAllow()
        {
            bool pushAllow = false;
            IDictionary<string, object> response;

            // get allow
            response = post("/apns/" + APP_ID + "/getAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            Assert.IsTrue(response.ContainsKey("value"));
            pushAllow = (bool)response["value"];

            // set allow
            response = post("/apns/" + APP_ID + "/setAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken },
                { "allow", (!pushAllow).ToString() }
            });

            Assert.IsTrue(response.ContainsKey("value"));
            var setAllow = (bool)response["value"];
            Assert.AreEqual(pushAllow, setAllow);

            // get allow
            response = post("/apns/" + APP_ID + "/getAllow", new Dictionary<string, string>
            {
                { "local_cd", LOCAL_CD },
                { "access_token", accessToken }
            });

            Assert.IsTrue(response.ContainsKey("value"));
            setAllow = (bool)response["value"];
            Assert.AreEqual(pushAllow, setAllow);
        }

        private IDictionary<string, object> post(string prefix, IDictionary<string, string> @params)   
        {
            using (var client = new WebClient())
            {
                var url = URL + prefix;

                foreach (var header in makeHeader())
                {
                    if (String.IsNullOrEmpty(client.Headers.Get(header.Key)))
                        client.Headers.Add(header.Key, header.Value);
                    else
                        client.Headers[header.Key] = header.Value;
                }

                var data = new NameValueCollection();
                foreach (var param in @params)
                    data.Add(param.Key, param.Value);

                var result = client.UploadValues(url, data);
                var response = Json.Deserialize(Encoding.UTF8.GetString(result)) as Dictionary<string, object>;
                Assert.AreEqual(response["result_code"], "000");
                Assert.AreEqual(response["result_msg"], "API_OK");
                return response;
            } 
        }

        private IDictionary<string, object> get(string prefix, IDictionary<string, string> @params)
        {
            using (var client = new WebClient())
            {
                foreach (var header in makeHeader())
                    client.Headers.Add(header.Key, header.Value);

                var url = URL + prefix;
                if (@params.Count > 0)
                {
                    var fields = new string[@params.Count];
                    var index = 0;
                    foreach (var param in @params)
                        fields[index++] = param.Key + "=" + param.Value;

                    url += "?" + String.Join("&", fields);
                }
                var result = client.DownloadString(url);
                var response = Json.Deserialize(result) as Dictionary<string, object>;
                Assert.AreEqual(response["result_code"], "000");
                Assert.AreEqual(response["result_msg"], "API_OK");
                return response;
            }
        }

        private IDictionary<string, string> makeHeader()
        {
            var mills = (long)((DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds);
            var digest = sha1(mills + APP_SECRET);

            var headers = new Dictionary<string, string>
                {
                    { "ver", "4" },
                    { "ts", mills.ToString() },
                    { "fp", digest },
                    { "User-Agent", "PmangPlus for Server" }
                };
            if (!String.IsNullOrEmpty(accessToken))
                headers.Add("access_token", accessToken);
            return headers;
        }

        private string sha1(string str)
        {
            var encoding = new UTF8Encoding();
            var bytes = encoding.GetBytes(str);
            var sha = new SHA1CryptoServiceProvider();
            var array = sha.ComputeHash(bytes);
            string text = "";
            for (var i = 0; i < array.Length; i++)
            {
                text += Convert.ToString(array[i], 16).PadLeft(2, '0');
            }
            return text.PadLeft(32, '0');
        }
    }
}
