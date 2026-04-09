# docs/bounded-contexts.md

# Bounded Contexts

## 1. Mục đích tài liệu
Tài liệu này **đề xuất** các candidate bounded contexts cho dự án **Order Fulfillment Platform (Backend)**. Cách tiếp cận ở đây là đi từ roadmap, business flows và các bài toán vận hành nổi bật của dự án, sau đó mới suy ra ranh giới domain hợp lý cho phiên bản kiến trúc đầu tiên. Roadmap hiện tại của dự án định hướng một backend hiện đại để học sâu về **Clean Architecture, DDD, Event-driven, security, observability**, bắt đầu từ **Modular Monolith** rồi mới tách dần sang **Microservices**. citeturn3search1turn3search2turn3search3

Roadmap cũng xác định các khối trọng tâm gồm **Identity, Catalog, Order, Inventory, Payment, Notification**, cùng các chủ đề vận hành như **idempotency, retry, eventual consistency, cache, logging, tracing**, và các flow demo như **đặt hàng thành công**, **thanh toán lỗi + retry**, **hoàn hàng/cancel**. citeturn3search1turn3search2turn3search3

Vì vậy, tài liệu này không cố gắng “chốt kiến trúc cuối cùng”, mà tập trung vào một bộ **candidate bounded contexts đủ hợp lý để bắt đầu thiết kế module và code tuần 1–4**. Phần nào là **đề xuất của tài liệu** sẽ được ghi rõ là **đề xuất**.

---

## 2. Nguyên tắc tách bounded context cho dự án này
Các nguyên tắc dưới đây là **đề xuất thiết kế** cho dự án:

1. **Tách theo trách nhiệm nghiệp vụ trước, không tách theo bảng dữ liệu.**
2. **Ưu tiên modular monolith trước**, sau đó mới cân nhắc tách service khi boundary đã rõ. Điều này phù hợp với roadmap hiện tại của dự án. citeturn3search1turn3search2turn3search3
3. **Tách riêng các phần có lifecycle và rule khác nhau**, đặc biệt là Order, Inventory và Payment, vì roadmap của bạn đã nhấn mạnh rõ các bài toán như price snapshot, reserve/release stock, idempotency và duplicate webhook/event. citeturn3search1turn3search2turn3search3
4. **Giữ notification ở dạng async side effect**, không để nó chặn request chính. Điều này cũng trùng với rủi ro đã nêu trong plan tuần Notification. citeturn3search1turn3search2turn3search3
5. **Ưu tiên boundary phục vụ được các failure scenarios thực tế** như oversell, duplicate request/webhook, eventual consistency và timeout liên service. Các chủ đề này xuất hiện rõ trong roadmap và trong các tài liệu recent về microservices/order fulfillment. citeturn3search1turn3search2turn3search3turn3search10turn3search17turn3search18turn3search19turn3search20turn3search21

---

## 3. Candidate bounded contexts được đề xuất

### 3.1. Identity Context
**Trạng thái:** core context cho giai đoạn đầu. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap dành hẳn một tuần cho **Identity + Security foundation** với các yêu cầu như đăng ký/đăng nhập, JWT, refresh token, RBAC, audit login và rate limit login. Điều này cho thấy authentication/authorization là một khối có rule riêng, lifecycle riêng và không nên trộn vào Order hay Catalog. citeturn3search1turn3search2turn3search3

**Trách nhiệm đề xuất:**
- quản lý user account
- xác thực người dùng
- cấp phát / refresh / revoke token
- quản lý role cơ bản (`USER`, `ADMIN`)
- lưu audit đăng nhập

**Core entities đề xuất:**
- User
- Role
- RefreshToken
- LoginAudit

**Out of scope đề xuất:**
- logic order
- logic payment
- inventory
- notification nghiệp vụ

---

### 3.2. Catalog Context
**Trạng thái:** core context cho giai đoạn đầu. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap có tuần riêng cho **Catalog + Cache**, với CRUD sản phẩm, filter/paging, Redis cache-aside và invalidation. Điều này cho thấy Catalog có bài toán đọc dữ liệu, hiệu năng và invalidation riêng, khác bản chất với Order hoặc Payment. citeturn3search1turn3search2turn3search3

