package me.bubble.bubble.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.bubble.bubble.dto.response.CurveInfoResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutCreateRequest {
    private CurveInfoResponse curve;
}
