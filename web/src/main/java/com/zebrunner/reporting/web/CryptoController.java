package com.zebrunner.reporting.web;

import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.web.documented.CryptoDocumentedController;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequestMapping(path = "api/security", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class CryptoController extends AbstractController implements CryptoDocumentedController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/cryptokey")
    @Override
    public void regenerateCryptoKey() {
        cryptoService.regenerateKey();
    }

}
