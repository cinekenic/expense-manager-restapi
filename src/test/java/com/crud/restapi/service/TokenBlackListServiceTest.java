package com.crud.restapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlackListServiceTest {

    private TokenBlackListService tokenBlackListService;

    @BeforeEach
    void setUp() {
        tokenBlackListService = new TokenBlackListService();
    }

    @Test
    void shouldAddTokenToBlacklistAndDetectIt() {
        // given
        String token = "dummy.jwt.token";

        // when
        tokenBlackListService.addTokenToBlacklist(token);

        // then
        assertThat(tokenBlackListService.isTokenBlacklisted(token)).isTrue();
    }

    @Test
    void shouldReturnFalseIfTokenNotBlacklisted() {
        // given
        String token = "not-in-blacklist";

        // then
        assertThat(tokenBlackListService.isTokenBlacklisted(token)).isFalse();
    }
}
