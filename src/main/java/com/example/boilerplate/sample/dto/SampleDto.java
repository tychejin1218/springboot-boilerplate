package com.example.boilerplate.sample.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

public class SampleDto {

  @Alias("MemberRequest")
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class MemberRequest {

    private Long id;
    private String name;
    private String email;
    private String password;

    public static MemberRequest of(Long id) {
      return MemberRequest.builder()
          .id(id)
          .build();
    }

    public static MemberRequest of(InsertMemberRequest insertMemberRequest) {
      return MemberRequest.builder()
          .name(insertMemberRequest.getName())
          .email(insertMemberRequest.getEmail())
          .password(insertMemberRequest.getPassword())
          .build();
    }
  }

  @Alias("MemberResponse")
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class MemberResponse {

    private Long id;
    private String name;
    private String email;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class InsertMemberRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class UpdateMemberRequest {

    @Min(1)
    @NotNull
    private Long id;

    private String name;
    private String email;
    private String password;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class DeleteMemberRequest {

    @Min(1)
    @NotNull
    private Long id;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  public static class DeleteMemberResponse {

    private long deletedCount;

    public static DeleteMemberResponse of(Long deletedCount) {
      return DeleteMemberResponse.builder()
          .deletedCount(deletedCount)
          .build();
    }
  }
}
