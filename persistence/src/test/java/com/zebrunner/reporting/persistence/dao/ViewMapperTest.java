package com.zebrunner.reporting.persistence.dao;

import java.util.List;

import com.zebrunner.reporting.persistence.PersistenceTestConfig;
import com.zebrunner.reporting.persistence.dao.mysql.application.ViewMapper;
import com.zebrunner.reporting.persistence.utils.Sort;
import com.zebrunner.reporting.domain.db.Project;
import com.zebrunner.reporting.domain.db.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
@ContextConfiguration(classes = PersistenceTestConfig.class)
public class ViewMapperTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private ViewMapper viewMapper;

    /**
     * Turn this on to enable this test
     */
    private static final boolean ENABLED = false;

    private static final Project PROJECT = new Project() {
        private static final long serialVersionUID = 1L;
        {
            setName("p1");
            setDescription("d1");
        }
    };

    private static final View VIEW = new View() {
        private static final long serialVersionUID = 1L;
        {
            setName("n1");
            setProject(PROJECT);
        }
    };

    @Test(enabled = ENABLED)
    public void createViewTest() {
        viewMapper.createView(VIEW);
        Assert.assertNotNull(VIEW.getId(), "");
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createViewTest" })
    public void getViewByIdTest() {
        View view = viewMapper.getViewById(VIEW.getId());
        checkView(view);
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createViewTest", "getViewByIdTest" })
    public void getAllViewsTest() {
        List<View> viewList = viewMapper.getAllViews(null);
        Sort<View> viewSort = new Sort<>();
        viewList = viewSort.sortById(viewList);
        checkView(viewList.get(viewList.size() - 1));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createViewTest", "getViewByIdTest", "getAllViewsTest" })
    public void updateViewTest() {
        VIEW.setName("n2");
        viewMapper.updateView(VIEW);
        checkView(viewMapper.getViewById(VIEW.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = { "createViewTest", "getViewByIdTest", "getAllViewsTest", "updateViewTest" })
    public void deleteViewByIdTest() {
        viewMapper.deleteViewById(VIEW.getId());
        Assert.assertNull(viewMapper.getViewById(VIEW.getId()));
    }

    private void checkView(View view) {
        Assert.assertEquals(view.getId(), view.getId(), "");
        Assert.assertEquals(view.getName(), view.getName(), "");
    }
}