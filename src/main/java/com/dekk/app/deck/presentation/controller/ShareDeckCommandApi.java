package com.dekk.app.deck.presentation.controller;

import com.dekk.app.deck.domain.exception.DeckErrorCode;
import com.dekk.app.deck.presentation.request.SharedDeckJoinRequest;
import com.dekk.app.deck.presentation.response.ShareTokenResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.security.oauth2.CustomUserDetails;
import com.dekk.global.swagger.ApiErrorExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "쉐어덱 상태 및 참여 API", description = "쉐어덱 상태(ON/OFF) 관리 및 초대 링크 토큰 발급/참여 API")
public interface ShareDeckCommandApi {

    @Operation(
            summary = "쉐어덱 공유 켜기 및 링크 발급 (호스트 전용)",
            description =
                    "보관함 상태를 SHARED로 변경하고 24시간 유효한 초대 토큰을 발급합니다. 이미 켜져 있고 남은 시간이 10분 초과일 경우 기존 토큰을 반환(멱등성)하며, 10분 이하일 경우 새 토큰을 발급하고 구 토큰은 10분 뒤 자연 소멸되도록 오버랩합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SD20013)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<ShareTokenResponse>> turnOnShare(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "공유를 켤 커스텀덱 ID", in = ParameterIn.PATH) Long customDeckId);

    @Operation(
            summary = "쉐어덱 공유 끄기 (호스트 전용)",
            description = "덱 상태를 CUSTOM으로 되돌리고, 발급된 토큰을 파기하며, 참여 중인 GUEST들을 모두 내보내기(Soft Delete) 처리합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SD20014)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<Void>> turnOffShare(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "공유를 끌 쉐어덱 ID", in = ParameterIn.PATH) Long customDeckId);

    @Operation(
            summary = "초대 링크로 쉐어덱 참여 (게스트 전용)",
            description = "초대 링크의 토큰을 사용하여 쉐어덱에 GUEST 권한으로 참여합니다. 덱 총량(9개) 제한의 영향을 받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SD20015)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<Void>> joinSharedDeck(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @RequestBody(description = "참여용 토큰 정보") SharedDeckJoinRequest request);

    @Operation(
            summary = "쉐어덱 자진 퇴장 (게스트 전용)",
            description =
                    "GUEST 권한을 가진 사용자가 쉐어덱에서 자진 퇴장(Soft Delete)합니다. HOST는 이 API를 사용할 수 없으며 '공유 끄기' 또는 '덱 삭제'를 이용해야 합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공 (SD20016)")
    @ApiErrorExceptions({DeckErrorCode.class})
    ResponseEntity<ApiResponse<Void>> leaveSharedDeck(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "퇴장할 쉐어덱 ID", in = ParameterIn.PATH) Long sharedDeckId);
}
