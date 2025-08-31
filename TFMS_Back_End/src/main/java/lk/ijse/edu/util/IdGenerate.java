package lk.ijse.edu.util;

import org.springframework.stereotype.Component;

@Component
public class IdGenerate {
    public String generateNextUserId(String lastId) {
        if (lastId == null) return "U-000-001";

        String[] parts = lastId.split("-");
        int major = Integer.parseInt(parts[1]);
        int minor = Integer.parseInt(parts[2]);

        minor++;

        if (minor > 999) {
            minor = 1;
            major++;
        }

        if (major > 999) {
            throw new IllegalStateException("User ID count has ended. Please contact the developer.");
        }

        return String.format("U-%03d-%03d", major, minor);
    }
}
