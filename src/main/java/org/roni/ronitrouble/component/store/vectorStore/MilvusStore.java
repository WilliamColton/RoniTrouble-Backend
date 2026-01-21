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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public abstract class MilvusStore<T> extends ParameterizedTypeRef<MilvusStore<T>> {

    private final String collectionName = getUncapitaliseClassName();
    @Autowired
    private MilvusClientV2 milvusClientV2;
    @Autowired
    private Gson gson;

    public void upsert(Integer id, List<Double> vector) {
        Map<String, Object> map = new HashMap<>();
        map.put("vector", vector);
        map.put("id", id);
        UpsertReq upsertReq = UpsertReq.builder()
                .collectionName(collectionName)
                .data(List.of(gson.toJsonTree(map).getAsJsonObject()))
                .build();
        milvusClientV2.upsert(upsertReq);
    }

    public void deleteById(Integer id) {
        DeleteReq deleteReq = DeleteReq.builder()
                .collectionName(collectionName)
                .ids(List.of(id))
                .build();
        milvusClientV2.delete(deleteReq);
    }

    public List<SimilarResult> listSimilarResultsByVector(List<Double> vector, Integer limit) {
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

    public SimilarResult toSimilarResult(SearchResp.SearchResult searchResult) {
        SimilarResult similarResult = new SimilarResult();
        similarResult.score = searchResult.getScore();
        if (searchResult.getId() instanceof Long id) {
            similarResult.id = id.intValue();
        } else {
            throw new RuntimeException("数据库 id 字段异常");
        }
        return similarResult;
    }

    @Getter
    public static class SimilarResult {

        private Float score;
        private Integer id;

    }

}
