# Bản thiết kế công việc 12 tuần – Backend Website hiện đại

- Người dùng: Le Minh Quang
- Đề tài trọng tâm: Order Fulfillment Platform (Backend)
- Ngày tạo: 08/04/2026

## 1. Mục tiêu tổng quát
Xây một backend website hiện đại đủ thực tế để học sâu về Clean Architecture, DDD, Event-driven, security, observability và các bài toán production thường gặp.

## 2. Nguyên tắc làm việc
- Tập trung vào một đề tài đủ thực tế để học được nhiều pattern hiện đại: Order, Inventory, Payment, Notification, Auth.
- Đi từ Modular Monolith trước, sau đó mới tách dần thành Microservices để giảm độ phức tạp ban đầu.
- Ưu tiên học qua sản phẩm thật: mỗi tuần đều phải có deliverable chạy được, test được, và có note rút kinh nghiệm.
- Mỗi tính năng đều gắn với một bài toán production cụ thể: idempotency, retry, eventual consistency, cache, logging, tracing, security.

## 3. Tiêu chí hoàn thành
- Có repo backend chạy được local bằng Docker Compose.
- Có 5 domain chính: Identity, Catalog, Order, Inventory, Payment, Notification.
- Có CI cơ bản (build + test) và tài liệu README/ADR rõ ràng.
- Có observability cơ bản: logs có correlation-id, metrics, tracing sơ bộ.
- Có demo 3 flow: đặt hàng thành công, thanh toán lỗi + retry, hoàn hàng/cancel.

## 4. Lộ trình 12 tuần

### Tuần 1: Khởi tạo dự án + Kiến trúc nền
- Mục tiêu: Dựng skeleton dự án, xác định bounded contexts, viết tài liệu kiến trúc ban đầu.
- Đầu ra bắt buộc: Monorepo hoặc multi-module repo; README; sơ đồ kiến trúc v1; danh sách domain; backlog MVP.
- Task chính: Tạo repo; chọn stack; dựng Docker Compose (Postgres, Redis); tạo modules; viết coding conventions; tạo seed project; viết ADR-001 kiến trúc.
- Rủi ro cần tránh: Nhảy vào code quá sớm, thiếu thiết kế domain.
- Khối lượng gợi ý: 20-25 giờ

### Tuần 2: Identity + Security foundation
- Mục tiêu: Đăng ký/đăng nhập, JWT, RBAC cơ bản.
- Đầu ra bắt buộc: Auth API; refresh token; role USER/ADMIN; Postman collection; test cho auth flow.
- Task chính: Entity user/role; password hashing; access/refresh token; middleware bảo vệ route; audit cho login; rate limit login.
- Rủi ro cần tránh: Bỏ qua revoke token, lưu secret không an toàn.
- Khối lượng gợi ý: 18-22 giờ

### Tuần 3: Catalog + Cache
- Mục tiêu: Xây product catalog và cache-aside với Redis.
- Đầu ra bắt buộc: API CRUD sản phẩm; filter/paging; Redis cache cho list/detail; invalidation khi update.
- Task chính: Product, category, price; query paging; cache key strategy; benchmark đơn giản trước/sau cache.
- Rủi ro cần tránh: Cache invalidation mơ hồ, query chưa có index.
- Khối lượng gợi ý: 18-22 giờ

### Tuần 4: Cart + Order core
- Mục tiêu: Tạo giỏ hàng, tạo đơn hàng, state machine ban đầu.
- Đầu ra bắt buộc: Cart API; Create Order API; OrderStatus; validation business rules; test nghiệp vụ.
- Task chính: Cart item; price snapshot; order aggregate; transaction boundaries; optimistic locking thử nghiệm.
- Rủi ro cần tránh: Trộn logic order với payment/inventory.
- Khối lượng gợi ý: 20-24 giờ

### Tuần 5: Inventory + chống oversell
- Mục tiêu: Reservation hàng tồn kho và xử lý đồng thời.
- Đầu ra bắt buộc: Inventory API; reserve/release stock; demo concurrent order; tài liệu decision về locking.
- Task chính: Stock table; reservation expiry; optimistic/pessimistic locking; idempotency key cho create order.
- Rủi ro cần tránh: Cho phép trừ kho trực tiếp mà không có reserve.
- Khối lượng gợi ý: 20-24 giờ

