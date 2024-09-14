package me.bubble.bubble.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class PutResponse {
    private final List<PutResponseObject> delete;
    private final List<PutResponseObject> update;
    private final List<PutResponseObject> create;

    public PutResponse(List<PutResponseObject> delete,
                       List<PutResponseObject> update,
                       List<PutResponseObject> create) {
        this.delete = delete == null ? Collections.emptyList() : Collections.unmodifiableList(delete);;
        this.update = update == null ? Collections.emptyList() : Collections.unmodifiableList(update);;
        this.create = create == null ? Collections.emptyList() : Collections.unmodifiableList(create);;
    }
}
