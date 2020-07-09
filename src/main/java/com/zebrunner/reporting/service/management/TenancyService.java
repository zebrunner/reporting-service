package com.zebrunner.reporting.service.management;

import com.zebrunner.reporting.persistence.dao.mysql.management.TenancyMapper;
import com.zebrunner.reporting.persistence.utils.TenancyContext;
import com.zebrunner.reporting.domain.db.Tenancy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
public class TenancyService {

    @Autowired
    private TenancyMapper tenancyMapper;

    @Getter
    @Value("#{new Boolean('${service.multitenant}')}")
    private Boolean isMultitenant;

    @Getter
    @Value("${service.use-artifact-proxy:false}")
    private boolean useArtifactsProxy;

    @Transactional(readOnly = true)
    public List<Tenancy> getAllTenancies() {
        return tenancyMapper.getAllTenancies();
    }

    @Transactional(readOnly = true)
    public Tenancy getTenancyByName(String name) {
        return tenancyMapper.getTenancyByName(name);
    }

    public void iterateItems(Runnable runnable) {
        if (isMultitenant) {
            iterateItems(tenancy -> runnable.run());
        } else {
            runnable.run();
        }
    }

    private void iterateItems(Consumer<Tenancy> tenancyConsumer) {
        getAllTenancies().forEach(tenancy -> {
            TenancyContext.setTenantName(tenancy.getName());
            tenancyConsumer.accept(tenancy);
            TenancyContext.setTenantName(null);
        });
    }
}
