package com.dekk.app.card.presentation.controller;

import com.dekk.app.card.domain.exception.CardErrorCode;
import com.dekk.app.card.presentation.request.AssignCategoriesRequest;
import com.dekk.app.card.presentation.request.RequestDeleteCardRequest;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.annotation.LoginAdmin;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "관리자 카드 관리 API", description = "관리자가 카드를 승인/반려하고 카테고리를 지정하는 API")
public interface CardCommandApi {

    @Operation(summary = "카드 승인", description = "PENDING 또는 REJECTED 상태의 카드를 승인합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "카드 승인 성공",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        examples = @ExampleObject(value = """
                                    {
                                      "code": "SC200003",
                                      "message": "카드 승인 성공"
                                    }"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "삭제 요청된 카드 상태 변경 불가",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        examples = @ExampleObject(value = """
                                    {
                                      "code": "EC40009",
                                      "message": "삭제 요청된 카드의 상태는 변경할 수 없습니다"
                                    }"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "카드를 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        examples = @ExampleObject(value = """
                                    {
                                      "code": "EC40401",
                                      "message": "카드를 찾을 수 없습니다"
                                    }""")))
            })
    ResponseEntity<ApiResponse<Void>> approveCard(
            @Parameter(description = "승인할 카드 ID", in = ParameterIn.PATH) Long cardId);

    @Operation(summary = "카드 반려", description = "PENDING 또는 APPROVED 상태의 카드를 반려합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "카드 반려 성공",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        examples = @ExampleObject(value = """
                                    {
                                      "code": "SC200004",
                                      "message": "카드 반려 성공"
                                    }"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "삭제 요청된 카드 상태 변경 불가",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        examples = @ExampleObject(value = """
                                    {
                                      "code": "EC40009",
                                      "message": "삭제 요청된 카드의 상태는 변경할 수 없습니다"
                                    }"""))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "카드를 찾을 수 없음",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        examples = @ExampleObject(value = """
                                    {
                                      "code": "EC40401",
                                      "message": "카드를 찾을 수 없습니다"
                                    }""")))
            })
    ResponseEntity<ApiResponse<Void>> rejectCard(
            @Parameter(description = "반려할 카드 ID", in = ParameterIn.PATH) Long cardId);

    @Operation(
            summary = "카드 카테고리 지정",
            description =
                    "카드에 연결된 카테고리를 지정합니다. 요청한 카테고리 목록으로 교체됩니다. (기존에 있었으나 요청에서 보내지 않는 경우 삭제, 기존에 없었으나 요청에 포함된 경우 추가 됩니다.)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카드 카테고리 지정 성공")
    @ApiErrorExceptions(CardErrorCode.class)
    ResponseEntity<ApiResponse<Void>> assignCategories(
            @Parameter(description = "카테고리를 지정할 카드 ID", in = ParameterIn.PATH) Long cardId,
            AssignCategoriesRequest request);

    @Operation(summary = "카드 삭제 요청", description = "카드의 상태를 DELETE_REQUESTED로 변경하고 삭제 사유를 기록합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카드 삭제 요청 성공")
    @ApiErrorExceptions(CardErrorCode.class)
    ResponseEntity<ApiResponse<Void>> requestDeleteCard(
            @Parameter(description = "삭제 요청할 카드 ID", in = ParameterIn.PATH) Long cardId,
            RequestDeleteCardRequest request,
            @LoginAdmin Long adminId);
}
