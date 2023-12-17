package webdoc.community.domain.entity.report.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCreateRequest {
    @NotNull
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String reason;
}
