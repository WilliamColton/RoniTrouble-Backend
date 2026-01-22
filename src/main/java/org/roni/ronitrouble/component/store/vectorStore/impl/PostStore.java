package org.roni.ronitrouble.component.store.vectorStore.impl;

import org.roni.ronitrouble.component.store.vectorStore.MilvusStore;
import org.roni.ronitrouble.entity.Post;
import org.springframework.stereotype.Service;

@Service
public class PostStore extends MilvusStore<Post,String> {

}
