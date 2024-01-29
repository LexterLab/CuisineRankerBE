package com.cranker.cranker.token;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("tokens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Token {
    @Id
    private String value;
    private String type;
    private String createdAt;
    @TimeToLive
    private Long expirySeconds;
    private String confirmedAt;
    private Long userId;
}