**Trách nhiệm đề xuất:**
- quản lý sản phẩm
- quản lý category
- trả dữ liệu list/detail cho client
- hỗ trợ search/filter/paging
- quản lý cache cho read model catalog

**Core entities đề xuất:**
- Product
- Category
- ProductPrice
- ProductStatus

**Out of scope đề xuất:**
- đặt hàng
- reserve stock
- thanh toán
- shipment

---

### 3.3. Cart Context
**Trạng thái:** supporting context cho giai đoạn đầu. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap tuần 4 có **Cart + Order core**, trong đó Cart API là một deliverable riêng. Cart phục vụ giai đoạn trước khi order được tạo và có lifecycle khác với order đã được xác nhận. citeturn3search1turn3search2turn3search3

**Trách nhiệm đề xuất:**
- thêm/xoá/cập nhật item trong giỏ
- lưu trạng thái giỏ tạm thời
- chuẩn bị dữ liệu trước checkout

**Core entities đề xuất:**
- Cart
- CartItem

**Out of scope đề xuất:**
- trạng thái đơn hàng chính thức
- transaction thanh toán
- reserve stock chính thức

---

### 3.4. Order Context
**Trạng thái:** core context quan trọng nhất của giai đoạn đầu. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap xác định rõ các yêu cầu như **Create Order API**, **OrderStatus**, **price snapshot**, **validation business rules**, **order aggregate**, **transaction boundaries**, và còn nhấn mạnh rủi ro không được trộn logic order với payment/inventory. Điều này là dấu hiệu rất mạnh cho thấy Order phải là một bounded context độc lập. citeturn3search1turn3search2turn3search3

**Trách nhiệm đề xuất:**
- tạo đơn hàng
- lưu price snapshot tại thời điểm checkout
- quản lý lifecycle cơ bản của order
- áp dụng business rules cho create/cancel/confirm
- phát event nghiệp vụ liên quan đến order

**Core entities đề xuất:**
- Order
- OrderItem
- OrderStatus
- OrderPriceSnapshot

**Out of scope đề xuất:**
- xử lý transaction thanh toán
- giữ/trừ kho
- gửi email trực tiếp
- shipment chi tiết

---

### 3.5. Inventory Context
**Trạng thái:** core context cho tính đúng đắn dữ liệu. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap tuần 5 tập trung vào **Inventory + chống oversell**, với `reserve/release stock`, `reservation expiry`, `optimistic/pessimistic locking`, `idempotency key cho create order`, và yêu cầu demo concurrent order. Các nguồn recent về order fulfillment cũng xem **inventory inaccuracy**, **overselling**, và thiếu **real-time visibility** là thách thức quan trọng. citeturn3search1turn3search2turn3search3turn3search10turn3search12turn3search13turn3search14

**Trách nhiệm đề xuất:**
- quản lý available stock
- reserve stock
- release stock
- commit/deduct stock khi điều kiện nghiệp vụ thoả mãn
- kiểm soát concurrent access ở mức phù hợp

**Core entities đề xuất:**
- InventoryItem
- StockReservation
- InventoryMovement

**Out of scope đề xuất:**
- xác thực user
- tạo order aggregate
- capture payment
- gửi notification

---

### 3.6. Payment Context
**Trạng thái:** core context cho reliability và async flow. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap tuần 6 có **Payment API**, **webhook mock**, các event `OrderCreated` / `PaymentSucceeded` / `PaymentFailed`, cùng yêu cầu tránh **duplicate webhook/event**. Các nguồn recent về microservices và distributed systems cũng nhấn mạnh vai trò của **idempotency**, **Outbox**, **eventual consistency**, và **duplicate event handling** trong payment flow. citeturn3search1turn3search2turn3search3turn3search17turn3search18turn3search20turn3search21

