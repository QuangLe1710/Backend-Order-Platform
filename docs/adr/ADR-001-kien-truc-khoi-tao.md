# ADR-001: Chọn kiến trúc khởi tạo

## Context
Cần một backend học tập đủ thực tế nhưng không quá phức tạp ngay từ đầu.

## Decision
Khởi đầu bằng 1 Spring Boot project theo hướng domain-first.
Chưa tách microservices ngay ở tuần 1.

## Consequences
- Dễ chạy local
- Dễ mở rộng dần
- Sẽ cần refactor thành module/domain rõ hơn ở các tuần sau