### Tuần 6: Payment mock + Event-driven
- Mục tiêu: Tạo payment service giả lập và luồng event cơ bản.
- Đầu ra bắt buộc: Payment API; webhook mock; các event OrderCreated/PaymentSucceeded/PaymentFailed; consumer xử lý.
- Task chính: Broker (Kafka hoặc RabbitMQ); producer/consumer; outbox hoặc retry thủ công ở mức học tập; audit payment events.
- Rủi ro cần tránh: Không xử lý duplicate webhook/event.
- Khối lượng gợi ý: 22-26 giờ

### Tuần 7: Notification + Async workflows
- Mục tiêu: Gửi email/in-app notification theo event.
- Đầu ra bắt buộc: Notification service; email template giả lập; dead-letter handling cơ bản; retry policy.
- Task chính: Consumer cho order confirmed/payment failed; template engine; DLQ; exponential backoff concept note.
- Rủi ro cần tránh: Notification làm chậm request chính.
- Khối lượng gợi ý: 16-20 giờ

### Tuần 8: Observability + SRE basics
- Mục tiêu: Có log, trace, metric để debug xuyên service.
- Đầu ra bắt buộc: Correlation ID; structured logging; metrics endpoint; dashboard tối thiểu; trace flow create-order.
- Task chính: OpenTelemetry; Prometheus/Grafana; log format JSON; health/readiness checks.
- Rủi ro cần tránh: Chỉ log text rời rạc, thiếu request id.
- Khối lượng gợi ý: 18-22 giờ

### Tuần 9: Testing + CI/CD cơ bản
- Mục tiêu: Tăng độ tin cậy khi sửa code.
- Đầu ra bắt buộc: Unit test; integration test; Testcontainers; pipeline build-test; badge trạng thái.
- Task chính: Test auth/order/inventory; seed test data; dockerized test; linting; pre-commit hooks nếu muốn.
- Rủi ro cần tránh: Test chỉ cover happy path.
- Khối lượng gợi ý: 18-22 giờ

### Tuần 10: Refactor sang ranh giới microservices rõ hơn
- Mục tiêu: Tách dần domain quan trọng khỏi monolith/module.
- Đầu ra bắt buộc: Ít nhất 2 service tách riêng (ví dụ Order + Payment hoặc Order + Notification); gateway nội bộ sơ bộ.
- Task chính: API contracts; anti-corruption layer nội bộ; config/service discovery đơn giản; update diagrams.
- Rủi ro cần tránh: Tách quá nhiều service cùng lúc.
- Khối lượng gợi ý: 20-25 giờ

### Tuần 11: Case study production scenarios
- Mục tiêu: Giải 3 tình huống thực tế bằng code và tài liệu.
- Đầu ra bắt buộc: Demo: oversell prevention, duplicate webhook, timeout liên service; doc lessons learned.
- Task chính: Kịch bản lỗi; retry/circuit-breaker concept; manual failure injection; retrospective note.
- Rủi ro cần tránh: Chỉ demo thành công, không demo failure cases.
- Khối lượng gợi ý: 16-20 giờ

### Tuần 12: Đóng gói portfolio + tài liệu dài hạn
- Mục tiêu: Biến project thành case study có thể dùng khi phỏng vấn hoặc nội bộ.
- Đầu ra bắt buộc: README hoàn chỉnh; architecture diagram cuối; roadmap v2; video demo ngắn; backlog nâng cấp 6-8 tuần tiếp theo.
- Task chính: Làm sạch code; chuẩn hoá docs; viết trade-offs; tạo release v1.0; tổng kết kiến thức học được.
- Rủi ro cần tránh: Có code nhưng không có câu chuyện để trình bày.
- Khối lượng gợi ý: 16-20 giờ

## 5. Checklist review cuối tuần
- Tuần này mình ship được gì chạy được thật, chứ không chỉ là code dở dang?
- Có decision kỹ thuật nào cần viết thành ADR để sau này không tự quên?
- Flow lỗi nào mình chưa test: duplicate request, timeout, retry, rollback logic?
- Nếu demo cho người khác trong 5 phút, mình sẽ demo case nào ấn tượng nhất?
- Điểm nghẽn lớn nhất tuần này là kiến thức, thiết kế, hay kỷ luật thực thi?
