package me.bubble.bubble.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutUpdateRequest {
    private Long id;
    private CurveInfoResponse curve;
}
