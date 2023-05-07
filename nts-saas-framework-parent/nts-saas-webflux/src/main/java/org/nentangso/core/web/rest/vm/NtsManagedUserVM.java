package org.nentangso.core.web.rest.vm;

import org.nentangso.core.service.dto.NtsAdminUserDTO;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 */
public class NtsManagedUserVM extends NtsAdminUserDTO {

    public NtsManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + "} ";
    }
}
