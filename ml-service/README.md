# ML Service

FastAPI 기반 예측 서비스입니다.

## 엔드포인트
- `GET /health`
- `POST /predict/lead-time`

## 실행 (Docker)
```bash
docker compose -f ../infra/docker-compose.yml up --build ml-service
```

## 예시 요청
```json
{
  "line_id": "Line-A",
  "utilization": 78.5,
  "defect_rate": 1.8,
  "backlog_hours": 6.5
}
```
