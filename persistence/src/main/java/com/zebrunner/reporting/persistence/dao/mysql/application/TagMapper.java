package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.Tag;
import com.zebrunner.reporting.domain.db.TestInfo;
import com.zebrunner.reporting.domain.dto.tag.IntegrationTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface TagMapper {

    /**
     * Creates tag if it is not exist
     * 
     * @param tag - to create
     */
    void createTag(Tag tag);

    /**
     * Creates tags if it is not exist
     * 
     * @param tags - to create
     */
    void createTags(@Param(value = "tags") Set<Tag> tags);

    Tag getTagById(Long id);

    Tag getTagByNameAndTestId(@Param(value = "name") String name, @Param(value = "testId") Long testId);

    List<TestInfo> getTestInfoByTagNameAndTestRunCiRunId(@Param(value = "tagName") IntegrationTag tagName,
                                                         @Param(value = "testRunCiRunId") String testRunCiRunId);

    Tag getTagByNameAndValue(@Param(value = "name") String name, @Param(value = "value") String value);

    Set<Tag> getAllTags();

    Set<Tag> getTagsByTestId(Long testId);

    Boolean isExists(String name);

    void updateTag(Tag tag);

    void deleteTagById(Long id);
}
