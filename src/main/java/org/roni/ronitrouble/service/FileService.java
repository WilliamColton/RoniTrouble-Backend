package org.roni.ronitrouble.service;

import cn.hutool.core.util.IdUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FileService {

    private final COSClient cosClient;

    @Value("${cos.bucket-name}")
    private String COS_BUCKET_NAME;

    public FileUrlInfo generateFileUploadUrl(String rawFileName, String category) {
        String fileName = category + "/" + IdUtil.simpleUUID();
        String presignedUrl = cosClient.generatePresignedUrl(COS_BUCKET_NAME, fileName, new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()), HttpMethodName.PUT).toString();
        String downloadUrl = generateFileDownloadUrl(fileName);
        var fileUrlInfo = new FileUrlInfo();
        fileUrlInfo.setDownloadUrl(downloadUrl);
        fileUrlInfo.setPresignedUrl(presignedUrl);
        return fileUrlInfo;
    }

    private String generateFileDownloadUrl(String fileName) {
        return cosClient.getObjectUrl(COS_BUCKET_NAME, fileName).toString();
    }

    @Data
    public static class FileUrlInfo {
        private String PresignedUrl;
        private String DownloadUrl;
    }

}
