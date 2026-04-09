# docs/business-flows-and-problems.md

# Business Flows and Problems

## 1. Mục đích tài liệu
Tài liệu này tổng hợp các **luồng nghiệp vụ phổ biến** và **các bài toán vận hành nổi bật** cho dự án **Order Fulfillment Platform (Backend)**. Dự án được định hướng là một backend hiện đại để học sâu về **Clean Architecture, DDD, Event-driven, security, observability**, đi theo hướng **Modular Monolith trước rồi mới tách dần thành Microservices**, và mỗi tính năng đều gắn với một bài toán production cụ thể như **idempotency, retry, eventual consistency, cache, logging, tracing**. citeturn3search1turn3search2turn3search3

Theo kế hoạch 12 tuần, các khối trọng tâm của dự án gồm **Identity, Catalog, Order, Inventory, Payment, Notification**; đồng thời tiêu chí hoàn thành còn yêu cầu có các flow demo như **đặt hàng thành công**, **thanh toán lỗi + retry**, và **hoàn hàng/cancel**. citeturn3search1turn3search2turn3search3

---

## 2. Phạm vi nghiệp vụ tổng quát của hệ thống
Trong các tài liệu gần đây về ecommerce/order fulfillment, workflow phổ biến của hệ thống thường đi qua các bước: **order receipt / checkout**, **inventory check**, **picking**, **packing**, **shipping**, **tracking**, và **returns/refunds**. Các tài liệu này cũng nhấn mạnh rằng fulfillment không chỉ là “pick-pack-ship”, mà là một chuỗi hoạt động cần phối hợp giữa inventory, order processing, customer communication và reverse logistics. citeturn3search5turn3search6turn3search7turn3search8turn3search9

Với dự án này, điều đó có nghĩa là backend không chỉ dừng ở CRUD cho đơn hàng, mà cần mô phỏng được những điểm quan trọng hơn như: tạo đơn đúng trạng thái, giữ tồn kho đúng cách, xử lý thanh toán và webhook, phát sinh thông báo theo event, và xử lý các trường hợp lỗi hoặc bù trừ nghiệp vụ khi có cancel / payment failed / return. Đây cũng chính là tinh thần của roadmap hiện tại. citeturn3search1turn3search2turn3search3

---

## 3. Các luồng nghiệp vụ phổ biến nên có trong dự án

### 3.1. Identity / Authentication / Authorization
**Mô tả ngắn:**
- Người dùng đăng ký tài khoản.
- Người dùng đăng nhập để nhận access token / refresh token.
- Hệ thống áp dụng role cơ bản như `USER` và `ADMIN` để kiểm soát quyền truy cập API. citeturn3search1turn3search2turn3search3

**Điểm đáng học trong flow này:**
- password hashing
- refresh token
- RBAC cơ bản
- audit cho login
- rate limit login citeturn3search1turn3search2turn3search3

---

### 3.2. Browse Catalog / Product Detail / Search & Filter
**Mô tả ngắn:**
- Người dùng xem danh sách sản phẩm.
- Người dùng lọc theo category / keyword / giá.
- Người dùng xem chi tiết sản phẩm.
- Hệ thống cache list/detail bằng Redis theo mô hình cache-aside. citeturn3search1turn3search2turn3search3

**Điểm đáng học trong flow này:**
- CRUD sản phẩm
- filter / paging
- Redis cache cho list/detail
- invalidation khi update
- benchmark đơn giản trước/sau cache citeturn3search1turn3search2turn3search3

---

### 3.3. Cart / Checkout / Create Order
**Mô tả ngắn:**
- Người dùng thêm sản phẩm vào giỏ hàng.
- Người dùng thay đổi số lượng item trong giỏ.
- Người dùng checkout để tạo đơn hàng.
- Hệ thống lưu **price snapshot** và tạo **OrderStatus** ban đầu. citeturn3search1turn3search2turn3search3

**Điểm đáng học trong flow này:**
- Cart API
- Create Order API
- validation business rules
- order aggregate
- transaction boundaries
- optimistic locking thử nghiệm citeturn3search1turn3search2turn3search3

---

### 3.4. Reserve Inventory / Release Inventory / Chống oversell
**Mô tả ngắn:**
- Sau khi order được tạo, hệ thống thực hiện reserve stock.
- Nếu order bị huỷ hoặc payment fail, hệ thống release stock.
- Kế hoạch dự án yêu cầu có `reserve/release stock`, demo concurrent order, reservation expiry, và decision về locking. citeturn3search1turn3search2turn3search3

