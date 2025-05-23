package propensi.amesta.service;

import propensi.amesta.payload.request.DashboardRequestDTO;
import propensi.amesta.payload.response.DashboardResponseDTO;

public interface DashboardService {
    DashboardResponseDTO getDynamicDashboardData(DashboardRequestDTO request);
}
