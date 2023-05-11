package org.nentangso.core.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import org.zalando.problem.Problem;

@JsonIncludeProperties("errors")
public class NtsProblem implements Problem {
    private Object errors;

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
