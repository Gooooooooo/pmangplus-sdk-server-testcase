package com.pmangplus.test.result;

public class LoginResult extends ResultBase {

    public class Value {
        public String access_token;
        public Member member;
    }

    public class Member {
        public String member_id;
        public String nickname;
    }

    public Value value;
}