**Bối cảnh thực tế:**
Các nguồn về order management và fulfillment gần đây đều xem **inventory inaccuracy**, **overselling**, và thiếu **real-time visibility** là những thách thức quan trọng, đặc biệt khi có nhiều kênh bán hàng hoặc lượng đơn cao. citeturn3search10turn3search12turn3search13turn3search14

**Điểm đáng học trong flow này:**
- stock table
- reservation expiry
- optimistic / pessimistic locking
- concurrent order demo
- không trừ kho trực tiếp trước khi có chiến lược reserve rõ ràng citeturn3search1turn3search2turn3search3

---

### 3.5. Payment Initiation / Payment Result / Webhook Processing
**Mô tả ngắn:**
- Hệ thống khởi tạo payment cho đơn hàng.
- Hệ thống nhận callback hoặc webhook từ payment provider/mock gateway.
- Hệ thống cập nhật trạng thái payment và phát sinh event tương ứng như `PaymentSucceeded` hoặc `PaymentFailed`. citeturn3search1turn3search2turn3search3

**Bối cảnh thực tế:**
Trong các bài viết gần đây về distributed systems và microservices, **idempotency** được xem là cơ chế rất quan trọng để ngăn duplicate charge, duplicate order hoặc duplicate side effects khi request bị retry hoặc khi event/webhook được gửi lặp. Một số nguồn cũng nhấn mạnh vai trò của **Outbox**, **eventual consistency**, và xử lý duplicate event trong các flow phân tán. citeturn3search17turn3search18turn3search20turn3search21

**Điểm đáng học trong flow này:**
- Payment API
- webhook mock
- duplicate webhook/event handling
- audit payment events
- producer/consumer cơ bản qua Kafka hoặc RabbitMQ citeturn3search1turn3search2turn3search3

---

### 3.6. Event-driven Order Updates
**Mô tả ngắn:**
- Khi có các event như `OrderCreated`, `PaymentSucceeded`, `PaymentFailed`, hệ thống sẽ xử lý tiếp các bước phụ thuộc bằng consumer tương ứng. Roadmap của dự án nêu rõ việc phát và tiêu thụ các event cơ bản trong tuần Payment mock + Event-driven. citeturn3search1turn3search2turn3search3

**Bối cảnh thực tế:**
Các tài liệu về event-driven microservices nhấn mạnh rằng khi dùng event để liên kết nhiều service/module, hệ thống phải chấp nhận **eventual consistency** và cần các pattern như **Outbox**, **Saga/compensation**, **retries**, **DLQ**, và **idempotent consumer** để giữ hệ thống ổn định trước partial failure. citeturn3search18turn3search19turn3search21turn3search16

---

### 3.7. Notification theo event
**Mô tả ngắn:**
- Gửi email hoặc in-app notification sau các event như order confirmed, payment failed, shipped, delivered.
- Kế hoạch dự án đã đưa Notification thành một khối riêng với consumer, template, retry policy và dead-letter handling cơ bản. citeturn3search1turn3search2turn3search3

**Điểm đáng học trong flow này:**
- template engine
- retry policy
- dead-letter queue
- không để notification làm chậm request chính citeturn3search1turn3search2turn3search3

---

### 3.8. Fulfillment / Shipping / Delivery Tracking
**Mô tả ngắn:**
- Sau khi order hợp lệ và payment hoàn tất, hệ thống có thể chuyển sang xử lý fulfillment.
- Trong các tài liệu về fulfillment gần đây, các bước phổ biến thường là **picking**, **packing**, **shipping**, **carrier handoff**, **tracking**, và **delivery**. citeturn3search5turn3search6turn3search7turn3search8

**Ý nghĩa đối với dự án:**
Dù roadmap hiện tại chưa tách riêng một tuần chỉ để build warehouse/shipping, luồng fulfillment vẫn là một phần tự nhiên của bài toán order fulfillment và là nơi tốt để mô phỏng trạng thái đơn sau thanh toán. Đây là **đề xuất phạm vi cho dự án**, không phải yêu cầu đã được ghi chi tiết trong roadmap.

---

### 3.9. Cancel Order / Compensation Flow
**Mô tả ngắn:**
- Người dùng hoặc hệ thống huỷ đơn hàng trước khi hoàn tất fulfillment.
- Nếu cần, hệ thống release stock và cập nhật lại trạng thái payment / order.
- Kế hoạch dự án nêu rõ demo flow **hoàn hàng/cancel** như một phần tiêu chí hoàn thành. citeturn3search2turn3search3

**Ý nghĩa đối với dự án:**
Flow này phù hợp để luyện các tình huống bù trừ nghiệp vụ (compensation) khi một phần của giao dịch phân tán đã hoàn tất còn phần khác thì lỗi. Nhận định này là **đề xuất thiết kế cho dự án**, được đưa ra từ phạm vi và mục tiêu học tập đã nêu trong roadmap. citeturn3search2turn3search3

