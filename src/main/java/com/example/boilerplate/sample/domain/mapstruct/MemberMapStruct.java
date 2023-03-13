package com.example.boilerplate.sample.domain.mapstruct;

import com.example.boilerplate.sample.domain.entity.Member;
import com.example.boilerplate.sample.domain.entity.Todo;
import com.example.boilerplate.sample.dto.MemberDto;
import com.example.boilerplate.sample.dto.MemberDto.Response;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapStruct {

  @Mapping(target = "todos", ignore = true)
  Member toEntity(MemberDto.Request memberRequest);

  MemberDto.Response toDto(Member member);

  MemberDto.Todo toDto(Todo todo);

  List<Response> membersToMemberResponses(List<Member> members);
}