**Trách nhiệm đề xuất:**
- tạo payment intent hoặc payment request
- nhận và xử lý webhook/callback
- quản lý transaction payment
- quản lý refund cơ bản (nếu có trong phase sau)
- phát event kết quả thanh toán

**Core entities đề xuất:**
- Payment
- PaymentTransaction
- PaymentStatus
- PaymentWebhookLog
- Refund

**Out of scope đề xuất:**
- định nghĩa price snapshot của order
- quản lý available stock
- gửi notification trực tiếp

---

### 3.7. Notification Context
**Trạng thái:** supporting context nhưng rất hữu ích để học async workflow. citeturn3search1turn3search2turn3search3

**Lý do tách context này:**
Roadmap tuần 7 nêu rõ **Notification service**, **email template giả lập**, **retry policy**, **dead-letter handling**, và rủi ro không để notification làm chậm request chính. Điều này cho thấy Notification không nên nằm bên trong Order hay Payment dưới dạng side effect đồng bộ. citeturn3search1turn3search2turn3search3

**Trách nhiệm đề xuất:**
- gửi email/in-app notification theo event
- quản lý template
- retry khi gửi thất bại
- ghi log kết quả gửi
- xử lý dead-letter ở mức học tập

**Core entities đề xuất:**
- NotificationMessage
- NotificationTemplate
- NotificationDeliveryLog

**Out of scope đề xuất:**
- quyết định nghiệp vụ create order
- reserve stock
- capture payment

---

### 3.8. Fulfillment Context
**Trạng thái:** candidate context mở rộng, nên mô phỏng ở mức vừa phải nếu còn thời gian. 

**Lý do tách context này:**
Các nguồn recent về order fulfillment mô tả các bước phổ biến sau order/payment là **picking**, **packing**, **shipping**, **carrier handoff**, **tracking**, và **delivery**. Điều đó cho thấy phần fulfillment/shipping có ngôn ngữ và lifecycle khác đáng kể so với Order hay Payment. citeturn3search5turn3search6turn3search7turn3search8turn3search9

**Trách nhiệm đề xuất:**
- xử lý bước pick/pack/ship ở mức mô phỏng
- quản lý shipment status cơ bản
- phản ánh trạng thái giao hàng về cho hệ thống

**Core entities đề xuất:**
- FulfillmentTask
- Shipment
- ShipmentStatus

**Out of scope đề xuất:**
- authentication
- price calculation
- payment transaction

**Ghi chú:**
Context này là **đề xuất mở rộng** cho đúng tinh thần “order fulfillment platform”. Nó không được roadmap mô tả chi tiết theo từng tuần như Identity/Catalog/Order/Inventory/Payment/Notification. citeturn3search1turn3search2turn3search3

---

### 3.9. Returns Context
**Trạng thái:** candidate context mở rộng sau MVP.

**Lý do tách context này:**
Roadmap yêu cầu có demo **hoàn hàng/cancel**, và các nguồn recent về fulfillment đều xem **returns/exchanges** là một thách thức lớn của ecommerce hiện đại. Điều đó cho thấy reverse flow có thể cần một ngôn ngữ riêng, khác với order creation ban đầu. citeturn3search2turn3search3turn3search7turn3search13turn3search14

**Trách nhiệm đề xuất:**
- tiếp nhận return request
- quản lý lifecycle hoàn trả ở mức cơ bản
- phối hợp refund/inventory nếu dự án mở rộng tới đây

**Core entities đề xuất:**
- ReturnRequest
- ReturnItem
- ReturnStatus
- RefundRequest

**Out of scope đề xuất:**
- auth
- browse catalog
- core payment initiation

**Ghi chú:**
Context này nên để ở phase sau nếu MVP hiện tại chưa cần giải bài toán reverse logistics sâu.

---

## 4. Quan hệ giữa các context (đề xuất)
Phần này là **đề xuất thiết kế**, dùng để định hướng package/module boundary trong giai đoạn đầu.

