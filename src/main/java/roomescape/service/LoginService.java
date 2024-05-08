package roomescape.service;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;
import roomescape.dao.MemberDao;
import roomescape.domain.Member;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.TokenResponse;
import roomescape.util.JwtTokenProvider;

@Service
public class LoginService {
    private final MemberDao memberDao;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginService(MemberDao memberDao, JwtTokenProvider jwtTokenProvider) {
        this.memberDao = memberDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Member findMember(LoginRequest loginRequest) {
        Member member = new Member(loginRequest.email(), loginRequest.password());
        return memberDao.find(member);
    }

    public TokenResponse createToken(Member member) {
        return new TokenResponse(jwtTokenProvider.createToken(member));
    }

    public Cookie generateCookie(TokenResponse tokenResponse) {
        Cookie cookie = new Cookie("token", tokenResponse.accessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
