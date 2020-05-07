package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.dto.EmailType;
import com.zebrunner.reporting.service.EmailService;
import com.zebrunner.reporting.service.UploadService;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.web.documented.FileUtilDocumentedController;
import com.zebrunner.reporting.domain.dto.aws.FileUploadType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@CrossOrigin
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FileUtilController extends AbstractController implements FileUtilDocumentedController {

    private static final String DATA_FOLDER = "/opt/apk/%s";
    private static final String[] ALLOWED_IMAGE_CONTENT_TYPES = {"image/png", "image/jpeg"};
    private static final String[] APP_EXTENSIONS = {"app", "ipa", "apk"};

    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;
    private static final long MAX_APP_PACKAGE_SIZE = 100 * 1024 * 1024;

    private final EmailService emailService;
    private final UploadService uploadService;
    private final ServletContext context;

    public FileUtilController(
            EmailService emailService,
            UploadService uploadService,
            ServletContext context
    ) {
        this.emailService = emailService;
        this.uploadService = uploadService;
        this.context = context;
    }

    @PostMapping("api/upload")
    @Override
    public String uploadFile(@RequestHeader("FileType") FileUploadType.Type type, @RequestParam("file") MultipartFile file) throws IOException {
        String resourceURL;
        if (FileUploadType.Type.COMMON.equals(type) || FileUploadType.Type.USERS.equals(type)) {
            // Performing size (less than 2 MB) and file type (JPG/PNG only) validation for images
            if (file.getSize() > MAX_IMAGE_SIZE || !ArrayUtils.contains(ALLOWED_IMAGE_CONTENT_TYPES, file.getContentType())) {
                throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INVALID_FILE, "File size should be less than 2MB and have format JPEG or PNG");
            }
            resourceURL = uploadService.uploadImage(type, file.getInputStream(), file.getOriginalFilename(), file.getSize());
        } else {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (ArrayUtils.contains(APP_EXTENSIONS, extension) && file.getSize() > MAX_APP_PACKAGE_SIZE) {
                throw new IllegalOperationException(IllegalOperationException.IllegalOperationErrorDetail.INVALID_FILE, "File size should be less than 100MB and have format APP, IPA or APK");
            }
            resourceURL = uploadService.uploadArtifact(type, file.getInputStream(), file.getOriginalFilename(), file.getSize());
        }
        return resourceURL;
    }

    @DeleteMapping("api/file")
    @Override
    public void deleteFile(@RequestHeader("FileType") FileUploadType.Type type, @RequestParam("key") String key) {
        if (FileUploadType.Type.COMMON.equals(type) || FileUploadType.Type.USERS.equals(type)) {
            uploadService.removeImage(key);
        } else {
            uploadService.removeArtifact(key);
        }
    }



    @PostMapping("api/upload/email")
    @Override
    public void sendImageByEmail(@RequestPart("file") MultipartFile file, @RequestPart("email") EmailType email) throws IOException {
        String fileExtension = String.format(".%s", FilenameUtils.getExtension(file.getOriginalFilename()));
        File attachmentFile = File.createTempFile(UUID.randomUUID().toString(), fileExtension);
        file.transferTo(attachmentFile);
        emailService.sendEmail(email, attachmentFile, file.getResource().getFilename());
    }

    @GetMapping("api/download")
    @Override
    public void downloadFile(HttpServletResponse response, @RequestParam("filename") String filename) throws IOException {
        File file = new File(String.format(DATA_FOLDER, filename));
        String mimeType = context.getMimeType(file.getPath());
        mimeType = mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mimeType;
        response.setContentType(mimeType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        response.setContentLength((int) file.length());
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("api/download/check")
    @Override
    public boolean checkFilePresence(@RequestParam("filename") String filename) {
        return new File(String.format(DATA_FOLDER, filename)).exists();
    }

}
