package org.nentangso.core.web.rest.errors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NtsErrors implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object errors;

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public NtsErrors() {
    }

    public NtsErrors(Object errors) {
        this.errors = errors;
    }

    public static NtsErrors singleError(String error) {
        return new NtsErrors(error);
    }

    public static NtsErrors mapErrors(Map<String, List<String>> errors) {
        return new NtsErrors(errors);
    }
}
