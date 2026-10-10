package com.kalitron.studio.web.rest.custom;

import com.kalitron.studio.security.AuthoritiesConstants;
import com.kalitron.studio.security.SecurityUtils;
import com.kalitron.studio.service.MobileSessionService;
import com.kalitron.studio.service.dto.mobile.MobileSessionDTO;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * Sessions for the mobile app (E12 #116). URL access is limited to ROLE_ADMIN and
 * ROLE_MEASURER in {@code SecurityConfiguration}; a measurer only sees assigned sessions.
 */
@RestController
@RequestMapping("/api/mobile/sessions")
public class MobileSessionResource {

    private final MobileSessionService mobileSessionService;

    public MobileSessionResource(MobileSessionService mobileSessionService) {
        this.mobileSessionService = mobileSessionService;
    }

    @GetMapping("")
    public ResponseEntity<List<MobileSessionDTO>> getSessions(
        @RequestParam(value = "measurer", required = false) String measurer,
        @PageableDefault(size = 50) @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return ResponseEntity.ok(mobileSessionService.findAssignedToCurrentUser());
        }
        Page<MobileSessionDTO> page = mobileSessionService.findAllOpen(measurer, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public MobileSessionDTO getSession(@PathVariable("id") Long id) {
        try {
            return mobileSessionService.findOne(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
