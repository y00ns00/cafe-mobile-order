# Cafe Mobile Order API Documentation

## 목차
- [주문 API](#주문-api)
- [회원 API](#회원-api)
- [결제 API](#결제-api)
- [상품 API](#상품-api)

---

## 주문 API

Base URL: `/orders`

### 1. 주문 생성
```
POST /orders
```

**설명**: 상품을 주문하고 결제를 진행합니다. 결제가 성공하면 주문이 생성되고, 실패하면 주문이 취소됩니다.

**Request Body**: `OrderCreateRequest`
- Content-Type: `application/json`

**Response**: `OrderResponse`
- Status: `201 CREATED`
- Content-Type: `application/json`

---

### 2. 주문 단건 조회
```
GET /orders/{orderId}
```

**설명**: 주문 ID로 특정 주문의 상세 정보를 조회합니다.

**Path Parameters**:
- `orderId` (Long, required): 주문 ID (1 이상)

**Response**: `OrderResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 3. 전체 주문 조회
```
GET /orders
```

**설명**: 시스템의 모든 주문 목록을 조회합니다.

**Query Parameters**:
- `page` (int, optional, default: 0): 페이지 번호 (0 이상)
- `size` (int, optional, default: 20): 페이지 크기 (1 이상)

**Response**: `OrderListResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 4. 회원별 주문 조회
```
GET /orders/members/{memberId}
```

**설명**: 특정 회원의 모든 주문 내역을 조회합니다.

**Path Parameters**:
- `memberId` (Long, required): 회원 ID (1 이상)

**Response**: `OrderListResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 5. 주문 취소
```
POST /orders/{orderId}/cancel
```

**설명**: 주문을 취소합니다. 본인의 주문만 취소할 수 있으며, 결제 취소도 함께 진행됩니다. 서빙 중이거나 완료된 주문은 취소할 수 없습니다.

**Path Parameters**:
- `orderId` (Long, required): 주문 ID (1 이상)

**Request Body**: `OrderCancelRequest`
- Content-Type: `application/json`

**Response**: `OrderResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

## 회원 API

### 1. 회원 가입
```
POST /members/signup
```

**설명**: 새로운 회원을 등록합니다.

**Request Body**: `MemberSignUpRequest`
- Content-Type: `application/json`

**Response**: `MemberResponse`
- Status: `201 CREATED`
- Content-Type: `application/json`

---

### 2. 회원 탈퇴
```
POST /members/{memberId}/withdraw
```

**설명**: 회원 탈퇴를 요청합니다. 상태가 WITHDRAW_REQUESTED로 변경되며, 30일 이내 철회 가능합니다.

**Path Parameters**:
- `memberId` (Long, required): 회원 ID

**Response**: `MemberResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 3. 회원 탈퇴 철회
```
POST /members/{memberId}/cancel-withdraw
```

**설명**: 회원 탈퇴를 철회합니다. WITHDRAW_REQUESTED 상태에서만 철회 가능하며, ACTIVE 상태로 복구됩니다. DELETED 상태는 철회 불가능합니다.

**Path Parameters**:
- `memberId` (Long, required): 회원 ID

**Response**: `MemberResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 4. 회원 상태 변경 (테스트용)
```
PUT /members/{memberId}/status
```

**설명**: 회원 상태를 변경합니다. (테스트용)

**Path Parameters**:
- `memberId` (Long, required): 회원 ID

**Request Body**: `ChangeStatusRequest`
- Content-Type: `application/json`

**Response**: `MemberResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

## 결제 API

Base URL: `/payments`

### 1. 결제 정보 조회
```
GET /payments/{paymentKey}
```

**설명**: 결제 키로 결제 정보를 조회합니다.

**Path Parameters**:
- `paymentKey` (String, required): 결제 키 (예: "test-payment-key")

**Response**: `PaymentInfoResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 2. 사용자 결제 목록 조회
```
GET /payments/members/{memberId}
```

**설명**: 회원 ID로 해당 사용자의 모든 결제 정보를 조회합니다.

**Path Parameters**:
- `memberId` (Long, required): 회원 ID

**Response**: `PaymentListResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 3. 결제 취소
```
POST /payments/cancel
```

**설명**: 주문 ID로 결제를 취소합니다.

**Request Body**: `PaymentCancelRequest`
- Content-Type: `application/json`

**Response**: `PaymentInfoResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

## 상품 API

Base URL: `/products`

### 1. 상품 생성
```
POST /products
```

**설명**: 새로운 상품을 등록합니다.

**Request Body**: `CreateProductRequest`
- Content-Type: `application/json`

**Response**: `ProductResponse`
- Status: `201 CREATED`
- Content-Type: `application/json`

---

### 2. 상품 조회
```
GET /products/{productId}
```

**설명**: 상품 ID로 상품을 조회합니다.

**Path Parameters**:
- `productId` (Long, required): 상품 ID

**Response**: `ProductResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 3. 전체 상품 조회
```
GET /products
```

**설명**: 모든 상품을 조회합니다.

**Response**: `List<ProductResponse>`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 4. 판매 가능한 상품 조회
```
POST /products/available
```

**설명**: 상품 ID 목록으로 판매 가능한 상품들을 조회합니다.

**Request Body**: `List<Long>` (상품 ID 목록)
- Content-Type: `application/json`

**Response**: `List<ProductResponse>`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 5. 상품 수정
```
PUT /products/{productId}
```

**설명**: 기존 상품 정보를 수정합니다.

**Path Parameters**:
- `productId` (Long, required): 상품 ID

**Request Body**: `UpdateProductRequest`
- Content-Type: `application/json`

**Response**: `ProductResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---

### 6. 상품 삭제
```
DELETE /products/{productId}
```

**설명**: 상품을 삭제합니다.

**Path Parameters**:
- `productId` (Long, required): 상품 ID

**Response**: 없음
- Status: `204 NO CONTENT`

---

### 7. 상품 상태 변경
```
PUT /products/{productId}/status
```

**설명**: 상품의 판매 상태를 변경합니다.

**Path Parameters**:
- `productId` (Long, required): 상품 ID

**Query Parameters**:
- `status` (String, required): 변경할 상태

**Response**: `ProductResponse`
- Status: `200 OK`
- Content-Type: `application/json`

---
