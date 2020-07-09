package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.TagIntegrationData;
import com.zebrunner.reporting.domain.dto.tag.IntegrationTag;
import com.zebrunner.reporting.domain.dto.tag.TagIntegrationDataDTO;
import com.zebrunner.reporting.service.TagService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("Tags operations")
@RequestMapping(path = "api/tags", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class TagController extends AbstractController {

    private final TagService tagService;
    private final Mapper mapper;

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves integration information", nickname = "getTestIntegrationInfo", httpMethod = "GET", response = TagIntegrationDataDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{ciRunId}/integration")
    public TagIntegrationDataDTO exportTagIntegrationData(@PathVariable("ciRunId") String ciRunId,
                                                          @RequestParam("integrationTag") IntegrationTag tagName) {
        TagIntegrationData tagIntegrationData = tagService.exportTagIntegrationData(ciRunId, tagName);
        return mapper.map(tagIntegrationData, TagIntegrationDataDTO.class);
    }

}