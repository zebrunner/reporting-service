package com.zebrunner.reporting.web.util.patch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchPatchDescriptor extends PatchDescriptor {

    @NotEmpty
    private List<Long> ids;

}
