<?php

require('vendor/autoload.php');

class PmangPlusTest extends PHPUnit_Framework_TestCase
{
	// application identifier
	private $APP_ID = 'pmang app id';
	private $APP_KEY = 'pmang app key';
	private $APP_SECRET = 'pmang app secret';
	private $LOCAL_CD = 'KOR';
	private $DEVICE_CD = 'ANDROID';
	//private $DEVICE_CD = 'IPHONE';

	// user identifier
	private $ID = 'pmang id';
	private $PW = 'pmang pw';

	// payment identifier
	private $DEVELOPER_PAYLOAD = 'payment developer payload';
	private $PURCHASE_TOKEN = 'payment purchase token';

	// QA/TQ/Live URL
	private $URL = 'http://qa.pmangplus.com';
	//private $URL = 'http://tq.pmangplus.com';
	//private $URL = 'http://pmangplus.com';

	private $access_token = '';
	private $member_id = '';
	private $nickname = '';


	protected function setUp()
	{
		$response = $this->post('/accounts/2/login', [
			'app_id' => $this->APP_ID,
			'app_key' => $this->APP_KEY,
			'app_secret' => $this->APP_SECRET,
			'udid' => 'TestUDID_PHP',
			'email' => $this->ID,
			'passwd' => $this->PW,
			'local_cd' => $this->LOCAL_CD,
			'device_cd' => $this->DEVICE_CD
		]);

		$value = $response->value;
		$this->access_token = $value->access_token;

		$this->assertNotNull($value->member);

		$member = $value->member;
		$this->member_id = $member->member_id;
		$this->nickname = $member->nickname;
	}

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
		
