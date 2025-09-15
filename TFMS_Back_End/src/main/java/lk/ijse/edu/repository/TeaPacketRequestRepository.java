package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaPacketRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeaPacketRequestRepository extends JpaRepository<TeaPacketRequest, String> {
    @Query(value = "SELECT request_id FROM tea_packet_request ORDER BY request_id DESC LIMIT 1", nativeQuery = true)
    String findLastTeaPacketRequestId();
}