- **Identity** phục vụ xác thực và phân quyền cho các request đi vào hệ thống. Điều này bám theo roadmap auth foundation. citeturn3search1turn3search2turn3search3
- **Catalog** cung cấp thông tin sản phẩm cho **Cart** và **Order** tại thời điểm người dùng duyệt hàng và checkout. Việc Catalog tập trung vào read/search/cache cũng bám đúng roadmap tuần Catalog. citeturn3search1turn3search2turn3search3
- **Cart** là vùng tạm trước khi chuyển sang **Order** chính thức. Roadmap tuần 4 đã đặt Cart cạnh Order core. citeturn3search1turn3search2turn3search3
- **Order** khởi phát các bước tiếp theo liên quan tới **Inventory** và **Payment**. Roadmap cũng đã nhấn mạnh ranh giới order với payment/inventory. citeturn3search1turn3search2turn3search3
- **Inventory** chịu trách nhiệm reserve/release stock, hỗ trợ bài toán chống oversell và concurrent order. citeturn3search1turn3search2turn3search3turn3search10turn3search12turn3search13turn3search14
- **Payment** xử lý webhook/result của thanh toán và phát sinh event nghiệp vụ. Các tài liệu recent nhấn mạnh yêu cầu idempotency ở khu vực này. citeturn3search17turn3search18turn3search20turn3search21
- **Notification** nên tiêu thụ event từ Order/Payment/Fulfillment thay vì chèn side effect trực tiếp vào request chính. Điều này phù hợp với roadmap notification async workflow. citeturn3search1turn3search2turn3search3
- **Fulfillment** và **Returns** là hướng mở rộng tự nhiên nếu bạn muốn dự án giống hơn với một order fulfillment platform hoàn chỉnh. Các nguồn về fulfillment gần đây đều xem shipping/tracking/returns là phần lõi của vận hành ecommerce. citeturn3search5turn3search6turn3search7turn3search8turn3search9turn3search13turn3search14

---

## 5. Context map sơ bộ (đề xuất)
```text
Identity  -> bảo vệ API và quản lý user/role
Catalog   -> cung cấp dữ liệu sản phẩm cho Cart/Order
Cart      -> chuyển sang Order khi checkout
Order     -> yêu cầu Inventory reserve stock
Order     -> khởi tạo Payment
Payment   -> trả kết quả về Order
Order     -> phát event cho Notification
Payment   -> phát event cho Notification
Order     -> (mở rộng) chuyển sang Fulfillment sau khi hợp lệ
Fulfillment -> (mở rộng) phát event cho Notification
Returns   -> (mở rộng) phối hợp với Payment và Inventory
```

Context map trên là **đề xuất kiến trúc ban đầu**. Nó được xây từ roadmap hiện tại và từ các workflow fulfillment phổ biến được mô tả trong các nguồn recent. citeturn3search1turn3search2turn3search3turn3search5turn3search6turn3search7turn3search8turn3search9

---

## 6. Ranh giới nên ưu tiên giữ chặt trong codebase
Phần này là **đề xuất kỹ thuật** cho modular monolith ban đầu:

1. **Order không được tự ý cập nhật stock trực tiếp.** Inventory phải là nơi giữ rule reserve/release/commit. Roadmap tuần Inventory đã nhấn mạnh rõ bài toán này. citeturn3search1turn3search2turn3search3
2. **Order không xử lý transaction payment nội bộ.** Payment cần độc lập để mô phỏng webhook, retry và duplicate event handling. citeturn3search1turn3search2turn3search3turn3search17turn3search20
3. **Notification không chặn request chính.** Roadmap notification đã nêu rủi ro này explicit. citeturn3search1turn3search2turn3search3
4. **Catalog không nên mang logic order/inventory/payment.** Catalog tập trung vào product + cache + query concerns. citeturn3search1turn3search2turn3search3
5. **Identity không nên bị trộn vào business module.** Nó là boundary ngang phục vụ authn/authz cho toàn hệ thống. Đây là đề xuất kiến trúc nhất quán với roadmap auth foundation. citeturn3search1turn3search2turn3search3

---

