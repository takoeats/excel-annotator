package io.github.takoeats.excelannotator.testdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryParams {
    private String status;
    private Integer page;
    private Integer size;
    private String sortBy;
}
