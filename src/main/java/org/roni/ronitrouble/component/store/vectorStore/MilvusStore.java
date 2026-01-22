package org.roni.ronitrouble.component.store.vectorStore;

import com.google.gson.Gson;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.UpsertReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.Getter;
import org.roni.ronitrouble.util.ParameterizedTypeRef;
import org.roni.ronitrouble.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MilvusStore<T, ID> {

    private final String collectionName = getUncapitaliseClassName();

    private String getUncapitaliseClassName() {
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(this.getClass(), MilvusStore.class);
        if (generics == null || generics.length < 2) {
            throw new RuntimeException("泛型参数解析失败");
        }
        return StringUtil.uncapitalise(generics[0].getSimpleName());
    }

    @Autowired
    private MilvusClientV2 milvusClientV2;
    @Autowired
    private Gson gson;

    public void upsert(ID id, List<Double> vector) {
        Map<String, Object> map = new HashMap<>();
        map.put("vector", vector);
        map.put("id", id);
        UpsertReq upsertReq = UpsertReq.builder()
                .collectionName(collectionName)
                .data(List.of(gson.toJsonTree(map).getAsJsonObject()))
                .build();
        milvusClientV2.upsert(upsertReq);
    }

    public void deleteById(ID id) {
        DeleteReq deleteReq = DeleteReq.builder()
                .collectionName(collectionName)
                .ids(List.of(id))
                .build();
        milvusClientV2.delete(deleteReq);
    }

    public List<SimilarResult<ID>> listSimilarResultsByVector(List<Double> vector, Integer limit) {
        SearchReq searchReq = SearchReq.builder()
                .collectionName(collectionName)
                .data(List.of(new FloatVec(vector.stream().map(Double::floatValue).toList())))
                .annsField("vector")
                .metricType(IndexParam.MetricType.COSINE)
                .limit(limit)
                .build();
        return milvusClientV2.search(searchReq).getSearchResults().getFirst().stream()
                .map(this::toSimilarResult)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public SimilarResult<ID> toSimilarResult(SearchResp.SearchResult searchResult) {
        SimilarResult<ID> similarResult = new SimilarResult<>();
        similarResult.score = searchResult.getScore();
        if (searchResult.getId() instanceof Long id) {
            similarResult.id = (ID) id;
        } else if (searchResult.getId() instanceof Integer id) {
            similarResult.id = (ID) id;
        } else if (searchResult.getId() instanceof String id) {
            similarResult.id = (ID) id;
        }
        return similarResult;
    }

    @Getter
    public static class SimilarResult<ID> {

        private Float score;
        private ID id;

    }

}