		$this->assertEquals($value->member_id, $this->member_id);
		$this->assertEquals($value->nickname, $this->nickname);
		$this->assertTrue(property_exists($value, 'crt_dt'));
		$this->assertTrue(property_exists($value, 'upd_dt'));
		$this->assertTrue(property_exists($value, 'status_cd'));
		$this->assertTrue(property_exists($value, 'profile_img_url'));
		$this->assertTrue(property_exists($value, 'feeling'));
		$this->assertTrue(property_exists($value, 'adult_auth_yn'));
		$this->assertTrue(property_exists($value, 'adult_auth_dt'));
		$this->assertTrue(property_exists($value, 'recent_login_dt'));
		$this->assertTrue(property_exists($value, 'recent_app_id'));
		$this->assertTrue(property_exists($value, 'email'));
		$this->assertTrue(property_exists($value, 'anonymous_yn'));
		$this->assertTrue(property_exists($value, 'reg_path'));
		$this->assertTrue(property_exists($value, 'recent_app_title'));
		$this->assertTrue(property_exists($value, 'last_msg_dt'));
		$this->assertTrue(property_exists($value, 'new_msg_yn'));
		$this->assertTrue(property_exists($value, 'friend_accept_cd'));
		$this->assertTrue(property_exists($value, 'achieve_detail_info_list'));
		$this->assertTrue(property_exists($value, 'member_achievement_summary'));
		$this->assertTrue(property_exists($value, 'summary'));
		$this->assertTrue(property_exists($value, 'options'));
		$this->assertTrue(property_exists($value, 'contact_require_phone_auth'));
		$this->assertTrue(property_exists($value, 'contact_require_import'));
		$this->assertTrue(property_exists($value, 'contact_friend_mapping'));
		$this->assertTrue(property_exists($value, 'contact_require_phone_auth_confirm'));
		$this->assertTrue(property_exists($value, 'contact'));
		$this->assertTrue(property_exists($value, 'pmang_usn'));
		$this->assertTrue(property_exists($value, 'ci'));
		$this->assertTrue(property_exists($value, 'sanction'));
		$this->assertTrue(property_exists($value, 'profile_img_url_raw'));
	}
	
	public function testMarketEffectuate()
	{
		// Arrange
		// Act
		$response = $this->post('/market/v3/effectuate', [
			'local_cd' => $this->LOCAL_CD,
			'developerPayload' => $this->DEVELOPER_PAYLOAD,
			'purchaseToken' => $this->PURCHASE_TOKEN
		]);

		// Assert
		$value = $response->value;

		// if success
		//$this->assertEquals($value->result, 'SUCCESS');
		//$this->assertTrue(property_exists($value, 'store_product_id'));
		//$this->assertTrue(property_exists($value, 'pay_id'));
		
		// if not success(FAILURE / DUPLICATED / NOTEXISTS)
		$this->assertTrue(property_exists($value, 'result'));
	}

	public function testGcmPublish()
	{
		// Arrange
		$member_ids = array($this->member_id, $this->member_id, $this->member_id);

		// Act
		$response = $this->post('/gcm/' . $this->APP_ID . '/publish', [
			'local_cd' => $this->LOCAL_CD,
			'member_ids' => join("|", $member_ids),
			'message' => '{"content":"내용", "title":"제목", "type":"ad", "url":"http://ppl.cm/abcdef"}'
		]);

		// Assert
		$this->assertNotNull($response->{'value'});
	}

	public function testGcmGetAllow()
	{
		// Arrange
		// Act
		$response = $this->post('/gcm/' . $this->APP_ID . '/getAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token
		]);

		// Assert
		$this->assertNotNull($response->{'value'});
	}

	public function testGcmSetAllow()
	{
		$push_allow = false;

		// get allow
		$response = $this->post('/gcm/' . $this->APP_ID . '/getAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token
		]);

		$this->assertNotNull($response->value);
		$push_allow = $response->value;

		// set allow
		$response = $this->post('/gcm/' . $this->APP_ID . '/setAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token,
			'allow' => !$push_allow
		]);

		$this->assertNotNull($response->value);

		// get allow
		$response = $this->post('/gcm/' . $this->APP_ID . '/getAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token
		]);

		$this->assertNotNull($response->value);
		$this->assertEquals($push_allow, $response->value);
	}

	public function testApnsPublish()
	{
		// Arrange
		$prefix = '/apns/' . $this->APP_ID . '/publish';
		$member_ids = array($this->member_id, $this->member_id, $this->member_id);

		// Act
		$response = $this->post($prefix, [
			'local_cd' => $this->LOCAL_CD,
			'member_ids' => join("|", $member_ids),
			'message' => '내용'
		]);

		// Assert
		$this->assertNotNull($response->{'value'});
	}

	public function testApnsGetAllow()
	{
		// Arrange
		// Act
		$response = $this->post("/apns/" . $this->APP_ID . "/getAllow", [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token
		]);

		// Assert
		$this->assertNotNull($response->{'value'});
	}

	public function testApnsSetAllow()
	{
		$push_allow = false;

		// get allow
		$response = $this->post('/apns/' . $this->APP_ID . '/getAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token
		]);

		$this->assertNotNull($response->value);
		$push_allow = $response->value;

		// set allow
		$response = $this->post('/apns/' . $this->APP_ID . '/setAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token,
			'allow' => !$push_allow
		]);

		$this->assertNotNull($response->value);

		// get allow
		$response = $this->post('/apns/' . $this->APP_ID . '/getAllow', [
			'local_cd' => $this->LOCAL_CD,
			'access_token' => $this->access_token
		]);

		$this->assertNotNull($response->value);
		$this->assertEquals($push_allow, $response->value);
	}

	private function post($prefix, $params)
	{
		$client = new GuzzleHttp\Client([
      'base_uri' => $this->URL
		]);

		$result = $client->post($prefix, [
			'headers' => $this->makeHeader(),
			'form_params' => $params
		]);

		$response = json_decode($result->getBody());
		$this->assertEquals($response->{'result_code'}, '000');
		$this->assertEquals($response->{'result_msg'}, 'API_OK');
		$this->assertNotNull($response->{'value'});

		return $response;
	}

	private function get($prefix, $params)
	{
		// TODO: get request
	}
	
	private function makeHeader()
	{
		$now = $this->currentMillis();
		$headers = array(
			'ver' => 4,
			'ts' => $now,
			'fp' => sha1($now . $this->APP_SECRET),
			'User-Agent' => 'PmangPlus for Server'
		);

		return $headers;
	}

	private function currentMillis()
	{
		$mt = explode(' ', microtime());
		return $mt[1] * 1000 + round($mt[0] * 1000);
	}
}

?>