---

### 3.10. Return / Refund
**Mô tả ngắn:**
- Sau giao hàng, khách có thể yêu cầu hoàn trả hoặc hoàn tiền.
- Các nguồn về fulfillment gần đây đều nhấn mạnh rằng **returns/exchanges** là một thách thức đáng kể của ecommerce hiện đại. citeturn3search7turn3search13turn3search14

**Ý nghĩa đối với dự án:**
Roadmap của dự án đã nhắc tới demo **hoàn hàng/cancel**; vì vậy return/refund là một candidate flow phù hợp để mở rộng về sau. Việc đưa flow này thành module độc lập hay chỉ mô phỏng ở mức cơ bản là **đề xuất cho dự án**, không phải đầu ra bắt buộc của các tuần đầu. citeturn3search2turn3search3

---

## 4. Các bài toán vận hành nổi bật nên được mô phỏng trong project

### 4.1. Oversell / inventory inconsistency
Các nguồn gần đây đều nhắc nhiều tới tình trạng **inventory records không khớp thực tế**, **overselling**, và thiếu **real-time visibility**, dẫn đến stockout, đơn bị chậm, hoặc trải nghiệm khách hàng kém. citeturn3search10turn3search12turn3search13turn3search14

**Tác động lên dự án:**
- cần reserve trước khi commit tồn kho
- cần chiến lược locking phù hợp
- cần test concurrent order
- cần xử lý reservation expiry citeturn3search1turn3search2turn3search3

---

### 4.2. Duplicate request / duplicate webhook / duplicate event
Các nguồn về idempotency và event-driven microservices nhấn mạnh rằng retry là hiện tượng bình thường trong distributed systems; nếu hệ thống không idempotent thì có thể phát sinh duplicate effect như tạo đơn trùng, charge trùng, hoặc xử lý event trùng. citeturn3search17turn3search20turn3search21

**Tác động lên dự án:**
- create order nên có idempotency strategy
- payment webhook cần deduplicate
- consumer cần xử lý at-least-once delivery an toàn hơn citeturn3search1turn3search2turn3search3turn3search17turn3search20

---

### 4.3. Eventual consistency / distributed transaction failure
Các tài liệu về distributed transaction failure và event-driven architecture mô tả tình huống một service commit thành công nhưng event không được publish hoặc consumer không xử lý được, từ đó tạo ra trạng thái không đồng bộ giữa các phần của hệ thống. Các pattern được nhắc đến gồm **Outbox Pattern**, **Saga**, **compensation**, và **idempotent processing**. citeturn3search18turn3search21turn3search16

**Tác động lên dự án:**
- Order, Payment, Inventory không nên giả định cập nhật đồng bộ tuyệt đối khi đã tách bằng event
- nên có chiến lược retry hoặc recovery cho các bước bất đồng bộ
- nên có decision note/ADR để ghi lại trade-off nhất quán dữ liệu citeturn3search1turn3search2turn3search3

---

### 4.4. Timeout / partial failure / flaky dependency
Các nguồn về resilience cho microservices và integration patterns đều nhấn mạnh rằng hệ thống phân tán luôn phải đối mặt với timeout, downstream unavailable, retry storms, và nhu cầu dùng DLQ hoặc retry policy để tránh “kẹt” workflow. citeturn3search19turn3search21turn3search16

**Tác động lên dự án:**
- cần timeout budget rõ ràng cho các call quan trọng
- retry phải có backoff thay vì retry vô hạn
- thất bại kéo dài cần được đẩy sang DLQ hoặc cơ chế quan sát được citeturn3search1turn3search2turn3search3

---

### 4.5. Cache stale / query performance
Roadmap của dự án nêu rõ rủi ro cần tránh ở tuần Catalog là **cache invalidation mơ hồ** và **query chưa có index**. Điều này cho thấy ngoài business logic, dự án cũng muốn người học chạm vào các vấn đề hiệu năng rất phổ biến ở tầng đọc dữ liệu. citeturn3search1turn3search2turn3search3

---

### 4.6. Thiếu observability khi debug flow phân tán
Trong kế hoạch 12 tuần, observability được tách thành một phần riêng gồm **correlation ID**, **structured logging**, **metrics endpoint**, **trace flow create-order**, và cảnh báo tránh kiểu log text rời rạc không có request id. citeturn3search1turn3search2turn3search3

**Tác động lên dự án:**
- cần truy được một luồng create-order qua nhiều module/service
- cần nhìn thấy số lần retry / fail / success
- cần biết order nào bị stuck ở bước nào citeturn3search1turn3search2turn3search3

