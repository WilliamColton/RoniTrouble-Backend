package org.roni.ronitrouble.component.store.vectorStore.impl;

import org.roni.ronitrouble.component.store.vectorStore.MilvusStore;
import org.roni.ronitrouble.entity.UserCredential;
import org.springframework.stereotype.Service;

@Service
public class UserCredentialStore extends MilvusStore<UserCredential> {

}
