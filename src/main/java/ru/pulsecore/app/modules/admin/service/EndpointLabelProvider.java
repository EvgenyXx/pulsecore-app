// modules/admin/service/EndpointLabelProvider.java
package ru.pulsecore.app.modules.admin.service;

import java.util.List;

public interface EndpointLabelProvider {
    List<EndpointLabel> labels();
}