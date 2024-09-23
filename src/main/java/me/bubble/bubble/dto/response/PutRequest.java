package me.bubble.bubble.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.bubble.bubble.dto.request.PutCreateRequest;
import me.bubble.bubble.dto.request.PutDeleteRequest;
import me.bubble.bubble.dto.request.PutUpdateRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutRequest { // delete, update, create는 요청에서 오는 배열의 이름과 똑같아야 함.
    private List<PutDeleteRequest> delete;
    private List<PutUpdateRequest> update;
    private List<PutCreateRequest> create;
}
