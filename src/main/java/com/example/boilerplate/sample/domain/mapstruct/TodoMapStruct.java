package com.example.boilerplate.sample.domain.mapstruct;

import com.example.boilerplate.sample.domain.entity.Todo;
import com.example.boilerplate.sample.domain.entity.TodoDynamic;
import com.example.boilerplate.sample.dto.TodoDto;
import com.example.boilerplate.sample.dto.TodoDto.Response;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TodoMapStruct {

  @Mapping(source = "memberId", target = "member.id")
  Todo toEntity(TodoDto.Request todoRequest);

  TodoDto.Response toDto(Todo todo);

  List<Response> todosToTodoResponses(List<Todo> todos);
}
