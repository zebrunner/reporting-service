package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.ViewMapper;
import com.zebrunner.reporting.domain.db.View;
import com.zebrunner.reporting.service.project.ProjectReassignable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ViewService implements ProjectReassignable {

    private final ViewMapper viewMapper;

    public ViewService(ViewMapper viewMapper) {
        this.viewMapper = viewMapper;
    }

    @Transactional(readOnly = true)
    public List<View> getAllViews(Long projectId) {
        return viewMapper.getAllViews(projectId);
    }

    @Transactional(readOnly = true)
    public View getViewById(Long id) {
        return viewMapper.getViewById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public View createView(View view) {
        viewMapper.createView(view);
        return view;
    }

    @Transactional(rollbackFor = Exception.class)
    public View updateView(View view) {
        viewMapper.updateView(view);
        return view;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteViewById(long id) {
        viewMapper.deleteViewById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reassignProject(Long fromId, Long toId) {
        viewMapper.reassignToProject(fromId, toId);
    }
}