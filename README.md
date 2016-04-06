# pmangplus-sdk-server-testcase
> PmangPlus interface server side test code

### TestCase List
Function             |     REST API            |     Remarks
-------------------- | ----------------------- | ---------------
로그인 회원검증       | /members/@self          |
결제 영수증 검증      | /market/v3/effectuate   | 
GCM 발송             | /gcm/{appId}/publish    |
GCM 수신거부 조회     | /gcm/{appId}/getAllow   | 
GCM 수신거부 설정     | /gcm/{appId}/setAllow   |
APNS 발송            | /apns/{appId}/publish   |
APNS 수신거부 조회    | /apns/{appId}/getAllow  |
APNS 수신거부 설정    | /apns/{appId}/setAllow  |


### Node.js
* modify application identifier (test/test-pmangplus.js)
* npm install
* npm test

```javascript
// example code 
it('test members @self', function(done) {
  async.series([
    function (callback) {
      var options = post('/members/@self');
      options.form = {
          'local_cd': LOCAL_CD,
          'access_token': access_token
      };
      request(options, function (error, response, body) {
        var res = get_response(error, response, body);
        // validate response
        done();
      });
    }
  ], done);
});
```


### Python
* modify application identifier (test_pmangplus.py)
* virtualenv env
* source env/bin/activate
* pip install -r requirements.txt
* py.test -q test_pmangplus.py
* deactivate

``` python
# example code 
def test_self(self):
  def _params():
    return {
      'local_cd': LOCAL_CD,
      'access_token': self._access_token
    }
  params = _params()
  response = self._get('/members/@self', params)
  # validate response
  assert response
```


### PHP
* modify application identifier (PmangPlusTest.php)
* install phpunit (https://phpunit.de/getting-started.html)
  * wget https://phar.phpunit.de/phpunit.phar
  * chmod +x phpunit.phar
  * sudo mv phpunit.phar /usr/local/bin/phpunit
  * phpunit --version
* install composer (https://www.digitalocean.com/community/tutorials/how-to-install-and-use-composer-on-ubuntu-14-04)
  * sudo apt-get install curl php5-cli git
  * curl -sS https://getcomposer.org/installer | sudo php -- --install-dir=/usr/local/bin --filename=composer
* composer install
* phpunit UnitTest PmangPlusTest.php

``` php
// example code
public function testMembersSelf()
{
  // Arrange
  // Act
  $response = $this->post('/members/@self', [
    'local_cd' => $this->LOCAL_CD,
    'access_token' => $this->access_token,
  ]);
  // Assert
  $value = $response->value;
}
```


### C#
* modify application identifier (ASP.NET.Tests/PmangPlusTests.cs)
* Test > Run > All Tests

``` c#
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
    // Assert response
}
```


### Java
* modify application identifier (src/test/com/pmangplus/test/PmangPlusTest.java)
* mvn install
* mvn test

``` java
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
}
```


### License 
MIT