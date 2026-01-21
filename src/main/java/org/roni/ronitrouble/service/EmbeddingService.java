package org.roni.ronitrouble.service;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final TextEmbedding textEmbedding;

    @Value("${embedding.api-key}")
    private String apiKey;

    public List<Double> buildQueryEmbedding(String text, String instruct) throws NoApiKeyException {
        var param = TextEmbeddingParam.builder()
                .apiKey(apiKey)
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V4)
                .dimension(1024)
                .outputType(TextEmbeddingParam.OutputType.DENSE)
                .textType(TextEmbeddingParam.TextType.QUERY)
                .instruct(instruct)
                .text(text)
                .build();
        return textEmbedding.call(param).getOutput().getEmbeddings().getFirst().getEmbedding();
    }

    public List<Double> buildDocumentEmbedding(String text) throws NoApiKeyException {
        var param = TextEmbeddingParam.builder()
                .apiKey(apiKey)
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V4)
                .dimension(1024)
                .outputType(TextEmbeddingParam.OutputType.DENSE)
                .textType(TextEmbeddingParam.TextType.DOCUMENT)
                .text(text)
                .build();
        return textEmbedding.call(param).getOutput().getEmbeddings().getFirst().getEmbedding();
    }

    public List<Double> buildProfileQueryEmbedding(String text) throws NoApiKeyException {
        var param = TextEmbeddingParam.builder()
                .apiKey(apiKey)
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V4)
                .dimension(1024)
                .outputType(TextEmbeddingParam.OutputType.DENSE)
                .textType(TextEmbeddingParam.TextType.QUERY)
                .instruct("Given a user preference query, retrieve other individuals who may be of interest to the user")
                .text(text)
                .build();
        return textEmbedding.call(param).getOutput().getEmbeddings().getFirst().getEmbedding();
    }

    public List<Double> buildProfileDocumentEmbedding(String text) throws NoApiKeyException {
        var param = TextEmbeddingParam.builder()
                .apiKey(apiKey)
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V4)
                .dimension(1024)
                .outputType(TextEmbeddingParam.OutputType.DENSE)
                .textType(TextEmbeddingParam.TextType.DOCUMENT)
                .text(text)
                .build();
        return textEmbedding.call(param).getOutput().getEmbeddings().getFirst().getEmbedding();
    }

}
