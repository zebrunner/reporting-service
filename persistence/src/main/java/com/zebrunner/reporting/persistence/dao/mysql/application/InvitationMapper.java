package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchCriteria;
import com.zebrunner.reporting.domain.db.Invitation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InvitationMapper {

    void createInvitation(Invitation invitation);

    Invitation getInvitationByCode(String code);

    Invitation getInvitationByEmail(String email);

    List<Invitation> getAllInvitations();

    void updateInvitation(Invitation invitation);

    void deleteInvitationById(Long id);

    void deleteInvitationByEmail(String email);

    List<Invitation> search(@Param("sc") SearchCriteria sc);

    Integer searchCount(@Param("sc") SearchCriteria sc);

}