## 7. MVP recommendation cho dự án hiện tại
Nếu cần chốt nhanh một phiên bản bounded contexts để bắt đầu code, tài liệu này **đề xuất** dùng bộ sau cho MVP:

### Bộ context cho MVP
- Identity
- Catalog
- Cart
- Order
- Inventory
- Payment
- Notification

### Context để phase 2 / mở rộng
- Fulfillment
- Returns

Lý do cho cách cắt này là roadmap hiện tại đã mô tả rất cụ thể các phần Identity, Catalog, Cart + Order, Inventory, Payment, Notification; trong khi Fulfillment và Returns hợp lý hơn nếu bạn muốn nâng project thành case study phong phú hơn sau MVP. citeturn3search1turn3search2turn3search3turn3search5turn3search6turn3search7turn3search8turn3search9turn3search13turn3search14

---

## 8. Gợi ý package/module map cho modular monolith (đề xuất)
Phần dưới đây là **đề xuất cấu trúc code** cho giai đoạn đầu:

```text
com.yourapp.identity
com.yourapp.catalog
com.yourapp.cart
com.yourapp.order
com.yourapp.inventory
com.yourapp.payment
com.yourapp.notification
```

Nếu mở rộng tiếp:

```text
com.yourapp.fulfillment
com.yourapp.returns
```

Đây là **đề xuất triển khai**, không phải nội dung được roadmap ghi nguyên văn. Mục tiêu là giữ boundary rõ ngay từ đầu để hỗ trợ các bài toán như idempotency, retry, observability và eventual consistency sau này. citeturn3search1turn3search2turn3search3turn3search17turn3search18turn3search20turn3search21

---

## 9. Quyết định sơ bộ cần ghi vào ADR
Dựa trên roadmap hiện tại, các decision sau rất phù hợp để viết thành ADR ở tuần 1. Đây là **đề xuất của tài liệu**:

1. Chọn **Modular Monolith trước, Microservices sau**. Điều này khớp trực tiếp với định hướng đã có trong kế hoạch. citeturn3search1turn3search2turn3search3
2. Tách riêng **Order / Inventory / Payment** thành boundary khác nhau ngay từ đầu. Điều này bám sát các risk và deliverable của tuần 4–6. citeturn3search1turn3search2turn3search3
3. Notification dùng **async workflow** và retry/DLQ ở mức học tập. Điều này nằm trong roadmap Notification. citeturn3search1turn3search2turn3search3
4. Các flow phân tán cần chuẩn bị cho **idempotency** và **eventual consistency**. Điều này được nhấn mạnh cả trong roadmap lẫn các tài liệu recent về microservices resilience. citeturn3search1turn3search2turn3search3turn3search17turn3search18turn3search19turn3search20turn3search21

---

## 10. Kết luận
Cho giai đoạn hiện tại, cách chia bounded contexts hợp lý nhất cho dự án là bắt đầu từ các khối đã được roadmap mô tả rõ: **Identity, Catalog, Cart, Order, Inventory, Payment, Notification**. Đây là bộ boundary đủ mạnh để bạn học được các bài toán quan trọng nhất của backend hiện đại như auth foundation, cache, order lifecycle, oversell prevention, idempotency, webhook handling, async notification, observability và failure scenarios. citeturn3search1turn3search2turn3search3turn3search10turn3search17turn3search18turn3search19turn3search20turn3search21

**Fulfillment** và **Returns** là hai candidate context mở rộng rất hợp lý nếu bạn muốn làm project “ra dáng order fulfillment platform” hơn ở phase sau, vì các workflow fulfillment gần đây đều xoay quanh shipping/tracking/returns như một phần quan trọng của vận hành ecommerce. citeturn3search5turn3search6turn3search7turn3search8turn3search9turn3search13turn3search14

Nếu dùng tài liệu này trong tuần 1, bạn có thể coi nó là đầu vào trực tiếp cho:
- `ADR-001-architecture.md`
- `architecture-v1.md`
- sơ đồ module/package ban đầu
- backlog kỹ thuật cho tuần 2–6
