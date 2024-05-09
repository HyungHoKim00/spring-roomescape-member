package roomescape.dto.response;

import roomescape.domain.Member;

public record MemberResponse(Long id, String name, String email) {
    public static MemberResponse fromMember(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getNameValue(),
                member.getEmail());
    }
}