---

### 4.7. Customer communication / delivery visibility / returns
Các nguồn về fulfillment nhấn mạnh rằng khách hàng ngày càng kỳ vọng giao nhanh, thông tin trạng thái rõ ràng, và chính sách return thuận tiện; khi fulfillment chậm, tracking thiếu minh bạch, hoặc return xử lý kém thì ảnh hưởng trực tiếp tới trải nghiệm khách hàng và chi phí vận hành. citeturn3search10turn3search13turn3search14turn3search9

**Tác động lên dự án:**
- notification không nên chỉ là tính năng “phụ”
- shipping status hoặc delivery status nên có mô hình trạng thái rõ ràng nếu phạm vi dự án mở rộng
- return/refund là hướng mở rộng hợp lý sau MVP citeturn3search2turn3search3turn3search7turn3search13turn3search14

---

## 5. Các scenario ưu tiên nên demo trong dự án
Phần này là **đề xuất cho dự án**, được xây dựng dựa trên roadmap và các bài toán vận hành vừa liệt kê.

### Scenario A — Place Order thành công
- Tạo order
- Reserve inventory
- Payment success
- Order confirmed
- Notification sent

**Lý do chọn:** đây là happy path lõi của toàn hệ thống. Roadmap cũng đã xoay quanh các khối Order, Inventory, Payment và Notification. citeturn3search1turn3search2turn3search3

### Scenario B — Payment webhook trùng nhưng hệ thống chỉ xử lý một lần
- Tạo order
- Nhận webhook `PaymentSucceeded` nhiều hơn một lần
- Hệ thống không cập nhật trạng thái hoặc side effects trùng lặp

**Lý do chọn:** roadmap đã nêu explicit rủi ro “không xử lý duplicate webhook/event”, còn các tài liệu về idempotency cũng xem đây là rủi ro thực tế của distributed systems. citeturn3search1turn3search2turn3search3turn3search17turn3search20

### Scenario C — Payment failed hoặc user cancel, stock được release đúng
- Tạo order
- Reserve inventory
- Payment failed hoặc user cancel
- Release inventory
- Update order status
- Notification sent

**Lý do chọn:** roadmap yêu cầu demo các case như thanh toán lỗi + retry và hoàn hàng/cancel; đây là flow rất phù hợp để luyện compensation. citeturn3search2turn3search3

### Scenario D — Concurrent order chống oversell
- Hai request cùng mua số lượng giới hạn
- Chỉ request hợp lệ được reserve thành công
- Request còn lại thất bại với lý do thiếu khả dụng

**Lý do chọn:** đây là đầu ra rất phù hợp với tuần Inventory + chống oversell, nơi roadmap yêu cầu demo concurrent order và decision về locking. citeturn3search1turn3search2turn3search3

---

## 6. Gợi ý cách dùng tài liệu này trong tuần 1
Theo tài liệu kế hoạch, đầu ra bắt buộc của tuần 1 là **README**, **sơ đồ kiến trúc v1**, **danh sách domain**, và **backlog MVP**; đồng thời cần tránh nhảy vào code quá sớm khi chưa có thiết kế domain. citeturn3search1turn3search2turn3search3

**Đề xuất thực hiện:**
1. Dùng mục **Các luồng nghiệp vụ phổ biến** để xác định các use cases cốt lõi của hệ thống.
2. Dùng mục **Các bài toán vận hành nổi bật** để xác định những failure cases mà project phải mô phỏng.
3. Từ hai mục trên, mới rút ra candidate domains hoặc bounded contexts cho phiên bản kiến trúc đầu tiên.

Ba bước trên là **đề xuất phương pháp làm việc cho dự án này**, không phải nội dung được ghi nguyên văn trong roadmap.

---

## 7. Kết luận
Tài liệu này cố tình đi từ **business flows** và **operational problems** thay vì cố chốt bounded contexts ngay từ đầu. Với dự án Order Fulfillment Platform hiện tại, đây là cách tiếp cận phù hợp vì roadmap của dự án tập trung rất mạnh vào các tình huống production như **oversell prevention**, **duplicate webhook**, **timeout liên service**, **retry**, **idempotency**, **observability**, và **cancel/return flows**. citeturn3search1turn3search2turn3search3turn3search17turn3search18turn3search19turn3search20turn3search21

Khi đã rõ các flow và pain points, việc xác định candidate domain/bounded context sẽ dễ hơn và ít mang tính “đoán mò” hơn. Đây là lý do tài liệu này nên được xem như một đầu vào cho `architecture-v1.md` hoặc `bounded-contexts.md` trong tuần khởi tạo dự án.
