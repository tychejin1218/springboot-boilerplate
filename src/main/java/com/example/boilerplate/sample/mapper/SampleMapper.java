package com.example.boilerplate.sample.mapper;

import com.example.boilerplate.sample.dto.SampleDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SampleMapper {

  /**
   * 이름과 이메일을 조건으로 회원을 조회한 후 아이디를 조건으로 내림차순 정렬 후 회원을 조회
   *
   * @param memberRequest 회원 요청 정보
   * @return 회원 정보 응답 리스트
   */
  List<SampleDto.MemberResponse> selectMembers(SampleDto.MemberRequest memberRequest);

  /**
   * 회원 ID를 기준으로 회원 정보를 조회
   *
   * @param memberRequest 회원 요청 정보 (회원 ID 포함)
   * @return 회원 정보 응답
   */
  SampleDto.MemberResponse selectMember(SampleDto.MemberRequest memberRequest);

  /**
   * 회원을 저장하고, 자동 생성된 ID(PK)를 memberRequest 객체에 설정
   *
   * @param memberRequest 저장할 회원 정보
   */
  void insertMember(SampleDto.MemberRequest memberRequest);

  /**
   * 회원 정보를 수정
   *
   * @param memberRequest 수정할 회원 정보
   * @return 수정된 회원의 수
   */
  int updateMember(SampleDto.MemberRequest memberRequest);

  /**
   * 회원을 삭제
   *
   * @param memberId 삭제할 회원의 ID
   * @return 삭제된 회원의 수
   */
  int deleteMember(Long memberId);
}
