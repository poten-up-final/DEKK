package com.dekk.app.admin.presentation.controller;

import com.dekk.app.admin.application.AdminInspectionCommandService;
import com.dekk.app.admin.application.AdminInspectionQueryService;
import com.dekk.app.admin.domain.model.InspectionStatus;
import com.dekk.app.admin.presentation.request.InspectionStatusUpdateRequest;
import com.dekk.app.admin.presentation.response.AdminResultCode;
import com.dekk.app.admin.presentation.response.ImageInspectionResponse;
import com.dekk.global.response.ApiResponse;
import com.dekk.global.response.PageResponse;
import com.dekk.global.security.annotation.LoginAdmin;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adm/v1/inspections")
@RequiredArgsConstructor
public class AdminInspectionController implements AdminInspectionApi {
    private final AdminInspectionCommandService commandService;
    private final AdminInspectionQueryService queryService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ImageInspectionResponse>>> getInspections(
            @RequestParam(defaultValue = "AI_PASSED") List<InspectionStatus> status, Pageable pageable) {
        PageResponse<ImageInspectionResponse> response = PageResponse.from(
                queryService.getInspectionsByStatuses(status, pageable).map(ImageInspectionResponse::from));
        return ResponseEntity.ok(ApiResponse.of(AdminResultCode.INSPECTION_LIST_QUERIED, response));
    }

    @Override
    @PatchMapping("/{inspectionId}/status")
    public ResponseEntity<ApiResponse<Void>> updateInspectionStatus(
            @PathVariable Long inspectionId,
            @Valid @RequestBody InspectionStatusUpdateRequest request,
            @LoginAdmin Long adminId) {

        commandService.updateInspectionStatus(request.toCommand(inspectionId, adminId));

        return ResponseEntity.ok(ApiResponse.from(AdminResultCode.INSPECTION_STATUS_UPDATED));
    }
}
